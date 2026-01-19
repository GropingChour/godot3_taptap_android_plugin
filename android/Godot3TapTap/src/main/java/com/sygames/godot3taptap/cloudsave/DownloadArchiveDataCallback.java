package com.sygames.godot3taptap.cloudsave;

import android.util.Log;
import androidx.annotation.NonNull;

import com.sygames.godot3taptap.Godot3TapTap;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * 下载存档数据回调
 * 自动解压到本地路径
 */
public class DownloadArchiveDataCallback extends BaseCloudSaveCallback {
    private final String localArchivePath;

    public DownloadArchiveDataCallback(Godot3TapTap plugin, String localArchivePath) {
        super(plugin);
        this.localArchivePath = localArchivePath;
    }

    @Override
    protected String getErrorSignalName() {
        return "onDownloadArchiveDataFailed";
    }

    @Override
    public void onArchiveDataResult(@NonNull byte[] archiveData) {
        try {
            if (localArchivePath == null || localArchivePath.isEmpty()) {
                Log.e("TapSDKBridge", "Local archive path is null or empty");
                emitSignal("onDownloadArchiveDataFailed", "{\"error\":\"Local archive path not specified\"}");
                return;
            }

            Log.d("TapSDKBridge", "Received archive data, size: " + archiveData.length + " bytes");

            // 清理目标路径
            File targetPath = new File(localArchivePath);
            if (targetPath.exists()) {
                if (targetPath.isFile()) {
                    if (!targetPath.delete()) {
                        Log.w("TapSDKBridge", "Failed to delete existing file: " + localArchivePath);
                    }
                } else if (targetPath.isDirectory()) {
                    File[] files = targetPath.listFiles();
                    if (files != null) {
                        for (File file : files) {
                            plugin.deleteRecursive(file);
                        }
                    }
                }
            }

            // 直接从字节流解压到目标路径
            if (plugin.unzipFromBytes(archiveData, localArchivePath)) {
                Log.d("TapSDKBridge", "Archive extracted successfully to: " + localArchivePath);
                
                JSONObject json = new JSONObject();
                json.put("path", localArchivePath);
                json.put("size", archiveData.length);
                emitSignal("onDownloadArchiveDataSuccess", json.toString());
            } else {
                Log.e("TapSDKBridge", "Failed to extract archive");
                emitSignal("onDownloadArchiveDataFailed", "{\"error\":\"Failed to extract archive\"}");
            }

        } catch (Exception e) {
            Log.e("TapSDKBridge", "Failed to process downloaded archive", e);
            emitSignal("onDownloadArchiveDataFailed", "{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}
