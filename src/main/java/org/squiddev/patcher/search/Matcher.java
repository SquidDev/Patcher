package org.squiddev.patcher.search;

import org.objectweb.asm.tree.*;

public class Matcher {
	/**
	 * Compare a {@link VarInsnNode} instruction.
	 *
	 * @param var   The index of the local variable
	 * @param match The node we are comparing to. If {@link VarInsnNode#var} is {@code -1} then any value is allowed
	 * @return If the nodes match
	 */
	public static boolean varInsnEqual(int var, VarInsnNode match) {
		return match.var == -1 || var == match.var;
	}

	/**
	 * Compare two {@link VarInsnNode} nodes.
	 *
	 * @param target The node to match
	 * @param match  The node we are comparing to. If {@link VarInsnNode#var} is {@code -1} then any value is allowed
	 * @return If the nodes match
	 */
	public static boolean varInsnEqual(VarInsnNode target, VarInsnNode match) {
		return varInsnEqual(target.var, match);
	}

	/**
	 * Compare a {@link MethodInsnNode} instruction.
	 *
	 * @param owner Type that owns the method
	 * @param name  Name of the method
	 * @param desc  Method signature
	 * @param match The node we are comparing to.
	 * @return If the nodes match
	 */
	public static boolean methodInsnEqual(String owner, String name, String desc, MethodInsnNode match) {
		return owner.equals(match.owner) && name.equals(match.name) && desc.equals(match.desc);
	}

	/**
	 * Compare two {@link MethodInsnNode} nodes.
	 *
	 * @param target The node to match
	 * @param match  The node we are comparing to.
	 * @return If the nodes match
	 */
	public static boolean methodInsnEqual(MethodInsnNode target, MethodInsnNode match) {
		return methodInsnEqual(target.owner, target.name, target.desc, match);
	}

	/**
	 * Compare a {@link FieldInsnNode} instruction.
	 *
	 * @param owner Type that owns the field
	 * @param name  Name of the field
	 * @param desc  Field signature
	 * @param match The node we are comparing to.
	 * @return If the nodes match
	 */
	public static boolean fieldInsnEqual(String owner, String name, String desc, FieldInsnNode match) {
		return owner.equals(match.owner) && name.equals(match.name) && desc.equals(match.desc);
	}

	/**
	 * Compare two {@link FieldInsnNode} nodes.
	 *
	 * @param target The node to match
	 * @param match  The node we are comparing to.
	 * @return If the nodes match
	 */
	public static boolean fieldInsnEqual(FieldInsnNode target, FieldInsnNode match) {
		return fieldInsnEqual(target.owner, target.name, target.desc, match);
	}

	/**
	 * Compare a {@link LdcInsnNode} instruction.
	 *
	 * @param cst   The constant being loaded
	 * @param match The node we are comparing to. If {@link LdcInsnNode#cst} is {@code null} then any value is allowed
	 * @return If the nodes match
	 */
	public static boolean ldcInsnEqual(Object cst, LdcInsnNode match) {
		return match.cst == null || cst.equals(match.cst);
	}

	/**
	 * Compare two {@link LdcInsnNode} nodes.
	 *
	 * @param target The node to match
	 * @param match  The node we are comparing to. If {@link LdcInsnNode#cst} is {@code null} then any value is allowed
	 * @return If the nodes match
	 */
	public static boolean ldcInsnEqual(LdcInsnNode target, LdcInsnNode match) {
		return ldcInsnEqual(target.cst, match);
	}

	/**
	 * Compare a {@link TypeInsnNode} instruction.
	 *
	 * @param desc  The node to match
	 * @param match The node we are comparing to. If {@link TypeInsnNode#desc} is {@code *} then any value is allowed
	 * @return If the nodes match
	 */
	public static boolean typeInsnEqual(String desc, TypeInsnNode match) {
		return match.desc.equals("*") || desc.equals(match.desc);
	}

	/**
	 * Compare two {@link TypeInsnNode} nodes.
	 *
	 * @param target The node to match
	 * @param match  The node we are comparing to. If {@link TypeInsnNode#desc} is {@code *} then any value is allowed
	 * @return If the nodes match
	 */
	public static boolean typeInsnEqual(TypeInsnNode target, TypeInsnNode match) {
		return typeInsnEqual(target.desc, match);
	}

	/**
	 * Compare a {@link IincInsnNode} instruction.
	 *
	 * @param var   The local variable
	 * @param incr  Amount to increment the variable by
	 * @param match The node we are comparing to.
	 * @return If the nodes match
	 */
	public static boolean iincInsnEqual(int var, int incr, IincInsnNode match) {
		return var == match.var && incr == match.incr;
	}

	/**
	 * Compare two {@link IincInsnNode} nodes.
	 *
	 * @param target The node to match
	 * @param match  The node we are comparing to.
	 * @return If the nodes match
	 */
	public static boolean iincInsnEqual(IincInsnNode target, IincInsnNode match) {
		return iincInsnEqual(target.var, target.incr, match);
	}

	/**
	 * Compare two {@link IntInsnNode} nodes.
	 *
	 * @param operand The operand to act on
	 * @param match   The node we are comparing to. If {@link IntInsnNode#operand} is {@code -1} then any value is allowed
	 * @return If the nodes match
	 */
	public static boolean intInsnEqual(int operand, IntInsnNode match) {
		return match.operand == -1 || operand == match.operand;
	}

	/**
	 * Compare two {@link IntInsnNode} nodes.
	 *
	 * @param target The node to match
	 * @param match  The node we are comparing to. If {@link IntInsnNode#operand} is {@code -1} then any value is allowed
	 * @return If the nodes match
	 */
	public static boolean intInsnEqual(IntInsnNode target, IntInsnNode match) {
		return intInsnEqual(target.operand, match);
	}

	/**
	 * Compare two {@link AbstractInsnNode}.
	 * <p/>
	 * This chooses the correct matcher and compares them. Read the type specific
	 * documentation for custom values that can be passed.
	 *
	 * @param target The node to match
	 * @param match  The node we are comparing to
	 * @return If the nodes match
	 */
	public static boolean areEqual(AbstractInsnNode target, AbstractInsnNode match) {
		if (target.getOpcode() != match.getOpcode()) return false;

		switch (match.getType()) {
			case AbstractInsnNode.VAR_INSN:
				return varInsnEqual((VarInsnNode) target, (VarInsnNode) match);
			case AbstractInsnNode.TYPE_INSN:
				return typeInsnEqual((TypeInsnNode) target, (TypeInsnNode) match);
			case AbstractInsnNode.FIELD_INSN:
				return fieldInsnEqual((FieldInsnNode) target, (FieldInsnNode) match);
			case AbstractInsnNode.METHOD_INSN:
				return methodInsnEqual((MethodInsnNode) target, (MethodInsnNode) match);
			case AbstractInsnNode.LDC_INSN:
				return ldcInsnEqual((LdcInsnNode) target, (LdcInsnNode) match);
			case AbstractInsnNode.IINC_INSN:
				return iincInsnEqual((IincInsnNode) target, (IincInsnNode) match);
			case AbstractInsnNode.INT_INSN:
				return intInsnEqual((IntInsnNode) target, (IntInsnNode) match);
			default:
				return target.equals(match);
		}
	}
}
