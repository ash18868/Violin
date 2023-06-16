package net.leonard.violin.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
//import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;


import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

/*
Basically, I want herobrine to always stare at the player, if the player gets too close to Herobrine, Herobrine will
approach the player, but after getting within a certain distance will teleport away, scaring, but not harming the player,
herobrine should also only be around at night
*/

//This is a test

public class Herobrine extends Monster implements NeutralMob, GeoEntity {
    private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private static final UUID SPEED_MODIFIER_ATTACKING_UUID = UUID.fromString("020E0DFB-87AE-4653-9556-831010E291A0");
    private int targetChangeTime;
    /*private static final EntityDataAccessor<Optional<BlockState>> DATA_CARRY_STATE = SynchedEntityData.defineId(Herobrine.class, EntityDataSerializers.OPTIONAL_BLOCK_STATE);
    private static final EntityDataAccessor<Boolean> DATA_CREEPY = SynchedEntityData.defineId(Herobrine.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_STARED_AT = SynchedEntityData.defineId(Herobrine.class, EntityDataSerializers.BOOLEAN);*/
    private static final AttributeModifier SPEED_MODIFIER_ATTACKING = new AttributeModifier(SPEED_MODIFIER_ATTACKING_UUID, "Attacking speed boost", (double)0.15F, AttributeModifier.Operation.ADDITION);

    public Herobrine(EntityType<? extends Herobrine> p_32485_, Level p_32486_) {
        super(p_32485_, p_32486_);
        this.setMaxUpStep(1.0F);
        this.setPathfindingMalus(BlockPathTypes.WATER, -1.0F);
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
        //this.goalSelector.addGoal(2, new AvoidEntityGoal<>(this, Player.class, 8.0F, 2.2D, 2.2D));

        //I cannot for the life of me get this to be reasonable
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, false));

        this.targetSelector.addGoal(1, new Herobrine.HerobrineLookForPlayerGoal(this, this::isAngryAt));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, false));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Creeper.class, true));
    }


    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    @Override
    public int getRemainingPersistentAngerTime() {
        return 0;
    }

    @Override
    public void setRemainingPersistentAngerTime(int p_21673_) {

    }

    @org.jetbrains.annotations.Nullable
    @Override
    public UUID getPersistentAngerTarget() {
        return null;
    }

    @Override
    public void setPersistentAngerTarget(@org.jetbrains.annotations.Nullable UUID p_21672_) {

    }

    @Override
    public void startPersistentAngerTimer() {

    }

    public void setTarget(@Nullable LivingEntity p_32537_) {
        AttributeInstance attributeinstance = this.getAttribute(Attributes.MOVEMENT_SPEED);
        if (p_32537_ == null) {
            this.targetChangeTime = 0;
            /*this.entityData.set(DATA_CREEPY, false);
            this.entityData.set(DATA_STARED_AT, false);*/
            attributeinstance.removeModifier(SPEED_MODIFIER_ATTACKING);
        } else {
            this.targetChangeTime = this.tickCount;
            /*this.entityData.set(DATA_CREEPY, true);*/
            if (!attributeinstance.hasModifier(SPEED_MODIFIER_ATTACKING)) {
                attributeinstance.addTransientModifier(SPEED_MODIFIER_ATTACKING);
            }
        }

        super.setTarget(p_32537_); //Forge: Moved down to allow event handlers to write data manager values.
    }

    /*protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_CARRY_STATE, Optional.empty());
        this.entityData.define(DATA_CREEPY, false);
        this.entityData.define(DATA_STARED_AT, false);
    }*/

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



// Begin Herobrine behavior
    boolean isLookingAtMe(Player p_32535_) {
        Vec3 vec3 = p_32535_.getViewVector(1.0F).normalize();
        Vec3 vec31 = new Vec3(this.getX() - p_32535_.getX(), this.getEyeY() - p_32535_.getEyeY(), this.getZ() - p_32535_.getZ());
        double d0 = vec31.length();
        vec31 = vec31.normalize();
        double d1 = vec3.dot(vec31);
        return d1 > 1.0D - 0.025D / d0 ? p_32535_.hasLineOfSight(this) : false;
    }

    protected float getStandingEyeHeight(Pose p_32517_, EntityDimensions p_32518_) {
        return 2.55F;
    }

    public void aiStep() {
        if (this.level.isClientSide) {
            for(int i = 0; i < 2; ++i) {
                this.level.addParticle(ParticleTypes.PORTAL, this.getRandomX(0.5D), this.getRandomY() - 0.25D, this.getRandomZ(0.5D), (this.random.nextDouble() - 0.5D) * 2.0D, -this.random.nextDouble(), (this.random.nextDouble() - 0.5D) * 2.0D);
            }
        }

        this.jumping = false;

        super.aiStep();
    }

    protected boolean teleport() {
        if (!this.level.isClientSide() && this.isAlive()) {
            double d0 = this.getX() + (this.random.nextDouble() - 0.5D) * 64.0D;
            double d1 = this.getY() + (double)(this.random.nextInt(64) - 32);
            double d2 = this.getZ() + (this.random.nextDouble() - 0.5D) * 64.0D;
            return this.teleport(d0, d1, d2);
        } else {
            return false;
        }
    }

    boolean teleportTowards(Entity p_32501_) {
        Vec3 vec3 = new Vec3(this.getX() - p_32501_.getX(), this.getY(0.5D) - p_32501_.getEyeY(), this.getZ() - p_32501_.getZ());
        vec3 = vec3.normalize();
        double d0 = 16.0D;
        double d1 = this.getX() + (this.random.nextDouble() - 0.5D) * 8.0D - vec3.x * 16.0D;
        double d2 = this.getY() + (double)(this.random.nextInt(16) - 8) - vec3.y * 16.0D;
        double d3 = this.getZ() + (this.random.nextDouble() - 0.5D) * 8.0D - vec3.z * 16.0D;
        return this.teleport(d1, d2, d3);
    }

    private boolean teleport(double p_32544_, double p_32545_, double p_32546_) {
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos(p_32544_, p_32545_, p_32546_);

        while(blockpos$mutableblockpos.getY() > this.level.getMinBuildHeight() && !this.level.getBlockState(blockpos$mutableblockpos).getMaterial().blocksMotion()) {
            blockpos$mutableblockpos.move(Direction.DOWN);
        }

        BlockState blockstate = this.level.getBlockState(blockpos$mutableblockpos);
        boolean flag = blockstate.getMaterial().blocksMotion();
        boolean flag1 = blockstate.getFluidState().is(FluidTags.WATER);
        if (flag && !flag1) {
            net.minecraftforge.event.entity.EntityTeleportEvent.EnderEntity event = net.minecraftforge.event.ForgeEventFactory.onEnderTeleport(this, p_32544_, p_32545_, p_32546_);
            if (event.isCanceled()) return false;
            Vec3 vec3 = this.position();
            boolean flag2 = this.randomTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ(), true);
            if (flag2) {
                this.level.gameEvent(GameEvent.TELEPORT, vec3, GameEvent.Context.of(this));
                if (!this.isSilent()) {
                    this.level.playSound((Player)null, this.xo, this.yo, this.zo, SoundEvents.ENDERMAN_TELEPORT, this.getSoundSource(), 1.0F, 1.0F);
                    this.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);
                }
            }

            return flag2;
        } else {
            return false;
        }
    }

    static class HerobrineLookForPlayerGoal extends NearestAttackableTargetGoal<Player> {
        private final Herobrine herobrine;
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
                return (p_32573_.isLookingAtMe((Player)p_269940_) || p_32573_.isAngryAt(p_269940_)) && !p_32573_.hasIndirectPassenger(p_269940_);
            };
            this.startAggroTargetConditions = TargetingConditions.forCombat().range(this.getFollowDistance()).selector(this.isAngerInducing);
        }

        /*public void setBeingStaredAt() {
            this.entityData.set(DATA_STARED_AT, true);
        }*/
        public boolean canUse() {
            this.pendingTarget = this.herobrine.level.getNearestPlayer(this.startAggroTargetConditions, this.herobrine);
            return this.pendingTarget != null;
        }

        /*public void start() {
            this.aggroTime = this.adjustedTickDelay(5);
            this.teleportTime = 0;
            this.herobrine.setBeingStaredAt();
        }*/

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

        public void tick() {
            System.out.println("We're running");
            if (this.herobrine.getTarget() == null) {
                super.setTarget((LivingEntity)null);
                System.out.println("NotLookingAtMe");
            }

            if (this.pendingTarget != null) {
                if (--this.aggroTime <= 0) {
                    this.target = this.pendingTarget;
                    this.pendingTarget = null;
                    super.start();
                }
                System.out.println("NotLookingAtMe");
            } else {
                if (this.target != null && !this.herobrine.isPassenger()) {
                    if (this.herobrine.isLookingAtMe((Player)this.target)) {
                        if (this.target.distanceToSqr(this.herobrine) < 16.0D) {
                            this.herobrine.teleport();
                        }

                        this.teleportTime = 0;
                    } else if (this.target.distanceToSqr(this.herobrine) > 256.0D && this.teleportTime++ >= this.adjustedTickDelay(30) && this.herobrine.teleportTowards(this.target)) {
                        this.teleportTime = 0;
                    }
                }

                super.tick();
            }

        }
    }

}