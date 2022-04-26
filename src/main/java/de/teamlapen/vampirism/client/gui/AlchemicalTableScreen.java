package de.teamlapen.vampirism.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.inventory.container.AlchemicalTableContainer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;

public class AlchemicalTableScreen extends ContainerScreen<AlchemicalTableContainer> {

    private static final ResourceLocation BREWING_STAND_LOCATION = new ResourceLocation(REFERENCE.MODID, "textures/gui/container/alchemy_table.png");
    private static final int[] BUBBLELENGTHS = new int[]{29, 24, 20, 16, 11, 6, 0};

    public AlchemicalTableScreen(AlchemicalTableContainer p_i51105_1_, PlayerInventory p_i51105_2_, ITextComponent p_i51105_3_) {
        super(p_i51105_1_, p_i51105_2_, p_i51105_3_);
        this.imageHeight = 181;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    protected void init() {
        super.init();
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
    }

    public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
        this.renderBackground(p_230430_1_);
        super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
        this.renderTooltip(p_230430_1_, p_230430_2_, p_230430_3_);
    }

    protected void renderBg(MatrixStack p_230450_1_, float p_230450_2_, int p_230450_3_, int p_230450_4_) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bind(BREWING_STAND_LOCATION);
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        this.blit(p_230450_1_, i, j, 0, 0, this.imageWidth, this.imageHeight);
        int k = this.menu.getFuel();
        int l = MathHelper.clamp((18 * k + 20 - 1) / 20, 0, 18);
        if (l > 0) {
            this.blit(p_230450_1_, i + 33, j + 60, 176, 9, l, 4);
        }

        int i1 = this.menu.getBrewingTicks();
        if (i1 > 0) {
            float j1 = 1.0F - ((float)i1 / 600.0F);
            if (j1 > 0) {
                this.blit(p_230450_1_, i + 73, j + 57, 176, 1, (int)(j1 * 28), 8);
                this.blit(p_230450_1_, i + 104, j+36, 176, 13, (int)(j1 * 32), 32);
            }
        }

    }
}
