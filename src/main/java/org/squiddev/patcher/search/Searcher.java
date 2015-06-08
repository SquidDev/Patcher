package org.squiddev.patcher.search;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.squiddev.patcher.InsnListSection;

import java.util.LinkedList;
import java.util.List;

/**
 * Used to search through nodes
 */
public class Searcher {
	/**
	 * Check if this instruction is considered 'important'.
	 * Unimportant instructions are:
	 * - {@link AbstractInsnNode#LINE}
	 * - {@link AbstractInsnNode#FRAME}
	 * - {@link AbstractInsnNode#LABEL}
	 *
	 * Though other 'metadata' style instructions may be added.
	 *
	 * @param insn The instruction to check
	 * @return If this instruction is important
	 */
	public static boolean isImportant(AbstractInsnNode insn) {
		switch (insn.getType()) {
			case AbstractInsnNode.LINE:
			case AbstractInsnNode.FRAME:
			case AbstractInsnNode.LABEL:
				return false;
			default:
				return true;
		}
	}

	/**
	 * Find occurrences of series of instructions
	 *
	 * @param haystack The nodes to search
	 * @param needle   The nodes to find
	 * @return List of matching sections
	 */
	public static List<InsnListSection> find(InsnListSection haystack, InsnListSection needle) {
		LinkedList<InsnListSection> list = new LinkedList<InsnListSection>();
		for (int start = 0; start <= haystack.size() - needle.size(); start++) {
			InsnListSection section = matches(haystack.drop(start), needle);
			if (section != null) {
				list.add(section);
				start = section.getEnd() - 1;
			}
		}

		return list;
	}

	/**
	 * Find occurrences of series of instructions
	 *
	 * @param haystack The nodes to search
	 * @param needle   The nodes to find
	 * @return List of matching sections
	 */
	public static List<InsnListSection> find(InsnList haystack, InsnListSection needle) {
		return find(new InsnListSection(haystack), needle);
	}

	/**
	 * Validate that two sections are equal
	 *
	 * @param haystack The nodes to search
	 * @param needle   The nodes to check against
	 * @return If the sections are equal
	 */
	public static InsnListSection matches(InsnListSection haystack, InsnListSection needle) {
		int h = 0, n = 0;
		for (; h < haystack.size() && n < needle.size(); h++) {
			AbstractInsnNode instruction = haystack.get(h);
			if (!isImportant(instruction)) continue;
			if (!Matcher.areEqual(haystack.get(h), needle.get(n))) return null;
			n++;
		}
		if (n != needle.size()) return null;

		return haystack.take(h);
	}

	/**
	 * Find a needle once.
	 *
	 * @param haystack The nodes to search
	 * @param needle   The nodes to find
	 * @return The matching section
	 * @throws RuntimeException If the needle cannot be found
	 */
	public static InsnListSection findOnce(InsnListSection haystack, InsnListSection needle) {
		List<InsnListSection> list = find(haystack, needle);
		if (list.size() != 1) {
			throw new RuntimeException("Needle found " + list.size() + " times in Haystack:\n" + haystack + "\n\n" + needle);
		}

		return list.get(0);
	}

	/**
	 * Find a needle once.
	 *
	 * @param haystack The nodes to search
	 * @param needle   The nodes to find
	 * @return The matching section
	 * @throws RuntimeException If the needle cannot be found
	 */
	public static InsnListSection findOnce(InsnList haystack, InsnListSection needle) {
		return findOnce(new InsnListSection(haystack), needle);
	}
}
