package org.squiddev.patcher.transformer.classes.patch;

/**
 * Used to test calling super
 */
public class Super extends Base {
	@Override
	public String getName() {
		return super.getName() + "Super";
	}
}
