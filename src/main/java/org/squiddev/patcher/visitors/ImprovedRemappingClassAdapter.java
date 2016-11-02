package org.squiddev.patcher.visitors;

import org.objectweb.asm.*;
import org.objectweb.asm.commons.Remapper;
import org.objectweb.asm.commons.RemappingAnnotationAdapter;
import org.objectweb.asm.commons.RemappingClassAdapter;

/**
 * A remapping class adapter that allows visiting
 */
public class ImprovedRemappingClassAdapter extends RemappingClassAdapter {
	public ImprovedRemappingClassAdapter(ClassVisitor classVisitor, Remapper remapper) {
		super(classVisitor, remapper);
	}

	@Override
	protected MethodVisitor createRemappingMethodAdapter(int access, String newDesc, MethodVisitor mv) {
		return new RemappingMethodAdapter(Opcodes.ASM5, mv, remapper);
	}

	public static class RemappingMethodAdapter extends MethodVisitor {
		protected final Remapper remapper;

		protected RemappingMethodAdapter(final int api, final MethodVisitor mv, final Remapper remapper) {
			super(api, mv);
			this.remapper = remapper;
		}

		@Override
		public AnnotationVisitor visitAnnotationDefault() {
			AnnotationVisitor av = super.visitAnnotationDefault();
			return av == null ? null : new RemappingAnnotationAdapter(av, remapper);
		}

		@Override
		public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
			AnnotationVisitor av = super.visitAnnotation(remapper.mapDesc(desc),
				visible);
			return av == null ? null : new RemappingAnnotationAdapter(av, remapper);
		}

		@Override
		public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
			AnnotationVisitor av = super.visitTypeAnnotation(typeRef, typePath, remapper.mapDesc(desc), visible);
			return av == null ? null : new RemappingAnnotationAdapter(av, remapper);
		}

		@Override
		public AnnotationVisitor visitParameterAnnotation(int parameter, String desc, boolean visible) {
			AnnotationVisitor av = super.visitParameterAnnotation(parameter, remapper.mapDesc(desc), visible);
			return av == null ? null : new RemappingAnnotationAdapter(av, remapper);
		}

		@Override
		public void visitFrame(int type, int nLocal, Object[] local, int nStack,
		                       Object[] stack) {
			super.visitFrame(type, nLocal, remapEntries(nLocal, local), nStack,
				remapEntries(nStack, stack));
		}

		private Object[] remapEntries(int n, Object[] entries) {
			for (int i = 0; i < n; i++) {
				if (entries[i] instanceof String) {
					Object[] newEntries = new Object[n];
					if (i > 0) {
						System.arraycopy(entries, 0, newEntries, 0, i);
					}
					do {
						Object t = entries[i];
						newEntries[i++] = t instanceof String ? remapper
							.mapType((String) t) : t;
					} while (i < n);
					return newEntries;
				}
			}
			return entries;
		}

		@Override
		public void visitFieldInsn(int opcode, String owner, String name, String desc) {
			super.visitFieldInsn(opcode, remapper.mapType(owner),
				remapper.mapFieldName(owner, name, desc),
				remapper.mapDesc(desc)
			);
		}

		@SuppressWarnings("deprecation")
		@Deprecated
		@Override
		public void visitMethodInsn(final int opcode, final String owner, final String name, final String desc) {
			if (api >= Opcodes.ASM5) {
				super.visitMethodInsn(opcode, owner, name, desc);
				return;
			}
			doVisitMethodInsn(opcode, owner, name, desc, opcode == Opcodes.INVOKEINTERFACE);
		}

		@Override
		public void visitMethodInsn(final int opcode, final String owner, final String name, final String desc, final boolean itf) {
			if (api < Opcodes.ASM5) {
				super.visitMethodInsn(opcode, owner, name, desc, itf);
				return;
			}
			doVisitMethodInsn(opcode, owner, name, desc, itf);
		}

		private void doVisitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
			// Calling super.visitMethodInsn requires to call the correct version
			// depending on this.api (otherwise infinite loops can occur). To
			// simplify and to make it easier to automatically remove the backward
			// compatibility code, we inline the code of the overridden method here.
			// IMPORTANT: THIS ASSUMES THAT visitMethodInsn IS NOT OVERRIDDEN IN
			// LocalVariableSorter.
			if (mv != null) {
				mv.visitMethodInsn(opcode, remapper.mapType(owner),
					remapper.mapMethodName(owner, name, desc),
					remapper.mapMethodDesc(desc), itf
				);
			}
		}

		@Override
		public void visitInvokeDynamicInsn(String name, String desc, Handle bsm, Object... bsmArgs) {
			for (int i = 0; i < bsmArgs.length; i++) {
				bsmArgs[i] = remapper.mapValue(bsmArgs[i]);
			}
			super.visitInvokeDynamicInsn(
				remapper.mapInvokeDynamicMethodName(name, desc),
				remapper.mapMethodDesc(desc), (Handle) remapper.mapValue(bsm),
				bsmArgs
			);
		}

		@Override
		public void visitTypeInsn(int opcode, String type) {
			super.visitTypeInsn(opcode, remapper.mapType(type));
		}

		@Override
		public void visitLdcInsn(Object cst) {
			super.visitLdcInsn(remapper.mapValue(cst));
		}

		@Override
		public void visitMultiANewArrayInsn(String desc, int dims) {
			super.visitMultiANewArrayInsn(remapper.mapDesc(desc), dims);
		}

		@Override
		public AnnotationVisitor visitInsnAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
			AnnotationVisitor av = super.visitInsnAnnotation(typeRef, typePath, remapper.mapDesc(desc), visible);
			return av == null ? null : new RemappingAnnotationAdapter(av, remapper);
		}

		@Override
		public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
			super.visitTryCatchBlock(start, end, handler, type == null ? null : remapper.mapType(type));
		}

		@Override
		public AnnotationVisitor visitTryCatchAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
			AnnotationVisitor av = super.visitTryCatchAnnotation(typeRef, typePath, remapper.mapDesc(desc), visible);
			return av == null ? null : new RemappingAnnotationAdapter(av, remapper);
		}

		@Override
		public void visitLocalVariable(String name, String desc, String signature,
		                               Label start, Label end, int index) {
			super.visitLocalVariable(name, remapper.mapDesc(desc),
				remapper.mapSignature(signature, true), start, end, index);
		}

		@Override
		public AnnotationVisitor visitLocalVariableAnnotation(int typeRef, TypePath typePath, Label[] start, Label[] end, int[] index, String desc, boolean visible) {
			AnnotationVisitor av = super.visitLocalVariableAnnotation(typeRef, typePath, start, end, index, remapper.mapDesc(desc), visible);
			return av == null ? null : new RemappingAnnotationAdapter(av, remapper);
		}
	}

}
