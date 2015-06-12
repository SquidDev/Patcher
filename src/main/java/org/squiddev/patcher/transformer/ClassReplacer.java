package org.squiddev.patcher.transformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.commons.RemappingClassAdapter;

import java.io.IOException;

/**
 * Replaces a class named {@link #className} with {@link #patchName}
 */
public class ClassReplacer extends ClassRewriter implements ISource {
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
		return new RemappingClassAdapter(delegate, context);
	}

	/**
	 * Get a reader for this source
	 *
	 * @param className The class name to use
	 * @return The reader or {@code null} if nothing to provide
	 * @throws java.io.IOException Sometimes things blow up...
	 */
	@Override
	public ClassReader getReader(String className) throws IOException {
		return matches(className) ? getSource(patchType + className.substring(classNameStart)) : null;
	}
}
