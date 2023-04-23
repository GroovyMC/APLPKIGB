# Austin's Powerful Language Provider: Keeping It Groovy Baby

The Grooviest language provider in Forgeland

[![APLP version](https://img.shields.io/badge/dynamic/xml?label=Latest%20APLP%20version&query=metadata//latest&url=https://maven.moddinginquisition.org/releases/net/thesilkminer/mc/austin/aplp-1.18.2/maven-metadata.xml)](https://maven.moddinginquisition.org/#/releases/net/thesilkminer/mc/austin/aplp-1.18.2)

## How to use
The recommended way to get started is to use the [1.18.2 branch of the GroovyMDK](https://github.com/GroovyMC/GroovyMDK/tree/1.18.2).
It contains everything you need to get started with Groovy modding in Minecraft 1.18.2. Alternatively, follow the steps below.

To use this language provider, simply add it as a dependency in your build script. The
language provider is available on Jared's maven, so you can simply add the following
to your build script, replacing `${aplpVersion}` with the version of APLP you want to use:
```groovy
repositories {
    maven {
        name 'Modding Inquisition Releases'
        url = 'https://maven.moddinginquisition.org/releases'
    }
}
dependencies {
    compileOnly "net.thesilkminer.mc.austin:aplp-1.18.2:${aplpVersion}"
    runtimeOnly "net.thesilkminer.mc.austin:aplp-1.18.2:${aplpVersion}:all"
}
```

In your `mods.toml`, specify `aplp` as the loader and the version of the software
as `loaderVersion`. See the following for a bare-bones example:

```toml
modLoader="aplp"
loaderVersion="[1,)"
license="All Rights Reserved"

[[mods]]
modId="mymojo"
version="1.0.0"
```

A bare-bones [mods.groovy](https://github.com/GroovyMC/ModsDotGroovy) example:
```groovy
ModsDotGroovy.make {
    modLoader = 'aplp'
    loaderVersion = '[1,)'
    license = 'All Rights Reserved'
  
    mod {
        modId = 'mymojo'
        version = '1.0.0'
    }
}
```

The main mod class should have a no-arg constructor and be annotated with either
`@Mojo` (suggested) or `@Mod` with its mod ID installed. Access to the mod and Forge
buses is provided respectively through the `modBus` and `forgeBus` properties:

```groovy
@Mojo("mymojo")
class MyMojo {
    private static final Logger LOGGER = LogManager.getLogger(MyMojo) // setup a logger
  
    MyMojo() {
        LOGGER.info('Hello from Groovy-land!')

        // register an FMLCommonSetupEvent listener with shorthand syntax
        modBus.onCommonSetup {
            LOGGER.info('Common setup!')
        }
      
        // register an FMLClientSetupEvent listener with typed param closure syntax
        if (FMLEnvironment.dist.isClient()) {
            modBus.addListener { FMLClientSetupEvent event ->
                LOGGER.info('Client setup!')
            }
        }
      
        // register event handler classes or use APLP's @EventBusSubscriber
        modBus.register(ModBusEventHandler)
        forgeBus.register(ForgeBusEventHandler)
    }
}
```

The `modBus` and `forgeBus` properties are added to your mod class at compile-time by the `@Mojo`/`@Mod`/`@GMod` annotation.
For IDE support, either install the [EnhancedGroovy](https://plugins.jetbrains.com/plugin/19844-enhancedgroovy) plugin
or implement `BaseMojo` or `BaseMod` in your mod class, like so:

```groovy
@Mojo("mymojo")
final class MyMojo implements BaseMojo {
    // ...
}
```

APLP also provides its own enhanced `@EventBusSubscriber` annotation. It works similar to Forge's, except it's more
forgiving about method signatures (doesn't require static), doesn't require the mod ID to be specified, has a shorter
syntax and even supports registering event handlers only in certain environments (handy for dev-only testing event
handlers):

```groovy
import net.thesilkminer.mc.austin.api.EventBusSubscriber
import net.thesilkminer.mc.austin.api.EventBus
import net.thesilkminer.mc.austin.api.Environment
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.eventbus.api.SubscribeEvent

// only subscribes event listeners if the mod is loaded in a development environment
@EventBusSubscriber(bus = EventBus.MOD, dist = Dist.CLIENT, environment = Environment.DEV) // mod ID is optional
final class ModBusEventHandler {
    @SubscribeEvent
    void onCommonSetup(final FMLCommonSetupEvent event) { // static is preferred, but not required
      
    }
  
    @SubscribeEvent
    static void onClientSetup(final FMLClientSetupEvent event) {
        
    }
}
```

### Groovy Version

The Groovy version provided by this language provider is: **4.0.11**.

Only actual releases will be supported by this language provider, not release candidates.
Moreover, once a version of Forge is deemed to be production-ready (i.e. a Recommended
Build is published), both the major and minor components of the Groovy version will be
locked. Patch releases will instead be updated periodically.

### Provided Modules

The modules that are provided by default in this language provider do not cover the
whole set of Groovy modules, but should still suffice for both common and some more
particular use cases.

- **Standard Library**: The essential bits to allow Groovy to work.
- **AST Builder**: Captures AST builders from code statements.
- **Contracts**: Allows specification of invariants, pre-, and post-conditions.
- **Date-Time**: Groovy extensions for the Java Date-Time library.
- **Date-Util**: Groovy extensions for the older Java Date library.
- **GINQ**: LINQ-style collection querying interfaces.
- **JSON**: Read and write JSON with a Groovy-like interface.
- **JSR223**: Allows usage of GroovyScript in the Java Scripting Engine.
- **Macro**: Supports the usage of Groovy Macros.
- **Macro Library**: Additional Macro extensions.
- **NIO**: Extension methods to allow for NIO usage in a Groovy-like fashion.
- **PicoCLI**: Allows for compact CLI interfaces.
- **Templates**: Basic templating usage with a set of engines.
- **TOML**: Read and write TOML with a Groovy-like interface.
- **Type Checkers**: Allows verification of regex.
- **XML**: Read and write XML with a Groovy-like interface.
- **YAML**: Read and write YAML with a Groovy-like interface.

### Dropped Modules

Some modules have been dropped from this language provider due to various reasons. They
are listed in the following text.

- **Ant**: This module is not needed in a Minecraft environment, as ForgeGradle already
  provides a dependency management system.
  - Removed in version 1.1.0
- **Binary**: Batch files to call Groovy programs are not required.
- **BOM**: Useful only for development.
- **BSF**: Not available.
- **CLI Commons**: PicoCLI is already available.
- **Compatibility with 2 & 3**: No previous releases with Groovy 2 or Groovy 3 were made.
- **Console**: Interactive execution of Groovy is outside the scope of this LP.
- **Doc Generator**: Useful only for development.
- **GroovyDoc**: Useful only for development.
- **GroovySH**: Interactive execution of Groovy is outside the scope of this LP.
- **JAXB**: Mainly used for Enterprise Development.
- **JMX**: Profiling tools, hardly useful in production.
- **JUnit 5 Test**
  - Removed in version 1.2.0
- **Servlet**: HTML web servers as MC mods?
- **SQL**: Interacting with a SQL database is extremely rare.
- **Swing**: Swing cannot be used in MC without major issues.
- **Test**
  - Removed in 1.2.0 
- **TestNG**: Not used as much as JUnit.

If you disagree with any of these reasons, please reach out to discuss.
