package com.sygames.godot3taptap.cloudsave;

import android.util.Log;
import androidx.annotation.NonNull;

import com.sygames.godot3taptap.Godot3TapTap;
import com.taptap.sdk.cloudsave.ArchiveData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * 获取存档列表回调
 */
public class GetArchiveListCallback extends BaseCloudSaveCallback {

    public GetArchiveListCallback(Godot3TapTap plugin) {
        super(plugin);
    }

    @Override
    protected String getErrorSignalName() {
        return "onGetArchiveListFailed";
    }

    @Override
    public void onArchiveListResult(@NonNull List<ArchiveData> archiveList) {
        try {
            JSONObject json = new JSONObject();
            JSONArray archivesArray = new JSONArray();
            for (ArchiveData archive : archiveList) {
                archivesArray.put(createArchiveDataJson(archive));
            }
            json.put("archives", archivesArray);
            json.put("count", archiveList.size());
            emitSignal("onGetArchiveListSuccess", json.toString());
        } catch (JSONException e) {
            Log.e("TapSDKBridge", "Failed to create archive list JSON", e);
            emitSignal("onGetArchiveListFailed", "{\"error\":\"JSON creation failed\"}");
        }
    }
}
