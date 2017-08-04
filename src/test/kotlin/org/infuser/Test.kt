
import org.infuser.ComponentId
import org.infuser.Module
import org.infuser.inject


object ComponentsRegistry {
    val name = ComponentId<String>()
    val greetingString = ComponentId<String>()
}

class Greeter(module: Module) {
    private val greeting by module.inject(ComponentsRegistry.greetingString)

    fun greet() { println(greeting) }
}

object Test {
    @JvmStatic fun main(args: Array<String>) {
        val nameModule = Module {
            bind(ComponentsRegistry.name) { "World" }
        }
        val greeterContext = Module {
            include(nameModule)
            bind(ComponentsRegistry.greetingString) { "Hello, ${get(ComponentsRegistry.name)}!" }
        }
        val greeter = Greeter(greeterContext)
        greeter.greet()
    }
}