package org.infuser

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
}