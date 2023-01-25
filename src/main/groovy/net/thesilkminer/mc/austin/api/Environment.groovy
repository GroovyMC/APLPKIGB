package net.thesilkminer.mc.austin.api

import groovy.transform.CompileStatic
import net.minecraftforge.fml.loading.FMLEnvironment

@CompileStatic
final enum Environment {
    DEV,
    PRODUCTION

    static Environment getCurrent() {
        return FMLEnvironment.production ? PRODUCTION : DEV
    }

    private Environment() {}
}
