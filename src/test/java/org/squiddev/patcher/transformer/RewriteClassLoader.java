package org.squiddev.patcher.transformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.squiddev.patcher.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

/**
 * Loads classes, rewriting them
 */
public class RewriteClassLoader extends ClassLoader {
	public final IPatcher[] patchers;

	private Set<String> loaded = new HashSet<String>();

	public RewriteClassLoader(IPatcher[] patchers) {
		this.patchers = patchers;
	}

	@Override
	protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
		if (!name.startsWith(ClassReplacerTest.PATCHES) || !loaded.add(name)) {
			return super.loadClass(name, resolve);
		}
		return findClass(name);
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		if (!name.startsWith(ClassReplacerTest.PATCHES)) {
			return super.findClass(name);
		}

		InputStream is = getClass().getResourceAsStream("/" + name.replace('.', '/') + ".class");
		if (is == null) {
			throw new ClassNotFoundException();
		}

		try {
			byte[] b = new byte[is.available()];
			int len = 0;
			while (true) {
				int n = is.read(b, len, b.length - len);
				if (n == -1) {
					if (len < b.length) {
						byte[] c = new byte[len];
						System.arraycopy(b, 0, c, 0, len);
						b = c;
					}
					return defineClass(name, b);
				}
				len += n;
				if (len == b.length) {
					int last = is.read();
					if (last < 0) {
						return defineClass(name, b);
					}
					byte[] c = new byte[b.length + 1000];
					System.arraycopy(b, 0, c, 0, len);
					c[len++] = (byte) last;
					b = c;
				}
			}
		} catch (IOException e) {
			throw new ClassNotFoundException("Nope", e);
		} finally {
			try {
				is.close();
			} catch (IOException ignored) {
			}
		}
	}

	protected Class<?> defineClass(String name, byte[] bytes) {
		try {
			int flags = ClassReader.SKIP_DEBUG;
			ClassWriter writer = null;
			ClassVisitor visitor = null;
			for (IPatcher patcher : patchers) {
				if (patcher.matches(name)) {
					if (visitor == null) {
						visitor = writer = new ClassWriter(0);
					}
					visitor = patcher.patch(name, visitor);
				}
			}

			if (visitor != null) {
				new ClassReader(bytes).accept(visitor, flags);
				bytes = writer.toByteArray();
			}
		} catch (Exception e) {
			Logger.error("Cannot load " + name, e);
		}

		return defineClass(name, bytes, 0, bytes.length);
	}
}
