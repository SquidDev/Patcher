package org.squiddev.patcher.transformer;

import org.objectweb.asm.ClassReader;
import org.squiddev.patcher.Logger;
import org.squiddev.patcher.visitors.RenameContext;

import java.io.InputStream;

/**
 * Abstract class for using custom sources
 */
public abstract class AbstractRewriter implements IPatcher {
	/**
	 * The logger to send output to.
	 */
	protected final Logger logger;

	protected final int classNameStart;

	/**
	 * The name of the class we are replacing
	 */
	protected final String className;

	/**
	 * The name of the class we are replacing with / instead of .
	 */
	protected final String classType;

	/**
	 * The name of the class to load
	 */
	protected final String patchName;

	/**
	 * The name of the class to load with / instead of .
	 */
	protected final String patchType;

	/**
	 * The remapper to use
	 */
	protected final RenameContext context;

	public AbstractRewriter(String className, String patchName) {
		this(Logger.instance, className, patchName);
	}

	public AbstractRewriter(Logger logger, String className, String patchName) {
		this.logger = logger;
		this.className = className;
		classNameStart = className.length();
		classType = className.replace('.', '/');

		this.patchName = patchName;
		patchType = patchName.replace('.', '/');

		RenameContext context = this.context = new RenameContext();
		context.prefixRenames.put(patchType, classType);
	}

	protected ClassReader getSource(String source) {
		source = "/" + source.replace('.', '/') + ".class";
		InputStream stream = AbstractRewriter.class.getResourceAsStream(source);

		if (stream == null) {
			logger.doWarn("Cannot find custom rewrite " + source);
			return null;
		}
		try {
			return new ClassReader(stream);
		} catch (Exception e) {
			logger.doError("Cannot load " + source + ", falling back to default", e);
		}

		return null;
	}

	/**
	 * Checks if the class matches
	 *
	 * @param className The name of the class
	 * @return If it should be patched
	 */
	@Override
	public boolean matches(String className) {
		return className.equals(this.className) || className.startsWith(this.className + "$");
	}
}
