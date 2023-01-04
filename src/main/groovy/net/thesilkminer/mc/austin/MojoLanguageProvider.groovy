/*
 * Copyright (c) 2023 TheSilkMiner
 * SPDX-License-Identifier: MIT
 */

package net.thesilkminer.mc.austin

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import net.minecraftforge.fml.Logging
import net.minecraftforge.forgespi.language.ILifecycleEvent
import net.minecraftforge.forgespi.language.IModLanguageProvider
import net.minecraftforge.forgespi.language.ModFileScanData
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.objectweb.asm.Type

import java.util.function.Consumer
import java.util.function.Supplier

@CompileStatic
final class MojoLanguageProvider implements IModLanguageProvider {
    @SuppressWarnings('SpellCheckingInspection') private static final String NAME = 'aplp'
    @SuppressWarnings('SpellCheckingInspection') private static final String MOD_DESC = 'Lnet/thesilkminer/mc/austin/api/Mod;'
    @SuppressWarnings('SpellCheckingInspection') private static final String MOJO_DESC = 'Lnet/thesilkminer/mc/austin/api/Mojo;'

    private static final Logger LOGGER = LogManager.getLogger(MojoLanguageProvider)
    private static final Type MOD_ANNOTATION = Type.getType(MOD_DESC)
    private static final Type MOJO_ANNOTATION = Type.getType(MOJO_DESC)

    MojoLanguageProvider() {
        LOGGER.info('Successfully initialized Mojo Language Provider on name {}', this.name())
    }

    @Override
    String name() {
        NAME
    }

    @CompileDynamic
    @Override
    Consumer<ModFileScanData> getFileVisitor() {
        return { scanData ->
            final Map<String, MojoLanguageLoader> mojos = scanData.annotations
                    .findAll { it.annotationType() == MOD_ANNOTATION || it.annotationType() == MOJO_ANNOTATION }
                    .collect { new MojoLanguageLoader(className: it.clazz().className, mojoId: it.annotationData()['value']) }
                    .each { LOGGER.debug(Logging.SCAN, 'Found entry-point Mojo class "{}" for ID "{}"', it.className, it.mojoId) }
                    .collectEntries { [it.mojoId, it] }
            scanData.addLanguageLoader(mojos)
        }
    }

    @Override
    <R extends ILifecycleEvent<R>> void consumeLifecycleEvent(final Supplier<R> consumeEvent) {}
}
