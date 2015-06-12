package org.squiddev.patcher.patch;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.RemappingClassAdapter;

/**
 * Replaces a class named {@link #className} with {@link #patchName}
 */
public class ClassReplacer extends ClassRewriter {
	public final static String NAME_SUFFIX = "_Rewrite";

	public ClassReplacer(String className, String actualName) {
		super(className, actualName);
	}

	public ClassReplacer(String className) {
		this(className, className + NAME_SUFFIX);
	}

	/**
	 * Patches a class. This loads files (by default called _Rewrite) and
	 * renames all references
	 *
	 * @param className The name of the class
	 * @param delegate  The visitor to delegate to
	 * @return The patching visitor
	 */
	@Override
	public ClassVisitor patch(String className, final ClassVisitor delegate) throws Exception {
		final ClassReader reader = getSource(patchType + className.substring(classNameStart));
		if (reader == null) return delegate;

		return new ClassVisitor(Opcodes.ASM5) {
			@Override
			public void visitEnd() {
				reader.accept(new RemappingClassAdapter(delegate, context), ClassReader.EXPAND_FRAMES);
			}
		};
	}
}
