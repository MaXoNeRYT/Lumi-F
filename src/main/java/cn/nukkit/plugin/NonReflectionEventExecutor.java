package cn.nukkit.plugin;

import cn.nukkit.event.Event;
import cn.nukkit.event.Listener;
import cn.nukkit.event.EventException;

public class NonReflectionEventExecutor<T extends Event> implements EventExecutor {
    private final Class<T> eventClass;
    private final NonReflectionEventConsumer<T> consumer;

    public NonReflectionEventExecutor(Class<T> eventClass, NonReflectionEventConsumer<T> consumer) {
        this.eventClass = eventClass;
        this.consumer = consumer;
    }

    @Override
    public void execute(Listener listener, Event event) throws EventException {
        if (eventClass.isInstance(event)) {
            consumer.execute(eventClass.cast(event));
        }
    }
}