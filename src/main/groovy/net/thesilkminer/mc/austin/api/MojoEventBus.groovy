/*
 * Copyright (c) 2023 TheSilkMiner
 * SPDX-License-Identifier: MIT
 */

package net.thesilkminer.mc.austin.api

import groovy.transform.CompileStatic
import groovy.transform.TupleConstructor
import groovy.transform.stc.ClosureParams
import groovy.transform.stc.SimpleType
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent
import net.minecraftforge.fml.loading.FMLEnvironment
import net.thesilkminer.mc.austin.extensions.EventBusExtensions

@CompileStatic
@TupleConstructor
final class MojoEventBus implements IEventBus {
    @Delegate
    final IEventBus delegate

    void onCommonSetup(@ClosureParams(value = SimpleType, options = 'net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent')
                       final Closure<?> closure) {
        EventBusExtensions.addListener(delegate, { FMLCommonSetupEvent event -> closure.call(event) })
    }

    void onClientSetup(@ClosureParams(value = SimpleType, options = 'net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent')
                       final Closure<?> closure) {
        if (FMLEnvironment.dist.isClient())
            EventBusExtensions.addListener(delegate, { FMLClientSetupEvent event -> closure.call(event) })
    }

    void onDedicatedServerSetup(@ClosureParams(value = SimpleType, options = 'net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent')
                                final Closure<?> closure) {
        if (FMLEnvironment.dist.isDedicatedServer())
            EventBusExtensions.addListener(delegate, { FMLDedicatedServerSetupEvent event -> closure.call(event) })
    }

    void onLoadComplete(@ClosureParams(value = SimpleType, options = 'net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent')
                        final Closure<?> closure) {
        EventBusExtensions.addListener(delegate, { FMLLoadCompleteEvent event -> closure.call(event) })
    }
}
