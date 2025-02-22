package de.teamlapen.vampirism.client.render.tiles;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.tileentity.TotemTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.BeaconTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class TotemTESR extends VampirismTESR<TotemTileEntity> {


    private static final ResourceLocation TEXTURE_BEACON_BEAM = new ResourceLocation(REFERENCE.MODID, "textures/entity/totem_beam.png");
    private final static int HEIGHT = 100;

    public TotemTESR(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(TotemTileEntity te, float partialTicks, @Nonnull MatrixStack matrixStack, @Nonnull IRenderTypeBuffer iRenderTypeBuffer, int i, int i1) {
        RenderSystem.alphaFunc(516, 0.1f);
        float textureScale = te.shouldRenderBeam();
        if (textureScale > 0.0f) {
            long totalWorldTime = te.getLevel().getGameTime();
            int captureProgress = te.getCaptureProgress();
            float[] baseColors = te.getBaseColors();
            int offset = 0;
            if (captureProgress > 0) {
                float[] overtakeColors = te.getCapturingColors();
                offset = (captureProgress * HEIGHT) / 100;
                BeaconTileEntityRenderer.renderBeaconBeam(matrixStack, iRenderTypeBuffer, TEXTURE_BEACON_BEAM, partialTicks, textureScale, totalWorldTime, 0, offset, overtakeColors, 0.2f, 0.25f);
            }
            BeaconTileEntityRenderer.renderBeaconBeam(matrixStack, iRenderTypeBuffer, TEXTURE_BEACON_BEAM, partialTicks, textureScale, totalWorldTime, offset, HEIGHT - offset, baseColors, 0.2f, 0.25f);
        } else {
            IFaction<?> faction = te.getControllingFaction();
            if (faction != null) {
                renderFactionName(faction, matrixStack, iRenderTypeBuffer, i);
            }
        }
    }

    @Override
    public boolean shouldRenderOffScreen(TotemTileEntity te) {
        return true;
    }

    private void renderFactionName(IFaction<?> faction, MatrixStack matrixStack, IRenderTypeBuffer iRenderTypeBuffer, int packedLight) {
        ITextComponent displayNameIn = faction.getNamePlural().plainCopy().withStyle(faction.getChatColor());
        matrixStack.pushPose();
        matrixStack.translate(0.5, 1, 0.5);
        matrixStack.mulPose(Minecraft.getInstance().gameRenderer.getMainCamera().rotation());
        matrixStack.scale(-0.025f, -0.025f, -0.025f);
        Matrix4f matrix4f = matrixStack.last().pose();
        float f1 = 0; //Minecraft.getInstance().gameSettings.getTextBackgroundOpacity(0.25f);
        int j = (int) (f1 * 255f) << 24;
        FontRenderer font = Minecraft.getInstance().font;
        float nameOffset = (float) (-font.width(displayNameIn) / 2);
        font.drawInBatch(displayNameIn, nameOffset, 0, 553648127, false, matrix4f, iRenderTypeBuffer, true, j, packedLight);
        font.drawInBatch(displayNameIn, nameOffset, 0, -1, false, matrix4f, iRenderTypeBuffer, true, 0, packedLight);
        matrixStack.popPose();
    }


}
