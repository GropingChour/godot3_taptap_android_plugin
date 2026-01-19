package com.sygames.godot3taptap.cloudsave;

import android.util.Log;
import androidx.annotation.NonNull;

import com.sygames.godot3taptap.Godot3TapTap;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 获取存档封面回调
 */
public class GetArchiveCoverCallback extends BaseCloudSaveCallback {

    public GetArchiveCoverCallback(Godot3TapTap plugin) {
        super(plugin);
    }

    @Override
    protected String getErrorSignalName() {
        return "onGetArchiveCoverFailed";
    }

    @Override
    public void onArchiveCoverResult(@NonNull byte[] coverData) {
        // 直接传递byte[]给Godot，不再使用Base64编码
        emitSignal("onGetArchiveCoverSuccess", coverData);
    }
}
