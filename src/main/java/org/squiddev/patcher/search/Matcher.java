package org.squiddev.patcher.search;

import org.objectweb.asm.tree.*;

public class Matcher {
	/**
	 * Compare two {@link VarInsnNode} nodes.
	 *
	 * @param target The node to match
	 * @param match  The node we are comparing to. If {@link VarInsnNode#var} is {@code -1} then any value is allowed
	 * @return If the nodes match
	 */
	public static boolean instructionsEqual(VarInsnNode target, VarInsnNode match) {
		return match.var == -1 || target.var == match.var;
	}

	/**
	 * Compare two {@link MethodInsnNode} nodes.
	 *
	 * @param target The node to match
	 * @param match  The node we are comparing to.
	 * @return If the nodes match
	 */
	public static boolean instructionsEqual(MethodInsnNode target, MethodInsnNode match) {
		return target.owner.equals(match.owner) && target.name.equals(match.name) && target.desc.equals(match.desc);
	}

	/**
	 * Compare two {@link FieldInsnNode} nodes.
	 *
	 * @param target The node to match
	 * @param match  The node we are comparing to.
	 * @return If the nodes match
	 */
	public static boolean instructionsEqual(FieldInsnNode target, FieldInsnNode match) {
		return target.owner.equals(match.owner) && target.name.equals(match.name) && target.desc.equals(match.desc);
	}

	/**
	 * Compare two {@link LdcInsnNode} nodes.
	 *
	 * @param target The node to match
	 * @param match  The node we are comparing to. If {@link LdcInsnNode#cst} is {@code null} then any value is allowed
	 * @return If the nodes match
	 */
	public static boolean instructionsEqual(LdcInsnNode target, LdcInsnNode match) {
		return match.cst == null || target.cst.equals(match.cst);
	}

	/**
	 * Compare two {@link TypeInsnNode} nodes.
	 *
	 * @param target The node to match
	 * @param match  The node we are comparing to. If {@link TypeInsnNode#desc} is {@code *} then any value is allowed
	 * @return If the nodes match
	 */
	public static boolean instructionsEqual(TypeInsnNode target, TypeInsnNode match) {
		return match.desc.equals("*") || target.desc.equals(match.desc);
	}

	/**
	 * Compare two {@link IincInsnNode} nodes.
	 *
	 * @param target The node to match
	 * @param match  The node we are comparing to.
	 * @return If the nodes match
	 */
	public static boolean instructionsEqual(IincInsnNode target, IincInsnNode match) {
		return target.var == match.var && target.incr == match.incr;
	}

	/**
	 * Compare two {@link IntInsnNode} nodes.
	 *
	 * @param target The node to match
	 * @param match  The node we are comparing to. If {@link IntInsnNode#operand} is {@code -1} then any value is allowed
	 * @return If the nodes match
	 */
	public static boolean instructionsEqual(IntInsnNode target, IntInsnNode match) {
		return match.operand == -1 || target.operand == match.operand;
	}

	/**
	 * Compare two {@link AbstractInsnNode}.
	 *
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
				return instructionsEqual((VarInsnNode) target, (VarInsnNode) match);
			case AbstractInsnNode.TYPE_INSN:
				return instructionsEqual((TypeInsnNode) target, (TypeInsnNode) match);
			case AbstractInsnNode.FIELD_INSN:
				return instructionsEqual((FieldInsnNode) target, (FieldInsnNode) match);
			case AbstractInsnNode.METHOD_INSN:
				return instructionsEqual((MethodInsnNode) target, (MethodInsnNode) match);
			case AbstractInsnNode.LDC_INSN:
				return instructionsEqual((LdcInsnNode) target, (LdcInsnNode) match);
			case AbstractInsnNode.IINC_INSN:
				return instructionsEqual((IincInsnNode) target, (IincInsnNode) match);
			case AbstractInsnNode.INT_INSN:
				return instructionsEqual((IntInsnNode) target, (IntInsnNode) match);
			default:
				return target.equals(match);
		}
	}
}
