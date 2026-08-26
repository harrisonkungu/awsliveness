package com.awsliveness

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
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
      }
    }
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
