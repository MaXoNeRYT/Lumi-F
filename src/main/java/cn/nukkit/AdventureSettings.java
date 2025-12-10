package cn.nukkit;

import cn.nukkit.network.protocol.ProtocolInfo;
import cn.nukkit.network.protocol.UpdateAbilitiesPacket;
import cn.nukkit.network.protocol.UpdateAdventureSettingsPacket;
import cn.nukkit.network.protocol.types.AbilityLayer;
import cn.nukkit.network.protocol.types.PlayerAbility;
import lombok.Getter;

import java.util.EnumMap;
import java.util.Map;

/**
 * Adventure settings
 *
 * @author MagicDroidX
 * Nukkit Project
 */
public class AdventureSettings implements Cloneable {


    /**
     * This constant is used to identify flags that should be set on the second field. In a sensible world, these
     * flags would all be set on the same packet field, but as of MCPE 1.2, the new abilities flags have for some
     * reason been assigned a separate field.
     */
    public static final int BITFLAG_SECOND_SET = 65536;

    public static final int WORLD_IMMUTABLE = 0x01;
    public static final int NO_PVM = 0x02;
    public static final int NO_MVP = 0x04;
    public static final int SHOW_NAME_TAGS = 0x10;
    public static final int AUTO_JUMP = 0x20;
    public static final int ALLOW_FLIGHT = 0x40;
    public static final int NO_CLIP = 0x80;
    public static final int WORLD_BUILDER = 0x100;
    public static final int FLYING = 0x200;
    public static final int MUTED = 0x400;
    public static final int MINE = 0x01 | BITFLAG_SECOND_SET;
    public static final int DOORS_AND_SWITCHES = 0x02 | BITFLAG_SECOND_SET;
    public static final int OPEN_CONTAINERS = 0x04 | BITFLAG_SECOND_SET;
    public static final int ATTACK_PLAYERS = 0x08 | BITFLAG_SECOND_SET;
    public static final int ATTACK_MOBS = 0x10 | BITFLAG_SECOND_SET;
    public static final int OPERATOR = 0x20 | BITFLAG_SECOND_SET;
    public static final int TELEPORT = 0x80 | BITFLAG_SECOND_SET;
    public static final int BUILD = 0x100 | BITFLAG_SECOND_SET;
    public static final int DEFAULT_LEVEL_PERMISSIONS = 0x200 | BITFLAG_SECOND_SET;

    private final Map<Type, Boolean> values = new EnumMap<>(Type.class);

    private Player player;

    public AdventureSettings(Player player) {
        this.player = player;
    }

    public AdventureSettings clone(Player newPlayer) {
        try {
            AdventureSettings settings = (AdventureSettings) super.clone();
            settings.player = newPlayer;
            return settings;
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    /**
     * Set an adventure setting value
     *
     * @param type adventure setting
     * @param value new value
     * @return AdventureSettings
     */
    public AdventureSettings set(Type type, boolean value) {
        this.values.put(type, value);
        return this;
    }

    /**
     * Get an adventure setting value
     *
     * @param type adventure setting
     * @return value
     */
    public boolean get(Type type) {
        Boolean value = this.values.get(type);
        return value == null ? type.getDefaultValue() : value;
    }

    /**
     * Send adventure settings values to the player
     */
    public void update() {
        this.update(true);
    }

    /**
     * Send adventure settings values to the player
     * @param reset reset in air ticks
     */
    void update(boolean reset) {
        UpdateAbilitiesPacket packet = new UpdateAbilitiesPacket();
        packet.setEntityId(player.getId());
        packet.setCommandPermission(player.isOp() ? UpdateAbilitiesPacket.CommandPermission.OPERATOR : UpdateAbilitiesPacket.CommandPermission.NORMAL);
        packet.setPlayerPermission(player.isOp() && !player.isSpectator() ? UpdateAbilitiesPacket.PlayerPermission.OPERATOR : UpdateAbilitiesPacket.PlayerPermission.MEMBER);

        AbilityLayer layer = new AbilityLayer();
        layer.setLayerType(AbilityLayer.Type.BASE);
        layer.getAbilitiesSet().addAll(PlayerAbility.VALUES);

        // TODO Multiversion 移除低版本不支持的内容
        for (Type type : Type.values()) {
            if (type.isAbility() && this.get(type) && player.protocol >= type.protocol) {
                layer.getAbilityValues().add(type.getAbility());
            }
        }

        // Because we send speed
        layer.getAbilityValues().add(PlayerAbility.WALK_SPEED);
        layer.getAbilityValues().add(PlayerAbility.FLY_SPEED);
        if (player.protocol >= ProtocolInfo.v1_21_60) {
            layer.getAbilitiesSet().add(PlayerAbility.VERTICAL_FLY_SPEED);
        }

        if (player.isCreative()) { // Make sure player can interact with creative menu
            layer.getAbilityValues().add(PlayerAbility.INSTABUILD);
        }

        if (player.isOp()) {
            layer.getAbilityValues().add(PlayerAbility.OPERATOR_COMMANDS);
        }

        layer.setWalkSpeed(player.getWalkSpeed());
        layer.setFlySpeed(player.getFlySpeed());
        layer.setVerticalFlySpeed(player.getVerticalFlySpeed());
        packet.getAbilityLayers().add(layer);

        if (player.isSpectator()) {
            AbilityLayer spectator = new AbilityLayer();
            spectator.setLayerType(AbilityLayer.Type.SPECTATOR);

            spectator.getAbilitiesSet().addAll(PlayerAbility.VALUES);
            spectator.getAbilitiesSet().remove(PlayerAbility.FLY_SPEED); //不要设置速度，这会导致视角出错
            spectator.getAbilitiesSet().remove(PlayerAbility.WALK_SPEED);
            spectator.getAbilitiesSet().remove(PlayerAbility.VERTICAL_FLY_SPEED);

            for (Type type : Type.values()) {
                if (type.isAbility() && this.get(type) && player.protocol >= type.protocol) {
                    spectator.getAbilityValues().add(type.getAbility());
                }
            }

            if (player.isOp()) {
                layer.getAbilityValues().add(PlayerAbility.OPERATOR_COMMANDS);
            }

            packet.getAbilityLayers().add(spectator);
        }

        UpdateAdventureSettingsPacket adventurePacket = new UpdateAdventureSettingsPacket();
        adventurePacket.setAutoJump(get(Type.AUTO_JUMP));
        adventurePacket.setImmutableWorld(get(Type.WORLD_IMMUTABLE));
        adventurePacket.setNoMvP(get(Type.NO_MVP));
        adventurePacket.setNoPvM(get(Type.NO_PVM));
        adventurePacket.setShowNameTags(get(Type.SHOW_NAME_TAGS));

        player.dataPacket(packet);
        player.dataPacket(adventurePacket);

        if (reset) {
            player.resetInAirTicks();
        }
    }

    /**
     * List of adventure settings
     */
    public enum Type {
        WORLD_IMMUTABLE(AdventureSettings.WORLD_IMMUTABLE, null, false),
        NO_PVM(AdventureSettings.NO_PVM, null, false),
        NO_MVP(AdventureSettings.NO_MVP, PlayerAbility.INVULNERABLE, false),
        SHOW_NAME_TAGS(AdventureSettings.SHOW_NAME_TAGS, null, false),
        AUTO_JUMP(AdventureSettings.AUTO_JUMP, null, true),
        ALLOW_FLIGHT(AdventureSettings.ALLOW_FLIGHT, PlayerAbility.MAY_FLY, false),
        NO_CLIP(AdventureSettings.NO_CLIP, PlayerAbility.NO_CLIP, false),
        WORLD_BUILDER(AdventureSettings.WORLD_BUILDER, PlayerAbility.WORLD_BUILDER, false),
        FLYING(AdventureSettings.FLYING, PlayerAbility.FLYING, false),
        MUTED(AdventureSettings.MUTED, PlayerAbility.MUTED, false),
        MINE(AdventureSettings.MINE, PlayerAbility.MINE, true),
        DOORS_AND_SWITCHED(AdventureSettings.DOORS_AND_SWITCHES, PlayerAbility.DOORS_AND_SWITCHES, true),
        OPEN_CONTAINERS(AdventureSettings.OPEN_CONTAINERS, PlayerAbility.OPEN_CONTAINERS, true),
        ATTACK_PLAYERS(AdventureSettings.ATTACK_PLAYERS, PlayerAbility.ATTACK_PLAYERS, true),
        ATTACK_MOBS(AdventureSettings.ATTACK_MOBS, PlayerAbility.ATTACK_MOBS, true),
        OPERATOR(AdventureSettings.OPERATOR, PlayerAbility.OPERATOR_COMMANDS, false),
        TELEPORT(AdventureSettings.TELEPORT, PlayerAbility.TELEPORT, false),
        BUILD(AdventureSettings.BUILD, PlayerAbility.BUILD, true),
        PRIVILEGED_BUILDER(0, PlayerAbility.PRIVILEGED_BUILDER, false),

        @Deprecated //1.19.30弃用
        DEFAULT_LEVEL_PERMISSIONS(AdventureSettings.DEFAULT_LEVEL_PERMISSIONS, null, false);

        @Getter
        private final int id;
        @Getter
        private final PlayerAbility ability;
        private final boolean defaultValue;
        private final int protocol;

        Type(int id, PlayerAbility ability, boolean defaultValue) {
            this(id, ability, defaultValue, -1);
        }

        Type(int id, PlayerAbility ability, boolean defaultValue, int protocol) {
            this.id = id;
            this.ability = ability;
            this.defaultValue = defaultValue;
            this.protocol = protocol;
        }

        /**
         * Get default value
         *
         * @return default value
         */
        public boolean getDefaultValue() {
            return this.defaultValue;
        }

        public boolean isAbility() {
            return this.ability != null;
        }
    }
}