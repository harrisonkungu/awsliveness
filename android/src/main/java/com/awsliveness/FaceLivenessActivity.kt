package com.awsliveness // Ensure same package as Java plugin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import com.amplifyframework.predictions.PredictionsException
import com.amplifyframework.ui.liveness.model.FaceLivenessDetectionException
import com.amplifyframework.ui.liveness.ui.FaceLivenessDetector
import com.amplifyframework.ui.liveness.ui.LivenessColorScheme

class FaceLivenessActivity : ComponentActivity() {

  companion object {
    private const val TAG = "FaceLivenessActivity"
    const val EXTRA_SESSION_ID = "sessionId"
    const val EXTRA_REGION = "region"
    const val RESULT_ERROR = "error"
    const val RESULT_REFERENCE_IMAGE = "referenceImage" // Add this
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
