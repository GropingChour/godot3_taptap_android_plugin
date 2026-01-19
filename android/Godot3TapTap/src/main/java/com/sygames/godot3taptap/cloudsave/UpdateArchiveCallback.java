package com.sygames.godot3taptap.cloudsave;

import android.util.Log;
import androidx.annotation.NonNull;

import com.sygames.godot3taptap.Godot3TapTap;
import com.taptap.sdk.cloudsave.ArchiveData;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 更新存档回调
 */
public class UpdateArchiveCallback extends CleanupCloudSaveCallback {

    public UpdateArchiveCallback(Godot3TapTap plugin, String tempZipPath) {
        super(plugin, tempZipPath);
    }

    @Override
    protected String getErrorSignalName() {
        return "onUpdateArchiveFailed";
    }

    @Override
    public void onArchiveUpdated(@NonNull ArchiveData archive) {
        try {
            JSONObject json = createArchiveDataJson(archive);
            emitSignal("onUpdateArchiveSuccess", json.toString());
        } catch (JSONException e) {
            Log.e("TapSDKBridge", "Failed to create archive JSON", e);
            emitSignal("onUpdateArchiveFailed", "{\"error\":\"JSON creation failed\"}");
        }
        cleanupTempFile();
    }
}
