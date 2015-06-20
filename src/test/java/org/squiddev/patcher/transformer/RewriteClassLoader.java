package org.squiddev.patcher.transformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.util.CheckClassAdapter;
import org.objectweb.asm.util.TraceClassVisitor;
import org.squiddev.patcher.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Loads classes, rewriting them
 */
public class RewriteClassLoader extends ClassLoader {
	public final TransformationChain chain;

	private Set<String> loaded = new HashSet<String>();
	private Set<String> prefixes = new HashSet<String>();

	public RewriteClassLoader(IPatcher... patchers) {
		chain = new TransformationChain();
		for (IPatcher patcher : patchers) {
			if (patcher instanceof ISource) {
				chain.add((ISource) patcher);
			}
			chain.add(patcher);
		}
		chain.finalise();

		prefixes.add(ClassReplacerTest.PATCHES);
	}

	public RewriteClassLoader addPrefixes(String... prefixes) {
		this.prefixes.addAll(Arrays.asList(prefixes));
		return this;
	}

	@Override
	protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
		if (!loaded.add(name)) {
			return super.loadClass(name, resolve);
		}

		for (String prefix : prefixes) {
			if (name.startsWith(prefix)) {
				return findClass(name);
			}
		}

		return super.loadClass(name, resolve);
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		boolean success = false;
		for (String prefix : prefixes) {
			if (name.startsWith(prefix)) {
				success = true;
				break;
			}
		}
		if (!success) return super.findClass(name);

		InputStream is = getClass().getResourceAsStream("/" + name.replace('.', '/') + ".class");
		if (is == null) {
			throw new ClassNotFoundException(name);
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
			bytes = chain.transform(name, bytes);
		} catch (Exception e) {
			Logger.error("Cannot load " + name, e);
		}

		validateClass(new ClassReader(bytes));

		return defineClass(name, bytes, 0, bytes.length);
	}

	public void validateClass(ClassReader reader) {
		StringWriter writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);

		Exception error = null;
		try {
			CheckClassAdapter.verify(reader, this, false, printWriter);
		} catch (Exception e) {
			error = e;
		}

		String contents = writer.toString();
		if (error != null || contents.length() > 0) {
			reader.accept(new TraceClassVisitor(printWriter), 0);
			throw new RuntimeException("Generation error\nDump for " + reader.getClassName() + "\n" + writer.toString(), error);
		}
	}
}
