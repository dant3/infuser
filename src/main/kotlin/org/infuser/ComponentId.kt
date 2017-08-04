package org.infuser

import kotlin.reflect.KProperty

class ComponentId<T>(private val customName: String?, val javaClass: Class<T>, val isOptional: Boolean) {
    val name: String
        get() = customName ?: javaClass.canonicalName

    companion object {
        inline operator fun <reified T> invoke(name: String? = null): ComponentId<T> {
            val isOptional = null is T
            return ComponentId(name, T::class.java, isOptional)
        }
    }

    override fun toString(): String {
        val isOptionalSign = if (isOptional) "?" else ""

        if (customName != null) {
            return "${javaClass.simpleName}$isOptionalSign($customName)"
        } else {
            return name
        }
    }

    class Delegate<T>(private val customName: String?, private val itemClass: Class<T>, private val isOptional: Boolean) {
        private val NOT_INITIALIZED = Any()
        private var instance: Any? = NOT_INITIALIZED

        @Suppress("UNCHECKED_CAST")
        private fun get(name: String): ComponentId<T> {
            var result = instance
            if (result === NOT_INITIALIZED) {
                synchronized(this) {
                    result = instance
                    if (result === NOT_INITIALIZED) {
                        result = ComponentId<T>(customName ?: name, itemClass, isOptional)
                        instance = result
                    }
                }
            }
            return result as ComponentId<T>
        }

        operator fun getValue(thisRef: Any?, property: KProperty<*>): ComponentId<T> = get(property.name)
    }
}

inline fun <reified T> componentId(customName: String? = null): ComponentId.Delegate<T> = ComponentId.Delegate(customName, T::class.java, null is T)