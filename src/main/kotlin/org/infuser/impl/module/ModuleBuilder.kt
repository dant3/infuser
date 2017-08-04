package org.infuser.impl.module

import org.infuser.ComponentId
import org.infuser.Module
import org.infuser.impl.Component
import org.infuser.impl.ComponentRegistry

class ModuleBuilder(allowOverride: Boolean) : Module.Builder {
    private val registryBuilder = ComponentRegistry.Builder(allowOverride)

    private val parentModules: MutableSet<Module> = mutableSetOf()

    override fun include(module: Module) {
        parentModules.add(module)
    }
    override fun <T : Any> bind(id: ComponentId<T>, provider: Module.() -> T) {
        val component = Component(id, provider)
        registryBuilder.register(component)
    }

    fun buildModule(): Module = when (parentModules.size) {
        0 -> DefaultModule(registry = registryBuilder.build())
        1 -> DefaultModule(parentModule = parentModules.first(), registry = registryBuilder.build())
        else -> DefaultModule(parentModule = JoinedModule(parentModules), registry = registryBuilder.build())
    }
}