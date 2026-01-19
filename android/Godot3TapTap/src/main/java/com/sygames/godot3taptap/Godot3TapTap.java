package com.sygames.godot3taptap;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.taptap.payment.iap.api.ITapIAP;
import com.taptap.payment.iap.api.ProductDetailsResponseListener;
import com.taptap.payment.iap.api.QueryProductDetailsParams;
import com.taptap.payment.iap.api.QueryProductDetailsParams.Product;
import com.taptap.payment.iap.api.TapTapIAP;
import com.taptap.payment.iap.api.bean.ProductDetails;
import com.taptap.payment.iap.api.BillingFlowParams;
import com.taptap.payment.iap.api.FinishPurchaseParams;
import com.taptap.payment.iap.api.bean.Purchase;
import com.taptap.payment.protocol.bean.TapPaymentResult;

import com.sygames.godot3taptap.cloudsave.*;

import com.taptap.sdk.cloudsave.ArchiveData;
import com.taptap.sdk.cloudsave.ArchiveMetadata;
import com.taptap.sdk.cloudsave.TapTapCloudSave;
import com.taptap.sdk.compliance.TapTapCompliance;
import com.taptap.sdk.core.TapTapLanguage;
import com.taptap.sdk.core.TapTapRegion;
import com.taptap.sdk.core.TapTapSdk;
import com.taptap.sdk.core.TapTapSdkOptions;
import com.taptap.sdk.kit.internal.callback.TapTapCallback;
import com.taptap.sdk.kit.internal.exception.TapTapException;
import com.taptap.sdk.login.Scopes;
import com.taptap.sdk.login.TapTapAccount;
import com.taptap.sdk.login.TapTapLogin;
import com.taptap.sdk.compliance.option.TapTapComplianceOptions;
import com.taptap.sdk.license.TapTapLicense;
import com.taptap.sdk.license.TapTapLicenseCallback;
import com.taptap.sdk.license.TapTapDLCCallback;
import com.taptap.sdk.cloudsave.TapTapCloudSave;
import com.taptap.sdk.cloudsave.internal.TapCloudSaveCallback;
import com.taptap.sdk.cloudsave.internal.TapCloudSaveRequestCallback;
import com.taptap.sdk.cloudsave.ArchiveMetadata;
import com.taptap.sdk.cloudsave.ArchiveData;

import com.sygames.godot3taptap.cloudsave.*;

import org.godotengine.godot.Godot;
import org.godotengine.godot.plugin.GodotPlugin;
import org.godotengine.godot.plugin.SignalInfo;
import org.godotengine.godot.plugin.UsedByGodot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import android.util.Base64;
import android.app.Activity;
import android.content.Intent;
import android.content.Context;

import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Godot3TapTap - TapTap SDK for Godot 3.x
 * <p>
 * 这个插件为Godot 3.x提供TapTap SDK的完整功能，包括：
 * - TapTap登录系统
 * - 内购(IAP)功能
 * - 合规认证
 * - 版权验证
 * - 云存档
 * <p>
 * 官方文档：
 *
 * @author SYGames
 * @version 1.0
 * @see <a href="https://developer.taptap.cn/docs/sdk/taptap-login/guide/tap-login/">TapTap登录</a>
 * @see <a href="https://developer.taptap.cn/docs/sdk/tap-iap/develop/android/">内购系统</a>
 * @see <a href="https://developer.taptap.cn/docs/sdk/compliance/features/">合规认证</a>
 * @see <a href="https://developer.taptap.cn/docs/sdk/copyright-verification/guide/">版权验证</a>
 * @see <a href="https://developer.taptap.cn/docs/sdk/tap-cloudsave/guide/">云存档</a>
 * <p>
 * 使用流程：
 * 1. 调用 initSdk() 初始化SDK
 * 2. 根据需要调用相应的功能方法
 * 3. 在Godot中连接对应的信号来处理回调
 * <p>
 * 信号列表：
 * - onLoginSuccess: 登录成功
 * - onLoginFail: 登录失败
 * - onLoginCancel: 登录取消
 * - onComplianceResult: 合规认证结果
 * - onLicenseSuccess: 正版验证成功
 * - onLicenseFailed: 正版验证失败
 * - onDLCQueryResult: DLC查询结果
 * - onDLCPurchaseResult: DLC购买结果
 * - onProductDetailsResponse: 商品详情查询结果
 * - onPurchaseUpdated: 购买状态更新
 * - onFinishPurchaseResponse: 完成订单结果
 * - onQueryUnfinishedPurchaseResponse: 未完成订单查询结果
 * - onLaunchBillingFlowResult: 启动购买流程结果
 * - onCloudSaveCallback: 云存档统一状态回调
 * - onCreateArchiveSuccess: 创建存档成功
 * - onCreateArchiveFailed: 创建存档失败
 * - onGetArchiveListSuccess: 获取存档列表成功
 * - onGetArchiveListFailed: 获取存档列表失败
 * - onGetArchiveDataSuccess: 下载存档数据成功
 * - onGetArchiveDataFailed: 下载存档数据失败
 * - onUpdateArchiveSuccess: 更新存档成功
 * - onUpdateArchiveFailed: 更新存档失败
 * - onDeleteArchiveSuccess: 删除存档成功
 * - onDeleteArchiveFailed: 删除存档失败
 * - onGetArchiveCoverSuccess: 获取存档封面成功
 * - onGetArchiveCoverFailed: 获取存档封面失败
 */
public class Godot3TapTap extends GodotPlugin {
    private TapTapIAP tapTapIAP = null;
    private final Map<String, ProductDetails> productDetailsCache = new HashMap<>();

    // 主线程 Handler，用于确保UI操作在主线程执行
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    // License verification callback implementation
    private final TapTapLicenseCallback licenseCallback = () -> {
        Log.d("TapSDKBridge", "License verification success");
        emitSignal("onLicenseSuccess");
    };

    // CloudSave统一状态监听
    // 用于接收SDK级别的状态通知（300001:需要登录, 300002:初始化失败）
    private final TapCloudSaveCallback cloudSaveCallback = resultCode -> {
        Log.d("TapSDKBridge", "CloudSave status callback: " + resultCode);
        emitSignal("onCloudSaveCallback", resultCode);
    };

    // DLC callback implementation
    private final TapTapDLCCallback dlcCallback = new TapTapDLCCallback() {
        @Override
        public void onQueryResult(int resultCode, @Nullable HashMap<String, Integer> resultList) {
            Log.d("TapSDKBridge", "DLC query callback: " + resultCode);
            try {
                JSONObject json = new JSONObject();
                json.put("resultCode", resultCode);

                if (resultList != null && !resultList.isEmpty()) {
                    JSONObject queryData = new JSONObject();
                    for (HashMap.Entry<String, Integer> entry : resultList.entrySet()) {
                        queryData.put(entry.getKey(), entry.getValue());
                    }
                    json.put("resultList", queryData);
                } else {
                    json.put("resultList", new JSONObject());
                }

                emitSignal("onDLCQueryResult", json.toString());
            } catch (JSONException e) {
                Log.e("TapSDKBridge", "Error creating DLC query result JSON", e);
                emitSignal("onDLCQueryResult", "{\"error\":\"JSON creation failed\",\"resultCode\":" + resultCode + "}");
            }
        }

        @Override
        public void onPurchaseResult(@NonNull String skuId, int status) {
            Log.d("TapSDKBridge", "DLC purchase callback: " + skuId + " - " + status);
            emitSignal("onDLCPurchaseResult", skuId, status);
        }
    };

    public Godot3TapTap(Godot godot) {
        super(godot);
    }

    @NonNull
    @Override
    public String getPluginName() {
        return "Godot3TapTap";
    }

    @NonNull
    @Override
    public Set<SignalInfo> getPluginSignals() {
        return Set.of(
                new SignalInfo("onLoginSuccess"),
                new SignalInfo("onLoginFail", String.class),
                new SignalInfo("onLoginCancel"),
                new SignalInfo("onComplianceResult", Integer.class, String.class),

                // License verification signals
                new SignalInfo("onLicenseSuccess"),
                new SignalInfo("onLicenseFailed"),
                new SignalInfo("onDLCQueryResult", String.class),
                new SignalInfo("onDLCPurchaseResult", String.class, Integer.class),

                // IAP signals - json
                new SignalInfo("onProductDetailsResponse", String.class),
                new SignalInfo("onPurchaseUpdated", String.class),
                new SignalInfo("onFinishPurchaseResponse", String.class),
                new SignalInfo("onQueryUnfinishedPurchaseResponse", String.class),
                new SignalInfo("onLaunchBillingFlowResult", String.class),

                // CloudSave signals - json
                new SignalInfo("onCloudSaveCallback", Integer.class),
                new SignalInfo("onCreateArchiveSuccess", String.class),
                new SignalInfo("onCreateArchiveFailed", String.class),
                new SignalInfo("onGetArchiveListSuccess", String.class),
                new SignalInfo("onGetArchiveListFailed", String.class),
                new SignalInfo("onDownloadArchiveDataSuccess", String.class),
                new SignalInfo("onDownloadArchiveDataFailed", String.class),
                new SignalInfo("onUpdateArchiveSuccess", String.class),
                new SignalInfo("onUpdateArchiveFailed", String.class),
                new SignalInfo("onDeleteArchiveSuccess", String.class),
                new SignalInfo("onDeleteArchiveFailed", String.class),
                new SignalInfo("onGetArchiveCoverSuccess", byte[].class),
                new SignalInfo("onGetArchiveCoverFailed", String.class)
        );
    }

    /**
     * 确保在主线程执行任务
     * 用于解决 Fragment UI 操作必须在主线程执行的问题
     */
    private void runOnMainThread(Runnable task) {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            // 当前已经在主线程，直接执行
            task.run();
        } else {
            // 在非主线程，通过 Handler 切换到主线程执行
            mainHandler.post(task);
        }
    }

    /**
     * 从 Android 资源文件读取解密密钥
     * 现在支持标准的 R.string 资源访问方式
     */
    private static String getDecryptKey(Context context) {
        try {
            // 方法1：尝试直接使用 R 类访问（如果可用）
            try {
                // 这里可以直接使用 R.string.taptap_decrypt_key，但为了兼容性还是使用反射
                Class<?> rStringClass = Class.forName(context.getPackageName() + ".R$string");
                int resourceId = rStringClass.getField("taptap_decrypt_key").getInt(null);
                String key = context.getString(resourceId);
                if (!key.isEmpty()) {
                    Log.d("TapSDKBridge", "Successfully loaded decrypt key from R.string resource");
                    return key;
                }
            } catch (Exception e) {
                Log.d("TapSDKBridge", "R.string access failed, falling back to identifier lookup", e);
            }

            // 方法2：使用运行时资源标识符查找（备用方案）
            int stringResourceId = context.getResources().getIdentifier("taptap_decrypt_key", "string", context.getPackageName());
            if (stringResourceId != 0) {
                String key = context.getString(stringResourceId);
                if (!key.isEmpty()) {
                    Log.d("TapSDKBridge", "Successfully loaded decrypt key from string resource via identifier");
                    return key;
                }
            }

            // 备选方案：从 raw 资源读取（如果存在）
            int rawResourceId = context.getResources().getIdentifier("taptap_decrypt_key", "raw", context.getPackageName());
            if (rawResourceId != 0) {
                InputStream is = context.getResources().openRawResource(rawResourceId);
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                reader.close();
                String key = sb.toString().trim();
                if (!key.isEmpty()) {
                    return key;
                }
            }
        } catch (Exception e) {
            Log.w("TapSDKBridge", "Failed to read decrypt key from resources, using fallback", e);
        }

        // 如果资源读取失败，使用内置密钥
        return "TapTapz9mdoNZSItSxJOvG";
    }

    /**
     * 解密加密的客户端 Token
     */
    private static String decryptToken(String encryptedToken, Context context) {
        try {
            String decryptKey = getDecryptKey(context);

            // Base64 解码 - 使用 Android 兼容的 Base64
            byte[] encryptedBytes = Base64.decode(encryptedToken, Base64.DEFAULT);
            byte[] keyBytes = decryptKey.getBytes(StandardCharsets.UTF_8);

            // XOR 解密
            byte[] decryptedBytes = new byte[encryptedBytes.length];
            for (int i = 0; i < encryptedBytes.length; i++) {
                decryptedBytes[i] = (byte) (encryptedBytes[i] ^ keyBytes[i % keyBytes.length]);
            }

            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            Log.e("TapSDKBridge", "Failed to decrypt token", e);
            return null;
        }
    }

    /**
     * 使用加密 Token 初始化 SDK
     *
     * @param encryptedClientToken XOR加密的 Client Token (Base64编码)
     * @param clientId             游戏 Client ID，从开发者中心获取
     * @param enableLog            是否启用日志
     * @see <a href="https://developer.taptap.cn/docs/sdk/tap-iap/develop/android/#sdk-%E5%88%9D%E5%A7%8B%E5%8C%96">SDK初始化文档</a>
     */
    @UsedByGodot
    public void initSdkWithEncryptedToken(String clientId, String encryptedClientToken, boolean enableLog, boolean withIAP) {
        try {
            Context context = Objects.requireNonNull(getActivity());
            String decryptedToken = decryptToken(encryptedClientToken, context);
            if (decryptedToken != null) {
                initSdk(clientId, decryptedToken, enableLog, withIAP); // 默认不启用IAP
            } else {
                Log.e("TapSDKBridge", "Failed to decrypt client token");
            }
        } catch (Exception e) {
            Log.e("TapSDKBridge", "Error initializing SDK with encrypted token", e);
        }
    }

    /**
     * 初始化 TapTapSDK 和 IAP 模块
     *
     * @param clientId    游戏 Client ID，从开发者中心获取
     * @param clientToken 游戏 Client Token，从开发者中心获取
     * @param enableLog   是否启用日志
     * @param withIAP     是否启用内购功能
     * @see <a href="https://developer.taptap.cn/docs/sdk/tap-iap/develop/android/#sdk-%E5%88%9D%E5%A7%8B%E5%8C%96">SDK初始化文档</a>
     */
    @UsedByGodot
    public void initSdk(String clientId, String clientToken, boolean enableLog, boolean withIAP) {

        TapTapSdkOptions tapSdkOptions = new TapTapSdkOptions(
                clientId, // 游戏 Client ID 开发者中心对应 Client ID
                clientToken, // 游戏 Client Token 开发者中心对应 Client Token
                TapTapRegion.CN // 游戏可玩区域: [TapTapRegion.CN]=国内 [TapTapRegion.GLOBAL]=海外
        );
        // 设置日志开关
        tapSdkOptions.setEnableLog(enableLog);

        // 可选配置 合规模块
        TapTapComplianceOptions tapComplianceOptions = new TapTapComplianceOptions(
                true, // 是否显示切换账号按钮
                false // 游戏是否需要获取真实年龄段信息，这个设置需要配合后台开关
        );

        // 初始化 TapSDK
        Log.d("TapSDKBridge", "TapTapSdk.init");
        TapTapSdk.init(Objects.requireNonNull(getActivity()), tapSdkOptions, tapComplianceOptions);

        TapTapCompliance.registerComplianceCallback((code, map) -> emitSignal("onComplianceResult", code, getMapJson(map)));

        // 注册正版验证回调
        Log.d("TapSDKBridge", "Registering license callback");
        TapTapLicense.registerLicenseCallback(licenseCallback);

        // 注册DLC回调
        Log.d("TapSDKBridge", "Registering DLC callback");
        TapTapLicense.registerDLCCallback(dlcCallback);

        // 注册云存档统一状态码监听
        Log.d("TapSDKBridge", "Registering CloudSave status callback");
        TapTapCloudSave.registerCloudSaveCallback(cloudSaveCallback);

        if (withIAP) {
            tapTapIAP = TapTapIAP.newBuilder().build();
        }
    }

    /**
     * 将Map转换为JSON字符串
     * 用于处理合规回调等返回的Map数据
     * <p>
     *
     * @param map 要转换的Map对象
     * @return JSON字符串，如果map为null则返回"{}"
     */
    public String getMapJson(@Nullable Map<String, ?> map) {
        if (map == null) {
            return "{}";
        }
        JSONObject json = new JSONObject();
        for (Map.Entry<String, ?> entry : map.entrySet()) {
            try {
                json.put(entry.getKey(), entry.getValue());
            } catch (org.json.JSONException e) {
                e.fillInStackTrace();
            }
        }
        return json.toString();
    }

    // ==================== 内部工具方法 ====================

    /**
     * 创建Purchase对象的JSON表示
     * 统一Purchase对象的序列化格式
     * <p>
     *
     * @param purchase Purchase对象
     * @return Purchase的JSON表示
     * @throws JSONException 如果JSON创建失败
     */
    private JSONObject createPurchaseJson(Purchase purchase) throws JSONException {
        JSONObject purchaseJson = new JSONObject();
        purchaseJson.put("orderId", purchase.getOrderId());
        purchaseJson.put("purchaseToken", purchase.getPurchaseToken());
        purchaseJson.put("orderToken", purchase.getOrderToken());
        purchaseJson.put("productId", purchase.getProductId());
        purchaseJson.put("purchaseState", purchase.getPurchaseState());
        purchaseJson.put("purchaseTime", purchase.getPurchaseTime());
        purchaseJson.put("obfuscatedAccountId", purchase.getObfuscatedAccountId());
        purchaseJson.put("quantity", purchase.getQuantity());
        purchaseJson.put("acknowledged", purchase.getAcknowledged());
        return purchaseJson;
    }

    /**
     * 创建TapPaymentResult的基础JSON对象
     * 统一支付结果的序列化格式
     * <p>
     *
     * @param result TapPaymentResult对象
     * @return 包含响应码和调试信息的JSON对象
     * @throws JSONException 如果JSON创建失败
     */
    private JSONObject createPaymentResultJson(TapPaymentResult result) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("responseCode", result.getResponseCode().toString());
        String debugMessage = result.getDebugMessage();
        if (debugMessage != null) {
            json.put("debugMessage", debugMessage);
        }
        return json;
    }

    /**
     * 安全发送信号，带有异常处理
     * 如果发送失败，会发送包含错误信息的备用信号
     * <p>
     *
     * @param signalName           信号名称
     * @param json                 要发送的JSON对象
     * @param fallbackErrorMessage 发生异常时的错误信息
     */
    private void safeEmitSignal(String signalName, JSONObject json, String fallbackErrorMessage) {
        try {
            emitSignal(signalName, json.toString());
        } catch (Exception e) {
            e.fillInStackTrace();
            // 发生错误时发送包含错误信息的信号
            try {
                JSONObject errorJson = new JSONObject();
                errorJson.put("error", fallbackErrorMessage + ": " + e.getMessage());
                emitSignal(signalName, errorJson.toString());
            } catch (JSONException jsonException) {
                jsonException.fillInStackTrace();
            }
        }
    }

    /**
     * TapTap 用户登录
     * <p>
     *
     * @param useProfile 是否请求用户公开资料权限 (public_profile)
     *                   - true: 获得 openId、unionId、用户昵称、用户头像
     *                   - false: 仅获得 openId 和 unionId (basic_info，支持无感登录)
     * @param useFriends 是否请求好友权限 (user_friends)
     *                   <p>
     *                   触发信号：
     *                   - onLoginSuccess: 登录成功
     *                   - onLoginFail: 登录失败，参数为错误信息
     *                   - onLoginCancel: 用户取消登录
     * @see <a href="https://developer.taptap.cn/docs/sdk/taptap-login/guide/tap-login/">TapTap登录文档</a>
     * <p>
     */
    @UsedByGodot
    public void login(boolean useProfile, boolean useFriends) {
        // 确保在主线程执行，避免 Fragment 操作异常
        runOnMainThread(() -> {
            try {
                // 判断登录后就不再登录
                if (isLogin()) {
                    // 登录成功
                    emitSignal("onLoginSuccess");
                    return;
                }

                // 定义授权范围
                /*
                 * public_profile    获得 openId、unionId、用户昵称、用户头像
                 * user_friends      获得访问 TapTap 好友相关数据的权限
                 * basic_info        获得 openId 和 unionId
                 * > 若游戏发起 TapTap 授权登录时只请求 basic_info 的权限，则用户可享受无感登录的特性，即用户不需要手动确认授权即可自动授权完成登录。
                 * */
                String infoScope = useProfile ? Scopes.SCOPE_PUBLIC_PROFILE : Scopes.SCOPE_BASIC_INFO;

                String[] scopes = useFriends ? new String[]{infoScope, Scopes.SCOPE_USER_FRIENDS} : new String[]{infoScope};
                Log.d("TapSDKBridge", "TapTapLogin.loginWithScopes");
                TapTapLogin.loginWithScopes(Objects.requireNonNull(getActivity()), scopes, new TapTapCallback<>() {
                    @Override
                    public void onSuccess(TapTapAccount tapTapAccount) {
                        // 登录成功
                        emitSignal("onLoginSuccess");
                    }

                    @Override
                    public void onFail(@NonNull TapTapException exception) {
                        // 登录失败
                        emitSignal("onLoginFail", exception.getMessage());
                    }

                    @Override
                    public void onCancel() {
                        // 登录取消
                        emitSignal("onLoginCancel");
                    }
                });
            } catch (Exception e) {
                Log.e("TapSDKBridge", "Failed to start login", e);
                emitSignal("onLoginFail", "登录启动失败: " + e.getMessage());
            }
        });
    }

    /**
     * 获取当前登录用户的资料信息
     *
     * @return JSON字符串，包含用户信息：
     * - avatar: 用户头像URL
     * - email: 用户邮箱
     * - name: 用户昵称
     * - accessToken: 访问令牌
     * - openId: 用户唯一标识
     * - unionId: 用户联合标识
     * 如果用户未登录则返回 null
     */
    @UsedByGodot
    public String getUserProfile() {
        Log.d("TapSDKBridge", "TapTapLogin.getCurrentTapAccount");
        TapTapAccount account = TapTapLogin.getCurrentTapAccount();
        if (account == null) {
            return null;
        }
        JSONObject json = new JSONObject();
        try {
            json.put("avatar", account.getAvatar());
            json.put("email", account.getEmail());
            json.put("name", account.getName());
            json.put("accessToken", account.getAccessToken());
            json.put("openId", account.getOpenId());
            json.put("unionId", account.getUnionId());
        } catch (org.json.JSONException e) {
            e.fillInStackTrace();

            return null;
        }
        return json.toString();
    }

    /**
     * 检查用户是否已登录
     *
     * @return true 如果用户已登录，false 如果用户未登录
     */
    @UsedByGodot
    public boolean isLogin() {
        Log.d("TapSDKBridge", "TapTapLogin.getCurrentTapAccount");
        return null != TapTapLogin.getCurrentTapAccount();
    }

    /**
     * 登出当前用户
     *
     * @see <a href="https://developer.taptap.cn/docs/sdk/taptap-login/guide/tap-login/#%E7%99%BB%E5%87%BA">TapTap登出文档</a>
     */
    @UsedByGodot
    public void logout() {
        Log.d("TapSDKBridge", "TapTapLogin.logout");
        TapTapLogin.logout();
    }

    // logout和Restart在android层连接，以免登出未完成就退出
    @UsedByGodot
    public void logoutThenRestart() {
        runOnMainThread(() -> {
            try {
                logout();
                Activity activity = getActivity();
                if (activity == null) {
                    Log.e("TapSDKBridge", "Cannot restart app: Activity is null");
                    return;
                }

                // 获取应用的启动 Intent
                Intent intent = activity.getPackageManager().getLaunchIntentForPackage(activity.getPackageName());
                if (intent == null) {
                    Log.e("TapSDKBridge", "Cannot restart app: Launch intent is null");
                    return;
                }

                // 设置 Intent 标志，清除任务栈并创建新任务
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                Log.d("TapSDKBridge", "Restarting application...");

                // 启动应用
                activity.startActivity(intent);

                // 终止当前进程
                activity.finish();
                System.exit(0);
            } catch (Exception e) {
                Log.e("TapSDKBridge", "Failed to restart app", e);
            }
        });
    }

    /**
     * 启动合规认证流程
     * <p>
     *
     * @see <a href="https://developer.taptap.cn/docs/sdk/compliance/features/">合规认证文档</a>
     * <p>
     * 触发信号：onComplianceResult，参数为合规状态码和详细信息
     */
    @UsedByGodot
    public void compliance() {
        // 确保在主线程执行，避免 Fragment 操作异常
        runOnMainThread(() -> {
            try {
                Log.d("TapSDKBridge", "TapTapLogin.getCurrentTapAccount");
                TapTapAccount currentTapAccount = TapTapLogin.getCurrentTapAccount();
                if (currentTapAccount != null) {
                    String unionId = currentTapAccount.getUnionId();
                    Log.d("TapSDKBridge", "TapTapCompliance.startup");
                    TapTapCompliance.startup(Objects.requireNonNull(getActivity()), unionId);
                } else {
                    Log.w("TapSDKBridge", "No current TapTap account for compliance check");
                    emitSignal("onComplianceResult", -1, "未登录");
                }
            } catch (Exception e) {
                Log.e("TapSDKBridge", "Failed to start compliance check", e);
                emitSignal("onComplianceResult", -1, "合规检查启动失败: " + e.getMessage());
            }
        });
    }

    /**
     * 检查游戏许可证
     * <p>
     *
     * @param forceCheck 是否强制检查，true为强制检查，false为使用缓存
     * @see <a href="https://developer.taptap.cn/docs/sdk/copyright-verification/guide/">版权验证文档</a>
     * <p>
     */
    @UsedByGodot
    public void checkLicense(boolean forceCheck) {
        runOnMainThread(() -> {
            Log.d("TapSDKBridge", "TapTapLicense.checkLicense forceCheck=" + forceCheck);
            TapTapLicense.checkLicense(Objects.requireNonNull(getActivity()), forceCheck);
        });
    }

    // ==================== DLC 相关方法 ====================

    /**
     * 查询DLC购买状态
     * <p>
     * 通过商品 skuId 数组批量查询多个商品购买状态，结果通过查询回调返回。
     *
     * @param skuIds 要查询的DLC商品ID数组
     *               <p>
     *               触发信号：onDLCQueryResult
     *               返回JSON格式：
     *               {
     *               "code": "查询结果代码",
     *               "codeName": "查询结果名称",
     *               "queryList": {
     *               "sku_id": 0或1, // 0表示未购买，1表示已购买
     *               ...
     *               }
     *               }
     * @see <a href="https://developer.taptap.cn/docs/sdk/copyright-verification/guide/#%E6%9F%A5%E8%AF%A2%E8%B4%AD%E4%B9%B0%E7%8A%B6%E6%80%81">DLC查询文档</a>
     */
    @UsedByGodot
    public void queryDLC(String[] skuIds) {
        runOnMainThread(() -> {
//        Log.d("TapSDKBridge", "TapTapLicense.queryDLC skuIds=" + java.util.Arrays.toString(skuIds));
            TapTapLicense.queryDLC(Objects.requireNonNull(getActivity()), List.of(skuIds));
        });
    }

    /**
     * 购买DLC商品
     * <p>
     * 通过商品 skuId 发起购买，结果通过购买回调返回。
     *
     * @param skuId 要购买的DLC商品ID
     *              <p>
     *              触发信号：onDLCPurchaseResult
     *              参数：skuId (String), status (String)
     *              <p>
     *              购买状态说明：
     *              - DLC_NOT_PURCHASED: 未完成支付
     *              - DLC_PURCHASED: 支付成功
     *              - DLC_RETURN_ERROR: 支付异常
     * @see <a href="https://developer.taptap.cn/docs/sdk/copyright-verification/guide/#%E8%B4%AD%E4%B9%B0%E5%95%86%E5%93%81">DLC购买文档</a>
     */
    @UsedByGodot
    public void purchaseDLC(String skuId) {
        Log.d("TapSDKBridge", "TapTapLicense.purchaseDLC skuId=" + skuId);

        // 确保在主线程执行，避免 Fragment 操作异常
        runOnMainThread(() -> {
            try {
                TapTapLicense.purchaseDLC(Objects.requireNonNull(getActivity()), skuId);
            } catch (Exception e) {
                Log.e("TapSDKBridge", "Failed to purchase DLC: " + skuId, e);
                emitSignal("onDLCPurchaseResult", skuId, "DLC_RETURN_ERROR");
            }
        });
    }

    // ==================== IAP 内购相关方法 ====================

    /**
     * 查询应用内商品详情
     * <p>
     *
     * @param products 要查询的商品ID数组，这些ID需要在TapTap开发者中心配置
     *                 <p>
     *                 触发信号：onProductDetailsResponse
     *                 返回JSON格式：
     *                 {
     *                 "responseCode": "响应码",
     *                 "debugMessage": "调试信息(可选)",
     *                 "productDetails": {
     *                 "product_id": {
     *                 "productType": "商品类型",
     *                 "productId": "商品ID",
     *                 "name": "商品名称",
     *                 "description": "商品描述",
     *                 "regionId": "区域ID",
     *                 "icon": "商品图标URL",
     *                 "oneTimePurchaseOfferDetails": {}
     *                 }
     *                 },
     *                 "unavailableProductIds": ["不可用的商品ID列表"]
     *                 }
     * @see <a href="https://developer.taptap.cn/docs/sdk/tap-iap/develop/android/#%E5%B1%95%E7%A4%BA%E5%8F%AF%E4%BE%9B%E8%B4%AD%E4%B9%B0%E7%9A%84%E5%95%86%E5%93%81">商品查询文档</a>
     * <p>
     */
    @UsedByGodot
    public void queryProductDetailsAsync(String[] products) {
        List<Product> queryProductList = new ArrayList<>();
        // 支持批量查询 Product, 设置好对应的 ProductID、ProductType
        // ProductType 目前仅支持 ProductType.INAPP
        for (String productId : products) {

            Product product = Product.newBuilder()
                    .setProductId(productId)
                    .setProductType(ITapIAP.ProductType.INAPP)
                    .build();
            queryProductList.add(product);
        }
        QueryProductDetailsParams params = QueryProductDetailsParams.newBuilder()
                .setProductList(queryProductList).build();
        tapTapIAP.queryProductDetailsAsync(params, new ProductDetailsResponseListener() {
            @Override
            public void onProductDetailsResponse(@NonNull TapPaymentResult result,
                                                 List<ProductDetails> productDetails, List<String> unavailableProductIds) {
                JSONObject json = new JSONObject();
                try {
                    // TapPaymentResult 信息
                    json.put("responseCode", result.getResponseCode());
                    String debugMessage = result.getDebugMessage();
                    if (debugMessage != null) {
                        json.put("debugMessage", debugMessage);
                    }

                    // ProductDetails 字典，以 productId 为 key
                    JSONObject productDetailsDict = getProductDetailsDict(productDetails);
                    json.put("productDetails", productDetailsDict);

                    // 不可用的产品ID列表
                    JSONArray unavailableArray = new JSONArray();
                    if (unavailableProductIds != null) {
                        for (String unavailableId : unavailableProductIds) {
                            unavailableArray.put(unavailableId);
                        }
                    }
                    json.put("unavailableProductIds", unavailableArray);

                } catch (JSONException e) {
                    e.fillInStackTrace();
                    // 发生错误时也要发送信号，包含错误信息
                    try {
                        json.put("error", "JSON serialization failed: " + e.getMessage());
                    } catch (JSONException jsonException) {
                        jsonException.fillInStackTrace();
                    }
                } finally {
                    emitSignal("onProductDetailsResponse", json.toString());
                }
            }

            @NonNull
            private JSONObject getProductDetailsDict(List<ProductDetails> productDetails) throws JSONException {
                JSONObject productDetailsDict = new JSONObject();
                if (productDetails != null) {
                    for (ProductDetails detail : productDetails) {
                        // 缓存 ProductDetails 供购买时使用
                        productDetailsCache.put(detail.getProductId(), detail);

                        JSONObject detailJson = new JSONObject();
                        detailJson.put("productType", detail.getProductType());
                        detailJson.put("productId", detail.getProductId());
                        detailJson.put("name", detail.getName());
                        detailJson.put("description", detail.getDescription());
                        detailJson.put("regionId", detail.getRegionId());
                        detailJson.put("icon", detail.getIcon());

                        // OneTimePurchaseOfferDetails
                        if (detail.getOneTimePurchaseOfferDetails() != null) {
                            JSONObject offerJson = new JSONObject();
                            // 根据 OneTimePurchaseOfferDetails 的实际字段添加
                            detailJson.put("oneTimePurchaseOfferDetails", offerJson);
                        }

                        // 使用 productId 作为 key
                        productDetailsDict.put(detail.getProductId(), detailJson);
                    }
                }
                return productDetailsDict;
            }
        });
    }

    /**
     * 启动购买流程
     *
     * @param productId           要购买的商品ID，必须先通过 queryProductDetailsAsync 查询过
     * @param obfuscatedAccountId 混淆账户ID，建议使用游戏内的订单ID或用户ID等唯一标识
     *                            <p>
     *                            触发信号：
     *                            - onLaunchBillingFlowResult: 启动购买流程的结果
     *                            - onPurchaseUpdated: 购买状态更新（在购买过程中会多次触发）
     *                            <p>
     *                            购买状态更新返回JSON格式：
     *                            {
     *                            "responseCode": "响应码",
     *                            "debugMessage": "调试信息(可选)",
     *                            "purchase": {
     *                            "orderId": "订单ID",
     *                            "purchaseToken": "购买令牌",
     *                            "orderToken": "订单令牌",
     *                            "productId": "商品ID",
     *                            "purchaseState": "购买状态",
     *                            "purchaseTime": "购买时间",
     *                            "obfuscatedAccountId": "混淆账户ID",
     *                            "quantity": "数量",
     *                            "acknowledged": "是否已确认"
     *                            }
     *                            }
     * @see <a href="https://developer.taptap.cn/docs/sdk/tap-iap/develop/android/#%E5%90%AF%E5%8A%A8%E8%B4%AD%E4%B9%B0%E6%B5%81%E7%A8%8B">启动购买流程文档</a>
     */
    @UsedByGodot
    public void launchBillingFlow(String productId, String obfuscatedAccountId) {
        // 确保在主线程执行，避免 Fragment 操作异常
        runOnMainThread(() -> {
            if (tapTapIAP == null) {
                showTip("TapTapIAP not initialized");
                return;
            }

            try {
                ProductDetails productDetails = productDetailsCache.get(productId);
                if (productDetails == null) {
                    JSONObject resultJson = new JSONObject();
                    resultJson.put("responseCode", "DEVELOPER_ERROR");
                    resultJson.put("error", "Product details not found. Please call queryProductDetailsAsync first.");
                    emitSignal("onLaunchBillingFlowResult", resultJson.toString());
                    return;
                }

                BillingFlowParams.ProductDetailsParams productDetailsParams = BillingFlowParams.ProductDetailsParams.newBuilder()
                        .setProductDetails(productDetails)
                        .build();

                BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                        .setProductDetailsParams(productDetailsParams)
                        .setObfuscatedAccountId(obfuscatedAccountId)
                        .build();

                TapPaymentResult result = tapTapIAP.launchBillingFlow(Objects.requireNonNull(getActivity()),
                        billingFlowParams, (result1, purchase) -> {
                            // 处理购买状态更新并发送信号
                            try {
                                JSONObject json = createPaymentResultJson(result1);

                                if (purchase != null) {
                                    json.put("purchase", createPurchaseJson(purchase));
                                }

                                emitSignal("onPurchaseUpdated", json.toString());
                            } catch (JSONException e) {
                                e.fillInStackTrace();
                                safeEmitSignal("onPurchaseUpdated", new JSONObject(), "Failed to process purchase update");
                            }
                        });

                // 发送启动结果
                JSONObject resultJson = createPaymentResultJson(result);
                emitSignal("onLaunchBillingFlowResult", resultJson.toString());

            } catch (JSONException e) {
                e.fillInStackTrace();
                safeEmitSignal("onLaunchBillingFlowResult", new JSONObject(), "Failed to launch billing flow");
            }
        });
    }

    /**
     * 完成订单，确认商品已发放
     * <p>
     *
     * @param orderId       订单ID，从购买回调中获取
     * @param purchaseToken 购买令牌，从购买回调中获取
     *                      <p>
     *                      触发信号：onFinishPurchaseResponse
     *                      返回JSON格式包含响应码和购买信息
     * @see <a href="https://developer.taptap.cn/docs/sdk/tap-iap/develop/android/#%E8%BF%9B%E8%A1%8C%E5%95%86%E5%93%81%E7%9A%84%E5%8F%91%E6%94%BE%EF%BC%8C%E4%B8%94%E5%AE%8C%E6%88%90%E8%AE%A2%E5%8D%95">完成订单文档</a>
     * <p>
     * 重要：确认发放商品非常重要，如果您没有调用此方法来完成订单，
     * 用户将无法再次购买该商品，且该订单将会在3天后自动退款。
     * <p>
     */
    @UsedByGodot
    public void finishPurchaseAsync(String orderId, String purchaseToken) {
        if (tapTapIAP == null) {
            showTip("TapTapIAP not initialized");
            return;
        }

        FinishPurchaseParams params = FinishPurchaseParams.newBuilder()
                .setId(orderId)
                .setPurchaseToken(purchaseToken)
                .build();

        tapTapIAP.finishPurchaseAsync(params, (result, purchase) -> {
            try {
                JSONObject json = createPaymentResultJson(result);

                if (purchase != null) {
                    json.put("purchase", createPurchaseJson(purchase));
                }

                emitSignal("onFinishPurchaseResponse", json.toString());
            } catch (JSONException e) {
                e.fillInStackTrace();
                safeEmitSignal("onFinishPurchaseResponse", new JSONObject(), "Failed to process finish purchase response");
            }
        });
    }

    /**
     * 查询未完成的订单列表
     *
     * @see <a href="https://developer.taptap.cn/docs/sdk/tap-iap/develop/android/#%E8%8E%B7%E5%8F%96%E6%9C%AA%E5%AE%8C%E6%88%90%E7%9A%84%E8%AE%A2%E5%8D%95%E5%88%97%E8%A1%A8">获取未完成订单列表文档</a>
     * <p>
     * 使用场景：
     * - 在购买过程中出现网络问题，用户成功购买但应用未收到通知
     * - 多设备间同步购买状态
     * - 应用异常崩溃后恢复购买状态
     * <p>
     * 建议在应用的 onResume() 中调用此方法，确保所有购买交易都得到正确处理。
     * <p>
     * 触发信号：onQueryUnfinishedPurchaseResponse
     * 返回JSON格式：
     * {
     * "responseCode": "响应码",
     * "debugMessage": "调试信息(可选)",
     * "purchases": [
     * {
     * "orderId": "订单ID",
     * "purchaseToken": "购买令牌",
     * // ... 其他Purchase字段
     * }
     * ]
     * }
     */
    @UsedByGodot
    public void queryUnfinishedPurchaseAsync() {
        if (tapTapIAP == null) {
            showTip("TapTapIAP not initialized");
            return;
        }

        tapTapIAP.queryUnfinishedPurchaseAsync((result, purchases) -> {
            try {
                JSONObject json = createPaymentResultJson(result);

                if (purchases != null) {
                    JSONArray purchasesArray = new JSONArray();
                    for (Purchase purchase : purchases) {
                        purchasesArray.put(createPurchaseJson(purchase));
                    }
                    json.put("purchases", purchasesArray);
                }

                emitSignal("onQueryUnfinishedPurchaseResponse", json.toString());
            } catch (JSONException e) {
                e.fillInStackTrace();
                safeEmitSignal("onQueryUnfinishedPurchaseResponse", new JSONObject(), "Failed to process unfinished purchase query");
            }
        });
    }

    // ==================== 工具方法 ====================

    /**
     * 显示Toast提示信息
     *
     * @param text 要显示的文本内容
     */
    @UsedByGodot
    public void showTip(String text) {
        runOnUiThread(() -> Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show());
    }

    /**
     * 发送信号到 Godot（供回调类使用）
     * 这是对 protected emitSignal 的公共包装
     */
    public void emitGodotSignal(String signalName, Object... args) {
        emitSignal(signalName, args);
    }

    // ==================== 云存档相关方法 ====================
    // 回调类已移至 com.sygames.godot3taptap.cloudsave 包

    /**
     * 从 Godot Dictionary 构建 ArchiveMetadata
     * 
     * @param metadata Dictionary 包含以下字段：
     *                 - name: 存档名称（String）
     *                 - summary: 存档摘要/描述（String，可选）
     *                 - extra: 额外信息（String，可选）
     *                 - playtime: 游戏时长/秒（int，可选，默认0）
     * @return ArchiveMetadata 对象
     */
    private ArchiveMetadata buildArchiveMetadata(org.godotengine.godot.Dictionary metadata) {
        ArchiveMetadata.Builder builder = new ArchiveMetadata.Builder();
        
        // name 是必需字段
        if (metadata.containsKey("name")) {
            builder.setName((String) metadata.get("name"));
        } else {
            throw new IllegalArgumentException("Archive metadata must contain 'name' field");
        }
        
        // 可选字段
        if (metadata.containsKey("summary")) {
            builder.setSummary((String) metadata.get("summary"));
        }
        
        if (metadata.containsKey("extra")) {
            builder.setExtra((String) metadata.get("extra"));
        }
        
        if (metadata.containsKey("playtime")) {
            Object playtime = metadata.get("playtime");
            if (playtime instanceof Integer) {
                builder.setPlaytime((Integer) playtime);
            }
        }
        
        return builder.build();
    }

    /**
     * 创建游戏存档并上传云端
     * <p>
     * 注意：
     * - 存档名称仅支持【英文/数字/下划线/中划线】，不支持中文
     * - 存档摘要 (summary) 以及额外信息 (extra) 无此限制
     * - 一分钟内仅可调用一次（与更新存档共享冷却时间）
     * - 单个存档文件大小不超过10MB
     * - 封面大小不超过512KB
     * <p>
     *
     * @param metadata         存档元数据，Dictionary 包含字段：
     *                         - name: 存档名称（必填）
     *                         - summary: 存档摘要/描述（可选）
     *                         - extra: 额外信息（可选）
     *                         - playtime: 游戏时长/秒（可选，默认0）
     * @param archiveFilePath  存档文件路径
     * @param archiveCoverPath 存档封面路径（可选，可为null）
     *                         <p>
     *                         触发信号：
     *                         - onCreateArchiveSuccess: 创建成功，参数为存档信息的JSON字符串
     *                         - onCreateArchiveFailed: 创建失败，参数为错误信息的JSON字符串
     * @see <a href="https://developer.taptap.cn/docs/sdk/tap-cloudsave/guide/#创建存档">创建存档文档</a>
     */
    @UsedByGodot
    public void createArchive(org.godotengine.godot.Dictionary metadata, String archiveFilePath, 
                              @Nullable String archiveCoverPath) {
        runOnMainThread(() -> {
            String tempZipPath = null;
            try {
                // 检查路径是否为目录，如果是则先压缩
                File archiveFile = new File(archiveFilePath);
                String actualFilePath = archiveFilePath;

                if (archiveFile.isDirectory()) {
                    Log.d("TapSDKBridge", "Archive path is a directory, creating zip file");
                    tempZipPath = archiveFilePath + ".cloudsave.zip";
                    if (zipDirectory(archiveFilePath, tempZipPath)) {
                        actualFilePath = tempZipPath;
                    } else {
                        emitSignal("onCreateArchiveFailed", "{\"error\":\"Failed to compress directory\"}");
                        return;
                    }
                }

                final String finalFilePath = actualFilePath;
                final String finalTempZipPath = tempZipPath;

                // 从 Dictionary 构建元数据
                ArchiveMetadata archiveMetadata = buildArchiveMetadata(metadata);

                CreateArchiveCallback callback = new CreateArchiveCallback(this, finalTempZipPath);
                TapTapCloudSave.createArchive(archiveMetadata, finalFilePath, archiveCoverPath, callback);
            } catch (Exception e) {
                Log.e("TapSDKBridge", "Failed to create archive", e);
                emitSignal("onCreateArchiveFailed", "{\"error\":\"" + e.getMessage() + "\"}");
                // 清理临时ZIP文件
                if (tempZipPath != null) {
                    File tempFile = new File(tempZipPath);
                    if (tempFile.exists() && !tempFile.delete()) {
                        Log.w("TapSDKBridge", "Failed to delete temporary zip file: " + tempZipPath);
                    }
                }
            }
        });
    }

    /**
     * 获取当前用户的存档列表
     * <p>
     * 触发信号：
     * - onGetArchiveListSuccess: 获取成功，参数为存档列表的JSON字符串
     * - onGetArchiveListFailed: 获取失败，参数为错误信息的JSON字符串
     *
     * @see <a href="https://developer.taptap.cn/docs/sdk/tap-cloudsave/guide/#获取存档列表">获取存档列表文档</a>
     */
    @UsedByGodot
    public void getArchiveList() {
        runOnMainThread(() -> {
            try {
                GetArchiveListCallback callback = new GetArchiveListCallback(this);
                TapTapCloudSave.getArchiveList(callback);
            } catch (Exception e) {
                Log.e("TapSDKBridge", "Failed to get archive list", e);
                emitSignal("onGetArchiveListFailed", "{\"error\":\"" + e.getMessage() + "\"}");
            }
        });
    }

    /**
     * 下载存档并解压覆盖本地存档
     * <p>
     * 此方法会下载云端存档，自动解压并覆盖本地指定路径的存档文件或目录
     *
     * @param archiveUuid      存档UUID
     * @param archiveFileId    存档文件ID
     * @param localArchivePath 本地存档路径（文件或目录）
     *                         <p>
     *                         触发信号：
     *                         - onDownloadArchiveDataSuccess: 下载并解压成功，参数为包含路径和大小的JSON字符串
     *                         - onDownloadArchiveDataFailed: 下载或解压失败，参数为错误信息的JSON字符串
     * @see <a href="https://developer.taptap.cn/docs/sdk/tap-cloudsave/guide/#下载存档">下载存档文档</a>
     */
    @UsedByGodot
    public void downloadArchiveData(String archiveUuid, String archiveFileId, String localArchivePath) {
        runOnMainThread(() -> {
            try {
                DownloadArchiveDataCallback callback = new DownloadArchiveDataCallback(this, localArchivePath);
                TapTapCloudSave.getArchiveData(archiveUuid, archiveFileId, callback);
            } catch (Exception e) {
                Log.e("TapSDKBridge", "Failed to download archive data", e);
                emitSignal("onDownloadArchiveDataFailed", "{\"error\":\"" + e.getMessage() + "\"}");
            }
        });
    }

    /**
     * 更新指定的存档文件
     * <p>
     * 注意：
     * - 存档名称仅支持【英文/数字/下划线/中划线】，不支持中文
     * - 存档摘要 (summary) 以及额外信息 (extra) 无此限制
     * - 一分钟内仅可调用一次（与创建存档共享冷却时间）
     * - 单个存档文件大小不超过10MB
     * - 封面大小不超过512KB
     * <p>
     *
     * @param archiveUuid      存档UUID
     * @param metadata         存档元数据，Dictionary 包含字段：
     *                         - name: 存档名称（必填）
     *                         - summary: 存档摘要/描述（可选）
     *                         - extra: 额外信息（可选）
     *                         - playtime: 游戏时长/秒（可选，默认0）
     * @param archiveFilePath  新的存档文件路径
     * @param archiveCoverPath 新的存档封面路径（可选，可为null）
     *                         <p>
     *                         触发信号：
     *                         - onUpdateArchiveSuccess: 更新成功，参数为存档信息的JSON字符串
     *                         - onUpdateArchiveFailed: 更新失败，参数为错误信息的JSON字符串
     * @see <a href="https://developer.taptap.cn/docs/sdk/tap-cloudsave/guide/#更新存档">更新存档文档</a>
     */
    @UsedByGodot
    public void updateArchive(String archiveUuid, org.godotengine.godot.Dictionary metadata,
                              String archiveFilePath, @Nullable String archiveCoverPath) {
        runOnMainThread(() -> {
            String tempZipPath = null;
            try {
                // 检查路径是否为目录，如果是则先压缩
                File archiveFile = new File(archiveFilePath);
                String actualFilePath = archiveFilePath;

                if (archiveFile.isDirectory()) {
                    Log.d("TapSDKBridge", "Archive path is a directory, creating zip file");
                    tempZipPath = archiveFilePath + ".cloudsave.zip";
                    if (zipDirectory(archiveFilePath, tempZipPath)) {
                        actualFilePath = tempZipPath;
                    } else {
                        emitSignal("onUpdateArchiveFailed", "{\"error\":\"Failed to compress directory\"}");
                        return;
                    }
                }

                final String finalFilePath = actualFilePath;
                final String finalTempZipPath = tempZipPath;

                // 从 Dictionary 构建元数据
                ArchiveMetadata archiveMetadata = buildArchiveMetadata(metadata);

                UpdateArchiveCallback callback = new UpdateArchiveCallback(this, finalTempZipPath);
                TapTapCloudSave.updateArchive(archiveUuid, archiveMetadata, finalFilePath, archiveCoverPath, callback);
            } catch (Exception e) {
                Log.e("TapSDKBridge", "Failed to update archive", e);
                emitSignal("onUpdateArchiveFailed", "{\"error\":\"" + e.getMessage() + "\"}");
                // 清理临时ZIP文件
                if (tempZipPath != null) {
                    File tempFile = new File(tempZipPath);
                    if (tempFile.exists() && !tempFile.delete()) {
                        Log.w("TapSDKBridge", "Failed to delete temporary zip file: " + tempZipPath);
                    }
                }
            }
        });
    }

    /**
     * 删除指定的存档文件
     * <p>
     *
     * @param archiveUuid 存档UUID
     *                    <p>
     *                    触发信号：
     *                    - onDeleteArchiveSuccess: 删除成功，参数为存档信息的JSON字符串
     *                    - onDeleteArchiveFailed: 删除失败，参数为错误信息的JSON字符串
     * @see <a href="https://developer.taptap.cn/docs/sdk/tap-cloudsave/guide/#删除存档">删除存档文档</a>
     */
    @UsedByGodot
    public void deleteArchive(String archiveUuid) {
        runOnMainThread(() -> {
            try {
                DeleteArchiveCallback callback = new DeleteArchiveCallback(this);
                TapTapCloudSave.deleteArchive(archiveUuid, callback);
            } catch (Exception e) {
                Log.e("TapSDKBridge", "Failed to delete archive", e);
                emitSignal("onDeleteArchiveFailed", "{\"error\":\"" + e.getMessage() + "\"}");
            }
        });
    }

    /**
     * 获取指定存档的封面图片
     * <p>
     *
     * @param archiveUuid   存档UUID
     * @param archiveFileId 存档文件ID
     *                      <p>
     *                      触发信号：
     *                      - onGetArchiveCoverSuccess: 获取成功，参数为Base64编码的封面数据JSON字符串
     *                      - onGetArchiveCoverFailed: 获取失败，参数为错误信息的JSON字符串
     * @see <a href="https://developer.taptap.cn/docs/sdk/tap-cloudsave/guide/#获取存档封面">获取存档封面文档</a>
     */
    @UsedByGodot
    public void getArchiveCover(String archiveUuid, String archiveFileId) {
        runOnMainThread(() -> {
            try {
                GetArchiveCoverCallback callback = new GetArchiveCoverCallback(this);
                TapTapCloudSave.getArchiveCover(archiveUuid, archiveFileId, callback);
            } catch (Exception e) {
                Log.e("TapSDKBridge", "Failed to get archive cover", e);
                emitSignal("onGetArchiveCoverFailed", "{\"error\":\"" + e.getMessage() + "\"}");
            }
        });
    }

    /**
     * 创建ArchiveData对象的JSON表示
     * 统一ArchiveData对象的序列化格式
     *
     * @param archive ArchiveData对象
     * @return ArchiveData的JSON表示
     * @throws JSONException 如果JSON创建失败
     */
    public JSONObject createArchiveDataJson(ArchiveData archive) throws JSONException {
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

    // ==================== 云存档辅助方法 ====================

    /**
     * 递归删除文件或目录
     *
     * @param fileOrDirectory 要删除的文件或目录
     */
    public void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory()) {
            File[] files = fileOrDirectory.listFiles();
            if (files != null) {
                for (File child : files) {
                    deleteRecursive(child);
                }
            }
        }
        if (!fileOrDirectory.delete()) {
            Log.w("TapSDKBridge", "Failed to delete: " + fileOrDirectory.getAbsolutePath());
        }
    }

    /**
     * 压缩目录为ZIP文件
     *
     * @param sourceDirPath 要压缩的目录路径
     * @param zipFilePath   输出的ZIP文件路径
     * @return 压缩是否成功
     */
    private boolean zipDirectory(String sourceDirPath, String zipFilePath) {
        try {
            File sourceDir = new File(sourceDirPath);
            if (!sourceDir.exists() || !sourceDir.isDirectory()) {
                Log.e("TapSDKBridge", "Source directory does not exist or is not a directory: " + sourceDirPath);
                return false;
            }

            FileOutputStream fos = new FileOutputStream(zipFilePath);
            ZipOutputStream zos = new ZipOutputStream(fos);

            // 直接压缩目录内的文件和子目录，不包含源目录本身
            File[] children = sourceDir.listFiles();
            if (children != null) {
                for (File child : children) {
                    zipDirectoryRecursive(child, child.getName(), zos);
                }
            }

            zos.close();
            fos.close();

            Log.d("TapSDKBridge", "Successfully compressed directory: " + sourceDirPath + " to " + zipFilePath);
            return true;
        } catch (IOException e) {
            Log.e("TapSDKBridge", "Failed to compress directory", e);
            return false;
        }
    }

    /**
     * 递归压缩目录中的文件
     *
     * @param fileToZip 要压缩的文件或目录
     * @param fileName  文件在ZIP中的名称
     * @param zos       ZIP输出流
     * @throws IOException 如果IO操作失败
     */
    private void zipDirectoryRecursive(File fileToZip, String fileName, ZipOutputStream zos) throws IOException {
        if (fileToZip.isHidden()) {
            return;
        }

        if (fileToZip.isDirectory()) {
            if (fileName.endsWith("/")) {
                zos.putNextEntry(new ZipEntry(fileName));
                zos.closeEntry();
            } else {
                zos.putNextEntry(new ZipEntry(fileName + "/"));
                zos.closeEntry();
            }

            File[] children = fileToZip.listFiles();
            if (children != null) {
                for (File childFile : children) {
                    zipDirectoryRecursive(childFile, fileName + "/" + childFile.getName(), zos);
                }
            }
            return;
        }

        FileInputStream fis = new FileInputStream(fileToZip);
        ZipEntry zipEntry = new ZipEntry(fileName);
        zos.putNextEntry(zipEntry);

        byte[] buffer = new byte[8192];
        int length;
        while ((length = fis.read(buffer)) >= 0) {
            zos.write(buffer, 0, length);
        }

        fis.close();
    }

    /**
     * 从字节数组解压ZIP到指定目录
     *
     * @param zipData ZIP数据字节数组
     * @param destDirPath 目标目录路径
     * @return 解压是否成功
     */
    public boolean unzipFromBytes(byte[] zipData, String destDirPath) {
        try {
            File destDir = new File(destDirPath);
            if (!destDir.exists()) {
                destDir.mkdirs();
            }

            ByteArrayInputStream bais = new ByteArrayInputStream(zipData);
            ZipInputStream zis = new ZipInputStream(bais);
            ZipEntry zipEntry = zis.getNextEntry();

            while (zipEntry != null) {
                File newFile = newFileInDestDir(destDir, zipEntry);

                if (zipEntry.isDirectory()) {
                    if (!newFile.isDirectory() && !newFile.mkdirs()) {
                        throw new IOException("Failed to create directory " + newFile);
                    }
                } else {
                    // 确保父目录存在
                    File parent = newFile.getParentFile();
                    if (!Objects.requireNonNull(parent).isDirectory() && !parent.mkdirs()) {
                        throw new IOException("Failed to create directory " + parent);
                    }

                    // 写入文件内容
                    FileOutputStream fos = new FileOutputStream(newFile);
                    byte[] buffer = new byte[8192];
                    int length;
                    while ((length = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, length);
                    }
                    fos.close();
                }

                zipEntry = zis.getNextEntry();
            }

            zis.closeEntry();
            zis.close();
            bais.close();

            Log.d("TapSDKBridge", "Successfully unzipped from byte array to " + destDirPath);
            return true;
        } catch (IOException e) {
            Log.e("TapSDKBridge", "Failed to unzip from bytes", e);
            return false;
        }
    }

    /**
     * 解压ZIP文件到指定目录
     *
     * @param zipFilePath ZIP文件路径
     * @param destDirPath 目标目录路径
     * @return 解压是否成功
     */
    public boolean unzipFile(String zipFilePath, String destDirPath) {
        try {
            File destDir = new File(destDirPath);
            if (!destDir.exists()) {
                destDir.mkdirs();
            }

            FileInputStream fis = new FileInputStream(zipFilePath);
            ZipInputStream zis = new ZipInputStream(fis);
            ZipEntry zipEntry = zis.getNextEntry();

            while (zipEntry != null) {
                File newFile = newFileInDestDir(destDir, zipEntry);

                if (zipEntry.isDirectory()) {
                    if (!newFile.isDirectory() && !newFile.mkdirs()) {
                        throw new IOException("Failed to create directory " + newFile);
                    }
                } else {
                    // 确保父目录存在
                    File parent = newFile.getParentFile();
                    if (!Objects.requireNonNull(parent).isDirectory() && !parent.mkdirs()) {
                        throw new IOException("Failed to create directory " + parent);
                    }

                    // 写入文件内容
                    FileOutputStream fos = new FileOutputStream(newFile);
                    byte[] buffer = new byte[8192];
                    int length;
                    while ((length = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, length);
                    }
                    fos.close();
                }

                zipEntry = zis.getNextEntry();
            }

            zis.closeEntry();
            zis.close();
            fis.close();

            Log.d("TapSDKBridge", "Successfully unzipped file: " + zipFilePath + " to " + destDirPath);
            return true;
        } catch (IOException e) {
            Log.e("TapSDKBridge", "Failed to unzip file", e);
            return false;
        }
    }

    /**
     * 安全创建ZIP解压目标文件，防止路径遍历攻击
     *
     * @param destinationDir 目标目录
     * @param zipEntry       ZIP条目
     * @return 目标文件
     * @throws IOException 如果路径不安全
     */
    private File newFileInDestDir(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());

        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();

        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }

        return destFile;
    }

    /**
     * 重启应用
     * <p>
     * 完全重启当前应用，适用于需要重新加载配置或重置应用状态的场景。
     * 此方法会关闭当前应用并重新启动主 Activity。
     * <p>
     * 使用场景：
     * - 切换账号后需要重新初始化
     * - 更新关键配置后需要重启
     * - 修复某些状态异常
     * <p>
     * 注意：此方法会立即终止当前进程，请确保在调用前已保存必要的数据。
     */
    @UsedByGodot
    public void restartApp() {
        runOnMainThread(() -> {
            try {
                Activity activity = getActivity();
                if (activity == null) {
                    Log.e("TapSDKBridge", "Cannot restart app: Activity is null");
                    return;
                }

                // 获取应用的启动 Intent
                Intent intent = activity.getPackageManager().getLaunchIntentForPackage(activity.getPackageName());
                if (intent == null) {
                    Log.e("TapSDKBridge", "Cannot restart app: Launch intent is null");
                    return;
                }

                // 设置 Intent 标志，清除任务栈并创建新任务
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                Log.d("TapSDKBridge", "Restarting application...");

                // 启动应用
                activity.startActivity(intent);

                // 终止当前进程
                activity.finish();
                System.exit(0);
            } catch (Exception e) {
                Log.e("TapSDKBridge", "Failed to restart app", e);
            }
        });
    }
}
