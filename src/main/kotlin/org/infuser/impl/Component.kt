package org.infuser.impl

import org.infuser.ComponentId
import org.infuser.Module
import org.infuser.Provider

class Component<T>(val id: ComponentId<T>, private val provider: Provider<T>) {
    fun provide(module: Module): T = provider.provide(module)
}