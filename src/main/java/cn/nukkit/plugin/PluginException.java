package cn.nukkit.plugin;

import cn.nukkit.utils.ServerException;

/**
 * PluginException
 *
 * @author MagicDroidX
 * Nukkit Project
 */
public class PluginException extends ServerException {

    public PluginException(String message) {
        super(message);
    }

    public PluginException(String message, Throwable cause) {
        super(message, cause);
    }
}
