package org.squiddev.patcher.visitors;

import org.objectweb.asm.*;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Modifier;
import java.util.*;

import static org.objectweb.asm.Opcodes.ACC_ABSTRACT;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.squiddev.patcher.visitors.AnnotationHelper.getAnnotation;
import static org.squiddev.patcher.visitors.AnnotationHelper.getAnnotationValue;

/**
 * Merge two classes together
 */
public class MergeVisitor extends ClassVisitor {
	private final ClassNode node;

	private final Set<String> visited = new HashSet<String>();

	private final Map<String, Integer> access = new HashMap<String, Integer>();

	private final Map<String, String> memberNames = new HashMap<String, String>();
	private final Map<String, String> blocks = new HashMap<String, String>();

	private RenameContext context;

	protected boolean writingOverride = false;
	protected String superClass = null;

	/**
	 * Merge two classes together.
	 *
	 * @param cv      The visitor to write to
	 * @param node    The node that holds override methods
	 * @param context Mapper for override classes to new ones
	 */
	public MergeVisitor(ClassVisitor cv, ClassNode node, RenameContext context) {
		super(Opcodes.ASM5);
		this.cv = new ClassRemapper(cv, context);
		this.node = node;
		this.context = context;
		populateRename();
	}

	/**
	 * Merge two classes together.
	 *
	 * @param cv      The visitor to write to
	 * @param node    The class reader that holds override properties
	 * @param context Mapper for override classes to new ones
	 */
	public MergeVisitor(ClassVisitor cv, ClassReader node, RenameContext context) {
		this(cv, makeNode(node), context);
	}

	/**
	 * Helper method to make a {@link ClassNode}
	 *
	 * @param reader The class reader to make a node
	 * @return The created node
	 */
	private static ClassNode makeNode(ClassReader reader) {
		ClassNode node = new ClassNode();
		reader.accept(node, ClassReader.EXPAND_FRAMES);
		return node;
	}

	@Override
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
		if (AnnotationHelper.hasAnnotation(node, AnnotationHelper.STUB)) {
			// If we are a stub, visit normally
			super.visit(version, access, name, signature, superName, interfaces);
		} else if (AnnotationHelper.hasAnnotation(node, AnnotationHelper.REWRITE)) {
			// If we are a total rewrite, then visit the overriding class
			node.accept(cv);

			// And prevent writing the normal one
			cv = null;
		} else {
			// Merge both interfaces
			Set<String> overrideInterfaces = new HashSet<String>();
			for (String inter : node.interfaces) {
				overrideInterfaces.add(context.mapType(inter));
			}
			Collections.addAll(overrideInterfaces, interfaces);
			interfaces = overrideInterfaces.toArray(new String[overrideInterfaces.size()]);

			writingOverride = true;
			superClass = superName;

			super.visit(node.version, checkAbstract(node.access, access), name, node.signature, superName, interfaces);

			// Visit fields
			for (FieldNode field : node.fields) {
				if (!AnnotationHelper.hasAnnotation(field.invisibleAnnotations, AnnotationHelper.STUB) && !field.name.equals(AnnotationHelper.ANNOTATION)) {
					List<String> renameTo = AnnotationHelper.getAnnotationValue(AnnotationHelper.getAnnotation(field.invisibleAnnotations, AnnotationHelper.RENAME), "to");
					if (renameTo == null) {
						field.accept(this);
					} else {
						for (String to : renameTo) {
							field.name = to;
							field.accept(this);
						}
					}
				} else {
					this.access.put(field.name, field.access);
				}

				// Prepare field renames
				List<String> renameFrom = AnnotationHelper.getAnnotationValue(AnnotationHelper.getAnnotation(field.invisibleAnnotations, AnnotationHelper.RENAME), "from");
				if (renameFrom != null) {
					for (String from : renameFrom) {
						memberNames.put(from, field.name);
					}
				}

				// Prepare method blocking
				List<String> blocks = AnnotationHelper.getAnnotationValue(AnnotationHelper.getAnnotation(field.invisibleAnnotations, AnnotationHelper.BLOCKS), "value");
				if (blocks != null) {
					for (String block : blocks) {
						this.blocks.put(block, field.name);
					}
				}
			}

			// Visit methods
			for (MethodNode method : node.methods) {
				String desc = "(" + context.mapMethodDesc(method.desc) + ")";
				String whole = method.name + desc;

				if (!method.name.equals("<init>") && !method.name.equals("<clinit>")) {
					if (!AnnotationHelper.hasAnnotation(method.invisibleAnnotations, AnnotationHelper.STUB)) {
						List<String> renameFrom = AnnotationHelper.getAnnotationValue(AnnotationHelper.getAnnotation(method.invisibleAnnotations, AnnotationHelper.RENAME), "to");
						if (renameFrom == null) {
							method.accept(this);
						} else {
							for (String to : renameFrom) {
								method.name = to;
								method.accept(this);
							}
						}
					} else {
						this.access.put(whole, method.access);
					}
				}

				// Prepare method renames
				List<String> renameTo = AnnotationHelper.getAnnotationValue(AnnotationHelper.getAnnotation(method.invisibleAnnotations, AnnotationHelper.RENAME), "from");
				if (renameTo != null) {
					for (String from : renameTo) {
						memberNames.put(from + desc, method.name);
					}
				}

				// Prepare method blocking
				List<String> blocks = AnnotationHelper.getAnnotationValue(AnnotationHelper.getAnnotation(method.invisibleAnnotations, AnnotationHelper.BLOCKS), "value");
				if (blocks != null) {
					for (String block : blocks) {
						this.blocks.put(block, whole);
					}
				}
			}

			writingOverride = false;
		}
	}

	@Override
	public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
		// Allows overriding access types
		access = getMap(this.access, name, access);
		name = getMap(this.memberNames, name, name);

		String block = this.blocks.get(name);
		if ((block == null || block.equals(name)) && visited.add(name)) {
			return super.visitField(access, name, desc, signature, value);
		}

		return null;
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		String description = "(" + context.mapMethodDesc(desc) + ")";
		String wholeName = name + description;

		// Allows overriding access types
		access = checkAbstract(getMap(this.access, wholeName, access), access);
		name = getMap(memberNames, wholeName, name);

		String block = this.blocks.get(name);
		if ((block == null || block.equals(wholeName)) && visited.add(name + description)) {
			MethodVisitor visitor = super.visitMethod(access, name, desc, signature, exceptions);

			// We remap super methods if the method is not static and we are writing the override methods
			if (visitor != null && !Modifier.isStatic(access) && writingOverride && superClass != null) {
				return new SuperMethodVisitor(api, visitor);
			}

			return visitor;
		}

		return null;
	}

	/**
	 * Adds to the rename context from the {@link MergeVisitor.Rename} annotation
	 */
	public void populateRename() {
		Map<String, Object> annotation = getAnnotation(node, AnnotationHelper.RENAME);
		List<String> from = getAnnotationValue(annotation, "from");
		List<String> to = getAnnotationValue(annotation, "to");

		if (from != null && to != null && from.size() == to.size()) {
			for (int i = 0; i < from.size(); i++) {
				context.renames.put(from.get(i), to.get(i));
			}
		}
	}

	public static <T> T getMap(Map<String, T> map, String key, T def) {
		T result = map.get(key);
		return result == null ? def : result;
	}

	protected static int checkAbstract(int newAccess, int oldAccess) {
		return (oldAccess & ACC_ABSTRACT) == ACC_ABSTRACT ? newAccess : (newAccess & ~ACC_ABSTRACT);
	}

	/**
	 * Visitor that remaps super calls
	 */
	public class SuperMethodVisitor extends MethodVisitor {
		public SuperMethodVisitor(int api, MethodVisitor mv) {
			super(api, mv);
		}

		@Override
		public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
			// If it is a constructor, or it is in the current class (private method)
			// we shouldn't remap to the base class
			// Reference: http://stackoverflow.com/questions/20382652/detect-super-word-in-java-code-using-bytecode
			if (opcode == INVOKESPECIAL && !name.equals("<init>") && owner.equals(node.superName)) {
				owner = superClass;
			}
			super.visitMethodInsn(opcode, owner, name, desc, itf);
		}
	}

	/**
	 * Don't rewrite the original class
	 */
	@Target({ElementType.METHOD, ElementType.FIELD, ElementType.TYPE, ElementType.CONSTRUCTOR})
	@Retention(RetentionPolicy.CLASS)
	public @interface Stub {
	}

	/**
	 * Rewrite the original class instead of merging
	 * Put on a field called ANNOTATION
	 */
	@Target({ElementType.TYPE, ElementType.FIELD})
	@Retention(RetentionPolicy.CLASS)
	public @interface Rewrite {
	}

	/**
	 * Rename a class inside
	 * or rename method
	 */
	@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
	@Retention(RetentionPolicy.CLASS)
	public @interface Rename {
		/**
		 * List of types to map from from or method to rename from
		 *
		 * @return The type names in slash format
		 */
		String[] from() default "";

		/**
		 * List of types to map to from or method this to
		 *
		 * @return The type names in slash format
		 */
		String[] to() default "";
	}

	/**
	 * Blocks other members of the type from being written
	 */
	@Target({ElementType.METHOD, ElementType.FIELD})
	@Retention(RetentionPolicy.CLASS)
	public @interface Blocks {
		/**
		 * List of methods to block
		 *
		 * @return The methods to block
		 */
		String[] value() default "";
	}
}
