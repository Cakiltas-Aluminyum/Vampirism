package de.teamlapen.vampirism.inventory.container;

import de.teamlapen.lib.lib.inventory.InventoryContainer;
import de.teamlapen.lib.lib.inventory.InventoryHelper;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.core.ModContainer;
import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.core.ModTags;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.entity.hunter.HunterTrainerEntity;
import de.teamlapen.vampirism.items.HunterIntelItem;
import de.teamlapen.vampirism.player.hunter.HunterLevelingConf;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.IInventoryChangedListener;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nullable;

/**
 * Container which handles hunter levelup at an hunter trainer
 */
public class HunterTrainerContainer extends InventoryContainer implements IInventoryChangedListener {
    private static final SelectorInfo[] SELECTOR_INFOS = new SelectorInfo[]{new SelectorInfo(Items.IRON_INGOT, 27, 26), new SelectorInfo(Items.GOLD_INGOT, 57, 26), new SelectorInfo(ModTags.Items.HUNTER_INTEL, 86, 26)};
    private final PlayerEntity player;
    @Nullable
    private final HunterTrainerEntity entity;
    private boolean changed = false;
    private ItemStack missing = ItemStack.EMPTY;

    @Deprecated
    public HunterTrainerContainer(int id, PlayerInventory playerInventory) {
        this(id, playerInventory, null);
    }

    public HunterTrainerContainer(int id, PlayerInventory playerInventory, @Nullable HunterTrainerEntity trainer) {
        super(ModContainer.hunter_trainer, id, playerInventory, trainer == null ? IWorldPosCallable.DUMMY : IWorldPosCallable.of(trainer.world, trainer.getPosition()), new Inventory(SELECTOR_INFOS.length), SELECTOR_INFOS);
        ((Inventory) this.inventory).addListener(this);
        this.player = playerInventory.player;
        this.addPlayerSlots(playerInventory);
        this.entity = trainer;
    }

    @Override
    public boolean canInteractWith(PlayerEntity player) {
        if (entity == null) return false;
        return new Vec3d(player.posX, player.posY, player.posZ).distanceTo(new Vec3d(entity.posX, entity.posY, entity.posZ)) < 5;
    }

    /**
     * @return If the player can levelup with the given tileInventory
     */
    public boolean canLevelup() {
        int targetLevel = FactionPlayerHandler.get(player).getCurrentLevel(VReference.HUNTER_FACTION) + 1;
        HunterLevelingConf levelingConf = HunterLevelingConf.instance();
        if (!levelingConf.isLevelValidForTrainer(targetLevel)) return false;
        int[] req = levelingConf.getItemRequirementsForTrainer(targetLevel);
        int level = levelingConf.getHunterIntelMetaForLevel(targetLevel);
        missing = InventoryHelper.checkItems(inventory, new Item[]{Items.IRON_INGOT, Items.GOLD_INGOT, HunterIntelItem.getIntelForLevel(level)}, new int[]{req[0], req[1], 1}, (supplied, required) -> supplied.equals(required) || (supplied instanceof HunterIntelItem && required instanceof HunterIntelItem && ((HunterIntelItem) supplied).getLevel() >= ((HunterIntelItem) required).getLevel()));
        return missing.isEmpty();
    }

    /**
     * @return The missing Itemstack or null if nothing is missing
     */
    public ItemStack getMissingItems() {
        return this.missing;
    }

    /**
     * @return If the inventory has changed since the last call
     */
    public boolean hasChanged() {
        if (changed) {
            changed = false;
            return true;
        }
        return false;
    }

    @Override
    public void onContainerClosed(PlayerEntity playerIn) {
        super.onContainerClosed(playerIn);
        if (!playerIn.getEntityWorld().isRemote) {
            clearContainer(playerIn, playerIn.getEntityWorld(), inventory);
        }
    }

    @Override
    public void onInventoryChanged(IInventory iInventory) {
        changed = true;
    }

    /**
     * Called via input packet, when the player clicks the levelup button.
     */
    public void onLevelupClicked() {
        if (canLevelup()) {
            int old = FactionPlayerHandler.get(player).getCurrentLevel(VReference.HUNTER_FACTION);
            FactionPlayerHandler.get(player).setFactionLevel(VReference.HUNTER_FACTION, old + 1);
            int[] req = HunterLevelingConf.instance().getItemRequirementsForTrainer(old + 1);
            InventoryHelper.removeItems(inventory, new int[]{req[0], req[1], 1});
            player.addPotionEffect(new EffectInstance(ModEffects.saturation, 400, 2));
            changed = true;
        }
    }

    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerEntity, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack slotStack = slot.getStack();
            result = slotStack.copy();
            if (index >= 3) {
                if (index < 30) {
                    if (!this.mergeItemStack(slotStack, 0, 3, false)) {
                        return ItemStack.EMPTY;
                    } else if (this.mergeItemStack(slotStack, 30, 39, false)) {
                        return ItemStack.EMPTY;
                    }
                } else {
                    if (!this.mergeItemStack(slotStack, 0, 30, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            } else if (!this.mergeItemStack(slotStack, 3, 39, false)) {
                return ItemStack.EMPTY;
            }

            if (slotStack.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }

            if (slotStack.getCount() == result.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerEntity, slotStack);
        }
        //changed = true;
        return result;
    }
}
