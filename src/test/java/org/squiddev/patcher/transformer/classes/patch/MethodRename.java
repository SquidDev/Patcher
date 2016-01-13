package org.squiddev.patcher.transformer.classes.patch;

import org.squiddev.patcher.visitors.MergeVisitor;

/**
 * Used to test {@link org.squiddev.patcher.visitors.MergeVisitor.Rename} on methods
 */
@MergeVisitor.Rename(from = "java/lang/Integer", to = "java/lang/Number")
public class MethodRename extends Base {
	@MergeVisitor.Rename(to = "getName")
	public String anotherGetName() {
		return parentGetName() + "Bar";
	}

	@MergeVisitor.Rename(to = "getName")
	public String anotherGetName(Integer foobar) {
		return parentGetName() + foobar;
	}

	@MergeVisitor.Rename(from = "getName")
	@MergeVisitor.Stub
	public String parentGetName() {
		return "Foo";
	}
}
