package org.infuser.impl

import org.infuser.ComponentId
import org.infuser.error.ComponentOverrideException

class ComponentRegistry private constructor(private val components: Map<ComponentId<*>, Component<*>>) {
    fun <T : Any> get(componentId: ComponentId<T>): Component<T> = getOrNull(componentId)!!
    fun <T : Any> getOrNull(componentId: ComponentId<T>): Component<T>? = components[componentId] as Component<T>?

    fun ids(): Set<ComponentId<*>> = components.keys

    class Builder(val allowOverride: Boolean = false) {
        private val components: MutableMap<ComponentId<*>, Component<*>> = mutableMapOf()

        fun <T : Any> register(component: Component<T>) {
            val oldComponent = components.put(component.id, component)
            if (oldComponent != null && !allowOverride) {
                throw ComponentOverrideException("Trying to override component for id ${component.id} in a context where overriding is not allowed", component.id)
            }
        }

        internal fun build(): ComponentRegistry = ComponentRegistry(components)
    }
}