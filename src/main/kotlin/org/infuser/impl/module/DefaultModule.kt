package org.infuser.impl.module

import org.infuser.ComponentId
import org.infuser.Module
import org.infuser.error.CyclicDependencyException
import org.infuser.impl.ComponentRegistry
import java.util.*

class DefaultModule internal constructor(private val parentModule: Module? = null, private val registry: ComponentRegistry) : Module {
    override fun providedIds(): Set<ComponentId<*>> = registry.ids()

    private val dependencyStack: Deque<ComponentId<*>> = ArrayDeque<ComponentId<*>>()

    override fun <T : Any> getOptionally(id: ComponentId<T>): T? {
        if (dependencyStack.contains(id)) {
            val cyclicStack = listOf(id) + dependencyStack.toList()
            throw CyclicDependencyException("Component with id $id depends on itself through chain ${cyclicStack.joinToString(" -> ")}")
        } else {
            dependencyStack.push(id)

            val instanceOfParent = parentModule?.getOptionally(id)
            val instance = instanceOfParent ?: registry.getOrNull(id)?.instantiate(this)

            dependencyStack.pop()
            return instance
        }
    }


    companion object {
        //operator fun invoke(parentModule: Module? = null, )
    }
}