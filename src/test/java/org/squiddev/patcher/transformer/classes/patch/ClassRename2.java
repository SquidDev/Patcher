package org.squiddev.patcher.transformer.classes.patch;

import org.squiddev.patcher.visitors.MergeVisitor;

/**
 * Used to test {@link MergeVisitor.Rename}
 */
@MergeVisitor.Rename(
	from = "org/squiddev/patcher/transformer/classes/patch/Base$Baz",
	to = "org/squiddev/patcher/transformer/classes/patch/Base$Foo"
)
public class ClassRename2 extends Base {
	@MergeVisitor.Stub
	private Baz getFoo() {
		return null;
	}
}
