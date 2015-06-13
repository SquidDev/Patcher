package org.squiddev.patcher.visitors;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
import org.squiddev.patcher.search.Matcher;

/**
 * A basic visitor that visits nodes
 */
public abstract class FindingVisitor extends ClassVisitor {
	protected final AbstractInsnNode[] nodes;

	public FindingVisitor(ClassVisitor classVisitor, AbstractInsnNode... nodes) {
		super(Opcodes.ASM5, classVisitor);

		this.nodes = nodes;
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		return new FindingMethodVisitor(super.visitMethod(access, name, desc, signature, exceptions));
	}

	public abstract void handle(InsnList nodes, MethodVisitor visitor);

	protected class FindingMethodVisitor extends MethodVisitor {
		protected InsnList builder = new InsnList();
		protected int index = 0;

		public FindingMethodVisitor(MethodVisitor mv) {
			super(Opcodes.ASM5, mv);
		}

		protected void clearCache() {
			builder.accept(mv);
			builder.clear();
			index = 0;
		}

		protected void add(AbstractInsnNode node) {
			builder.add(node);
			index++;

			if (index == nodes.length) {
				handle(builder, mv);
				builder.clear();
				index = 0;
			}
		}

		@Override
		public void visitInsn(int opcode) {
			AbstractInsnNode node = nodes[index];
			if (node.getOpcode() != opcode) {
				clearCache();
				super.visitInsn(opcode);
			} else {
				add(new InsnNode(opcode));
			}
		}


		@Override
		public void visitIntInsn(int opcode, int operand) {
			AbstractInsnNode node = nodes[index];
			if (node.getOpcode() != opcode || !Matcher.intInsnEqual(operand, (IntInsnNode) node)) {
				clearCache();
				super.visitIntInsn(opcode, operand);
			} else {
				add(new IntInsnNode(opcode, operand));
			}
		}

		@Override
		public void visitVarInsn(int opcode, int var) {
			AbstractInsnNode node = nodes[index];
			if (node.getOpcode() != opcode || !Matcher.varInsnEqual(var, (VarInsnNode) node)) {
				clearCache();
				super.visitVarInsn(opcode, var);
			} else {
				add(new VarInsnNode(opcode, var));
			}
		}

		@Override
		public void visitTypeInsn(int opcode, String type) {
			AbstractInsnNode node = nodes[index];
			if (node.getOpcode() != opcode || !Matcher.typeInsnEqual(type, (TypeInsnNode) node)) {
				clearCache();
				super.visitTypeInsn(opcode, type);
			} else {
				add(new TypeInsnNode(opcode, type));
			}
		}

		@Override
		public void visitFieldInsn(int opcode, String owner, String name, String desc) {
			AbstractInsnNode node = nodes[index];
			if (node.getOpcode() != opcode || !Matcher.fieldInsnEqual(owner, name, desc, (FieldInsnNode) node)) {
				clearCache();
				super.visitFieldInsn(opcode, owner, name, desc);
			} else {
				add(new FieldInsnNode(opcode, owner, name, desc));
			}
		}

		@Override
		public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
			AbstractInsnNode node = nodes[index];
			if (node.getOpcode() != opcode || !Matcher.methodInsnEqual(owner, name, desc, (MethodInsnNode) node)) {
				clearCache();
				super.visitMethodInsn(opcode, owner, name, desc, itf);
			} else {
				add(new MethodInsnNode(opcode, owner, name, desc, itf));
			}
		}

		@Override
		public void visitLdcInsn(Object cst) {
			AbstractInsnNode node = nodes[index];
			if (node.getOpcode() != Opcodes.LDC || !Matcher.ldcInsnEqual(cst, (LdcInsnNode) node)) {
				clearCache();
				super.visitLdcInsn(cst);
			} else {
				add(new LdcInsnNode(cst));
			}
		}

		@Override
		public void visitIincInsn(int var, int increment) {
			AbstractInsnNode node = nodes[index];
			if (node.getOpcode() != Opcodes.IINC || !Matcher.iincInsnEqual(var, increment, (IincInsnNode) node)) {
				clearCache();
				super.visitIincInsn(var, increment);
			} else {
				add(new IincInsnNode(var, increment));
			}
			super.visitIincInsn(var, increment);
		}

		@Override
		public void visitInvokeDynamicInsn(String name, String desc, Handle bsm, Object... bsmArgs) {
			clearCache();
			super.visitInvokeDynamicInsn(name, desc, bsm, bsmArgs);
		}

		@Override
		public void visitJumpInsn(int opcode, Label label) {
			clearCache();
			super.visitJumpInsn(opcode, label);
		}

		@Override
		public void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels) {
			clearCache();
			super.visitTableSwitchInsn(min, max, dflt, labels);
		}

		@Override
		public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
			clearCache();
			super.visitLookupSwitchInsn(dflt, keys, labels);
		}

		@Override
		public void visitMultiANewArrayInsn(String desc, int dims) {
			clearCache();
			super.visitMultiANewArrayInsn(desc, dims);
		}

		@Override
		public void visitEnd() {
			clearCache();
			super.visitEnd();
		}
	}
}
