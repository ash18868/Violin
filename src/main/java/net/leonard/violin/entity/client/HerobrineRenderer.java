package net.leonard.violin.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.leonard.violin.Violin;
import net.leonard.violin.entity.custom.Herobrine;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class HerobrineRenderer extends GeoEntityRenderer<Herobrine> {
    public HerobrineRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new HerobrineModel());
    }

    @Override
    public ResourceLocation getTextureLocation(Herobrine animatable) {
        return new ResourceLocation(Violin.MOD_ID, "textures/entity/herobrine.png");
    }

    @Override
    public void render(Herobrine entity, float entityYaw, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight) {
        if(entity.isBaby()) {
            poseStack.scale(0.4f, 0.4f, 0.4f);
        }

        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}