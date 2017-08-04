package org.infuser.impl

import org.infuser.ComponentId
import org.infuser.Module

class Component<T : Any>(val id: ComponentId<T>, private val constructor: Module.() -> T) {
    fun instantiate(module: Module): T = constructor.invoke(module)
}