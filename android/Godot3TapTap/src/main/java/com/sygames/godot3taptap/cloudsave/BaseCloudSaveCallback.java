package com.sygames.godot3taptap.cloudsave;

import android.util.Log;
import androidx.annotation.NonNull;

import com.sygames.godot3taptap.Godot3TapTap;
import com.taptap.sdk.cloudsave.ArchiveData;
import com.taptap.sdk.cloudsave.internal.TapCloudSaveRequestCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * 云存档回调基类
 * 实现所有接口方法为空，子类只需 override 需要的方法
 * 提供通用的辅助方法
 */
public abstract class BaseCloudSaveCallback implements TapCloudSaveRequestCallback {
    protected final Godot3TapTap plugin;

    public BaseCloudSaveCallback(Godot3TapTap plugin) {
        this.plugin = plugin;
    }

    /**
     * 子类返回对应的错误信号名称
     */
    protected abstract String getErrorSignalName();

    /**
     * 统一处理错误回调
     */
    @Override
    public void onRequestError(int errorCode, @NonNull String errorMessage) {
        try {
            JSONObject json = new JSONObject();
            json.put("code", errorCode);
            json.put("message", errorMessage);
            emitSignal(getErrorSignalName(), json.toString());
        } catch (JSONException e) {
            Log.e("TapSDKBridge", "Failed to create error JSON", e);
            emitSignal(getErrorSignalName(), "{\"error\":\"" + errorMessage + "\"}");
        }
    }

    /**
     * 创建ArchiveData对象的JSON表示
     */
    protected JSONObject createArchiveDataJson(ArchiveData archive) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("uuid", archive.getUuid());
        json.put("name", archive.getName());
        json.put("summary", archive.getSummary());
        json.put("extra", archive.getExtra());
        json.put("playtime", archive.getPlaytime());
        json.put("fileId", archive.getFileId());
        json.put("coverSize", archive.getCoverSize());
        json.put("createdTime", archive.getCreatedTime());
        json.put("modifiedTime", archive.getModifiedTime());
        json.put("saveSize", archive.getSaveSize());
        return json;
    }

    /**
     * 发送信号到 Godot
     */
    protected void emitSignal(String signalName, Object... args) {
        plugin.emitGodotSignal(signalName, args);
    }

    // 默认空实现，子类按需 override
    @Override
    public void onArchiveCreated(@NonNull ArchiveData archive) {}

    @Override
    public void onArchiveListResult(@NonNull List<ArchiveData> archiveList) {}

    @Override
    public void onArchiveDataResult(@NonNull byte[] archiveData) {}

    @Override
    public void onArchiveUpdated(@NonNull ArchiveData archive) {}

    @Override
    public void onArchiveDeleted(@NonNull ArchiveData archive) {}

    @Override
    public void onArchiveCoverResult(@NonNull byte[] coverData) {}
}
