package cn.nukkit.plugin;

import cn.nukkit.event.Cancellable;
import cn.nukkit.event.Event;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.EventException;

/**
 * @author MagicDroidX
 * Nukkit Project
 */
public class RegisteredListener {

    private final Listener listener;
    private final NonReflectionEventConsumer<?> consumer;

    private final EventPriority priority;

    private final Plugin plugin;

    private final EventExecutor executor;

    private final boolean ignoreCancelled;

    public RegisteredListener(Listener listener, NonReflectionEventConsumer<?> consumer, EventExecutor executor, EventPriority priority, Plugin plugin, boolean ignoreCancelled) {
        this.listener = listener;
        this.consumer = consumer;
        this.priority = priority;
        this.plugin = plugin;
        this.executor = executor;
        this.ignoreCancelled = ignoreCancelled;
    }

    public Listener getListener() {
        return listener;
    }

    public NonReflectionEventConsumer<?> getConsumer() {
        return consumer;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public EventPriority getPriority() {
        return priority;
    }

    public void callEvent(Event event) throws EventException {
        if (event instanceof Cancellable) {
            if (event.isCancelled() && ignoreCancelled) {
                return;
            }
        }
        executor.execute(listener, event);
    }

    public boolean isIgnoringCancelled() {
        return ignoreCancelled;
    }
}
