package com.mobilemaslahah;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReactContext;
// import com.facebook.react.bridge.ReactActivity;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.util.Locale;

import id.vida.liveness.VIDAException;
import id.vida.liveness.VidaLiveness;
import id.vida.liveness.config.VidaFaceDetectionOption;
import id.vida.liveness.config.VidaUICustomizationOption;
import id.vida.liveness.constants.Shape;
import id.vida.liveness.dto.VidaLivenessRequest;
import id.vida.liveness.dto.VidaLivenessResponse;
import id.vida.liveness.listeners.VidaLivenessListener;
import id.vida.liveness.services.LogService;
import com.mobilemaslahah.KeyConstant;

public class VidaLivenessModule extends ReactContextBaseJavaModule {

    private VidaLiveness livenessDetection;

    public VidaLivenessModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return "VidaLivenessModule";
    }

    @ReactMethod
    public void startLiveness(Promise promise) {
        Log.d("VidaLivenessModule", "MODULENYA KE PANGGIL");
        VidaLivenessRequest livenessRequest = new VidaLivenessRequest();
        livenessRequest.setApiKey(KeyConstant.API_KEY); // This is a mandatory parameter
        livenessRequest.setLicenseKey(KeyConstant.LICENSE_KEY); // This is a mandatory parameter
        final WeakReference<Activity> activity = new WeakReference<>(getCurrentActivity());

        try {
            livenessDetection = VidaLiveness.VidaLivenessBuilder
                    .newInstance(activity, livenessRequest, new VidaLivenessListener() {
                        @Override
                        public void onSuccess(VidaLivenessResponse response) {
                            Log.d("VidaLivenessModule", "ALHAMDULILLAH SUKSES");
                            // Convert the image to base64 and return via Promise
                            String encoded = Base64.encodeToString(response.getImageBytes(), Base64.DEFAULT);
                            promise.resolve(encoded);
                            livenessDetection.release();
                        }

                        @Override
                        public void onError(int errorCode, @NonNull String errorMessage, VidaLivenessResponse response) {
                            Log.d("VidaLivenessModule", "WADUH ERROR NIH");
                            // Return error via Promise
                            promise.reject("ERROR_CODE_" + errorCode, errorMessage);
                            livenessDetection.release();
                        }

                        @Override
                        public void onInitialized() {
                            livenessDetection.startDetection();
                        }
                    })
                    .setDetectionOptions(VidaFaceDetectionOption.VidaFaceDetectionOptionBuilder.newInstance()
                            .setMinimumFrame(20)
                            .setEyeOpenProbability(0.6f)
                            .setLuminanceThreshold(0.6f)
                            .setDetectionTimeout(60)
                            .build())
                    .setUICustomizationOptions(VidaUICustomizationOption.VidaUICustomizationOptionBuilder.newInstance()
                            .setOverlayShape(Shape.OVAL)
                            .setLocal(new Locale("en"))
                            .setShowTutorialScreen(true)
                            .setShowReviewScreen(false)
                            .build())
                    .build();
            livenessDetection.initialize();
        } catch (VIDAException exception) {
            Log.d("VidaLivenessModule", "VIDANYA GA KEPANGGIL NIH");
            exception.printStackTrace();
            promise.reject("VIDA_EXCEPTION", exception.getMessage());
        }
    }
}
