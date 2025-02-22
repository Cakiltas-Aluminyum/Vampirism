package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.items.enchantment.EnchantmentArrowFrugality;
import de.teamlapen.vampirism.items.enchantment.EnchantmentVampireSlayer;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

import static de.teamlapen.lib.lib.util.UtilLib.getNull;


public class ModEnchantments {
    public static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, REFERENCE.MODID);

    public static final RegistryObject<EnchantmentArrowFrugality> CROSSBOWFRUGALITY = ENCHANTMENTS.register("crossbowfrugality", () -> new EnchantmentArrowFrugality(Enchantment.Rarity.VERY_RARE));
    public static final RegistryObject<EnchantmentVampireSlayer> VAMPIRESLAYER = ENCHANTMENTS.register("vampireslayer", () -> new EnchantmentVampireSlayer(Enchantment.Rarity.UNCOMMON));


    static void registerEnchantments(IEventBus bus) {
        //Don't forget to add a enchantment description "enchantment.vampirism.<id>.desc" for new enchantments #624
        ENCHANTMENTS.register(bus);
    }

    static void fixMapping(RegistryEvent.MissingMappings<Enchantment> event) {
        event.getAllMappings().forEach(missingMapping -> {
            if ("vampirism:crossbowinfinite".equals(missingMapping.key.toString())) {
                missingMapping.remap(Enchantments.INFINITY_ARROWS);
            }
        });

    }
}
