tool
extends EditorScript

# TapTap 密钥生成工具
# 生成随机密钥并更新密钥文件

func _run():
	print("=".repeat(50))
	print("🔑 TapTap 密钥生成工具")
	print("=".repeat(50))
	
	# 生成随机密钥
	var crypto = Crypto.new()
	var random_bytes = crypto.generate_random_bytes(16)
	var random_key = "TapTap" + Marshalls.raw_to_base64(random_bytes).replace("=", "").replace("/", "").replace("+", "").substr(0, 16)
	
	print("生成的随机密钥: ", random_key)
	
	# 更新 Android 资源文件
	var res_file = File.new()
	var res_path = "res://android/build/res/raw/taptap_decrypt_key.txt"
	var absolute_path = ProjectSettings.globalize_path(res_path)
	
	# 确保目录存在
	var dir = Directory.new()
	var dir_path = absolute_path.get_base_dir()
	if not dir.dir_exists(dir_path):
		dir.make_dir_recursive(dir_path)
	
	if res_file.open(absolute_path, File.WRITE) == OK:
		res_file.store_string(random_key)
		res_file.close()
		print("✅ 已更新 Android 资源文件")
		print("   位置: ", absolute_path)
		print("   内容: ", random_key)
	else:
		print("❌ 无法写入 Android 资源文件，请手动更新")
		print("   文件路径: ", absolute_path)
		print("   密钥内容: ", random_key)
	
	print("\n🔒 使用说明:")
	print("• 新密钥已保存到 Android 资源文件")
	print("• 请使用 TapTap 配置工具重新生成加密 Token")
	print("• 配置工具会自动读取新密钥")
	print("• 不要将密钥文件提交到公开的版本控制系统")
	print("• 建议定期更换密钥以提高安全性")
	
	print("\n📝 下一步操作:")
	print("1. 打开 Project → Tools → TapTap RSA 密钥配置")
	print("2. 新密钥会自动加载到配置界面")
	print("3. 重新加密所有需要的 Token")
	print("4. 更新 Java 代码中的解密方法")
	print("=".repeat(50))