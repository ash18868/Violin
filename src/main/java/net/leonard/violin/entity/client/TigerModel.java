package net.leonard.violin.entity.client;

import net.leonard.violin.Violin;
import net.leonard.violin.entity.custom.Tiger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class TigerModel extends GeoModel<Tiger> {
    @Override
    public ResourceLocation getModelResource(Tiger animatable) {
        return new ResourceLocation(Violin.MOD_ID, "geo/tiger.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(Tiger animatable) {
        return new ResourceLocation(Violin.MOD_ID, "textures/entity/tiger.png");
    }

    @Override
    public ResourceLocation getAnimationResource(Tiger animatable) {
        return new ResourceLocation(Violin.MOD_ID, "animations/tiger.animation.json");
    }

    @Override
    public void setCustomAnimations(Tiger animatable, long instanceId, AnimationState<Tiger> animationState) {
        CoreGeoBone head = getAnimationProcessor().getBone("head");

        if (head != null) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);

            head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
            head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
        }
    }
}