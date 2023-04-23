/*
 * Copyright (c) 2023 TheSilkMiner
 * SPDX-License-Identifier: MIT
 */

package net.thesilkminer.mc.austin.mojotest

import groovy.transform.CompileStatic
import net.minecraft.client.Minecraft
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import net.thesilkminer.mc.austin.api.EventBus
import net.thesilkminer.mc.austin.api.EventBusSubscriber
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

@EventBusSubscriber(bus = EventBus.MOJO, dist = Dist.CLIENT)
class AustinPowerfulMojoSubsTest {
    private static final Logger LOGGER = LogManager.getLogger(AustinPowerfulMojoSubsTest)

    @SubscribeEvent
    void onCommon(final FMLCommonSetupEvent event) {
        LOGGER.info('Successfully received event {} on mojoBus', event)
        LOGGER.info('Our meta-class is {} and we are {}', this.metaClass, this.toString())
    }

    @SubscribeEvent
    static void onStaticCommon(final FMLCommonSetupEvent event) {
        LOGGER.info('Successfully received static event {} on mojoBus', event)
        LOGGER.info "username: ${Minecraft.instance.user.name}"
    }

    @CompileStatic
    @SubscribeEvent
    static void onStaticCommonCompileStatic(final FMLCommonSetupEvent event) {
        LOGGER.info('Successfully received static event {} on mojoBus using CompileStatic', event)
        LOGGER.info "compilestatic username: ${Minecraft.instance.user.name}"
    }
}
