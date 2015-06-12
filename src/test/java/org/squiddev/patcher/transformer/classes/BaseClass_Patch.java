package org.squiddev.patcher.transformer.classes;

public class BaseClass_Patch extends BaseClass {
	@Override
	public String getMessage() {
		return message + "_Patch";
	}
}
