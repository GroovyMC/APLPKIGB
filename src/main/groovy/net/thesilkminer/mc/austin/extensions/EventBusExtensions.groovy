package net.thesilkminer.mc.austin.extensions

import groovy.transform.CompileStatic
import groovy.transform.stc.ClosureParams
import groovy.transform.stc.FromAbstractTypeMethods
import net.minecraftforge.eventbus.api.Event
import net.minecraftforge.eventbus.api.EventPriority
import net.minecraftforge.eventbus.api.IEventBus

@CompileStatic
class EventBusExtensions {
    /**
     * @param closure A closure or borrowed method with the first parameter being event type you want to listen to
     * @param priority {@link EventPriority} for this listener
     * @param receiveCancelled Indicates whether this listener should receive cancelled events
     */
    static <T extends Event> void addListener(final IEventBus self,
                                              final EventPriority priority = EventPriority.NORMAL,
                                              final boolean receiveCancelled = false,
                                              @ClosureParams(value = FromAbstractTypeMethods, options = 'net.minecraftforge.eventbus.api.Event') final Closure<?> closure) {
        if (closure.parameterTypes.size() === 0 || closure.parameterTypes[0] === Object)
            throw new IllegalArgumentException('Closure must have one explicitly typed parameter. For example: modBus.addListener { FMLCommonSetupEvent event -> ... }')

        final Class<T> eventClass = closure.parameterTypes[0] as Class<T>
        self.<T>addListener(priority, receiveCancelled, eventClass, (T event) -> closure.call(event))
    }
}
