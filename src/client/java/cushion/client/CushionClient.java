package cushion.client;

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
				InputConstants.Type.KEYBOARD,
				InputConstants.KEY_B,
				category
		));

		config = CushionConfig.load();
		AutoInteractHandler.setEnabled(config.enabledByDefault);

		ClientTickEvents.END_CLIENT_TICK.register(AutoInteractHandler::onClientTick);
	}
}
