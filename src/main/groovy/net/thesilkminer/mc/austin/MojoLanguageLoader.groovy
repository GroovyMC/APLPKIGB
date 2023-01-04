/*
 * Copyright (c) 2023 TheSilkMiner
 * SPDX-License-Identifier: MIT
 */

package net.thesilkminer.mc.austin

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import groovy.transform.MapConstructor
import groovy.transform.PackageScope
import groovy.transform.VisibilityOptions
import groovy.transform.options.Visibility
import net.minecraftforge.fml.Logging
import net.minecraftforge.fml.loading.FMLEnvironment
import net.minecraftforge.forgespi.language.IModInfo
import net.minecraftforge.forgespi.language.IModLanguageProvider
import net.minecraftforge.forgespi.language.ModFileScanData
import net.thesilkminer.mc.austin.mappings.MappingMetaClassCreationHandle
import net.thesilkminer.mc.austin.mappings.MappingsProvider
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import java.lang.reflect.InvocationTargetException

@CompileStatic
@MapConstructor
@PackageScope
@VisibilityOptions(constructor = Visibility.PACKAGE_PRIVATE)
final class MojoLanguageLoader implements IModLanguageProvider.IModLanguageLoader {
    @SuppressWarnings('SpellCheckingInspection') private static final String MOJO_CONTAINER = 'net.thesilkminer.mc.austin.MojoContainer'
    @SuppressWarnings('SpellCheckingInspection') private static final String MOD_LOADING_EXCEPTION = 'net.minecraftforge.fml.ModLoadingException'
    @SuppressWarnings('SpellCheckingInspection') private static final String MOD_LOADING_STAGE = 'net.minecraftforge.fml.ModLoadingStage'

    @SuppressWarnings('SpellCheckingInspection') private static final String LOADING_FAILED = "fml.modloading.failedtoloadmodclass"

    private static final Logger LOGGER = LogManager.getLogger(MojoLanguageLoader)

    final String className
    final String mojoId

    @CompileDynamic
    @Override
    <T> T loadMod(final IModInfo info, final ModFileScanData modFileScanResults, final ModuleLayer layer) {
        // cpw and his love for over-complicated stuff
        // oh well, let's have neat class-loader separation
        def threadLoader = Thread.currentThread().getContextClassLoader()

        if (FMLEnvironment.production) {
            // Only load this while in production; no need to cause potentially unexpected behavior in dev.
            MappingMetaClassCreationHandle.applyCreationHandle(MappingsProvider.INSTANCE.mappingsProvider.get(), threadLoader)
        }

        // GroovyAssignabilityChecks are suppressed because they are actually throwables at runtime
        def throwModLoadingException = { String stage, Throwable cause, String message ->
            def modLoadingException = Class.forName(MOD_LOADING_EXCEPTION, true, threadLoader)

            if (cause instanceof InvocationTargetException && modLoadingException.isInstance(cause.targetException)) {
                //noinspection GroovyAssignabilityCheck
                throw modLoadingException.cast(cause.targetException)
            }

            def modLoadingStage = Class.forName(MOD_LOADING_STAGE, true, threadLoader)
            def modLoadingExceptionConstructor = modLoadingException.getConstructor(IModInfo, modLoadingStage, String, Throwable)
            //noinspection GroovyAssignabilityCheck
            throw modLoadingExceptionConstructor.newInstance(info, Enum.valueOf(modLoadingStage, stage), message, cause)
        }

        try {
            def mojoContainer = Class.forName(MOJO_CONTAINER, true, threadLoader)
            if (mojoContainer.classLoader != threadLoader) {
                LOGGER.error(Logging.LOADING, 'Attempting to load MojoContainer from classloader {} actually resulted in {}', threadLoader, mojoContainer.classLoader)
            }
            def mojoConstructor = mojoContainer.getConstructor(IModInfo, String, ModFileScanData, ModuleLayer)
            mojoConstructor.newInstance(info, this.className, modFileScanResults, layer) as T
        } catch (final InvocationTargetException e) {
            LOGGER.fatal(Logging.LOADING, "A fatal error occurred while attempting to build mojo ${this.mojoId}", e)

            throwModLoadingException('CONSTRUCT', e, LOADING_FAILED)
        } catch (NoSuchMethodException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            LOGGER.fatal(Logging.LOADING, "A fatal error has occurred while attempting to load the MojoContainer for mod ${this.mojoId}", e)

            throwModLoadingException('CONSTRUCT', e, LOADING_FAILED)
        }
    }

    @Override
    String toString() {
        return "${this.mojoId}@${this.className}"
    }
}
