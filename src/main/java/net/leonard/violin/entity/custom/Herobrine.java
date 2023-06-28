package net.leonard.violin.entity.custom;
/*
Basically, I want herobrine to always stare at the player, if the player gets too close to Herobrine, Herobrine will
approach the player, but after getting within a certain distance will teleport away, scaring, but not harming the player,
herobrine should also only be around at night
*/

import java.util.EnumSet;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;

import net.leonard.violin.sound.ModSounds;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;

public class Herobrine extends Monster implements NeutralMob, GeoEntity {
    //Gecko Library
    private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    private static final UUID SPEED_MODIFIER_ATTACKING_UUID = UUID.fromString("020E0DFB-87AE-4653-9556-831010E291A0");
    private static final AttributeModifier SPEED_MODIFIER_ATTACKING = new AttributeModifier(SPEED_MODIFIER_ATTACKING_UUID, "Attacking speed boost", (double)0.15F, AttributeModifier.Operation.ADDITION);
    private static final EntityDataAccessor<Optional<BlockState>> DATA_CARRY_STATE = SynchedEntityData.defineId(net.leonard.violin.entity.custom.Herobrine.class, EntityDataSerializers.OPTIONAL_BLOCK_STATE);
    private static final EntityDataAccessor<Boolean> DATA_CREEPY = SynchedEntityData.defineId(net.leonard.violin.entity.custom.Herobrine.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_STARED_AT = SynchedEntityData.defineId(net.leonard.violin.entity.custom.Herobrine.class, EntityDataSerializers.BOOLEAN);
    private int lastStareSound = Integer.MIN_VALUE;
    private int targetChangeTime;
    private static final UniformInt PERSISTENT_ANGER_TIME = TimeUtil.rangeOfSeconds(20, 39);
    private int remainingPersistentAngerTime;
    @Nullable
    private UUID persistentAngerTarget;

    public Herobrine(EntityType<? extends net.leonard.violin.entity.custom.Herobrine> p_32485_, Level p_32486_) {
        super(p_32485_, p_32486_);
        this.setMaxUpStep(1.0F);
        this.setPathfindingMalus(BlockPathTypes.WATER, -1.0F);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 500.0f, 1.0f, false)); //Stare at player for up to 500 blocks
        this.goalSelector.addGoal(1, new Herobrine.HerobrineFreezeWhenLookedAt(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, false));
        this.targetSelector.addGoal(1, new Herobrine.HerobrineLookForPlayerGoal(this, this::isAngryAt));
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(4, new ResetUniversalAngerTargetGoal<>(this, false));
    }

    public static AttributeSupplier setAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 40.0D)
                .add(Attributes.MOVEMENT_SPEED, (double)0.3F)
                .add(Attributes.ATTACK_DAMAGE, 7.0D)
                .add(Attributes.FOLLOW_RANGE, 64.0D).build(); //Changed from 64 to 4
    }

    // More Geckolibrary
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }
    //Animations
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



    public void setTarget(@Nullable LivingEntity p_32537_) {
        AttributeInstance attributeinstance = this.getAttribute(Attributes.MOVEMENT_SPEED);
        if (p_32537_ == null) {
            this.targetChangeTime = 0;
            this.entityData.set(DATA_CREEPY, false);
            this.entityData.set(DATA_STARED_AT, false);
            attributeinstance.removeModifier(SPEED_MODIFIER_ATTACKING);
        } else {
            this.targetChangeTime = this.tickCount;
            this.entityData.set(DATA_CREEPY, true);
            if (!attributeinstance.hasModifier(SPEED_MODIFIER_ATTACKING)) {
                attributeinstance.addTransientModifier(SPEED_MODIFIER_ATTACKING);
            }
        }
        super.setTarget(p_32537_);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_CARRY_STATE, Optional.empty());
        this.entityData.define(DATA_CREEPY, false);
        this.entityData.define(DATA_STARED_AT, false);
    }

    public void startPersistentAngerTimer() {
        this.setRemainingPersistentAngerTime(PERSISTENT_ANGER_TIME.sample(this.random));
    }

    public void setRemainingPersistentAngerTime(int p_32515_) {
        this.remainingPersistentAngerTime = p_32515_;
    }

    public int getRemainingPersistentAngerTime() {
        return this.remainingPersistentAngerTime;
    }

    public void setPersistentAngerTarget(@Nullable UUID p_32509_) {
        this.persistentAngerTarget = p_32509_;
    }

    @Nullable
    public UUID getPersistentAngerTarget() {
        return this.persistentAngerTarget;
    }

    public void playStareSound() {
        if (this.tickCount >= this.lastStareSound + 400) {
            this.lastStareSound = this.tickCount;
            if (!this.isSilent()) {
                this.level().playLocalSound(this.getX(), this.getEyeY(), this.getZ(), ModSounds.HEROBRINE_ATTACK.get(), this.getSoundSource(), 5.0F, 1.0F, false);
            }
        }
    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> p_32513_) {
        if (DATA_CREEPY.equals(p_32513_) && this.hasBeenStaredAt() && this.level().isClientSide) {
            this.playStareSound();
        }

        super.onSyncedDataUpdated(p_32513_);
    }

    boolean isLookingAtMe(Player p_32535_) {
        ItemStack itemstack = p_32535_.getInventory().armor.get(3);
        if (net.leonardforge.common.ForgeHooks.shouldSuppressEnderManAnger(this, p_32535_, itemstack)) {
            return false; // if this statement returns true, we'll register that the player is not technically looking at us
        } else {
            Vec3 vec3 = p_32535_.getViewVector(1.0F).normalize();
            Vec3 vec31 = new Vec3(this.getX() - p_32535_.getX(), this.getEyeY() - p_32535_.getEyeY(), this.getZ() - p_32535_.getZ());
            double d0 = vec31.length();
            vec31 = vec31.normalize();
            double d1 = vec3.dot(vec31);
            return d1 > 1.0D - 0.025D / d0 ? p_32535_.hasLineOfSight(this) : false;
        }
    }

    // Agro if player gets too close
    boolean isCloseToMe(Player p_32535_) {
        //Equation of a sphere used to find radius, used for setting Herobrine's spherical agro distance
        double distance = Math.sqrt(Math.pow(p_32535_.getX() - this.getX(), 2) + Math.pow(p_32535_.getZ() - this.getZ(),2) + Math.pow(p_32535_.getY() - this.getY(),2));
        if (distance < 15) {
            return true;
        }
        else {return false;}
    }

    protected float getStandingEyeHeight(Pose p_32517_, EntityDimensions p_32518_) {
        return 1.75F;
    }

    //update variables on tick
    public void aiStep() {
        if (this.isAlive()) {
            boolean flag = (this.level().isDay() && !this.level().isClientSide);
            if (flag) {
                this.teleportTo(this.getX(), -65, this.getZ()); // This essentially kills Herobrine
                }
            if (!this.level().isClientSide) {
                this.updatePersistentAnger((ServerLevel) this.level(), true);
            }

        super.aiStep(); //tick
        }
    }

    //extra tick function not in use
    protected void customServerAiStep() {super.customServerAiStep();}

    public boolean isCreepy() {
        return this.entityData.get(DATA_CREEPY);
    }

    public boolean hasBeenStaredAt() {
        return this.entityData.get(DATA_STARED_AT);
    }

    public void setBeingStaredAt() {
        this.entityData.set(DATA_STARED_AT, true);
    }

    static class HerobrineFreezeWhenLookedAt extends Goal {
        private final net.leonard.violin.entity.custom.Herobrine herobrine;
        @Nullable
        private LivingEntity target;

        public HerobrineFreezeWhenLookedAt(net.leonard.violin.entity.custom.Herobrine p_32550_) {
            this.herobrine = p_32550_;
            this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
        }

        public boolean canUse() {
            this.target = this.herobrine.getTarget();
            if (!(this.target instanceof Player)) {
                return false;
            } else {
                double d0 = this.target.distanceToSqr(this.herobrine);
                return d0 > 256.0D ? false : this.herobrine.isLookingAtMe((Player)this.target);
            }
        }

        public void start() {
            this.herobrine.getNavigation().stop();
        }

        public void tick() {
            this.herobrine.getLookControl().setLookAt(this.target.getX(), this.target.getEyeY(), this.target.getZ());
        }
    }

    static class HerobrineLookForPlayerGoal extends NearestAttackableTargetGoal<Player> {
        private final net.leonard.violin.entity.custom.Herobrine herobrine;
        @Nullable
        private Player pendingTarget;
        private int aggroTime;
        private int teleportTime;
        private final TargetingConditions startAggroTargetConditions;
        private final TargetingConditions continueAggroTargetConditions = TargetingConditions.forCombat().ignoreLineOfSight();
        private final Predicate<LivingEntity> isAngerInducing;

        public HerobrineLookForPlayerGoal(Herobrine p_32573_, @Nullable Predicate<LivingEntity> p_32574_) {
            super(p_32573_, Player.class, 10, false, false, p_32574_);
            this.herobrine = p_32573_;
            this.isAngerInducing = (p_269940_) -> {
                return (p_32573_.isCloseToMe((Player)p_269940_) || p_32573_.isAngryAt(p_269940_)) && !p_32573_.hasIndirectPassenger(p_269940_);
            };
            this.startAggroTargetConditions = TargetingConditions.forCombat().range(this.getFollowDistance()).selector(this.isAngerInducing);
        }

        public boolean canUse() {
            this.pendingTarget = this.herobrine.level().getNearestPlayer(this.startAggroTargetConditions, this.herobrine);
            return this.pendingTarget != null;
        }

        public void start() {
            this.aggroTime = this.adjustedTickDelay(5);
            this.teleportTime = 0;
            this.herobrine.setBeingStaredAt();
        }

        public void stop() {
            this.pendingTarget = null;
            super.stop();
        }

        public boolean canContinueToUse() {
            if (this.pendingTarget != null) {
                if (!this.isAngerInducing.test(this.pendingTarget)) {
                    return false;
                } else {
                    this.herobrine.lookAt(this.pendingTarget, 10.0F, 10.0F);
                    return true;
                }
            } else {
                if (this.target != null) {
                    if (this.herobrine.hasIndirectPassenger(this.target)) {
                        return false;
                    }
                    if (this.continueAggroTargetConditions.test(this.herobrine, this.target)) {
                        return true;
                    }
                }
                return super.canContinueToUse();
            }
        }

        //Once Herobrine finishes chasing player he will despawn
        public void isTooCloseToMe() {
            //Equation of a sphere used to find radius, used for setting Herobrine's spherical agro distance
            double distance = Math.sqrt(Math.pow(target.getX() - this.herobrine.getX(), 2) + Math.pow(target.getZ() - this.herobrine.getZ(),2) + Math.pow(target.getY() - this.herobrine.getY(),2));
            if (distance < 1.5) {
                this.herobrine.teleportTo(this.herobrine.getX(), -65, this.herobrine.getZ());
            }
        }

        //Update target
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
            }
            if (this.target != null) {
                this.isTooCloseToMe();
                super.tick();
            }
            else {
                super.tick();
            }
        }
    }
}
