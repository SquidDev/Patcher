package org.squiddev.patcher.search;

import org.junit.Test;
import org.objectweb.asm.tree.*;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.objectweb.asm.Opcodes.*;
import static org.squiddev.patcher.search.Matcher.areEqual;

/**
 * Tests for {@link Matcher}
 */
public class MatcherTest {
	@Test
	public void testVarInsnNode() {
		assertTrue(areEqual(new VarInsnNode(ALOAD, 0), new VarInsnNode(ALOAD, 0)));
		assertTrue(areEqual(new VarInsnNode(ALOAD, 12), new VarInsnNode(ALOAD, -1)));

		assertFalse(areEqual(new VarInsnNode(ASTORE, 0), new VarInsnNode(ALOAD, 0)));
		assertFalse(areEqual(new VarInsnNode(ASTORE, 12), new VarInsnNode(ALOAD, -1)));
	}

	@Test
	public void testMethodInsnNode() throws Exception {
		assertTrue(areEqual(new MethodInsnNode(INVOKEVIRTUAL, "a", "b", "c", false), new MethodInsnNode(INVOKEVIRTUAL, "a", "b", "c", false)));

		assertTrue(areEqual(new MethodInsnNode(INVOKEVIRTUAL, "a", "b", "c", false), new MethodInsnNode(INVOKEVIRTUAL, null, "b", "c", false)));
		assertTrue(areEqual(new MethodInsnNode(INVOKEVIRTUAL, "a", "b", "c", false), new MethodInsnNode(INVOKEVIRTUAL, "a", null, "c", false)));
		assertTrue(areEqual(new MethodInsnNode(INVOKEVIRTUAL, "a", "b", "c", false), new MethodInsnNode(INVOKEVIRTUAL, "a", "b", null, false)));

		assertFalse(areEqual(new MethodInsnNode(INVOKEVIRTUAL, "a", "b", "c", false), new MethodInsnNode(INVOKESTATIC, "a", "b", "c", false)));
		assertFalse(areEqual(new MethodInsnNode(INVOKEVIRTUAL, "a", "b", "c", false), new MethodInsnNode(INVOKEVIRTUAL, "foo", "b", "c", false)));
		assertFalse(areEqual(new MethodInsnNode(INVOKEVIRTUAL, "a", "b", "c", false), new MethodInsnNode(INVOKEVIRTUAL, "a", "foo", "c", false)));
		assertFalse(areEqual(new MethodInsnNode(INVOKEVIRTUAL, "a", "b", "c", false), new MethodInsnNode(INVOKEVIRTUAL, "a", "b", "foo", false)));
	}

	@Test
	public void testFieldInsnNode() throws Exception {
		assertTrue(areEqual(new FieldInsnNode(GETFIELD, "a", "b", "c"), new FieldInsnNode(GETFIELD, "a", "b", "c")));

		assertTrue(areEqual(new FieldInsnNode(GETFIELD, "a", "b", "c"), new FieldInsnNode(GETFIELD, null, "b", "c")));
		assertTrue(areEqual(new FieldInsnNode(GETFIELD, "a", "b", "c"), new FieldInsnNode(GETFIELD, "a", null, "c")));
		assertTrue(areEqual(new FieldInsnNode(GETFIELD, "a", "b", "c"), new FieldInsnNode(GETFIELD, "a", "b", null)));

		assertFalse(areEqual(new FieldInsnNode(GETFIELD, "a", "b", "c"), new FieldInsnNode(PUTFIELD, "a", "b", "c")));
		assertFalse(areEqual(new FieldInsnNode(GETFIELD, "a", "b", "c"), new FieldInsnNode(GETFIELD, "foo", "b", "c")));
		assertFalse(areEqual(new FieldInsnNode(GETFIELD, "a", "b", "c"), new FieldInsnNode(GETFIELD, "a", "foo", "c")));
		assertFalse(areEqual(new FieldInsnNode(GETFIELD, "a", "b", "c"), new FieldInsnNode(GETFIELD, "a", "b", "foo")));
	}

	@Test
	public void testLdcInsnNode() throws Exception {
		assertTrue(areEqual(new LdcInsnNode("foo"), new LdcInsnNode("foo")));
		assertTrue(areEqual(new LdcInsnNode(12), new LdcInsnNode(12)));
		assertTrue(areEqual(new LdcInsnNode(12.0), new LdcInsnNode(12.0)));

		assertTrue(areEqual(new LdcInsnNode("foo"), new LdcInsnNode(null)));
		assertTrue(areEqual(new LdcInsnNode(12), new LdcInsnNode(null)));
		assertTrue(areEqual(new LdcInsnNode(12.0), new LdcInsnNode(null)));

		assertFalse(areEqual(new LdcInsnNode("foo"), new LdcInsnNode("bar")));
		assertFalse(areEqual(new LdcInsnNode(12), new LdcInsnNode(12.0)));
	}

	@Test
	public void testTypeInsnNode() throws Exception {
		assertTrue(areEqual(new TypeInsnNode(CHECKCAST, "foo"), new TypeInsnNode(CHECKCAST, "foo")));
		assertTrue(areEqual(new TypeInsnNode(CHECKCAST, "foo"), new TypeInsnNode(CHECKCAST, null)));

		assertFalse(areEqual(new TypeInsnNode(CHECKCAST, "foo"), new TypeInsnNode(CHECKCAST, "bar")));
		assertFalse(areEqual(new TypeInsnNode(CHECKCAST, "foo"), new TypeInsnNode(NEW, "bar")));
	}

	@Test
	public void testIntInsnNode() throws Exception {
		assertTrue(areEqual(new IntInsnNode(BIPUSH, 0), new IntInsnNode(BIPUSH, 0)));

		assertFalse(areEqual(new IntInsnNode(BIPUSH, 12), new IntInsnNode(BIPUSH, 0)));
		assertFalse(areEqual(new IntInsnNode(BIPUSH, 0), new IntInsnNode(NEWARRAY, 0)));
	}
}
