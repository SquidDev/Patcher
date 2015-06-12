package org.squiddev.patcher.patch;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.ACC_FINAL;
import static org.objectweb.asm.Opcodes.ASM5;

/**
 * Strips final from methods
 */
public class StripFinal implements IPatcher {
	public final String className;

	public StripFinal(String className) {
		this.className = className;
	}

	@Override
	public boolean matches(String className) {
		return className.equals(this.className);
	}

	@Override
	public ClassVisitor patch(String className, ClassVisitor delegate) throws Exception {
		return new ClassVisitor(ASM5, delegate) {
			@Override
			public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
				return super.visitMethod(access & ~ACC_FINAL, name, desc, signature, exceptions);
			}
		};
	}
}
