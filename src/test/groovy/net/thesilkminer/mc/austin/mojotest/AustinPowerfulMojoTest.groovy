/*
 * Copyright (c) 2023 TheSilkMiner
 * SPDX-License-Identifier: MIT
 */

package net.thesilkminer.mc.austin.mojotest

import groovy.transform.CompileStatic
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.thesilkminer.mc.austin.api.BaseMojo
import net.thesilkminer.mc.austin.api.Mojo
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

@CompileStatic
@Mojo('mojotest')
class AustinPowerfulMojoTest implements BaseMojo {
    private static final Logger LOGGER = LogManager.getLogger(AustinPowerfulMojoTest)

    AustinPowerfulMojoTest() {
        LOGGER.info('Successfully loaded Groovy mojo "{}"', this.toString())
        LOGGER.info('Buses are mojo "{}" and Forge "{}"', mojoBus, forgeBus)
        this.modBus.addListener { FMLClientSetupEvent event ->
            LOGGER.info('Successfully received client event {} on mojoBus using @CompileStatic addListener', event)
            LOGGER.info SV(GroovySystem.version)
        }
    }
}
