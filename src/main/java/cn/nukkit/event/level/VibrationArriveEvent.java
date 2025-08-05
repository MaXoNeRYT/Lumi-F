package cn.nukkit.event.level;

import cn.nukkit.event.HandlerList;
import cn.nukkit.level.vibration.VibrationListener;
import lombok.Getter;

public class VibrationArriveEvent extends VibrationEvent {

    @Getter
    private static final HandlerList handlers = new HandlerList();

    protected VibrationListener listener;

    public VibrationArriveEvent(cn.nukkit.level.vibration.VibrationEvent vibrationEvent, VibrationListener listener) {
        super(vibrationEvent);
        this.listener = listener;
    }
}
