package de.teamlapen.vampirism.client.model;


import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import de.teamlapen.vampirism.entity.vampire.VampireBaronEntity;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.MathHelper;

/**
 * Attire designed for the male vampire baron - RebelT
 * Created using Tabula 7.1.0
 */
public class BaronAttireModel extends EntityModel<VampireBaronEntity> {
    public ModelRenderer hood;
    public ModelRenderer cloak;
    private float enragedProgress = 0;

    public BaronAttireModel() {
        this.texWidth = 128;
        this.texHeight = 64;
        this.hood = new ModelRenderer(this, 44, 0);
        this.hood.setPos(0.0F, 0.0F, 0.0F);
        this.hood.addBox(-4.5F, -8.5F, -4.0F, 9, 9, 9, 0.0F);
        this.cloak = new ModelRenderer(this, 0, 0);
        this.cloak.setPos(0.0F, 0.0F, 0.0F);
        this.cloak.addBox(-8.5F, -0.5F, -2.5F, 17, 22, 5, 0.0F);
        this.hood.addChild(this.cloak);
    }

    @Override
    public void prepareMobModel(VampireBaronEntity entityIn, float limbSwing, float limbSwingAmount, float partialTick) {
        enragedProgress = entityIn.getEnragedProgress();
    }

    @Override
    public void renderToBuffer(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        hood.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        matrixStackIn.pushPose();
        matrixStackIn.scale(1 - 0.4f * enragedProgress, 1 - 0.7f * enragedProgress, 1 - 0.4f * enragedProgress);
        cloak.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        matrixStackIn.popPose();
    }

    @Override
    public void setupAnim(VampireBaronEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        float bodyRotateAngleY = 0;
        if (this.attackTime > 0.0F) {
            HandSide handside = this.getSwingingSide(entityIn);
            float f1 = this.attackTime;
            bodyRotateAngleY = MathHelper.sin(MathHelper.sqrt(f1) * ((float) Math.PI * 2F)) * 0.2F;
            if (handside == HandSide.LEFT) {
                bodyRotateAngleY *= -1.0F;
            }
        }
        this.hood.yRot = bodyRotateAngleY;
        this.cloak.yRot = bodyRotateAngleY;
    }


    protected HandSide getSwingingSide(VampireBaronEntity entity) {
        HandSide handside = entity.getMainArm();
        return entity.swingingArm == Hand.MAIN_HAND ? handside : handside.getOpposite();
    }
}