package dev.gaphunter.micronauthttpclientcreatecompanion.gutter

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProviderDescriptor
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.openapi.project.DumbAware
import com.intellij.psi.PsiElement
import dev.gaphunter.micronauthttpclientcreatecompanion.detect.JavaClientCreateFinder
import dev.gaphunter.micronauthttpclientcreatecompanion.detect.KotlinClientCreateFinder
import dev.gaphunter.micronauthttpclientcreatecompanion.model.ClientCreateHit
import dev.gaphunter.micronauthttpclientcreatecompanion.review.ReviewPrompt

class ClientCreateLineMarkerProvider : LineMarkerProviderDescriptor(), DumbAware {

    override fun getName(): String = "Micronaut HttpClient.create() inside application code"

    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? = null

    override fun collectSlowLineMarkers(elements: MutableList<out PsiElement>, result: MutableCollection<in LineMarkerInfo<*>>) {
        val file = elements.firstOrNull()?.containingFile ?: return
        val hits = when (file.language.id) {
            "JAVA" -> JavaClientCreateFinder.findAll(file)
            "kotlin" -> KotlinClientCreateFinder.findAll(file)
            else -> emptyList()
        }
        if (hits.isEmpty()) return

        val hitsByElement = hits.associateBy { it.callElement }
        for (element in elements) {
            val hit = hitsByElement[element] ?: continue
            result.add(buildMarker(hit))

            val path = file.virtualFile?.path ?: continue
            val lineNumber = file.viewProvider.document?.getLineNumber(element.textRange.startOffset) ?: -1
            ReviewPrompt.recordHit(file.project, "$path:$lineNumber")
        }
    }

    private fun buildMarker(hit: ClientCreateHit): LineMarkerInfo<PsiElement> {
        val tooltip = "HttpClient.create(...) is called here -- Micronaut's own javadoc says this factory " +
            "should only be used outside the context of a Micronaut application, and that the creator is " +
            "responsible for closing the client to avoid leaking connections. Use @Inject to get a managed client instead"
        return LineMarkerInfo(
            hit.callElement,
            hit.callElement.textRange,
            ClientCreateIcons.RISK,
            { _: PsiElement -> tooltip },
            null,
            GutterIconRenderer.Alignment.RIGHT,
            { tooltip },
        )
    }
}
