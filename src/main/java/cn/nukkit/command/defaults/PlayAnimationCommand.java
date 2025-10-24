package cn.nukkit.command.defaults;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.tree.ParamList;
import cn.nukkit.command.utils.CommandLogger;
import cn.nukkit.entity.Entity;
import cn.nukkit.network.protocol.AnimateEntityPacket;

import java.util.List;
import java.util.Map;

/**
 * @author PowerNukkitX Project Team
 */
public class PlayAnimationCommand extends VanillaCommand {

    public PlayAnimationCommand(String name) {
        super(name, "commands.playanimation.description");
        this.setPermission("nukkit.command.playanimation");
        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[]{
                CommandParameter.newType("entity", CommandParamType.TARGET),
                CommandParameter.newType("animation", CommandParamType.STRING),
                CommandParameter.newType("next_state", true, CommandParamType.STRING),
                CommandParameter.newType("blend_out_time", true, CommandParamType.FLOAT),
                CommandParameter.newType("stop_expression", true, CommandParamType.STRING),
                CommandParameter.newType("controller", true, CommandParamType.STRING),
        });
        this.enableParamTree();
    }

    @Override
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        var list = result.getValue();
        List<Entity> entities = list.getResult(0);
        if (entities.isEmpty()) {
            log.addNoTargetMatch().output();
            return 0;
        }

        var animationBuilder = AnimateEntityPacket.Animation.builder();
        animationBuilder.animation(list.getResult(1));

        if (list.hasResult(2)) animationBuilder.nextState(list.getResult(2));
        if (list.hasResult(3)) animationBuilder.blendOutTime(list.getResult(3));
        if (list.hasResult(4)) animationBuilder.stopExpression(list.getResult(4));
        if (list.hasResult(5)) animationBuilder.controller(list.getResult(5));

        AnimateEntityPacket.Animation ani = animationBuilder.build();

        // выводим данные анимации в чат игрока напрямую через поля
        sender.sendMessage("§aОтправляем анимацию:");
        sender.sendMessage("§eanimation: §f" + ani.animation);
        sender.sendMessage("§enextState: §f" + ani.nextState);
        sender.sendMessage("§eblendOutTime: §f" + ani.blendOutTime);
        sender.sendMessage("§estopExpression: §f" + ani.stopExpression);
        sender.sendMessage("§econtroller: §f" + ani.controller);
        sender.sendMessage("§estopExpressionVersion: §f" + ani.stopExpressionVersion);

        Entity.playAnimationOnEntities(ani, entities);

        log.addSuccess("commands.playanimation.success").output();
        return 1;
    }

}
