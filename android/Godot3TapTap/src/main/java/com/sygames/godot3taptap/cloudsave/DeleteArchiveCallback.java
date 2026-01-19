package com.sygames.godot3taptap.cloudsave;

import android.util.Log;
import androidx.annotation.NonNull;

import com.sygames.godot3taptap.Godot3TapTap;
import com.taptap.sdk.cloudsave.ArchiveData;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 删除存档回调
 */
public class DeleteArchiveCallback extends BaseCloudSaveCallback {

    public DeleteArchiveCallback(Godot3TapTap plugin) {
        super(plugin);
    }

    @Override
    protected String getErrorSignalName() {
        return "onDeleteArchiveFailed";
    }

    @Override
    public void onArchiveDeleted(@NonNull ArchiveData archive) {
        try {
            JSONObject json = createArchiveDataJson(archive);
            emitSignal("onDeleteArchiveSuccess", json.toString());
        } catch (JSONException e) {
            Log.e("TapSDKBridge", "Failed to create archive JSON", e);
            emitSignal("onDeleteArchiveFailed", "{\"error\":\"JSON creation failed\"}");
        }
    }
}
