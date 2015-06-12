package org.squiddev.patcher.transformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Finds classes in the /patch/ directory and uses
 * them instead
 */
public class ClassPatcher implements IPatcher {
	/**
	 * The root directory we are finding patches at
	 */
	public final String patchPath;

	/**
	 * Name of the class we are replacing
	 */
	public final String targetName;

	public ClassPatcher(String targetName, String patchPath) {
		if (!patchPath.endsWith("/")) patchPath += "/";

		this.patchPath = patchPath;
		this.targetName = targetName;
	}

	public ClassPatcher(String targetName) {
		this(targetName, "/patch/");
	}

	/**
	 * Checks if the class matches
	 *
	 * @param className The name of the class
	 * @return If it should be patched
	 */
	@Override
	public boolean matches(String className) {
		return className.startsWith(this.targetName);
	}

	/**
	 * Patches a class. Replaces className with {@link #patchPath}.
	 * FIXME: This is really inefficient a method and is a con of returning ClassVisitors
	 *
	 * @param className The name of the class
	 * @param delegate  The visitor to delegate to
	 * @return The patching visitor
	 */
	@Override
	public ClassVisitor patch(String className, final ClassVisitor delegate) throws Exception {
		String source = patchPath + className.replace('.', '/') + ".class";
		final ClassReader reader = new ClassReader(ClassPatcher.class.getResourceAsStream(source));

		return new ClassVisitor(Opcodes.ASM5) {
			@Override
			public void visitEnd() {
				reader.accept(delegate, ClassReader.EXPAND_FRAMES);
			}
		};
	}
}
