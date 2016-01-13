package org.squiddev.patcher.transformer.classes.patch;

import org.squiddev.patcher.visitors.MergeVisitor;

/**
 * Tests the blocks annotation
 */
public class Blocks extends Base {
	@MergeVisitor.Blocks({"testingF", "anotherF"})
	public int testingF;

	public int anotherF;

	@MergeVisitor.Blocks({"testing", "getPrivateName", "getName"})
	public void testing() {
	}
}
