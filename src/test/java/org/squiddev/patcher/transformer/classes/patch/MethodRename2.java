package org.squiddev.patcher.transformer.classes.patch;

import org.squiddev.patcher.visitors.MergeVisitor;

/**
 * Used to test {@link MergeVisitor.Rename} on methods
 */
public class MethodRename2 extends Base {
	@MergeVisitor.Rename(from = "getName")
	@MergeVisitor.Stub
	public String parentGetName() {
		return "ParentName";
	}

	@Override
	public String getName() {
		return "Something";
	}
}
