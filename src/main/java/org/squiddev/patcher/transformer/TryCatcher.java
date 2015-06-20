package org.squiddev.patcher.transformer;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.*;

/**
 * Wraps every method in try catch
 */
public class TryCatcher implements IPatcher {
	protected final String name;
	protected final boolean start;

	public TryCatcher(String name) {
		this(name, false);
	}

	public TryCatcher(String name, boolean start) {
		this.name = name;
		this.start = start;
	}

	@Override
	public boolean matches(String className) {
		return start ? className.startsWith(name) : className.equals(name);
	}

	@Override
	public ClassVisitor patch(String className, ClassVisitor delegate) throws Exception {
		return new ClassVisitor(ASM5, delegate) {
			@Override
			public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
				final Label start = new Label(), end = new Label(), handler = new Label(), exit = new Label();
				return new MethodVisitor(ASM5, super.visitMethod(access, name, desc, signature, exceptions)) {
					@Override
					public void visitCode() {
						super.visitCode();
						visitTryCatchBlock(start, end, handler, "java/lang/RuntimeException");
						visitLabel(start);
					}

					@Override
					public void visitMaxs(int maxStack, int maxLocals) {
						visitLabel(end);
						visitFrame(F_FULL, 0, new Object[0], 1, new Object[]{"java/lang/Throwable"});
						visitInsn(ATHROW);

						visitLabel(handler);
						visitFrame(F_SAME1, 0, new Object[0], 1, new Object[]{"java/lang/RuntimeException"});
						visitInsn(DUP);
						visitMethodInsn(INVOKEVIRTUAL, "java/lang/RuntimeException", "printStackTrace", "()V", false);
						visitInsn(ATHROW);
						visitLabel(exit);

						super.visitMaxs(Math.max(maxStack, 2), maxLocals);
					}
				};
			}
		};
	}
}
