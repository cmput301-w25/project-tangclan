package com.example.tangclan;

import android.content.Context;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.io.InputStream;
import org.json.JSONObject;

public class FirebaseProjectManager {

    public static void switchFirebaseProject(Context context, String projectFileName) {
        try {
            // Read the JSON file from assets
            InputStream stream = context.getAssets().open("firebase_configs/" + projectFileName);
            byte[] buffer = new byte[stream.available()];
            stream.read(buffer);
            stream.close();

            String json = new String(buffer, "UTF-8");
            JSONObject jsonObject = new JSONObject(json);

            // Extract Firebase credentials
            String projectId = jsonObject.getJSONObject("project_info").getString("project_id");
            String apiKey = jsonObject.getJSONArray("api_key").getJSONObject(0).getString("current_key");
            String appId = jsonObject.getJSONArray("client").getJSONObject(0)
                    .getJSONObject("client_info").getString("mobilesdk_app_id");
            String storageBucket = jsonObject.getJSONObject("project_info").getString("storage_bucket");

            // Create FirebaseOptions
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setProjectId(projectId)
                    .setApiKey(apiKey)
                    .setApplicationId(appId)
                    .setStorageBucket(storageBucket)
                    .build();

            // Delete the default Firebase instance (if exists)
            for (FirebaseApp app : FirebaseApp.getApps(context)) {
                if (app.getName().equals("DEFAULT")) {
                    app.delete();
                }
            }

            // Initialize Firebase with new options
            FirebaseApp.initializeApp(context, options);

            // RESET FirebaseAuth and FirebaseFirestore
            resetFirebaseServices();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void resetFirebaseServices() {
        // Sign out the current user
        FirebaseAuth.getInstance().signOut();

        // Reinitialize FirebaseAuth and FirebaseFirestore
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    }
}
