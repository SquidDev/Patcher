package org.squiddev.patcher.transformer;

import org.objectweb.asm.ClassReader;

import java.io.IOException;

/**
 * A custom method of providing a Java reader
 */
public interface ISource {
	/**
	 * Get a reader for this source
	 *
	 * @param className The class name to use
	 * @return The reader or {@code null} if nothing to provide
	 * @throws java.io.IOException Sometimes things blow up...
	 */
	public ClassReader getReader(String className) throws IOException;
}
