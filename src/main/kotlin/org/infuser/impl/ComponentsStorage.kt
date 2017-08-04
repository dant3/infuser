package org.infuser.impl

import org.infuser.ComponentId
import org.infuser.error.ComponentOverrideException

class ComponentsStorage private constructor(private val components: Map<ComponentId<*>, Component<*>>) {
    fun <T> find(componentId: ComponentId<T>): Component<T>? = components[componentId] as Component<T>?

    fun ids(): Set<ComponentId<*>> = components.keys

    class Builder(val allowOverride: Boolean = false) {
        private val components: MutableMap<ComponentId<*>, Component<*>> = mutableMapOf()

        fun <T> register(component: Component<T>) {
            val oldComponent = components.put(component.id, component)
            if (oldComponent != null && !allowOverride) {
                throw ComponentOverrideException("Trying to override component for id ${component.id} in a context where overriding is not allowed", component.id)
            }
        }

        internal fun build(): ComponentsStorage = ComponentsStorage(components)
    }
}