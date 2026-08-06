package cushion.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 模组日志工具。 */
public final class CushionLog {
	private static final Logger LOGGER = LoggerFactory.getLogger(CushionClient.MOD_ID);

	private CushionLog() {
	}

	public static void info(String message) {
		LOGGER.info(message);
	}

	public static void warn(String message, Throwable throwable) {
		LOGGER.warn(message, throwable);
	}
}
