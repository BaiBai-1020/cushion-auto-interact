package cushion.client;

import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

/**
 * 自动交互核心逻辑。
 * <p>
 * 功能开启时，每个客户端 tick 检查：准心目标是否为命中配置的实体；
 * 若是且距上次交互已超过配置间隔，则通过 {@code MultiPlayerGameMode#interact}
 * 模拟一次使用键（默认右键）交互——与玩家手动右键的行为一致，由服务器裁决结果。
 */
public final class AutoInteractHandler {
	private static boolean enabled;
	private static long lastInteractAtMs;

	private AutoInteractHandler() {
	}

	public static boolean isEnabled() {
		return enabled;
	}

	public static void setEnabled(boolean value) {
		enabled = value;
	}

	/** 客户端每 tick 调用。 */
	public static void onClientTick(Minecraft client) {
		if (CushionClient.toggleKey != null && CushionClient.toggleKey.consumeClick()) {
			setEnabled(!enabled);
			if (client.player != null) {
				client.player.sendSystemMessage(Component.translatable(
						enabled ? "message.cushion.enabled" : "message.cushion.disabled"));
			}
		}

		if (!enabled) {
			return;
		}
		// 打开任何界面（聊天、容器、暂停菜单等）时不自动交互
		// 26.x 中当前屏幕从 Minecraft 移到了 Gui（通过 screen() 访问器获取）
		if (client.gui.screen() != null) {
			return;
		}
		if (client.player == null || client.level == null || client.gameMode == null) {
			return;
		}

		long now = System.currentTimeMillis();
		if (now - lastInteractAtMs < CushionClient.config.clickIntervalMs) {
			return;
		}

		Entity target = resolveCrosshairEntity(client);
		if (target == null) {
			return;
		}

		Identifier typeId = BuiltInRegistries.ENTITY_TYPE.getKey(target.getType());
		if (typeId == null || !CushionClient.config.matches(typeId)) {
			return;
		}

		lastInteractAtMs = now;
		// 与手动右键完全相同的客户端交互路径；结果由服务器判定
		client.gameMode.interact(client.player, target, new EntityHitResult(target), InteractionHand.MAIN_HAND);
	}

	/** 返回准心指向的实体（若准心未命中实体则返回 {@code null}）。 */
	private static Entity resolveCrosshairEntity(Minecraft client) {
		HitResult hitResult = client.hitResult;
		if (hitResult == null || hitResult.getType() != HitResult.Type.ENTITY) {
			return null;
		}
		if (hitResult instanceof EntityHitResult entityHitResult) {
			return entityHitResult.getEntity();
		}
		return null;
	}
}
