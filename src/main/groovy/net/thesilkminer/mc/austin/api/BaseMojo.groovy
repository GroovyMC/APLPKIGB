/*
 * Copyright (c) 2023 TheSilkMiner
 * SPDX-License-Identifier: MIT
 */

package net.thesilkminer.mc.austin.api

import groovy.transform.CompileStatic
import net.minecraftforge.eventbus.api.IEventBus

/**
 * An interface providing IDE support for properties added by the {@literal @}Mod, {@literal @}GMod and {@literal @}Mojo annotations.
 *
 * <p>This is solely used for IDEs missing the
 * <a href="https://plugins.jetbrains.com/plugin/19844-enhancedgroovy">EnhancedGroovy</a> plugin and is not required.</p>
 */
@CompileStatic
interface BaseMojo {
    default IEventBus getModBus() {
        throw new IllegalStateException('@Mojo/@Mod Transformer failed injection')
    }

    default IEventBus getMojoBus() {
        throw new IllegalStateException('@Mojo/@Mod Transformer failed injection')
    }

    default IEventBus getForgeBus() {
        throw new IllegalStateException('@Mojo/@Mod Transformer failed injection')
    }
}
