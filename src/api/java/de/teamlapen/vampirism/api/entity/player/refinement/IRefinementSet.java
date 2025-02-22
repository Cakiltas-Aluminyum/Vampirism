package de.teamlapen.vampirism.api.entity.player.refinement;

import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.items.IRefinementItem;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.Set;

public interface IRefinementSet extends IForgeRegistryEntry<IRefinementSet> {

    int getColor();

    @Nonnull
    IFaction<?> getFaction();

    @Nonnull
    ITextComponent getName();

    @Nonnull
    Rarity getRarity();

    /**
     * TODO 1.19 remove and rename the new method
     * Use {@link #getRefinementRegistryObjects()}
     */
    @Deprecated
    @Nonnull
    Set<IRefinement> getRefinements();

    @Nonnull
    Set<RegistryObject<? extends IRefinement>> getRefinementRegistryObjects();

    /**
     * @return The accessory type this can be on, or empty if all
     */
    Optional<IRefinementItem.AccessorySlotType> getSlotType();

    enum Rarity {
        COMMON(4, TextFormatting.WHITE),
        UNCOMMON(3, TextFormatting.GREEN),
        RARE(3, TextFormatting.BLUE),
        EPIC(2, TextFormatting.DARK_PURPLE),
        LEGENDARY(1, TextFormatting.GOLD);

        public final int weight;
        public final TextFormatting color;

        Rarity(int weight, TextFormatting color) {
            this.weight = weight;
            this.color = color;
        }
    }
}
