package com.sextou.architecture

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.ext.list.withNameEndingWith
import com.lemonappdev.konsist.api.verify.assertTrue
import org.junit.Test

class RepositoryArchitectureTest {
    @Test
    fun `repository contracts stay in domain feature repository packages`() {
        Konsist.scopeFromModule("domain")
            .interfaces(includeNested = false)
            .withNameEndingWith("Repository")
            .assertTrue(
                strict = true,
                additionalMessage = ORIGIN,
            ) { declaration ->
                declaration.resideInPackage("com.sextou.domain..repository")
            }
    }

    @Test
    fun `remote repository operations are suspend and return domain Result`() {
        val functions = Konsist.scopeFromModule("domain")
            .interfaces()
            .filter { it.name == "Remote" && it.resideInPackage("com.sextou.domain..repository") }
            .flatMap { it.functions(includeNested = false, includeLocal = false) }
        functions
            .assertTrue(
                strict = true,
                additionalMessage = ORIGIN,
            ) { function ->
                function.hasSuspendModifier && function.returnType?.name?.startsWith("Result<") == true
            }
    }

    @Test
    fun `remote implementations stay in networking adapters`() {
        Konsist.scopeFromModule("networking")
            .classes(includeNested = false, includeLocal = false)
            .withNameEndingWith("RemoteImpl")
            .assertTrue(
                strict = true,
                additionalMessage = ORIGIN,
            ) { declaration ->
                declaration.resideInPackage("com.sextou.networking.adapter")
            }
    }

    @Test
    fun `domain does not import Google Android Retrofit or networking types`() {
        Konsist.scopeFromModule("domain")
            .imports
            .assertTrue(additionalMessage = ORIGIN) { import ->
                listOf(
                    "com.google.",
                    "android.",
                    "androidx.",
                    "retrofit2.",
                    "com.sextou.networking.",
                ).none(import.name::startsWith)
            }
    }

    @Test
    fun `API response DTOs own their domain mapping`() {
        val responses = Konsist.scopeFromPackage("com.sextou.networking.response", moduleName = "networking")
            .classes(includeNested = false, includeLocal = false)
            .withNameEndingWith("Response")
        responses
            .assertTrue(
                strict = true,
                additionalMessage = ORIGIN,
            ) { declaration ->
                declaration.parents().any { it.name.startsWith("DomainMapperResponse<") }
            }
    }

    private companion object {
        const val ORIGIN =
            "Origem: android-app-architecture/references/repository.md — Convenções obrigatórias e Checklist de Repository."
    }
}
