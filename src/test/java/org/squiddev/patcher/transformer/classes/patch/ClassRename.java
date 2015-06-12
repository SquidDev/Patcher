package org.squiddev.patcher.transformer.classes.patch;

import org.squiddev.patcher.visitors.MergeVisitor;

/**
 * Used to test {@link org.squiddev.patcher.visitors.MergeVisitor.Rename}
 */
@MergeVisitor.Rename(from = "org/squiddev/patcher/transformer/classes/patch/Base$Foo", to = "org/squiddev/patcher/transformer/classes/patch/Base$Bar")
public class ClassRename extends Base {
}
