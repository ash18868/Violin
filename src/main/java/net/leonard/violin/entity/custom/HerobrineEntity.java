package net.leonard.violin.entity.custom;

import net.leonard.violin.entity.ModEntities;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
//import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;

/*
Basically, I want herobrine to always stare at the player, if the player gets too close to Herobrine, Herobrine will
approach the player, but after getting within a certain distance will teleport away, scaring, but not harming the player,
herobrine should also only be around at night
*/

public class HerobrineEntity extends Monster implements GeoEntity {
    private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    public HerobrineEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    /*public static AttributeSupplier.Builder createAttributes()
    {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 40.0D)
                .add(Attributes.MOVEMENT_SPEED, (double) 0.3F)
                .add(Attributes.ATTACK_DAMAGE, 12.0D);
    }*/

    //This does not work, but we'll leave it here
    public static AttributeSupplier setAttributes() {
        return Monster.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 16D)
                .add(Attributes.ATTACK_DAMAGE, 2048.0f)
                .add(Attributes.ATTACK_SPEED, 1.0f)
                .add(Attributes.MOVEMENT_SPEED, 0.4f).build();
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 500.0f, 1.0f, false));
        this.goalSelector.addGoal(2, new AvoidEntityGoal<>(this, Player.class, 8.0F, 2.2D, 2.2D));

        //I cannot for the life of me get this to be reasonable
        //this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, false));

        // Goal Ideas
        // Look at player goal!!!!
        //GhastLookGoal
        //avoid entity goal
        //Enderman leaveblock goal
        //followownergoal
        //movetowardsrestrictiongoal
        //stalkpreygoal
        //LeapAtTargetGoal
        //FleeSunGoal

        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, false));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Creeper.class, true));
    }


    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> tAnimationState) {
        if(tAnimationState.isMoving()) {
            tAnimationState.getController().setAnimation(RawAnimation.begin().then("animation.herobrine.walk", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }

        tAnimationState.getController().setAnimation(RawAnimation.begin().then("animation.herobrine.idle", Animation.LoopType.LOOP));
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
// Begin Herobrine behavior


    //Check every tick
    public void tick() {
        if (this.herobrine.getTarget() == null) {
            super.setTarget((LivingEntity)null);
        }

        if (this.pendingTarget != null) {
            if (--this.aggroTime <= 0) {
                this.target = this.pendingTarget;
                this.pendingTarget = null;
                super.start();
            }
        } else {
            if (this.target != null && !this.enderman.isPassenger()) {
                if (this.enderman.isLookingAtMe((Player)this.target)) {
                    if (this.target.distanceToSqr(this.enderman) < 16.0D) {
                        this.enderman.teleport();
                    }

                    this.teleportTime = 0;
                } else if (this.target.distanceToSqr(this.enderman) > 256.0D && this.teleportTime++ >= this.adjustedTickDelay(30) && this.enderman.teleportTowards(this.target)) {
                    this.teleportTime = 0;
                }
            }

            super.tick();
        }

    }


}