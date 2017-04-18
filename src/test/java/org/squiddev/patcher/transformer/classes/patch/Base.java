package org.squiddev.patcher.transformer.classes.patch;

/**
 * Base class for all rewrites
 */
@SuppressWarnings("unused")
public class Base extends VeryBase {
	public static String foo = "foo";

	private static class Foo {
		public String getName() {
			return "Foo";
		}
	}

	private static class Bar {
		public String getName() {
			return "Bar";
		}
	}

	public static class Baz {
		public String getName() {
			return "Baz";
		}
	}

	@Override
	public String getName() {
		return new Foo().getName();
	}

	private Foo getFoo() {
		return new Foo();
	}

	private void onlyExistsToMakeSureBarHasSameConstructorAsFoo() {
		new Bar().getName();
	}

	private String getPrivateName() {
		return "Private";
	}
}
