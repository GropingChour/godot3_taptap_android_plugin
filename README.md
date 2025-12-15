# Godot3 TapTap Android Plugin

一个功能完整的 TapTap SDK Android 插件，专为 Godot 3.x 游戏引擎开发，集成了 TapTap 登录、内购(IAP)、合规认证、正版验证等核心功能。

## ✨ 主要特性

- 🔐 **TapTap 登录系统** - 支持无感登录和授权登录
- 💰 **内购系统 (IAP)** - 完整的商品查询、购买、订单管理功能
- ⚖️ **合规认证** - 防沉迷、实名认证等合规功能
- 🛡️ **正版验证** - DLC 管理和版权验证
- 🔒 **Token 加密** - 可配置的加密系统保护敏感信息
- 🚀 **CI/CD 自动化** - GitHub Actions 自动构建和发布

## 📋 目录

- [快速开始](#快速开始)
- [编译方式](#编译方式)
- [使用方式](#使用方式)
- [API 文档](#api-文档)
- [CI/CD 配置](#cicd-配置)
- [常见问题](#常见问题)

## 🚀 快速开始

### 前置要求

- Godot 3.x (推荐 3.5+)
- JDK 11 或更高版本
- Gradle 7.0+
- Android SDK (API Level 21+)
- TapTap 开发者账号和应用配置

### 下载插件

#### 方式一：从 Release 下载（推荐）

1. 访问 [Releases](../../releases) 页面
2. 下载最新版本的插件包
3. 解压到你的 Godot 项目目录

#### 方式二：手动编译

参见 [编译方式](#编译方式) 章节

## 🔨 编译方式

### 本地编译

1. **克隆仓库**
   ```bash
   git clone https://github.com/your-username/godot3_taptap_android_plugin.git
   cd godot3_taptap_android_plugin
   ```

2. **编译 Android 插件**
   ```bash
   cd android
   ./gradlew :Godot3TapTap:assembleRelease
   ```

3. **部署插件文件**
   ```bash
   ./gradlew deployAar
   ```
   
   这个命令会：
   - 编译生成 AAR 文件
   - 自动复制 `Godot3TapTap.aar` 到 `release/android/plugins/`
   - 自动复制 `Godot3TapTap.gdap` 到 `release/android/plugins/`
   - 自动复制 `addons/` 文件夹到 `release/addons/`

4. **编译产物位置**
   ```
   release/
   ├── android/
   │   └── plugins/
   │       ├── Godot3TapTap.aar        # Android 插件库
   │       └── Godot3TapTap.gdap       # Godot 插件配置
   └── addons/
       └── godot3-taptap/              # GDScript 插件文件
   ```

### Windows 编译说明

在 Windows 上使用 PowerShell 或 CMD：

```powershell
cd android
.\gradlew.bat :Godot3TapTap:assembleRelease
.\gradlew.bat deployAar
```

## 📦 使用方式

### 1. 安装插件到 Godot 项目

将编译产物复制到你的 Godot 项目：

```
your-godot-project/
├── android/
│   └── plugins/
│       ├── Godot3TapTap.aar
│       └── Godot3TapTap.gdap
└── addons/
    └── godot3-taptap/
        ├── plugin.cfg
        ├── plugin.gd
        ├── taptap.gd
        ├── taptap_config_window.gd
        ├── generate_secure_key.gd
        └── example_usage.gd
```

### 2. 启用插件

1. 打开 Godot 编辑器
2. 进入 `项目 → 项目设置 → 插件`
3. 启用 `Godot3 TapTap` 插件

### 3. 配置 Android 导出

1. 进入 `项目 → 导出`
2. 选择 Android 预设（如果没有则新建）
3. 在 `Custom Build` 下启用 `Use Custom Build`
4. 在 `Plugins` 部分确认 `Godot3TapTap` 已勾选

### 4. 配置 Token 加密（推荐）

为了安全地使用 TapTap SDK，建议使用加密的 Client Token：

#### 使用图形界面工具

1. 在 Godot 编辑器菜单栏：`项目 → 工具 → TapTap RSA 密钥配置`
2. 点击 **"🎲 生成随机密钥"**
3. 点击 **"💾 保存密钥"**（密钥会自动保存到 Android 资源文件）
4. 输入你的 TapTap Client Token
5. 点击 **"🔐 生成加密 Token"**
6. 复制生成的加密 Token 和 Java 解密代码

#### 更新 Java 解密代码

将工具生成的 Java 代码复制到：
```
android/Godot3TapTap/src/main/java/com/sygames/godot3taptap/Godot3TapTap.java
```

找到 `getDecryptKey()` 方法并更新密钥。

### 5. 在游戏中使用

创建一个 GDScript 场景脚本：

```gdscript
extends Node

# TapTap 配置
const CLIENT_ID = "your_client_id_here"
const ENCRYPTED_TOKEN = "your_encrypted_token_here"  # 从配置工具获取

func _ready():
    # 连接信号
    TapTap.connect("onLoginSuccess", self, "_on_login_success")
    TapTap.connect("onLoginFail", self, "_on_login_fail")
    TapTap.connect("onComplianceResult", self, "_on_compliance_result")
    
    # 初始化 SDK（使用加密 Token，推荐）
    TapTap.initSdkWithEncryptedToken(CLIENT_ID, ENCRYPTED_TOKEN, true, false)
    
    # 或使用明文 Token（不推荐生产环境）
    # TapTap.initSdk(CLIENT_ID, "your_client_token", true, false)

func start_login():
    # 启动 TapTap 登录
    # useProfile=true: 获取用户昵称和头像
    # useFriends=false: 不获取好友列表
    TapTap.login(true, false)

func _on_login_success():
    print("登录成功！")
    # 获取用户信息
    yield(TapTap.loadUserInfo(), "completed")
    var profile = TapTap.getUserProfile()
    print("用户名: ", TapTap.userName)
    
    # 启动合规认证
    TapTap.compliance()

func _on_login_fail(message: String):
    print("登录失败: ", message)

func _on_compliance_result(code: int, info: String):
    match code:
        TapTap.ComplianceMessage.LOGIN_SUCCESS:
            print("合规检查通过，开始游戏")
        TapTap.ComplianceMessage.PERIOD_RESTRICT:
            print("当前时间段无法游戏")
        TapTap.ComplianceMessage.DURATION_LIMIT:
            print("游戏时长已达上限")
```

## 📚 API 文档

### 核心方法

#### SDK 初始化

```gdscript
# 使用加密 Token（推荐）
TapTap.initSdkWithEncryptedToken(clientId: String, encryptedToken: String, enableLog: bool, withIAP: bool)

# 使用明文 Token
TapTap.initSdk(clientId: String, clientToken: String, enableLog: bool, withIAP: bool)
```

**参数说明：**
- `clientId`: TapTap 应用的 Client ID
- `clientToken` / `encryptedToken`: Client Token（明文或加密）
- `enableLog`: 是否启用调试日志
- `withIAP`: 是否启用内购功能

#### 登录功能

```gdscript
# 启动登录
TapTap.login(useProfile: bool, useFriends: bool)

# 获取用户信息
yield(TapTap.loadUserInfo(), "completed")
var profile = TapTap.getUserProfile()

# 登出
TapTap.logout()

# 获取当前用户
var user = TapTap.getCurrentUser()
```

#### 合规认证

```gdscript
# 启动合规认证
TapTap.compliance()

# 退出合规认证
TapTap.exitCompliance()
```

#### 正版验证

```gdscript
# 正版验证
TapTap.checkLicense()

# 查询 DLC
TapTap.queryDLC(dlcIds: Array)

# 购买 DLC
TapTap.purchaseDLC(dlcId: String)
```

#### 内购功能

```gdscript
# 查询商品详情
TapTap.queryProductDetails(productIds: Array)

# 启动购买流程
TapTap.launchBillingFlow(productId: String, serverId: String, roleId: String, 
                         extraParams: Dictionary)

# 完成订单
TapTap.finishPurchase(purchaseToken: String)

# 查询未完成订单
TapTap.queryUnfinishedPurchases()
```

### 信号列表

```gdscript
# 登录相关
signal onLoginSuccess()
signal onLoginFail(message: String)
signal onLoginCancel()

# 合规认证
signal onComplianceResult(code: int, info: String)

# 正版验证
signal onLicenseSuccess()
signal onLicenseFailed()
signal onDLCQueryResult(jsonData: String)
signal onDLCPurchaseResult(dlcId: String, status: int)

# 内购相关
signal onProductDetailsResponse(jsonData: String)
signal onPurchaseUpdated(jsonData: String)
signal onFinishPurchaseResponse(jsonData: String)
signal onQueryUnfinishedPurchaseResponse(jsonData: String)
signal onLaunchBillingFlowResult(jsonData: String)
```

### 合规状态码

```gdscript
enum ComplianceMessage {
    LOGIN_SUCCESS = 500              # 正常进入游戏
    EXITED = 1000                    # 退出认证，返回登录页
    SWITCH_ACCOUNT = 1001            # 切换账号
    PERIOD_RESTRICT = 1030           # 当前时间无法游戏
    DURATION_LIMIT = 1050            # 无可玩时长
    AGE_LIMIT = 1100                 # 年龄限制
    INVALID_CLIENT_OR_NETWORK_ERROR = 1200  # 网络或配置错误
    REAL_NAME_STOP = 9002            # 关闭实名窗口
}
```

## 🤖 CI/CD 配置

本项目配置了 GitHub Actions 自动化工作流，可自动编译和发布插件。

### 自动触发条件

- 推送到 `main`、`master`、`develop` 分支
- 创建 Pull Request
- 手动触发（workflow_dispatch）

### 工作流程

1. **检出代码** - 获取最新源码
2. **设置 JDK 17** - 配置 Java 编译环境
3. **编译模块** - 执行 Gradle 构建
4. **准备发布目录** - 创建 release 结构
5. **复制文件** - 复制 AAR、GDAP 和 addons 文件
6. **上传产物** - 保存构建结果供下载
7. **创建 Release** (仅标签推送) - 自动发布到 GitHub

### 手动触发构建

1. 访问仓库的 Actions 页面
2. 选择 "Build and Deploy Godot3TapTap Plugin" 工作流
3. 点击 "Run workflow"

### 下载构建产物

1. 进入 Actions 页面
2. 选择成功的工作流运行
3. 在 Artifacts 部分下载 `godot3-taptap-plugin`

### 创建正式发布

创建并推送一个标签，会自动创建 GitHub Release：

```bash
git tag v1.0.0
git push origin v1.0.0
```

## 🔧 项目结构

```
godot3_taptap_android_plugin/
├── .github/
│   └── workflows/
│       └── ci.yml                    # CI/CD 配置
├── addons/
│   └── godot3-taptap/                # GDScript 插件
│       ├── plugin.cfg                # 插件配置
│       ├── plugin.gd                 # 插件入口
│       ├── taptap.gd                 # 主 API 接口
│       ├── taptap_config_window.gd   # Token 加密工具(GUI)
│       ├── generate_secure_key.gd    # 密钥生成器
│       ├── example_usage.gd          # 使用示例
│       └── README.md                 # 插件文档
├── android/
│   ├── Godot3TapTap/                 # Android 模块
│   │   ├── build.gradle              # 构建配置
│   │   ├── Godot3TapTap.gdap         # Godot 插件描述
│   │   └── src/main/java/            # Java 源码
│   │       └── com/sygames/godot3taptap/
│   │           └── Godot3TapTap.java # 插件主类
│   ├── libs/                         # Godot 库文件
│   │   ├── debug/
│   │   │   └── godot-lib.debug.aar
│   │   └── release/
│   │       └── godot-lib.release.aar
│   ├── build.gradle                  # 项目构建配置
│   └── gradlew                       # Gradle 包装器
├── release/                          # 构建产物（被 .gitignore 忽略）
│   ├── android/
│   │   └── plugins/
│   │       ├── Godot3TapTap.aar
│   │       └── Godot3TapTap.gdap
│   └── addons/
│       └── godot3-taptap/
├── .gitignore                        # Git 忽略配置
└── README.md                         # 项目说明文档
```

## ❓ 常见问题

### 编译相关

**Q: Gradle 编译失败，提示找不到 Godot 库？**

A: 确保 `android/libs/release/godot-lib.release.aar` 文件存在。这个文件需要从 Godot 官方或你的 Godot 项目中获取。

**Q: Windows 下 gradlew 权限问题？**

A: 使用 `gradlew.bat` 替代 `gradlew`，或在 PowerShell 中执行。

### 使用相关

**Q: 插件在 Godot 中不显示？**

A: 检查以下几点：
1. 插件文件夹是否正确放置在 `res://addons/godot3-taptap/`
2. 插件是否在项目设置中启用
3. 重启 Godot 编辑器

**Q: Android 导出时插件未包含？**

A: 
1. 确保使用自定义构建（Custom Build）
2. 在导出设置的 Plugins 列表中勾选 Godot3TapTap
3. 检查 `android/plugins/` 目录是否有 AAR 和 GDAP 文件

**Q: 登录失败或 SDK 初始化失败？**

A: 
1. 检查 Client ID 和 Client Token 是否正确
2. 检查网络连接
3. 启用日志查看详细错误信息：`initSdk(..., enableLog: true)`
4. 确认在 TapTap 开发者平台正确配置了应用

### Token 加密相关

**Q: 加密工具无法保存密钥？**

A: 检查 Android 资源目录是否存在，密钥文件路径应为：
```
android/Godot3TapTap/src/main/res/raw/taptap_decrypt_key.txt
```

**Q: Android 端解密失败？**

A: 确认以下几点：
1. 密钥文件内容与加密工具中使用的密钥一致
2. Java 代码中的解密方法已正确更新
3. 重新编译 AAR 文件

## 📖 相关文档

- [TapTap 开发者中心](https://developer.taptap.cn/)
- [TapTap 登录文档](https://developer.taptap.cn/docs/sdk/taptap-login/guide/tap-login/)
- [TapTap 内购文档](https://developer.taptap.cn/docs/sdk/tap-iap/develop/android/)
- [Godot Android 插件开发](https://docs.godotengine.org/en/3.5/tutorials/platform/android/android_plugin.html)

## 📄 许可证

本项目基于 MIT 许可证开源。详见 LICENSE 文件。

## 🤝 贡献

欢迎提交 Issue 和 Pull Request！

## 📮 联系方式

- GitHub Issues: [提交问题](../../issues)
- TapTap 开发者社区: [访问论坛](https://developer.taptap.cn/)

---

**祝你的 Godot 游戏开发顺利！🎮**
