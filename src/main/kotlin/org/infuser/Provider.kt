package org.infuser

interface Provider<T> {
    fun provide(module: Module): T
}

fun <T> instance(value: T): Provider<T> = object : Provider<T> {
    override fun provide(module: Module): T = value
}

fun <T> singleton(constructor: Module.() -> T): Provider<T> = object : Provider<T> {
    private val NOT_INITIALIZED = Any()
    private var instance: Any? = NOT_INITIALIZED

    @Suppress("UNCHECKED_CAST")
    override fun provide(module: Module): T {
        var result = instance
        if (result === NOT_INITIALIZED) {
            synchronized(this) {
                result = instance
                if (result === NOT_INITIALIZED) {
                    result = constructor.invoke(module)
                    instance = result
                }
            }
        }
        return result as T
    }
}

fun <T> factory(constructor: Module.() -> T): Provider<T> = object : Provider<T> {
    override fun provide(module: Module): T = constructor(module)
}