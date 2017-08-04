package org.infuser

import kotlin.reflect.KClass

class ComponentId<T : Any>(private val customName: String?, val klass: KClass<T>) {
    constructor(klass: KClass<T>) : this(null, klass)

    val name: String
        get() = customName ?: klass.java.canonicalName

    companion object {
        inline operator fun <reified T: Any> invoke() = ComponentId(T::class)
    }

    override fun toString(): String {
        if (customName != null) {
            return "${klass.java.simpleName}($customName)"
        } else {
            return name
        }
    }
}