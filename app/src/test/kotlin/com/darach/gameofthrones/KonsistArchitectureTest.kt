package com.darach.gameofthrones

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.ext.list.withNameEndingWith
import com.lemonappdev.konsist.api.verify.assertTrue
import org.junit.Test

class KonsistArchitectureTest {

    @Test
    fun `classes with 'UseCase' suffix should reside in domain layer`() {
        Konsist
            .scopeFromProject()
            .classes()
            .withNameEndingWith("UseCase")
            .assertTrue { it.resideInPackage("..domain..") }
    }

    @Test
    fun `classes with 'Repository' suffix should reside in data layer`() {
        Konsist
            .scopeFromProject()
            .classes()
            .withNameEndingWith("Repository")
            .assertTrue {
                it.resideInPackage("..data..") || it.resideInPackage("..domain..")
            }
    }

    @Test
    fun `classes with 'ViewModel' suffix should reside in ui layer`() {
        Konsist
            .scopeFromProject()
            .classes()
            .withNameEndingWith("ViewModel")
            .assertTrue { it.resideInPackage("..ui..") }
    }

    @Test
    fun `interfaces with 'Repository' suffix should reside in domain layer`() {
        Konsist
            .scopeFromProject()
            .interfaces()
            .withNameEndingWith("Repository")
            .assertTrue { it.resideInPackage("..domain..") }
    }

    @Test
    fun `ViewModels should have Hilt ViewModel annotation`() {
        Konsist
            .scopeFromProject()
            .classes()
            .withNameEndingWith("ViewModel")
            .assertTrue {
                it.hasAnnotationOf(dagger.hilt.android.lifecycle.HiltViewModel::class)
            }
    }

    @Test
    fun `UseCases should have single public operator function invoke`() {
        Konsist
            .scopeFromProject()
            .classes()
            .withNameEndingWith("UseCase")
            .assertTrue { useCase ->
                val publicFunctions = useCase.functions().filter { it.hasPublicModifier }
                publicFunctions.size == 1 && publicFunctions.first().name == "invoke"
            }
    }
}
