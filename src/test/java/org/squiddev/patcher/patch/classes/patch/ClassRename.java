package org.squiddev.patcher.patch.classes.patch;

import org.squiddev.patcher.patch.MergeVisitor;

/**
 * Used to test {@link org.squiddev.patcher.patch.MergeVisitor.Rename}
 */
@MergeVisitor.Rename(from = "org/squiddev/patcher/patch/classes/patch/Base$Foo", to = "org/squiddev/patcher/patch/classes/patch/Base$Bar")
public class ClassRename extends Base {
}
