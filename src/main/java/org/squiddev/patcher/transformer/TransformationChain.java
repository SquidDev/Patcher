package org.squiddev.patcher.transformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Loads classes, rewriting them
 */
public class TransformationChain {
	protected List<IPatcher> patchers = new ArrayList<IPatcher>();
	protected List<ISource> sources = new ArrayList<ISource>();

	protected boolean finalised = false;

	public byte[] transform(String className, byte[] bytes) throws Exception {
		ClassReader reader = null;
		ClassWriter writer = null;
		ClassVisitor visitor = null;
		for (IPatcher patcher : patchers) {
			if (patcher.matches(className)) {
				if (visitor == null) {
					for (ISource source : sources) {
						reader = source.getReader(className);
						if (reader != null) break;
					}

					if (reader == null) reader = new ClassReader(bytes);

					visitor = writer = new ClassWriter(reader, ClassWriter.COMPUTE_FRAMES);
				}

				visitor = patcher.patch(className, visitor);
			}
		}

		if (visitor != null) {
			reader.accept(visitor, 0);
			bytes = writer.toByteArray();
		}

		return bytes;
	}

	public <T extends IPatcher & ISource> void addBoth(T patcher) {
		if (finalised) throw new IllegalStateException("Cannot add new patchers once finalised");
		patchers.add(patcher);
		sources.add(patcher);
	}

	public void add(IPatcher patcher) {
		if (finalised) throw new IllegalStateException("Cannot add new patchers once finalised");
		patchers.add(patcher);
	}

	public void add(ISource source) {
		if (finalised) throw new IllegalStateException("Cannot add new sources once finalised");
		sources.add(source);
	}

	public void finalise() {
		finalised = true;

		// We reverse them so those added later are applied later in the chain
		Collections.reverse(patchers);
		Collections.reverse(sources);
	}
}
