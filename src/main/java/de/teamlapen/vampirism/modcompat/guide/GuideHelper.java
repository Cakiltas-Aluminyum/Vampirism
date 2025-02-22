package de.teamlapen.vampirism.modcompat.guide;

import de.maxanier.guideapi.page.PageHolderWithLinks;
import de.maxanier.guideapi.page.PageItemStack;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.task.Task;
import de.teamlapen.vampirism.api.entity.player.task.TaskUnlocker;
import de.teamlapen.vampirism.player.tasks.reward.ItemReward;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Collection of helper methods
 */
public class GuideHelper {

    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * Adds multiple strings together seperated by a double line break
     *
     * @param unlocalized Unlocalized strings
     */
    public static String append(String... unlocalized) {
        StringBuilder s = new StringBuilder();
        for (String u : unlocalized) {
            s.append(UtilLib.translate(u)).append("\n\n");
        }
        return s.toString();
    }

    /**
     * Create a simple page informing the reader about a task that can be used to obtain an item
     */
    public static PageItemStack createItemTaskDescription(Task task) {
        assert task.getReward() instanceof ItemReward;
        Ingredient ingredient = Ingredient.of(((ItemReward) task.getReward()).getAllPossibleRewards().stream());
        List<ITextProperties> text = new ArrayList<>();
        StringTextComponent newLine = new StringTextComponent("\n");
        IPlayableFaction<?> f = task.getFaction();
        String type = f == null ? "" : f.getName().getString() + " ";
        text.add(new TranslationTextComponent("text.vampirism.task.reward_obtain", type));
        text.add(newLine);
        text.add(newLine);
        text.add(task.getTranslation());
        text.add(newLine);
        text.add(new TranslationTextComponent("text.vampirism.task.prerequisites"));
        text.add(newLine);
        TaskUnlocker[] unlockers = task.getUnlocker();
        if (unlockers.length > 0) {
            for (TaskUnlocker u : unlockers) {
                text.add(new StringTextComponent("- ").append(u.getDescription()).append(newLine));
            }


        } else {
            text.add(new TranslationTextComponent("text.vampirism.task.prerequisites.none"));
        }
        return new PageItemStack(ITextProperties.composite(text), ingredient);
    }

    /**
     * TODO 1.17 remove
     */
    public static class URLLink extends PageHolderWithLinks.URLLink {
        private final ITextComponent name;

        public URLLink(ITextComponent name, URI link) {
            super("dummy", link);
            this.name = name;
        }

        @Override
        public ITextComponent getDisplayName() {
            return name;
        }
    }
}
