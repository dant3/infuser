package org.infuser.impl.module

import org.infuser.ComponentId
import org.infuser.Module
import org.infuser.error.ComponentNotFoundException

class JoinedModule private constructor(private val childModules: Set<Module>, private val providedIds: Set<ComponentId<*>>) : Module {
    override fun providedIds(): Set<ComponentId<*>> = providedIds

    override fun <T> get(id: ComponentId<T>): T {
        val moduleWithSuchId = childModules.firstOrNull { it.contains(id) }
        return when (moduleWithSuchId) {
            null -> throw ComponentNotFoundException("Could not find component with id $id")
            else -> moduleWithSuchId.get(id)
        }
    }

    companion object {
        operator fun invoke(childModules: Set<Module>, allowOverlaps: Boolean = false): JoinedModule {
            val providedIds = childModules.flatMapTo(mutableSetOf(), Module::providedIds)
            if (!allowOverlaps) {
                val initialIdsCount = childModules.fold(0, { acc, module -> module.providedIds().size + acc })
                val uniqueIdsCount = providedIds.size
                require(initialIdsCount == uniqueIdsCount) {
                    val overlappedIds = childModules
                            .flatMapTo(mutableListOf(), Module::providedIds)
                            .groupBy { it }
                            .entries
                            .filter { it.value.size > 1 }
                            .map { it.key }
                    "modules contain overlapped ids: $overlappedIds"
                }
            }
            return JoinedModule(childModules, providedIds)
        }
    }
}