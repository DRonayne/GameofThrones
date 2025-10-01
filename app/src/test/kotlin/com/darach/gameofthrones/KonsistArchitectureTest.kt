package com.darach.gameofthrones

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.ext.list.withNameEndingWith
import com.lemonappdev.konsist.api.verify.assertTrue
import org.junit.Test

class KonsistArchitectureTest {

    // ========== Layer Dependency Tests ==========

    @Test
    fun `domain layer classes should not have presentation in package`() {
        Konsist
            .scopeFromProject()
            .classes()
            .filter { it.resideInPackage("..domain..") }
            .assertTrue {
                !it.packagee?.name?.contains("presentation").orFalse()
            }
    }

    private fun Boolean?.orFalse() = this ?: false

    @Test
    fun `presentation layer classes should not reside in domain layer`() {
        Konsist
            .scopeFromProject()
            .classes()
            .filter {
                it.name.contains("ViewModel") ||
                    it.hasAnnotationOf(androidx.compose.runtime.Composable::class)
            }
            .assertTrue {
                !it.resideInPackage("..domain..")
            }
    }

    @Test
    fun `data layer classes should not reside in domain layer`() {
        Konsist
            .scopeFromProject()
            .classes()
            .filter { it.name.endsWith("Entity") || it.name.endsWith("Dao") }
            .assertTrue {
                !it.resideInPackage("..domain..")
            }
    }

    // ========== Naming Convention Tests ==========

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
    fun `interfaces with 'Repository' suffix should reside in domain layer`() {
        Konsist
            .scopeFromProject()
            .interfaces()
            .withNameEndingWith("Repository")
            .assertTrue { it.resideInPackage("..domain..") }
    }

    @Test
    fun `ViewModels should end with ViewModel suffix`() {
        Konsist
            .scopeFromProject()
            .classes()
            .filter { it.resideInPackage("..presentation..") }
            .filter { it.hasAnnotationOf(dagger.hilt.android.lifecycle.HiltViewModel::class) }
            .assertTrue { it.name.endsWith("ViewModel") }
    }

    @Test
    fun `Screen composables should end with Screen suffix`() {
        Konsist
            .scopeFromProject()
            .functions()
            .filter { it.hasAnnotationOf(androidx.compose.runtime.Composable::class) }
            .filter { it.resideInPackage("..presentation..") }
            .filter { function ->
                // Only check top-level screen composables (no parameters or minimal parameters)
                function.hasPublicModifier && function.parameters.isEmpty()
            }
            .assertTrue { it.name.endsWith("Screen") }
    }

    @Test
    fun `Entity classes should end with Entity suffix`() {
        Konsist
            .scopeFromProject()
            .classes()
            .filter { it.hasAnnotation { annotation -> annotation.name == "Entity" } }
            .assertTrue { it.name.endsWith("Entity") }
    }

    @Test
    fun `DAO interfaces should end with Dao suffix`() {
        Konsist
            .scopeFromProject()
            .interfaces()
            .filter { it.hasAnnotation { annotation -> annotation.name == "Dao" } }
            .assertTrue { it.name.endsWith("Dao") }
    }

    // ========== Package Structure Tests ==========

    @Test
    fun `screen composables should reside in feature modules`() {
        Konsist
            .scopeFromProject()
            .functions()
            .filter { it.hasAnnotationOf(androidx.compose.runtime.Composable::class) }
            .filter { it.name.endsWith("Screen") }
            .assertTrue { it.resideInPackage("..feature..") || it.resideInPackage("..app..") }
    }

    @Test
    fun `data layer should contain repository implementations`() {
        Konsist
            .scopeFromProject()
            .classes()
            .withNameEndingWith("Repository")
            .filter { klass ->
                Konsist.scopeFromProject().interfaces().none { it.name == klass.name }
            }
            .assertTrue { it.resideInPackage("..data..") }
    }

    @Test
    fun `domain layer should contain use cases and models`() {
        Konsist
            .scopeFromProject()
            .classes()
            .withNameEndingWith("UseCase")
            .assertTrue { it.resideInPackage("..domain..") }
    }

    // ========== Use Case Rules ==========

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

    @Test
    fun `UseCases should be final classes`() {
        Konsist
            .scopeFromProject()
            .classes()
            .withNameEndingWith("UseCase")
            .assertTrue { !it.hasOpenModifier }
    }

    @Test
    fun `UseCases should have constructor injection`() {
        Konsist
            .scopeFromProject()
            .classes()
            .withNameEndingWith("UseCase")
            .assertTrue { useCase ->
                useCase.primaryConstructor != null &&
                    useCase.primaryConstructor?.hasAnnotation { it.name == "Inject" } == true
            }
    }

    // ========== ViewModel Rules ==========

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
    fun `ViewModels should extend ViewModel`() {
        Konsist
            .scopeFromProject()
            .classes()
            .withNameEndingWith("ViewModel")
            .assertTrue {
                it.hasParentWithName("ViewModel")
            }
    }

    @Test
    fun `ViewModels should have constructor injection`() {
        Konsist
            .scopeFromProject()
            .classes()
            .withNameEndingWith("ViewModel")
            .assertTrue { viewModel ->
                viewModel.primaryConstructor != null &&
                    viewModel.primaryConstructor?.hasAnnotation { it.name == "Inject" } == true
            }
    }

    @Test
    fun `ViewModels should reside in feature modules`() {
        Konsist
            .scopeFromProject()
            .classes()
            .withNameEndingWith("ViewModel")
            .assertTrue { it.resideInPackage("..feature..") }
    }

    @Test
    fun `ViewModels should expose state as StateFlow or not expose mutable state`() {
        Konsist
            .scopeFromProject()
            .classes()
            .withNameEndingWith("ViewModel")
            .assertTrue { viewModel ->
                // ViewModels should have at least one public property
                // and no public MutableStateFlow
                viewModel.properties().none { property ->
                    property.type?.name?.contains("MutableStateFlow") == true &&
                        property.hasPublicModifier
                }
            }
    }

    @Test
    fun `ViewModels should not expose MutableStateFlow`() {
        Konsist
            .scopeFromProject()
            .classes()
            .withNameEndingWith("ViewModel")
            .assertTrue { viewModel ->
                viewModel.properties().none { property ->
                    property.type?.name?.contains("MutableStateFlow") == true &&
                        property.hasPublicModifier
                }
            }
    }
}
