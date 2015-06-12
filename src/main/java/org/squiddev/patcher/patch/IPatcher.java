package org.squiddev.patcher.patch;

import org.objectweb.asm.ClassVisitor;

/**
 * Patches methods on a class
 */
public interface IPatcher {
	/**
	 * Checks if the class matches
	 *
	 * @param className The name of the class
	 * @return If it should be patched
	 */
	boolean matches(String className);

	/**
	 * Create a visitor to patch a class
	 *
	 * @param className The name of the class
	 * @param delegate  The visitor to delegate to
	 * @return The patching visitor
	 * @throws java.lang.Exception When any error occurs. This will stop the patching process
	 */
	ClassVisitor patch(String className, ClassVisitor delegate) throws Exception;
}
