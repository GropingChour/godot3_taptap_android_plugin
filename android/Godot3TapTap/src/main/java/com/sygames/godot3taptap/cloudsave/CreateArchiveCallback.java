package com.sygames.godot3taptap.cloudsave;

import android.util.Log;
import androidx.annotation.NonNull;

import com.sygames.godot3taptap.Godot3TapTap;
import com.taptap.sdk.cloudsave.ArchiveData;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 创建存档回调
 */
public class CreateArchiveCallback extends CleanupCloudSaveCallback {

    public CreateArchiveCallback(Godot3TapTap plugin, String tempZipPath) {
        super(plugin, tempZipPath);
    }

    @Override
    protected String getErrorSignalName() {
        return "onCreateArchiveFailed";
    }

    @Override
    public void onArchiveCreated(@NonNull ArchiveData archive) {
        try {
            JSONObject json = createArchiveDataJson(archive);
            emitSignal("onCreateArchiveSuccess", json.toString());
        } catch (JSONException e) {
            Log.e("TapSDKBridge", "Failed to create archive JSON", e);
            emitSignal("onCreateArchiveFailed", "{\"error\":\"JSON creation failed\"}");
        }
        cleanupTempFile();
    }
}
