package org.rust.ide.intentions

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import org.rust.lang.core.psi.RsBaseType
import org.rust.lang.core.psi.RsRefLikeType
import org.rust.lang.core.psi.RsPsiFactory
import org.rust.lang.core.psi.impl.isMut
import org.rust.lang.core.psi.impl.isRef
import org.rust.lang.core.psi.util.parentOfType

/**
 * Set reference mutable
 *
 * ```
 * &type
 * ```
 *
 * to this:
 *
 * ```
 * &mut type
 * ```
 */
open class SetMutableIntention : RsElementBaseIntentionAction<SetMutableIntention.Context>() {
    override fun getText() = "Set reference mutable"
    override fun getFamilyName() = text

    open val mutable = true

    data class Context(
        val refType: RsRefLikeType,
        val baseType: RsBaseType
    )

    override fun findApplicableContext(project: Project, editor: Editor, element: PsiElement): Context? {
        val refType = element.parentOfType<RsRefLikeType>() ?: return null
        if (!refType.isRef) return null
        val baseType = refType.typeReference as? RsBaseType ?: return null
        if (refType.isMut == mutable) return null
        return Context(refType, baseType)

    }

    override fun invoke(project: Project, editor: Editor, ctx: Context) {
        val newType = RsPsiFactory(project).createReferenceType(ctx.baseType.text, mutable)
        ctx.refType.replace(newType)
    }
}
