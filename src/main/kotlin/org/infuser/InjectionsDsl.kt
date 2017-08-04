package org.infuser

fun <T: Any> Module.inject(componentId: ComponentId<T>) = lazy { get(componentId) }