package org.squiddev.patcher.patch.classes.patch;

import org.squiddev.patcher.patch.MergeVisitor;

/**
 * Used to test {@link org.squiddev.patcher.patch.MergeVisitor.Rename} on methods
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
