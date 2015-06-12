package org.squiddev.patcher.transformer;

import org.objectweb.asm.ClassReader;

import java.io.IOException;

/**
 * Finds classes in the /patch/ directory and uses
 * them instead
 */
public class ClassReplaceSource implements ISource {
	/**
	 * The root directory we are finding patches at
	 */
	public final String patchPath;

	/**
	 * Name of the class we are replacing
	 */
	public final String targetName;

	public ClassReplaceSource(String targetName, String patchPath) {
		if (!patchPath.endsWith("/")) patchPath += "/";

		this.patchPath = patchPath;
		this.targetName = targetName;
	}

	public ClassReplaceSource(String targetName) {
		this(targetName, "/patch/");
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
		if (className.startsWith(this.targetName)) {
			String source = patchPath + className.replace('.', '/') + ".class";
			return new ClassReader(ClassReplaceSource.class.getResourceAsStream(source));
		}

		return null;
	}
}
