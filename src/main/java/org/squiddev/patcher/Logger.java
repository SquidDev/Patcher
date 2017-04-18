package org.squiddev.patcher;

public class Logger {
	public static Logger instance = new Logger();

	public static void debug(String message) {
		instance.doDebug(message);
	}

	public static void warn(String message) {
		instance.doWarn(message);
	}

	public static void error(String message, Throwable e) {
		instance.doError(message, e);
	}

	public void doDebug(String message) {
		System.out.println("[DEBUG] " + message);
	}

	public void doWarn(String message) {
		System.out.println("[WARN] " + message);
	}

	public void doError(String message, Throwable e) {
		System.out.println("[ERROR] " + message);
		if (e != null) e.printStackTrace(System.out);
	}
}
