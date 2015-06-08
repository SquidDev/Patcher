package org.squiddev.patcher;

/**
 * org.squiddev.patcher (Patcher
 */
public class Logger {
	public static void debug(String message) {
		System.out.println("[DEBUG] " + message);
	}

	public static void warn(String message) {
		System.out.println("[WARN] " + message);
	}

	public static void error(String message, Throwable e) {
		System.out.println("[ERROR] " + message);
		e.printStackTrace(System.out);
	}
}
