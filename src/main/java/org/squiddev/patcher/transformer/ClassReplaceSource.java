package org.squiddev.patcher.transformer;

import org.objectweb.asm.ClassReader;
import org.squiddev.patcher.Logger;

import java.io.IOException;
import java.io.InputStream;

/**
 * Finds classes in the /patch/ directory and uses
 * them instead
 */
public class ClassReplaceSource implements ISource {
	private final Logger logger;
	/**
	 * The root directory we are finding patches at
	 */
	public final String patchPath;

	/**
	 * Name of the class we are replacing
	 */
	public final String targetName;

	public ClassReplaceSource(String targetName, String patchPath) {
		this(Logger.instance, targetName, patchPath);
	}

	public ClassReplaceSource(Logger logger, String targetName, String patchPath) {
		this.logger = logger;
		if (!patchPath.endsWith("/")) patchPath += "/";

		this.patchPath = patchPath;
		this.targetName = targetName;
	}

	public ClassReplaceSource(Logger logger, String targetName) {
		this(logger, targetName, "/patch/");
	}

	public ClassReplaceSource(String targetName) {
		this(Logger.instance, targetName, "/patch/");
	}

	/**
	 * Patches a class. Replaces className with {@link #patchPath}.
	 *
	 * @param className The class name to use
	 * @return The reader or {@code null} if nothing to provide
	 * @throws java.io.IOException Sometimes things blow up...
	 */
	@Override
	public ClassReader getReader(String className) throws IOException {
		if (className.equals(this.targetName) || className.startsWith(this.targetName + "$")) {
			String source = patchPath + className.replace('.', '/') + ".class";

			InputStream stream = ClassReplaceSource.class.getResourceAsStream(source);
			if (stream == null) {
				logger.doWarn("Cannot find custom replacement " + className);
				return null;
			}

			return new ClassReader(stream);
		}

		return null;
	}
}
