package dev.gaphunter.micronauthttpclientcreatecompanion.detect

import com.intellij.psi.JavaRecursiveElementWalkingVisitor
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiMethodCallExpression
import com.intellij.psi.PsiReferenceExpression
import dev.gaphunter.micronauthttpclientcreatecompanion.model.ClientCreateHit

/**
 * Finds `HttpClient.create(...)` static factory calls (Micronaut's
 * `io.micronaut.http.client.HttpClient`) anywhere in application code
 * -- the javadoc for this exact method is explicit: "Note that this
 * method should only be used outside the context of a Micronaut
 * application" and "Within a Micronaut application use @Inject to
 * inject a client instead", adding that "the creator is responsible
 * for closing the client to avoid leaking connections" when using
 * this factory. Any call inside a Micronaut app's own source is
 * exactly the case the javadoc warns against.
 *
 * **v0.1 scope, stated honestly:** matches by simple class/method name
 * (`HttpClient.create`), not real type resolution, so it works whether
 * the real Micronaut jar is on the classpath or not -- an unrelated
 * `HttpClient.create(...)` from a different library sharing the same
 * simple class name is a possible (rare) false positive. Flags every
 * occurrence unconditionally (no attempt to distinguish "outside a
 * Micronaut app" callers, e.g. a CLI tool module, from application
 * beans) -- the javadoc's own guidance is unconditional for code that
 * lives inside the app, and a v0.1 static scanner has no reliable way
 * to know which module a given file belongs to.
 */
object JavaClientCreateFinder {

    fun findAll(file: PsiFile): List<ClientCreateHit> {
        val hits = mutableListOf<ClientCreateHit>()
        file.accept(object : JavaRecursiveElementWalkingVisitor() {
            override fun visitMethodCallExpression(expression: PsiMethodCallExpression) {
                super.visitMethodCallExpression(expression)
                hitFor(expression)?.let { hits += it }
            }
        })
        return hits
    }

    private fun hitFor(call: PsiMethodCallExpression): ClientCreateHit? {
        if (call.methodExpression.referenceName != "create") return null
        val qualifier = call.methodExpression.qualifierExpression as? PsiReferenceExpression ?: return null
        if (qualifier.referenceName != "HttpClient") return null

        return ClientCreateHit(leafOf(call))
    }

    /** Descends to a real leaf PSI element -- LineMarkerInfo must never anchor on a composite node. */
    private fun leafOf(element: PsiElement): PsiElement {
        var current = element
        while (current.firstChild != null) current = current.firstChild
        return current
    }
}
