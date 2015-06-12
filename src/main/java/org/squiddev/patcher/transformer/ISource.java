package org.squiddev.patcher.transformer;

import org.objectweb.asm.ClassReader;

/**
 * A custom method of providing a Java reader
 */
public interface ISource {
	/**
	 * Get a reader for this source
	 *
	 * @param className The class name to use
	 * @return The reader or {@code null} if nothing to provide
	 */
	public ClassReader getReader(String className);
}
