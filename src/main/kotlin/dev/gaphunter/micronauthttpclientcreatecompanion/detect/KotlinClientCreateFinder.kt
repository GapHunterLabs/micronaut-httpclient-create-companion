package dev.gaphunter.micronauthttpclientcreatecompanion.detect

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import dev.gaphunter.micronauthttpclientcreatecompanion.model.ClientCreateHit
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtTreeVisitorVoid

/** Kotlin counterpart of [JavaClientCreateFinder]. */
object KotlinClientCreateFinder {

    fun findAll(file: PsiFile): List<ClientCreateHit> {
        if (file !is KtFile) return emptyList()
        val hits = mutableListOf<ClientCreateHit>()
        file.accept(object : KtTreeVisitorVoid() {
            override fun visitDotQualifiedExpression(expression: KtDotQualifiedExpression) {
                super.visitDotQualifiedExpression(expression)
                hitFor(expression)?.let { hits += it }
            }
        })
        return hits
    }

    private fun hitFor(expression: KtDotQualifiedExpression): ClientCreateHit? {
        val call = expression.selectorExpression as? KtCallExpression ?: return null
        if (call.calleeExpression?.text != "create") return null
        val receiver = expression.receiverExpression as? KtNameReferenceExpression ?: return null
        if (receiver.getReferencedName() != "HttpClient") return null

        return ClientCreateHit(leafOf(expression))
    }

    private fun leafOf(element: PsiElement): PsiElement {
        var current = element
        while (current.firstChild != null) current = current.firstChild
        return current
    }
}
