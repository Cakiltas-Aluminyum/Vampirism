package de.teamlapen.vampirism.inventory.inventory;

import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.items.IWeaponTableRecipe;
import de.teamlapen.vampirism.blocks.WeaponTableBlock;
import de.teamlapen.vampirism.core.ModRecipes;
import de.teamlapen.vampirism.core.ModStats;
import de.teamlapen.vampirism.player.hunter.HunterPlayer;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.NonNullList;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * Result slot for the hunter weapon crafting table
 */
public class WeaponTableCraftingSlot extends Slot {
    private final PlayerEntity player;
    private final IWorldPosCallable worldPos;
    private final CraftingInventory craftMatrix;
    private int amountCrafted = 0;

    public WeaponTableCraftingSlot(PlayerEntity player, CraftingInventory craftingInventory, IInventory inventoryIn, int index, int xPosition, int yPosition, IWorldPosCallable worldPosCallable) {
        super(inventoryIn, index, xPosition, yPosition);
        this.player = player;
        this.craftMatrix = craftingInventory;
        this.worldPos = worldPosCallable;
    }

    @Override
    public boolean mayPlace(@Nullable ItemStack stack) {
        return false;
    }

    @Override
    public ItemStack onTake(PlayerEntity playerIn, ItemStack stack) {
        this.checkTakeAchievements(stack);
        final int lava = worldPos.evaluate(((world, blockPos) -> {
            if (world.getBlockState(blockPos).getBlock() instanceof WeaponTableBlock) {
                return world.getBlockState(blockPos).getValue(WeaponTableBlock.LAVA);
            }
            return 0;
        }), 0);
        final HunterPlayer hunterPlayer = HunterPlayer.get(playerIn);
        final IWeaponTableRecipe recipe = findMatchingRecipe(playerIn, hunterPlayer, lava);
        if (recipe != null && recipe.getRequiredLavaUnits() > 0) {
            worldPos.execute(((world, pos) -> {
                int remainingLava = Math.max(0, lava - recipe.getRequiredLavaUnits());
                if (world.getBlockState(pos).getBlock() instanceof WeaponTableBlock) {
                    world.setBlockAndUpdate(pos, world.getBlockState(pos).setValue(WeaponTableBlock.LAVA, remainingLava));
                }
            }));
        }
        net.minecraftforge.common.ForgeHooks.setCraftingPlayer(playerIn);
        NonNullList<ItemStack> remaining = playerIn.level.getRecipeManager().getRemainingItemsFor(ModRecipes.WEAPONTABLE_CRAFTING_TYPE, this.craftMatrix, playerIn.level);
        net.minecraftforge.common.ForgeHooks.setCraftingPlayer(null);
        for (int i = 0; i < remaining.size(); ++i) {
            ItemStack itemstack = this.craftMatrix.getItem(i);
            ItemStack itemstack1 = remaining.get(i);

            if (!itemstack.isEmpty()) {
                this.craftMatrix.removeItem(i, 1);
                itemstack = this.craftMatrix.getItem(i);
            }
            if (!itemstack1.isEmpty()) {
                if (itemstack.isEmpty()) {
                    this.craftMatrix.setItem(i, itemstack1);
                } else if (ItemStack.isSame(itemstack, itemstack1) && ItemStack.tagMatches(itemstack, itemstack1)) {
                    itemstack1.grow(itemstack.getCount());
                    this.craftMatrix.setItem(i, itemstack1);
                } else if (!this.player.inventory.add(itemstack1)) {
                    this.player.drop(itemstack1, false);
                }
            }
        }
        worldPos.execute(((world, pos) -> {
            if (recipe != null && !world.isClientSide) {
                //Play anvil sound
                world.levelEvent(1030, pos, 0);
            }
        }));
        playerIn.awardStat(ModStats.weapon_table);
        return stack;
    }

    @Override
    public ItemStack remove(int amount) {
        if (this.hasItem()) {
            this.amountCrafted += Math.min(amount, this.getItem().getCount());

        }
        return super.remove(amount);
    }

    @Override
    protected void checkTakeAchievements(ItemStack stack) {
        if (this.amountCrafted > 0) {
            stack.onCraftedBy(this.player.getCommandSenderWorld(), this.player, this.amountCrafted);
        }

        this.amountCrafted = 0;
    }

    protected IWeaponTableRecipe findMatchingRecipe(PlayerEntity playerIn, IFactionPlayer<?> factionPlayer, int lava) {
        Optional<IWeaponTableRecipe> optional = playerIn.getCommandSenderWorld().getRecipeManager().getRecipeFor(ModRecipes.WEAPONTABLE_CRAFTING_TYPE, this.craftMatrix, playerIn.getCommandSenderWorld());
        if (optional.isPresent()) {
            IWeaponTableRecipe recipe = optional.get();
            if (factionPlayer.getLevel() >= recipe.getRequiredLevel() && lava >= recipe.getRequiredLavaUnits() && Helper.areSkillsEnabled(factionPlayer.getSkillHandler(), recipe.getRequiredSkills())) {
                return recipe;
            }
        }
        return null;
    }

    @Override
    protected void onQuickCraft(ItemStack stack, int amount) {
        this.amountCrafted += amount;
        this.checkTakeAchievements(stack);
    }
}
