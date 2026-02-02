package com.awsliveness.android;

import android.app.Activity;
import android.content.Intent;

import androidx.activity.result.ActivityResult;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;
import com.amplifyframework.core.Amplify;
import com.awsliveness.FaceLivenessActivity;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.annotation.ActivityCallback;

@CapacitorPlugin(name = "AwsLiveness")
public class AwsLivenessPlugin extends Plugin {

  private boolean isAmplifyConfigured = false;


  @Override
  public void load() {
    super.load();
    Activity activity = getActivity();
    if (activity != null) {
      configureAmplify(activity);
    }
  }

  private void configureAmplify(Activity activity) {
    if (isAmplifyConfigured) {
      return;
    }

    try {
      Amplify.addPlugin(new AWSCognitoAuthPlugin());
      Amplify.configure(activity.getApplicationContext()); // No JSON needed!

      isAmplifyConfigured = true;
      System.out.println("✅ Amplify configured automatically from amplifyconfiguration.json");

    } catch (AmplifyException error) {
      System.err.println("❌ Could not initialize Amplify: " + error);
    }
  }

  @PluginMethod
  public void checkAvailability(PluginCall call) {
    JSObject ret = new JSObject();
    ret.put("available", true);
    call.resolve(ret);
  }

  @PluginMethod
  public void startLivenessCheck(PluginCall call) {
    String sessionId = call.getString("sessionId");
    String region = call.getString("region");

    if (sessionId == null || sessionId.isEmpty()) {
      call.reject("Session ID is required");
      return;
    }

    if (region == null || region.isEmpty()) {
      region = "us-east-1"; // Default region
    }

    Activity activity = getActivity();
    if (activity == null) {
      call.reject("Activity not available");
      return;
    }

    // Start the Kotlin-based Compose activity
    Intent intent = new Intent(activity, FaceLivenessActivity.class);
    intent.putExtra(FaceLivenessActivity.EXTRA_SESSION_ID, sessionId);
    intent.putExtra(FaceLivenessActivity.EXTRA_REGION, region);

    startActivityForResult(call, intent, "handleLivenessResult");
  }

  @ActivityCallback
  private void handleLivenessResult(PluginCall call, ActivityResult result) {
    if (call == null) {
      return;
    }

    JSObject ret = new JSObject();

    if (result.getResultCode() == Activity.RESULT_OK) {
      ret.put("success", true);
      call.resolve(ret);
    } else {
      Intent data = result.getData();
      String error = "Liveness check failed or was cancelled";

      if (data != null && data.hasExtra(FaceLivenessActivity.RESULT_ERROR)) {
        error = data.getStringExtra(FaceLivenessActivity.RESULT_ERROR);
      }

      ret.put("success", false);
      ret.put("error", error);
      call.resolve(ret);
    }
  }
}
