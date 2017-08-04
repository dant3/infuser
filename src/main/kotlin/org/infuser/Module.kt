package org.infuser

import org.infuser.impl.module.ModuleBuilder

interface Module {
    fun providedIds(): Set<ComponentId<*>>
    fun <T : Any> get(id: ComponentId<T>): T = getOptionally(id) ?: throw NoSuchElementException("Could not find component with id $id")

    fun <T : Any> getOptionally(id: ComponentId<T>): T?

    companion object {
        operator fun invoke(allowOverride: Boolean = false, builder: Builder.() -> Unit) =
                ModuleBuilder(allowOverride).apply(builder).buildModule()
    }

    interface Builder {
        fun include(module: Module)
        fun <T : Any> bind(id: ComponentId<T>, provider: Module.() -> T)
    }
}