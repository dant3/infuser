package org.infuser

import org.infuser.impl.module.ModuleBuilder

interface Module {
    fun providedIds(): Set<ComponentId<*>>

    fun <T> contains(id: ComponentId<T>): Boolean = providedIds().contains(id)
    fun <T> get(id: ComponentId<T>): T

    companion object {
        operator fun invoke(allowOverride: Boolean = false, builder: Builder.() -> Unit) =
                ModuleBuilder(allowOverride).apply(builder).buildModule()
    }

    interface Builder {
        fun include(module: Module)
        fun <T> bind(id: ComponentId<T>, provider: Module.() -> T) = bind(id, singleton(provider))
        fun <T> bind(id: ComponentId<T>, provider: Provider<T>)
        fun <T> bind(id: ComponentId<T>, value: T) = bind(id, instance(value))
        fun <T> bind(id: ComponentId<T>): DSL.BindKeyword<T> = object : DSL.BindKeyword<T> {
            override fun via(provider: Provider<T>) {
                bind(id, provider)
            }
        }
    }
}

object DSL {
    interface BindKeyword<T> {
        infix fun via(provider: Provider<T>)
    }
}