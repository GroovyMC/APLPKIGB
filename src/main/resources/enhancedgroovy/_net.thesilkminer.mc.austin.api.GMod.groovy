/*
 * Copyright (c) 2023 TheSilkMiner
 * SPDX-License-Identifier: MIT
 */

//file:noinspection GrPackage
import com.matyrobbrt.enhancedgroovy.dsl.ClassTransformer

((ClassTransformer) this.transformer).tap {
    addField name: 'mojoBus',
             type: 'net.minecraftforge.eventbus.api.IEventBus',
             modifiers: ['private', 'final']

    addField name: 'modBus',
             type: 'net.minecraftforge.eventbus.api.IEventBus',
             modifiers: ['private', 'final']

    addField name: 'forgeBus',
             type: 'net.minecraftforge.eventbus.api.IEventBus',
             modifiers: ['private', 'final']

    addMethod name: 'getMojoBus',
              returnType: 'net.minecraftforge.eventbus.api.IEventBus',
              modifiers: ['private', 'final']

    addMethod name: 'getModBus',
              returnType: 'net.minecraftforge.eventbus.api.IEventBus',
              modifiers: ['private', 'final']

    addMethod name: 'getForgeBus',
              returnType: 'net.minecraftforge.eventbus.api.IEventBus',
              modifiers: ['private', 'final']
}
