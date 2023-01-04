/*
 * Copyright (c) 2023 TheSilkMiner
 * SPDX-License-Identifier: MIT
 */

package net.thesilkminer.mc.austin.mojotest

import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.thesilkminer.mc.austin.api.EventBus
import net.thesilkminer.mc.austin.api.GrabEventBus
import net.thesilkminer.mc.austin.api.Mojo
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

@Mojo('mojotest')
class AustinPowerfulMojoTest {
    private static final Logger LOGGER = LogManager.getLogger(AustinPowerfulMojoTest)

    @GrabEventBus(EventBus.MOJO)
    private final IEventBus grabbedMojoBus

    @GrabEventBus(EventBus.FORGE)
    private final IEventBus grabbedForgeBus

    AustinPowerfulMojoTest() {
        LOGGER.info('Successfully loaded Groovy mojo "{}"', this.toString())
        LOGGER.info('Say hello to my meta-class {}', this.metaClass)
        //LOGGER.info('Buses are mojo "{}" and Forge "{}"', mojoBus, forgeBus)
        LOGGER.info('Grabbing event buses leads to {} and {}', this.grabbedMojoBus, this.grabbedForgeBus)
        this.grabbedMojoBus.register({ FMLClientSetupEvent event -> LOGGER.info('Successfully received client event {} on mojoBus', event) })
    }
}
