package org.squiddev.patcher.transformer.classes.patch;

import org.squiddev.patcher.visitors.MergeVisitor;

/**
 * Tests {@link org.squiddev.patcher.visitors.MergeVisitor.Stub}
 */
public class MethodStub extends Base {
	@Override
	@MergeVisitor.Stub
	public String getName() {
		return "Called a Stub";
	}

	@MergeVisitor.Stub
	public String getPrivateName() {
		return "Called the private stub";
	}
}
