package org.squiddev.patcher.transformer;

import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;

/**
 * Tests the {@link org.squiddev.patcher.transformer.ClassReplacer} and {@link ClassMerger} methods
 */
public class ClassReplacerTest {
	public static final String PATCHES = "org.squiddev.patcher.transformer.classes.";
	public static final String CLASS = PATCHES + "BaseClass";
	public static final String METHOD = "getMessage";
	public static final String FIELD = "message";

	@Test
	public void defaultClass() throws Exception {
		RewriteClassLoader loader = new RewriteClassLoader();

		Class<?> base = loader.loadClass(CLASS);
		Object instance = base.newInstance();

		Method method = base.getMethod(METHOD);
		assertEquals("Foo", method.invoke(instance));

		base.getField(FIELD).set(instance, "Bar");
		assertEquals("Bar", method.invoke(instance));
	}

	@Test
	public void defaultReplacer() throws Exception {
		RewriteClassLoader loader = new RewriteClassLoader(new ClassReplacer(CLASS));

		Class<?> base = loader.loadClass(CLASS);
		Object instance = base.newInstance();

		Method method = base.getMethod(METHOD);
		assertEquals("Bar_Rewrite", method.invoke(instance));

		base.getField("renamedMessage").set(instance, "Baz");
		assertEquals("Baz_Rewrite", method.invoke(instance));
	}

	@Test
	public void defaultPatch() throws Exception {
		RewriteClassLoader loader = new RewriteClassLoader(new ClassMerger(CLASS));

		Class<?> base = loader.loadClass(CLASS);
		Object instance = base.newInstance();

		Method method = base.getMethod(METHOD);
		assertEquals("Foo_Patch", method.invoke(instance));

		base.getField(FIELD).set(instance, "Bar");
		assertEquals("Bar_Patch", method.invoke(instance));
	}
}
