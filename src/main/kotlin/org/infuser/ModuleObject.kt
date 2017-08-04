package org.infuser

abstract class ModuleObject : Module {
    private val moduleImpl by lazy { Module { definition() } }

    override fun providedIds(): Set<ComponentId<*>> = moduleImpl.providedIds()
    override fun <T> get(id: ComponentId<T>): T = moduleImpl.get(id)

    protected abstract fun Module.Builder.definition(): Unit
}