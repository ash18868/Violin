package net.leonard.violin.entity.client;

import net.leonard.violin.Violin;
import net.leonard.violin.entity.custom.Herobrine;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class HerobrineModel extends GeoModel<Herobrine> {
    @Override
    public ResourceLocation getModelResource(Herobrine animatable) {
        return new ResourceLocation(Violin.MOD_ID, "geo/herobrine.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(Herobrine animatable) {
        return new ResourceLocation(Violin.MOD_ID, "textures/entity/herobrine.png");
    }

    @Override
    public ResourceLocation getAnimationResource(Herobrine animatable) {
        return new ResourceLocation(Violin.MOD_ID, "animations/herobrine.animation.json");
    }

    @Override
    public void setCustomAnimations(Herobrine animatable, long instanceId, AnimationState<Herobrine> animationState) {
        CoreGeoBone head = getAnimationProcessor().getBone("head");

        if (head != null) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);

            head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
            head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
        }
    }
}