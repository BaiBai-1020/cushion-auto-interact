package cushion.client;

import java.lang.reflect.Field;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;

/**
 * Cushion Auto-Interact 客户端入口。
 * <p>
 * 注册切换快捷键、加载配置并启动每 tick 的自动交互检测。
 */
public final class CushionClient implements ClientModInitializer {
	public static final String MOD_ID = "cushion";

	/** 已加载的配置。 */
	public static CushionConfig config;
	/** 用于切换功能开关的快捷键（默认 B 键）。 */
	public static KeyMapping toggleKey;

	@Override
	public void onInitializeClient() {
		KeyMapping.Category category = KeyMapping.Category.register(
				Identifier.fromNamespaceAndPath(MOD_ID, "main"));
		toggleKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
				"key.cushion.toggle",
				resolveKeyType(),
				InputConstants.KEY_B,
				category
		));

		config = CushionConfig.load();
		AutoInteractHandler.setEnabled(config.enabledByDefault);

		ClientTickEvents.END_CLIENT_TICK.register(AutoInteractHandler::onClientTick);
	}

	/**
	 * 解析 {@link InputConstants.Type} 的键盘枚举值。
	 * <p>
	 * 26.3 快照间该枚举曾改名：snapshot-3 及更早使用 {@code KEYSYM}，
	 * snapshot-7 起改名为 {@code KEYBOARD}。这里按名称反射查找以兼容全部 26.3 快照。
	 */
	private static InputConstants.Type resolveKeyType() {
		for (String name : new String[] { "KEYBOARD", "KEYSYM" }) {
			try {
				Field field = InputConstants.Type.class.getField(name);
				return (InputConstants.Type) field.get(null);
			} catch (NoSuchFieldException | IllegalAccessException e) {
				// 尝试下一个候选名称
			}
		}
		throw new IllegalStateException("无法解析 InputConstants.Type 的键盘枚举（KEYBOARD/KEYSYM）");
	}
}
