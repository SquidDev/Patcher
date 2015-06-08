package org.squiddev.patcher.patch.classes.patch;

import org.squiddev.patcher.patch.MergeVisitor;

/**
 * Tests {@link org.squiddev.patcher.patch.MergeVisitor.Stub}
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
