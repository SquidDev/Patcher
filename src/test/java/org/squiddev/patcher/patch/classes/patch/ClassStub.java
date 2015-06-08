package org.squiddev.patcher.patch.classes.patch;

import org.squiddev.patcher.patch.MergeVisitor;

/**
 * Tests {@link org.squiddev.patcher.patch.MergeVisitor.Stub} on classes
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
