package cushion.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.Identifier;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * 模组配置，保存在 {@code config/cushion.json}。
 * <p>
 * 字段说明：
 * <ul>
 *   <li>{@code enabledByDefault} - 进入游戏时功能是否默认开启。</li>
 *   <li>{@code clickIntervalMs} - 自动执行使用键交互的最小间隔（毫秒）。</li>
 *   <li>{@code targetEntities} - 要自动点击的目标实体 ID 列表，支持通配符：
 *       {@code cushion:*} 匹配整个命名空间，{@code *} 匹配所有实体，其余按
 *       {@code 命名空间:路径} 精确匹配。列表为空时默认匹配所有 {@code cushion:} 实体。</li>
 * </ul>
 */
public final class CushionConfig {
	public static final String FILE_NAME = "cushion.json";

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	/** 进入游戏时功能是否默认开启。 */
	public boolean enabledByDefault = true;
	/** 自动点击使用键的最小间隔（毫秒）。 */
	public long clickIntervalMs = 200;
	/** 目标实体 ID 列表。 */
	public List<String> targetEntities = new ArrayList<>(List.of("cushion:*"));

	/**
	 * 判断指定实体类型 ID 是否命中配置的目标列表。
	 *
	 * @param entityTypeId 实体类型 ID（如 {@code cushion:foo}）
	 */
	public boolean matches(Identifier entityTypeId) {
		if (targetEntities == null || targetEntities.isEmpty()) {
			// 未配置时，默认匹配 cushion 命名空间的所有实体
			return entityTypeId.getNamespace().equals(CushionClient.MOD_ID);
		}
		for (String pattern : targetEntities) {
			if (matchesPattern(pattern, entityTypeId)) {
				return true;
			}
		}
		return false;
	}

	private static boolean matchesPattern(String pattern, Identifier id) {
		if (pattern == null || pattern.isEmpty()) {
			return false;
		}
		if (pattern.equals("*")) {
			return true;
		}
		if (pattern.endsWith(":*")) {
			String namespace = pattern.substring(0, pattern.length() - 2);
			return id.getNamespace().equals(namespace);
		}
		return id.toString().equals(pattern);
	}

	/**
	 * 从 {@code config/cushion.json} 加载配置；文件不存在或损坏时写入默认配置。
	 */
	public static CushionConfig load() {
		Path file = FabricLoader.getInstance().getConfigDir().resolve(FILE_NAME);
		if (Files.exists(file)) {
			try {
				CushionConfig parsed = GSON.fromJson(Files.readString(file), CushionConfig.class);
				if (parsed != null) {
					parsed.normalize();
					return parsed;
				}
			} catch (IOException | JsonSyntaxException e) {
				CushionLog.warn("无法读取配置 " + file + "，将使用默认配置", e);
			}
		}
		CushionConfig config = new CushionConfig();
		config.save(file);
		return config;
	}

	private void normalize() {
		if (clickIntervalMs < 1) {
			clickIntervalMs = 1;
		}
		if (targetEntities == null) {
			targetEntities = new ArrayList<>();
		}
	}

	/** 将当前配置写回文件（若目录不存在会自动创建）。 */
	public void save() {
		save(FabricLoader.getInstance().getConfigDir().resolve(FILE_NAME));
	}

	private void save(Path file) {
		try {
			Files.createDirectories(file.getParent());
			Files.writeString(file, GSON.toJson(this));
		} catch (IOException e) {
			CushionLog.warn("无法保存配置到 " + file, e);
		}
	}
}
