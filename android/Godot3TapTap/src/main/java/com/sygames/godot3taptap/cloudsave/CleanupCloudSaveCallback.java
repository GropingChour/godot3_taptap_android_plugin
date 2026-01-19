package com.sygames.godot3taptap.cloudsave;

import android.util.Log;
import com.sygames.godot3taptap.Godot3TapTap;

import java.io.File;

/**
 * 带清理功能的云存档回调基类
 * 用于需要清理临时文件的操作（如创建和更新存档）
 */
public abstract class CleanupCloudSaveCallback extends BaseCloudSaveCallback {
    protected final String tempZipPath;

    public CleanupCloudSaveCallback(Godot3TapTap plugin, String tempZipPath) {
        super(plugin);
        this.tempZipPath = tempZipPath;
    }

    /**
     * 重写错误处理，添加临时文件清理
     */
    @Override
    public void onRequestError(int errorCode, @androidx.annotation.NonNull String errorMessage) {
        super.onRequestError(errorCode, errorMessage);
        cleanupTempFile();
    }

    /**
     * 清理临时ZIP文件
     */
    protected void cleanupTempFile() {
        if (tempZipPath != null) {
            File tempFile = new File(tempZipPath);
            if (tempFile.exists() && !tempFile.delete()) {
                Log.w("TapSDKBridge", "Failed to delete temp zip: " + tempZipPath);
            }
        }
    }
}
