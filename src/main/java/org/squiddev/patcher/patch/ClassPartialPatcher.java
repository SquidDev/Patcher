package org.squiddev.patcher.patch;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;

/**
 * Replaces parts of the class with
 */
public class ClassPartialPatcher extends ClassRewriter {
	public final static String NAME_SUFFIX = "_Patch";

	public ClassPartialPatcher(String className, String patchName) {
		super(className, patchName);
	}

	public ClassPartialPatcher(String className) {
		this(className, className + NAME_SUFFIX);
	}

	/**
	 * Patches a class. This loads files (by default called _Rewrite) and
	 * renames all references
	 *
	 * @param className The name of the class
	 * @param delegate  The visitor to delegate to
	 * @return The patching visitor
	 * @throws Exception When any error occurs. This will stop the patching process
	 */
	@Override
	public ClassVisitor patch(String className, ClassVisitor delegate) throws Exception {
		ClassReader override = getSource(patchType + className.substring(classNameStart));
		if (override == null) return delegate;

		return new MergeVisitor(delegate, override, context);
	}
}
