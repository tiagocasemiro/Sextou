package com.sextou.architecture

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.ext.list.withNameEndingWith
import com.lemonappdev.konsist.api.verify.assertTrue
import org.junit.Test

class UseCaseArchitectureTest {
    @Test
    fun `use cases stay in domain feature use case packages`() {
        Konsist.scopeFromModule("domain")
            .classes(includeNested = false, includeLocal = false)
            .withNameEndingWith("UseCase")
            .assertTrue(
                strict = true,
                additionalMessage = ORIGIN,
            ) { declaration ->
                declaration.resideInPackage("com.sextou.domain..usecase")
            }
    }

    @Test
    fun `domain use cases do not import infrastructure or UI types`() {
        Konsist.scopeFromModule("domain")
            .imports
            .assertTrue(additionalMessage = ORIGIN) { import ->
                listOf(
                    "android.",
                    "androidx.",
                    "com.sextou.networking.",
                    "com.sextou.local.",
                    "com.sextou.app.",
                    "retrofit2.",
                    "androidx.compose.",
                ).none(import.name::startsWith)
            }
    }

    private companion object {
        const val ORIGIN =
            "Origem: android-app-architecture/references/use-case.md — Convenções obrigatórias e Checklist de UseCase."
    }
}
