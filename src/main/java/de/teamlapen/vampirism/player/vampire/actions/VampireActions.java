package de.teamlapen.vampirism.player.vampire.actions;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.player.actions.AttackSpeedLordAction;
import de.teamlapen.vampirism.player.actions.SpeedLordAction;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

import static de.teamlapen.lib.lib.util.UtilLib.getNull;

/**
 * Registers and holds all skills for vampire player
 */
@ObjectHolder(REFERENCE.MODID)
public class VampireActions {
    public static final BatVampireAction bat = getNull();
    public static final DarkBloodProjectileAction dark_blood_projectile = getNull();
    public static final DisguiseVampireAction disguise_vampire = getNull();
    public static final FreezeVampireAction freeze = getNull();
    public static final HalfInvulnerableAction half_invulnerable = getNull();
    public static final RegenVampireAction regen = getNull();
    public static final SunscreenVampireAction sunscreen = getNull();
    public static final SummonBatVampireAction summon_bat = getNull();
    public static final TeleportVampireAction teleport = getNull();
    public static final InvisibilityVampireAction vampire_invisibility = getNull();
    public static final RageVampireAction vampire_rage = getNull();
    public static final HissingAction hissing = getNull();
    public static final InfectAction infect = getNull();
    public static final SpeedLordAction<IVampirePlayer> vampire_lord_speed = getNull();
    public static final AttackSpeedLordAction<IVampirePlayer> vampire_lord_attack_speed = getNull();

    public static void registerDefaultActions(IForgeRegistry<IAction> registry) {
        registry.register(new BatVampireAction().setRegistryName(REFERENCE.MODID, "bat"));
        registry.register(new DarkBloodProjectileAction().setRegistryName(REFERENCE.MODID, "dark_blood_projectile"));
        registry.register(new DisguiseVampireAction().setRegistryName(REFERENCE.MODID, "disguise_vampire"));
        registry.register(new FreezeVampireAction().setRegistryName(REFERENCE.MODID, "freeze"));
        registry.register(new HalfInvulnerableAction().setRegistryName(REFERENCE.MODID, "half_invulnerable"));
        registry.register(new RegenVampireAction().setRegistryName(REFERENCE.MODID, "regen"));
        registry.register(new SunscreenVampireAction().setRegistryName(REFERENCE.MODID, "sunscreen"));
        registry.register(new SummonBatVampireAction().setRegistryName(REFERENCE.MODID, "summon_bat"));
        registry.register(new TeleportVampireAction().setRegistryName(REFERENCE.MODID, "teleport"));
        registry.register(new InvisibilityVampireAction().setRegistryName(REFERENCE.MODID, "vampire_invisibility"));
        registry.register(new RageVampireAction().setRegistryName(REFERENCE.MODID, "vampire_rage"));
        registry.register(new HissingAction().setRegistryName(REFERENCE.MODID, "hissing"));
        registry.register(new InfectAction().setRegistryName(REFERENCE.MODID, "infect"));
        registry.register(new SpeedLordAction<>(VReference.VAMPIRE_FACTION).setRegistryName(REFERENCE.MODID, "vampire_lord_speed"));
        registry.register(new AttackSpeedLordAction<>(VReference.VAMPIRE_FACTION).setRegistryName(REFERENCE.MODID, "vampire_lord_attack_speed"));
    }
}
