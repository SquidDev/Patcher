package org.squiddev.patcher.transformer;

import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class TryCatcherTest {
	public static String CLASS = "org.squiddev.patcher.transformer.TryCatcherTest$Inner";

	@Test
	public void tryCatcher() throws Exception {
		RewriteClassLoader loader = new RewriteClassLoader(new TryCatcher(CLASS)).addPrefixes(CLASS);

		Class<?> base = loader.loadClass(CLASS);
		Object instance = base.newInstance();

		Method method = base.getMethod("explode");
		try {
			method.invoke(instance);
			fail("Expected exception");
		} catch (InvocationTargetException e) {
			assertEquals("Whatever", e.getTargetException().getMessage());
		}
	}

	public static class Inner {
		public void explode() {
			throw new RuntimeException("Whatever");
		}
	}
}
