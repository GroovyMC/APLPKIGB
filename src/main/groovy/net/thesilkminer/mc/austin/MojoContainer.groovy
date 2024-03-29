/*
 * Copyright (c) 2023 TheSilkMiner
 * SPDX-License-Identifier: MIT
 */

package net.thesilkminer.mc.austin

import groovy.transform.CompileStatic
import net.minecraftforge.eventbus.EventBusErrorMessage
import net.minecraftforge.eventbus.api.BusBuilder
import net.minecraftforge.eventbus.api.Event
import net.minecraftforge.fml.Logging
import net.minecraftforge.fml.ModContainer
import net.minecraftforge.fml.ModLoadingException
import net.minecraftforge.fml.ModLoadingStage
import net.minecraftforge.fml.config.IConfigEvent
import net.minecraftforge.fml.event.IModBusEvent
import net.minecraftforge.forgespi.language.IModInfo
import net.minecraftforge.forgespi.language.ModFileScanData
import net.thesilkminer.mc.austin.api.MojoEventBus
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import java.util.function.Consumer
import java.util.function.Supplier

@CompileStatic
final class MojoContainer extends ModContainer {
    private static final String CLASS_ERROR = 'fml.modloading.failedtoloadmodclass'
    private static final String MOD_ERROR = 'fml.modloading.failedtoloadmod'
    private static final String EVENT_ERROR = 'fml.modloading.errorduringevent'

    private static final Logger LOGGER = LogManager.getLogger(MojoContainer)

    final MojoEventBus mojoBus

    private final ModFileScanData scanData
    @SuppressWarnings('GrFinalVariableAccess') private final Class<?> mojoClass // It was initialized, Groovy

    private Object mojo

    MojoContainer(final IModInfo info, final String className, final ModFileScanData scanData, final ModuleLayer layer) {
        super(info)
        LOGGER.debug(Logging.LOADING,'Creating Mojo container for {} on classloader {} with layer {}', className, this.class.classLoader, layer)

        this.scanData = scanData
        this.mojoBus = new MojoEventBus(BusBuilder.builder()
                .setExceptionHandler {bus, event, listeners, i, cause -> LOGGER.error(new EventBusErrorMessage(event, i, listeners, cause)) }
                .setTrackPhases(false) // What does this even do?
                .markerType(IModBusEvent)
                .build())

        this.activityMap[ModLoadingStage.CONSTRUCT] = this.&constructMojo
        this.configHandler = Optional.of(this.&postConfigEvent as Consumer<IConfigEvent>)
        this.contextExtension = { null } as Supplier<?> // Oh yes, lambdas...

        try {
            final module = layer.findModule(info.owningFile.moduleName()).orElseThrow()
            this.mojoClass = Class.forName(module, className)
            LOGGER.trace(Logging.LOADING, 'Loaded class {} on class loader {}: time to get Groovy', this.mojoClass.name, this.mojoClass.classLoader)
        } catch (final Throwable t) {
            LOGGER.fatal(Logging.LOADING, "An error occurred while attempting to load class $className", t)
            throw new ModLoadingException(info, ModLoadingStage.CONSTRUCT, CLASS_ERROR, t)
        }
    }

    @Override
    boolean matches(final Object mod) {
        return mod !== null && mod.is(this.mojo)
    }

    @Override
    Object getMod() {
        return this.mojo
    }

    @Override
    <T extends Event & IModBusEvent> void acceptEvent(final T e) {
        try {
            LOGGER.trace(Logging.LOADING, 'Firing event {} for mojo {}', e, this.modId)
            this.mojoBus.post(e)
            LOGGER.trace(Logging.LOADING, 'Fired event {} for mojo {}', e, this.modId)
        } catch (Throwable t) {
            LOGGER.fatal(Logging.LOADING,"Caught exception in mojo '${this.modId}' during event dispatch for $e", t)
            throw new ModLoadingException(this.modInfo, this.modLoadingStage, EVENT_ERROR, t)
        }
    }

    private void constructMojo() {
        this.createMojo()
        if (this.scanData !== null) {
            this.injectEventListeners()
        }
    }

    private void createMojo() {
        try {
            LOGGER.trace(Logging.LOADING, 'Loading mojo class {} for {}', this.mojoClass.name, this.modId)
            final mojoConstructor = this.mojoClass.getConstructor(MojoContainer)
            this.mojo = mojoConstructor.newInstance(this)
            LOGGER.trace(Logging.LOADING, 'Successfully loaded mojo {}', this.modId)
        } catch (final Throwable t) {
            LOGGER.fatal(Logging.LOADING, "Failed to create mojo from class ${this.mojoClass.name} for mojo ${this.modId}", t)
            throw new ModLoadingException(this.modInfo, ModLoadingStage.CONSTRUCT, MOD_ERROR, t, this.mojoClass)
        }
    }

    private void injectEventListeners() {
        try {
            LOGGER.trace(Logging.LOADING, 'Registering event bus subscribers for mojo {}', this.modId)
            final MojoEventBusSubscriber subscriber = new MojoEventBusSubscriber(
                    mojoContainer: this,
                    scanData: this.scanData,
                    loader: this.mojoClass.classLoader
            )
            subscriber.doSubscribing()
            LOGGER.trace(Logging.LOADING, 'Successfully registered subscribers for mojo {}', this.modId)
        } catch (final Throwable t) {
            LOGGER.fatal(Logging.LOADING, "Failed to inject automatic event bus subscribers for mojo ${this.modId}", t)
            throw new ModLoadingException(this.modInfo, ModLoadingStage.CONSTRUCT, MOD_ERROR, t, this.mojoClass)
        }
    }

    private void postConfigEvent(final IConfigEvent event) {
        this.mojoBus.post(event.self())
    }
}
