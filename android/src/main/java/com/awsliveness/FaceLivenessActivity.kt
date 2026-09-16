package com.awsliveness

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.amplifyframework.ui.liveness.ui.FaceLivenessDetector
import com.amplifyframework.ui.liveness.ui.LivenessColorScheme

class FaceLivenessActivity : ComponentActivity() {

  companion object {
    private const val TAG = "FaceLivenessActivity"
    const val EXTRA_SESSION_ID = "sessionId"
    const val EXTRA_REGION = "region"
    const val RESULT_ERROR = "error"
    const val RESULT_REFERENCE_IMAGE = "referenceImage"
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val sessionId = intent.getStringExtra(EXTRA_SESSION_ID)
    val region = intent.getStringExtra(EXTRA_REGION) ?: "us-east-1"

    if (sessionId.isNullOrEmpty()) {
      finishWithError("Session ID not provided")
      return
    }

    Log.d(TAG, "Starting liveness check with sessionId: $sessionId, region: $region")

    setContent {
      MaterialTheme(
        colorScheme = LivenessColorScheme.default()
      ) {
        PermissionRequiredScreen(
          onPermissionGranted = {
            Scaffold(
              modifier = Modifier.fillMaxSize()
            ) { innerPadding ->
              Box(
                modifier = Modifier
                  .fillMaxSize()
                  .padding(innerPadding)
              ) {
                FaceLivenessDetector(
                  sessionId = sessionId,
                  region = region,
                  onComplete = {
                    finishWithSuccess("success image")
                  },
                  onError = { error ->
                    Log.e(TAG, "Error during Face Liveness flow ${error.message}")
                    finishWithError(error.message ?: "Unknown error occurred")
                  }
                )
              }
            }
          },
          onPermissionDenied = {
            finishWithError("Camera permission is required for liveness check.")
          }
        )
      }
    }
  }

  @Composable
  private fun PermissionRequiredScreen(
    onPermissionGranted: @Composable () -> Unit,
    onPermissionDenied: () -> Unit
  ) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var permissionState by remember {
      mutableStateOf(
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
          PermissionState.Granted
        } else {
          PermissionState.Unknown
        }
      )
    }

    var showRationale by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
      ActivityResultContracts.RequestPermission()
    ) { isGranted ->
      if (isGranted) {
        permissionState = PermissionState.Granted
      } else {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
          showRationale = true
        } else {
          showSettingsDialog = true
        }
      }
    }

    DisposableEffect(lifecycleOwner) {
      val observer = LifecycleEventObserver { _, event ->
        if (event == Lifecycle.Event.ON_RESUME) {
          if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            permissionState = PermissionState.Granted
          }
        }
      }
      lifecycleOwner.lifecycle.addObserver(observer)
      onDispose {
        lifecycleOwner.lifecycle.removeObserver(observer)
      }
    }

    LaunchedEffect(Unit) {
      if (permissionState != PermissionState.Granted) {
        permissionLauncher.launch(Manifest.permission.CAMERA)
      }
    }

    if (permissionState == PermissionState.Granted) {
      onPermissionGranted()
    }

    if (showRationale) {
      AlertDialog(
        onDismissRequest = {
          showRationale = false
          onPermissionDenied()
        },
        title = { Text("Camera Permission Required") },
        text = { Text("This app needs access to your camera to perform the liveness check. Please grant the permission.") },
        confirmButton = {
          Button(onClick = {
            showRationale = false
            permissionLauncher.launch(Manifest.permission.CAMERA)
          }) {
            Text("Try Again")
          }
        },
        dismissButton = {
          Button(onClick = {
            showRationale = false
            onPermissionDenied()
          }) {
            Text("Cancel")
          }
        }
      )
    }

    if (showSettingsDialog) {
      AlertDialog(
        onDismissRequest = {
          showSettingsDialog = false
          onPermissionDenied()
        },
        title = { Text("Permission Denied") },
        text = { Text("Camera permission is required. Please enable it in the app settings to continue.") },
        confirmButton = {
          Button(onClick = {
            showSettingsDialog = false
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
              data = Uri.fromParts("package", packageName, null)
            }
            startActivity(intent)
          }) {
            Text("Open Settings")
          }
        },
        dismissButton = {
          Button(onClick = {
            showSettingsDialog = false
            onPermissionDenied()
          }) {
            Text("Cancel")
          }
        }
      )
    }
  }

  private enum class PermissionState {
    Unknown, Granted
  }

  private fun finishWithSuccess(referenceImage: String = "") {
    val intent = Intent().apply {
      putExtra(RESULT_REFERENCE_IMAGE, referenceImage)
    }
    setResult(RESULT_OK, intent)
    finish()
  }

  private fun finishWithError(error: String) {
    val intent = Intent().apply {
      putExtra(RESULT_ERROR, error)
    }
    setResult(RESULT_CANCELED, intent)
    finish()
  }
}
