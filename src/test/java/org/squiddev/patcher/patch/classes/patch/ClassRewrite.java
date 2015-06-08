package org.squiddev.patcher.patch.classes.patch;

import org.squiddev.patcher.patch.MergeVisitor;

/**
 * Tests rewriting classes
 */
public class ClassRewrite {
	@MergeVisitor.Rewrite
	private static class Foo {
		public String getName(int times) {
			return new String(new char[times]).replace('\0', 'A');
		}
	}
}
