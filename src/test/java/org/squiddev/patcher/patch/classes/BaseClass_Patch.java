package org.squiddev.patcher.patch.classes;

public class BaseClass_Patch extends BaseClass {
	@Override
	public String getMessage() {
		return message + "_Patch";
	}
}
