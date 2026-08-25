package dev.gaphunter.micronauthttpclientcreatecompanion.model

import com.intellij.psi.PsiElement

/** One Micronaut `HttpClient.create(...)` static factory call found inside application code. */
data class ClientCreateHit(val callElement: PsiElement)
