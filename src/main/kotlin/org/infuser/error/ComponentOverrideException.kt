package org.infuser.error

import org.infuser.ComponentId

class ComponentOverrideException(message: String, val componentId: ComponentId<*>) : Exception(message)