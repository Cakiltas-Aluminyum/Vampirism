package de.teamlapen.vampirism.client.render.entities;

import de.teamlapen.vampirism.entity.SoulOrbEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.SpriteRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;


@OnlyIn(Dist.CLIENT)
public class SoulOrbRenderer extends SpriteRenderer<SoulOrbEntity> {

    public SoulOrbRenderer(EntityRendererManager renderManager, ItemRenderer renderItem) {
        super(renderManager, renderItem);
    }

    @Override
    public boolean shouldRender(SoulOrbEntity livingEntity, ClippingHelper camera, double camX, double camY, double camZ) { //shouldRender
        boolean flag = true;
        if (Minecraft.getInstance().player != null) {
            flag = !livingEntity.isInvisibleTo(Minecraft.getInstance().player);
        }
        return flag && super.shouldRender(livingEntity, camera, camX, camY, camZ);
    }


}
