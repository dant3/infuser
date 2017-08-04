package org.infuser.impl.module

import org.infuser.ComponentId
import org.infuser.Module
import org.infuser.error.ComponentNotFoundException
import org.infuser.error.CyclicDependencyException
import org.infuser.impl.ComponentsStorage
import java.util.*

class DefaultModule internal constructor(private val parentModule: Module? = null, private val registry: ComponentsStorage) : Module {
    override fun providedIds(): Set<ComponentId<*>> =
            if (parentModule == null) registry.ids()
            else registry.ids() + parentModule.providedIds()

    private val dependencyStack: Deque<ComponentId<*>> = ArrayDeque<ComponentId<*>>()

    override fun <T> get(id: ComponentId<T>): T {
        fun lookupStack(idList: List<ComponentId<*>>) = idList.joinToString(" -> ")
        fun lookupStack(stack: Deque<ComponentId<*>>) = lookupStack(stack.toList())

        if (dependencyStack.contains(id)) {
            val lookupStack = lookupStack(listOf(id) + dependencyStack.toList())
            throw CyclicDependencyException("Component with id $id depends on itself through chain $lookupStack")
        } else {
            dependencyStack.push(id)

            val requestedComponent = when {
                parentModule != null && parentModule.contains(id) -> parentModule.get(id)
                else -> {
                    val component = try {
                        registry.find(id)
                    } catch (ex: ComponentNotFoundException) {
                        throw ComponentNotFoundException("Could not find component with id $id through chain ${lookupStack(dependencyStack)}", ex)
                    }
                    when (component) {
                        null -> throw ComponentNotFoundException("Could not find component with id $id through chain ${lookupStack(dependencyStack)}")
                        else -> component.provide(this)
                    }
                }
            }

            dependencyStack.pop()
            return requestedComponent
        }
    }
}