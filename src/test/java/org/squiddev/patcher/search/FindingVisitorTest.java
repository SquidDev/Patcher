package org.squiddev.patcher.search;

import org.junit.Assert;
import org.junit.Test;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.*;
import org.squiddev.patcher.visitors.FindingVisitor;

import static org.objectweb.asm.Opcodes.*;

public class FindingVisitorTest {
	int called = 0;

	@Test
	public void testFinds() {
		ClassVisitor visitor = new FindingVisitor(null,
			new VarInsnNode(ALOAD, -1),
			new FieldInsnNode(GETFIELD, "my/class", "foo", "Lbar;")
		) {
			@Override
			public void handle(InsnList nodes, MethodVisitor visitor) {
				called++;
			}
		};

		MethodVisitor mv = visitor.visitMethod(ACC_PUBLIC, "foo", "()V", null, null);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, "my/class", "foo", "Lbar;");
		mv.visitVarInsn(ALOAD, 1);
		mv.visitFieldInsn(GETFIELD, "my/class", "foo", "Lbar;");

		Assert.assertEquals(2, called);
	}

	@Test
	public void testLabels() {
		final Label label = new Label();

		ClassVisitor visitor = new FindingVisitor(null,
			new JumpInsnNode(GOTO, null),
			new FieldInsnNode(GETFIELD, "my/class", "foo", "Lbar;"),
			new LabelNode(null)
		) {
			@Override
			public void handle(InsnList nodes, MethodVisitor visitor) {
				called++;
				Assert.assertEquals(label, ((JumpInsnNode) nodes.get(0)).label.getLabel());
				Assert.assertEquals(label, ((LabelNode) nodes.get(2)).getLabel());
			}
		}.mustFind();

		MethodVisitor mv = visitor.visitMethod(ACC_PUBLIC, "foo", "()V", null, null);
		mv.visitJumpInsn(GOTO, label);
		mv.visitFieldInsn(GETFIELD, "my/class", "foo", "Lbar;");
		mv.visitLabel(label);

		mv.visitEnd();
		visitor.visitEnd();
	}

	@Test
	public void testLabelNoMatch() {
		ClassVisitor visitor = new FindingVisitor(null,
			new FieldInsnNode(GETFIELD, "my/class", "foo", "Lbar;")
		) {
			@Override
			public void handle(InsnList nodes, MethodVisitor visitor) {
				called++;
			}
		};

		MethodVisitor mv = visitor.visitMethod(ACC_PUBLIC, "foo", "()V", null, null);
		mv.visitJumpInsn(GOTO, new Label());
		mv.visitEnd();
		visitor.visitEnd();

		Assert.assertEquals(0, called);
	}


	@Test
	public void testErrors() {
		ClassVisitor visitor = new FindingVisitor(null,
			new VarInsnNode(ALOAD, -1),
			new FieldInsnNode(GETFIELD, "my/class", "foo", "Lbar;")
		) {
			@Override
			public void handle(InsnList nodes, MethodVisitor visitor) {
			}
		}.mustFind();

		try {
			visitor.visitEnd();
			Assert.fail("Expected exception");
		} catch (IllegalStateException ignored) {
		}
	}

	@Test
	public void testMethod() {
		ClassVisitor visitor = new FindingVisitor(new ClassNode(),
			new VarInsnNode(ALOAD, -1),
			new FieldInsnNode(GETFIELD, "my/class", "foo", "Lbar;")
		) {
			@Override
			public void handle(InsnList nodes, MethodVisitor visitor) {
				called++;
			}
		}.onMethod("bar");

		MethodVisitor mv = visitor.visitMethod(ACC_PUBLIC, "foo", "()V", null, null);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, "my/class", "foo", "Lbar;");

		Assert.assertEquals(0, called);

		mv = visitor.visitMethod(ACC_PUBLIC, "bar", "()V", null, null);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, "my/class", "foo", "Lbar;");

		Assert.assertEquals(1, called);
	}

	@Test
	public void testFindsOnce() {
		ClassVisitor visitor = new FindingVisitor(null,
			new VarInsnNode(ALOAD, -1),
			new FieldInsnNode(GETFIELD, "my/class", "foo", "Lbar;")
		) {
			@Override
			public void handle(InsnList nodes, MethodVisitor visitor) {
				called++;
			}
		}.once();

		MethodVisitor mv = visitor.visitMethod(ACC_PUBLIC, "foo", "()V", null, null);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, "my/class", "foo", "Lbar;");
		mv.visitVarInsn(ALOAD, 1);
		mv.visitFieldInsn(GETFIELD, "my/class", "foo", "Lbar;");

		Assert.assertEquals(1, called);
	}
}
