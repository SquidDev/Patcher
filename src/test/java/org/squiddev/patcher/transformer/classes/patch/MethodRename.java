package org.squiddev.patcher.transformer.classes.patch;

import org.squiddev.patcher.visitors.MergeVisitor;

/**
 * Used to test {@link org.squiddev.patcher.visitors.MergeVisitor.Rename} on methods
 */
public class MethodRename extends Base {
	@MergeVisitor.Rename(to = "getName")
	public String anotherGetName() {
		return parentGetName() + "Bar";
	}

	@MergeVisitor.Rename(from = "getName")
	@MergeVisitor.Stub
	public String parentGetName() {
		return "Foo";
	}
}
