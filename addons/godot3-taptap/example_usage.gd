extends Control

# TapTap SDK 使用示例
# 这个脚本展示了如何使用 TapTap SDK 的各种功能

# 引用 TapTap 节点（假设已经作为 AutoLoad 添加）
onready var taptap = get_node("/root/TapTap")

# 配置信息（请替换为你的实际配置）
const CLIENT_ID = "your_client_id_here"
const CLIENT_TOKEN = "your_client_token_here"
const PRODUCT_IDS = ["product_1", "product_2", "premium_package"]

func _ready():
	# 连接所有相关信号
	connect_taptap_signals()
	
	# 初始化 SDK（启用内购功能）
	taptap.initSdk(CLIENT_ID, CLIENT_TOKEN, true)
	
	print("TapTap SDK 示例已启动")

func connect_taptap_signals():
	# 连接 TapTap 的所有信号
	# 登录相关信号
	taptap.connect("onLoginSuccess", self, "_on_login_success")
	taptap.connect("onLoginFail", self, "_on_login_fail")
	taptap.connect("onLoginCancel", self, "_on_login_cancel")
	
	# 合规认证信号
	taptap.connect("onComplianceResult", self, "_on_compliance_result")
	
	# IAP 相关信号
	taptap.connect("onProductDetailsResponse", self, "_on_product_details_response")
	taptap.connect("onPurchaseUpdated", self, "_on_purchase_updated")
	taptap.connect("onFinishPurchaseResponse", self, "_on_finish_purchase_response")
	taptap.connect("onQueryUnfinishedPurchaseResponse", self, "_on_query_unfinished_purchase_response")
	taptap.connect("onLaunchBillingFlowResult", self, "_on_launch_billing_flow_result")

# ==================== 登录相关回调 ====================

func _on_login_success():
	print("登录成功！")
	taptap.showTip("登录成功")
	
	# 登录成功后的操作
	load_user_info()
	start_compliance_check()
	query_products()
	check_unfinished_purchases()

func _on_login_fail(message: String):
	print("登录失败: ", message)
	taptap.showTip("登录失败: " + message)

func _on_login_cancel():
	print("用户取消登录")
	taptap.showTip("登录已取消")

func load_user_info():
	# 加载用户信息
	yield(taptap.loadUserInfo(), "completed")
	var profile = taptap.getUserProfile()
	if not profile.has("error"):
		print("用户信息: ", profile)
		print("用户名: ", taptap.userName)

# ==================== 合规认证回调 ====================

func _on_compliance_result(code: int, info: String):
	print("合规认证结果 - 代码: ", code, ", 信息: ", info)
	
	match code:
		taptap.ComplianceMessage.LOGIN_SUCCESS:
			print("合规检查通过，可以正常游戏")
			taptap.showTip("合规检查通过")
		taptap.ComplianceMessage.EXITED:
			print("退出合规认证，返回登录页")
			handle_compliance_exit()
		taptap.ComplianceMessage.SWITCH_ACCOUNT:
			print("用户要求切换账号")
			handle_switch_account()
		taptap.ComplianceMessage.PERIOD_RESTRICT:
			print("当前时间段无法游戏")
			taptap.showTip("当前时间段无法游戏")
		taptap.ComplianceMessage.DURATION_LIMIT:
			print("游戏时长已达上限")
			taptap.showTip("游戏时长已达上限")
		taptap.ComplianceMessage.AGE_LIMIT:
			print("年龄限制，无法进入游戏")
			taptap.showTip("年龄限制")
		_:
			print("其他合规状态: ", code)

func start_compliance_check():
	# 启动合规认证
	taptap.compliance()

func handle_compliance_exit():
	# 处理合规认证退出
	# 返回登录界面或退出游戏
	pass

func handle_switch_account():
	# 处理切换账号
	taptap.logout()
	# 显示登录界面
	pass

# ==================== IAP 相关回调 ====================

func _on_product_details_response(response_data: Dictionary):
	print("商品详情响应: ", response_data)
	
	if response_data.has("error"):
		print("查询商品详情失败: ", response_data.error)
		return
	
	if response_data.has("productDetails"):
		var products = response_data.productDetails
		print("可用商品数量: ", products.size())
		for product_id in products:
			var product = products[product_id]
			print("商品 ID: ", product_id)
			print("  名称: ", product.get("name", "未知"))
			print("  描述: ", product.get("description", "无描述"))
	
	if response_data.has("unavailableProductIds"):
		var unavailable = response_data.unavailableProductIds
		if unavailable.size() > 0:
			print("不可用商品: ", unavailable)

func _on_purchase_updated(purchase_data: Dictionary):
	print("购买状态更新: ", purchase_data)
	
	if purchase_data.has("error"):
		print("购买更新错误: ", purchase_data.error)
		return
	
	if purchase_data.has("purchase"):
		var purchase = purchase_data.purchase
		var product_id = purchase.get("productId", "")
		var order_id = purchase.get("orderId", "")
		var purchase_token = purchase.get("purchaseToken", "")
		var purchase_state = purchase.get("purchaseState", "")
		
		print("购买商品: ", product_id)
		print("订单ID: ", order_id)
		print("购买状态: ", purchase_state)
		
		# 根据购买状态处理
		handle_purchase_state_change(purchase)

func handle_purchase_state_change(purchase: Dictionary):
	# 处理购买状态变化
	var product_id = purchase.get("productId", "")
	var order_id = purchase.get("orderId", "")
	var purchase_token = purchase.get("purchaseToken", "")
	var purchase_state = purchase.get("purchaseState", "")
	
	# 这里根据实际的购买状态值来判断，具体值需要参考 TapTap 文档
	if purchase_state == "PURCHASED":  # 购买成功
		# 发放商品给用户
		give_item_to_user(product_id)
		
		# 确认订单
		taptap.finishPurchaseAsync(order_id, purchase_token)
		
		taptap.showTip("购买成功: " + product_id)
	elif purchase_state == "PENDING":  # 购买待处理
		print("购买待处理，等待确认")
		taptap.showTip("购买处理中...")

func give_item_to_user(product_id: String):
	# 给用户发放商品
	print("发放商品给用户: ", product_id)
	
	match product_id:
		"product_1":
			# 发放商品1
			print("发放商品1")
		"product_2":
			# 发放商品2
			print("发放商品2")
		"premium_package":
			# 发放高级包
			print("发放高级包")
		_:
			print("未知商品: ", product_id)

func _on_finish_purchase_response(response_data: Dictionary):
	print("完成订单响应: ", response_data)
	
	if response_data.has("error"):
		print("完成订单失败: ", response_data.error)
		return
	
	print("订单确认成功")
	taptap.showTip("订单已确认")

func _on_query_unfinished_purchase_response(response_data: Dictionary):
	print("未完成订单查询响应: ", response_data)
	
	if response_data.has("error"):
		print("查询未完成订单失败: ", response_data.error)
		return
	
	if response_data.has("purchases"):
		var purchases = response_data.purchases
		print("发现 ", purchases.size(), " 个未完成订单")
		
		for purchase in purchases:
			print("处理未完成订单: ", purchase.get("productId", ""))
			# 处理未完成的购买
			handle_purchase_state_change(purchase)

func _on_launch_billing_flow_result(result_data: Dictionary):
	print("启动购买流程结果: ", result_data)
	
	if result_data.has("error"):
		print("启动购买失败: ", result_data.error)
		taptap.showTip("启动购买失败")
		return
	
	print("购买流程已启动")

# ==================== UI 交互方法 ====================

func _on_login_button_pressed():
	# 登录按钮点击
	if taptap.isLogin():
		taptap.showTip("已经登录")
		return
	
	# 请求用户资料权限，不请求好友权限
	taptap.login(true, false)

func _on_logout_button_pressed():
	# 登出按钮点击
	taptap.logout()
	taptap.showTip("已登出")

func _on_buy_product_button_pressed(product_id: String):
	# 购买商品按钮点击
	if not taptap.isLogin():
		taptap.showTip("请先登录")
		return
	
	# 使用便利方法购买商品
	taptap.purchaseProduct(product_id)

func _on_query_products_button_pressed():
	# 查询商品按钮点击
	query_products()

func _on_check_license_button_pressed():
	# 检查许可证按钮点击
	taptap.checkLicense(false)  # 使用缓存

func query_products():
	# 查询商品详情
	taptap.queryProductDetailsAsync(PRODUCT_IDS)

func check_unfinished_purchases():
	# 检查未完成的购买
	taptap.queryUnfinishedPurchaseAsync()