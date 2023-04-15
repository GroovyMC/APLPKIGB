/*
 * Copyright (c) 2023 TheSilkMiner
 * SPDX-License-Identifier: MIT
 */

package net.thesilkminer.mc.austin.api

/**
 * Identifies the event bus to which an annotation refers to.
 *
 * @since 1.0.0
 */
final enum EventBus {
    /**
     * The Mojo bus, where main mojo lifecycle events are posted. This is the same as {@link EventBus#MOD}.
     *
     * @since 1.0.0
     */
    MOJO,
    /**
     * The Mojo bus, where main mojo lifecycle events are posted.
     *
     * @since 1.0.0
     */
    MOD,
    /**
     * The Forge bus, where game events are posted.
     *
     * @since 1.0.0
     */
    FORGE
}
