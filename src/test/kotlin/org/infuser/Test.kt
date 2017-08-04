
import org.infuser.*


object NameModule : ModuleObject() {
    val nullable by componentId<String?>()
    val name by componentId<String>()
    val world = ComponentId<String>("world")

    override fun Module.Builder.definition() {
        bind(nullable) via instance(null)
        bind(world) via singleton { "World" }
        bind(name) via singleton { get(world) }
    }
}


object GreeterModule : ModuleObject() {
    val greetingString by componentId<String>("greeting")

    override fun Module.Builder.definition() {
        include(NameModule)
        bind(greetingString) via factory { "Hello, ${get(NameModule.name)}!" }
    }
}


class Greeter(module: GreeterModule) {
    val greeting by module.inject(GreeterModule.greetingString)
    fun greet() { println(greeting) }
}


object Test {
    @JvmStatic fun main(args: Array<String>) {
        val greeterModule = GreeterModule

        val name = greeterModule.get(NameModule.name)
        val name2 = NameModule.get(NameModule.name)

        if (name !== name2) {
            throw IllegalStateException()
        }

        val greeter = Greeter(greeterModule)
        greeter.greet()

        if (greeter.greeting === greeterModule.get(GreeterModule.greetingString)) {
            throw IllegalStateException()
        }

        val nullValue = NameModule.get(NameModule.nullable)
        println("nullValue = $nullValue")

        println("ids: " + greeterModule.providedIds().joinToString(", ", "[", "]"))
    }
}