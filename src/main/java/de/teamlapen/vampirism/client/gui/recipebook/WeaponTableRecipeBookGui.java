package de.teamlapen.vampirism.client.gui.recipebook;

import com.google.common.collect.Lists;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.items.IWeaponTableRecipe;
import de.teamlapen.vampirism.core.ModRecipes;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import net.minecraft.client.gui.recipebook.RecipeBookGui;
import net.minecraft.client.gui.recipebook.RecipeList;
import net.minecraft.client.util.SearchTreeManager;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class WeaponTableRecipeBookGui extends RecipeBookGui {

    @Override
    public void updateCollections(boolean forceFirstPage) { //nearly copied from super method. Only added additional filter using faction player
        List<RecipeList> recipeLists = this.book.getCollection(this.selectedTab.getCategory());
        recipeLists.forEach((p_193944_1_) -> {
            p_193944_1_.canCraft(this.stackedContents, this.menu.getGridWidth(), this.menu.getGridHeight(), this.book);
        });

        List<RecipeList> list1 = Lists.newArrayList(recipeLists);
        FactionPlayerHandler.getOpt(this.minecraft.player).map(FactionPlayerHandler::getCurrentFactionPlayer).filter(Optional::isPresent).map(Optional::get).ifPresent(player -> {
            list1.removeIf(recipeList -> {
                if (recipeList.getRecipes().stream().anyMatch(recipe -> recipe.getType() != ModRecipes.WEAPONTABLE_CRAFTING_TYPE)) {
                    return true;
                }
                return recipeList.getRecipes().stream().anyMatch(recipe -> {
                    if (recipe instanceof IWeaponTableRecipe) {
                        ISkill[] skills = ((IWeaponTableRecipe) recipe).getRequiredSkills();
                        for (ISkill skill : skills) {
                            if (!player.getSkillHandler().isSkillEnabled(skill)) {
                                return true;
                            }
                        }
                        return false;
                    }
                    return true;
                });
            });
        });
        list1.removeIf((recipeList) -> !recipeList.hasKnownRecipes());
        list1.removeIf((p_193953_0_) -> !p_193953_0_.hasFitting());
        String s = this.searchBox.getValue();
        if (!s.isEmpty()) {
            ObjectSet<RecipeList> objectset = new ObjectLinkedOpenHashSet<>(this.minecraft.getSearchTree(SearchTreeManager.RECIPE_COLLECTIONS).search(s.toLowerCase(Locale.ROOT)));
            list1.removeIf((p_193947_1_) -> !objectset.contains(p_193947_1_));
        }
        if (this.book.isFiltering(this.menu)) {
            list1.removeIf((p_193958_0_) -> !p_193958_0_.hasCraftable());
        }
        this.recipeBookPage.updateCollections(list1, forceFirstPage);
    }
}