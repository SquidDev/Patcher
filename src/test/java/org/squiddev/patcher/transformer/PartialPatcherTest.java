package org.squiddev.patcher.transformer;

import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.*;

/**
 * Tests the {@link ClassMerger} methods
 */
public class PartialPatcherTest {
	public static final String PATCHES = ClassReplacerTest.PATCHES + "patch.";
	public static final String CLASS = PATCHES + "Base";

	@Test
	public void classRename() throws Exception {
		RewriteClassLoader loader = new RewriteClassLoader(new ClassMerger(CLASS, PATCHES + "ClassRename"));

		Class<?> base = loader.loadClass(CLASS);
		Object instance = base.newInstance();

		Method method = base.getMethod("getName");
		assertEquals("Bar", method.invoke(instance));
	}

	@Test
	public void classRename2() throws Exception {
		RewriteClassLoader loader = new RewriteClassLoader(new ClassMerger(CLASS, PATCHES + "ClassRename2"));

		Class<?> base = loader.loadClass(CLASS);
		Object instance = base.newInstance();

		Method method = base.getDeclaredMethod("getFoo");
		method.setAccessible(true);
		assertEquals("org.squiddev.patcher.transformer.classes.patch.Base$Foo", method.invoke(instance).getClass().getName());
	}

	@Test
	public void methodRename() throws Exception {
		RewriteClassLoader loader = new RewriteClassLoader(new ClassMerger(CLASS, PATCHES + "MethodRename"));

		Class<?> base = loader.loadClass(CLASS);
		Object instance = base.newInstance();

		Method method = base.getMethod("getName");
		assertEquals("FooBar", method.invoke(instance));
	}

	@Test
	public void methodArgRename() throws Exception {
		RewriteClassLoader loader = new RewriteClassLoader(new ClassMerger(CLASS, PATCHES + "MethodRename"));

		Class<?> base = loader.loadClass(CLASS);
		Object instance = base.newInstance();

		Method method = base.getMethod("getName", Number.class);
		assertEquals("Foo2", method.invoke(instance, 2));
	}

	@Test
	public void methodBlocks() throws Exception {
		RewriteClassLoader loader = new RewriteClassLoader(new ClassMerger(CLASS, PATCHES + "Blocks"));

		Class<?> base = loader.loadClass(CLASS);

		assertNotNull(base.getMethod("testing"));
		try {
			base.getMethod("getPrivateName");
			fail("getName should not exist");
		} catch (NoSuchMethodException ignored) {
		}

		assertNotNull(base.getDeclaredMethod("onlyExistsToMakeSureBarHasSameConstructorAsFoo"));
	}

	@Test
	public void fieldBlocks() throws Exception {
		RewriteClassLoader loader = new RewriteClassLoader(new ClassMerger(CLASS, PATCHES + "Blocks"));

		Class<?> base = loader.loadClass(CLASS);

		assertNotNull(base.getField("testingF"));
		try {
			base.getField("anotherF");
			fail("anotherF should not exist");
		} catch (NoSuchFieldException ignored) {
		}
	}

	@Test
	public void methodRenameII() throws Exception {
		RewriteClassLoader loader = new RewriteClassLoader(new ClassMerger(CLASS, PATCHES + "MethodRename2"));

		Class<?> base = loader.loadClass(CLASS);
		Object instance = base.newInstance();

		{
			Method method = base.getMethod("getName");
			assertEquals("Something", method.invoke(instance));
		}

		{
			Method method = base.getMethod("parentGetName");
			assertEquals("Foo", method.invoke(instance));
		}
	}

	@Test
	public void callSuper() throws Exception {
		RewriteClassLoader loader = new RewriteClassLoader(new ClassMerger(CLASS, PATCHES + "Super"));

		Class<?> base = loader.loadClass(CLASS);
		Object instance = base.newInstance();

		Method method = base.getMethod("getName");
		assertEquals("VeryBaseSuper", method.invoke(instance));
	}

	@Test
	public void stubMethod() throws Exception {
		RewriteClassLoader loader = new RewriteClassLoader(new ClassMerger(CLASS, PATCHES + "MethodStub"));

		Class<?> base = loader.loadClass(CLASS);
		Object instance = base.newInstance();

		Method method = base.getMethod("getName");
		assertEquals("Foo", method.invoke(instance));

		method = base.getMethod("getPrivateName");
		assertEquals("Private", method.invoke(instance));
	}

	@Test
	public void stubMethodAccess() throws Exception {
		RewriteClassLoader loader = new RewriteClassLoader(new ClassMerger(CLASS, PATCHES + "MethodStub"));

		Class<?> base = loader.loadClass(CLASS);
		Object instance = base.newInstance();

		Method method = base.getMethod("getPrivateName");
		assertEquals("Private", method.invoke(instance));
	}

	@Test
	public void stubClass() throws Exception {
		RewriteClassLoader loader = new RewriteClassLoader(new ClassMerger(CLASS, PATCHES + "ClassStub"));

		Class<?> base = loader.loadClass(CLASS);
		Object instance = base.newInstance();

		Method method = base.getMethod("getName");
		assertEquals("Foo Stub", method.invoke(instance));
	}

	@Test
	public void staticConstructors() throws Exception {
		RewriteClassLoader loader = new RewriteClassLoader(new ClassMerger(CLASS, PATCHES + "StaticConstructor"));

		Class<?> base = loader.loadClass(CLASS);

		assertEquals("foo", base.getField("foo").get(null));
	}

	@Test
	public void rewriteClass() throws Exception {
		RewriteClassLoader loader = new RewriteClassLoader(new ClassMerger(CLASS, PATCHES + "ClassRewrite"));

		Class<?> base = loader.loadClass(CLASS + "$Foo");
		{
			Method method = null;
			try {
				method = base.getMethod("getName", int.class);
			} catch (Exception ignored) {
			}

			assertNotNull(method);
		}

		{
			Method method = null;
			try {
				method = base.getMethod("getName");
			} catch (Exception ignored) {
			}

			assertNull(method);
		}

	}
}
