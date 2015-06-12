package org.squiddev.patcher.transformer.classes.patch;

import org.squiddev.patcher.visitors.MergeVisitor;

/**
 * Tests {@link org.squiddev.patcher.visitors.MergeVisitor.Stub} on classes
 */
public class ClassStub extends Base {
	@MergeVisitor.Stub
	private static class Foo {
		public String getName() {
			return "Created a Stub";
		}
	}

	@Override
	public String getName() {
		return new Foo().getName() + " Stub";
	}
}
