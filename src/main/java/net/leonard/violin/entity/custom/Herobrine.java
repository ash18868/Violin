package net.leonard.violin.entity.custom;
/*
Basically, I want herobrine to always stare at the player, if the player gets too close to Herobrine, Herobrine will
approach the player, but after getting within a certain distance will teleport away, scaring, but not harming the player,
herobrine should also only be around at night
*/

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;

import net.leonard.violin.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Endermite;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
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
    private static final int DELAY_BETWEEN_CREEPY_STARE_SOUND = 400;
    private static final int MIN_DEAGGRESSION_TIME = 600;
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
        this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 500.0f, 1.0f, false));
        this.goalSelector.addGoal(1, new Herobrine.HerobrineFreezeWhenLookedAt(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, false));
        this.targetSelector.addGoal(1, new Herobrine.HerobrineLookForPlayerGoal(this, this::isAngryAt));
//Maybe put this back in        //
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
        //this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Endermite.class, true, false));
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

        super.setTarget(p_32537_); //Forge: Moved down to allow event handlers to write data manager values.
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
                this.level.playLocalSound(this.getX(), this.getEyeY(), this.getZ(), ModSounds.HEROBRINE_ATTACK.get(), this.getSoundSource(), 5.0F, 1.0F, false);
                System.out.println("HEROBRINE_START SOUND");
            }
        }

    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> p_32513_) {
        if (DATA_CREEPY.equals(p_32513_) && this.hasBeenStaredAt() && this.level.isClientSide) {
            this.playStareSound();
        }

        super.onSyncedDataUpdated(p_32513_);
    }

    public void addAdditionalSaveData(CompoundTag p_32520_) {
        super.addAdditionalSaveData(p_32520_);
        BlockState blockstate = this.getCarriedBlock();
        if (blockstate != null) {
            p_32520_.put("carriedBlockState", NbtUtils.writeBlockState(blockstate));
        }

        this.addPersistentAngerSaveData(p_32520_);
    }

    public void readAdditionalSaveData(CompoundTag p_32511_) {
        super.readAdditionalSaveData(p_32511_);
        BlockState blockstate = null;
        if (p_32511_.contains("carriedBlockState", 10)) {
            blockstate = NbtUtils.readBlockState(this.level.holderLookup(Registries.BLOCK), p_32511_.getCompound("carriedBlockState"));
            if (blockstate.isAir()) {
                blockstate = null;
            }
        }

        this.setCarriedBlock(blockstate);
        this.readPersistentAngerSaveData(this.level, p_32511_);
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

    public void aiStep() {
        if (this.isAlive()) {
            boolean flag = (this.level.isDay() && !this.level.isClientSide);
            if (flag) {
                this.teleportTo(this.getX(), -65, this.getZ()); // This essentially kills Herobrine
                }

            if (!this.level.isClientSide) {
                this.updatePersistentAnger((ServerLevel) this.level, true);
            }

        super.aiStep(); //tick
        }
    }
//TODO Attempt to delete this
    public boolean isSensitiveToWater() {
        return true;
    }

    protected void customServerAiStep() {
        //Debugging


        super.customServerAiStep();
    }







    /*protected SoundEvent getAmbientSound() {
        System.out.println("HEROBRINE_SCREAM AND HEROBRINE_AMBIENT SOUND");
        return this.isCreepy() ? SoundEvents.ENDERMAN_SCREAM : SoundEvents.ENDERMAN_AMBIENT;

    }*/

    /*protected SoundEvent getHurtSound(DamageSource p_32527_) {
        System.out.println("HEROBRINE_HURT SOUND");
        return SoundEvents.ENDERMAN_HURT;
    }*/

    /*protected SoundEvent getDeathSound() {
        System.out.println("HEROBRINE_DEATH SOUND");
        return SoundEvents.ENDERMAN_DEATH;
    }*/

    protected void dropCustomDeathLoot(DamageSource p_32497_, int p_32498_, boolean p_32499_) {
        super.dropCustomDeathLoot(p_32497_, p_32498_, p_32499_);
        BlockState blockstate = this.getCarriedBlock();
        if (blockstate != null) {
            ItemStack itemstack = new ItemStack(Items.DIAMOND_AXE);
            itemstack.enchant(Enchantments.SILK_TOUCH, 1);
            LootContext.Builder lootcontext$builder = (new LootContext.Builder((ServerLevel)this.level)).withRandom(this.level.getRandom()).withParameter(LootContextParams.ORIGIN, this.position()).withParameter(LootContextParams.TOOL, itemstack).withOptionalParameter(LootContextParams.THIS_ENTITY, this);

            for(ItemStack itemstack1 : blockstate.getDrops(lootcontext$builder)) {
                this.spawnAtLocation(itemstack1);
            }
        }

    }

    public void setCarriedBlock(@Nullable BlockState p_32522_) {
        this.entityData.set(DATA_CARRY_STATE, Optional.ofNullable(p_32522_));
    }

    @Nullable
    public BlockState getCarriedBlock() {
        return this.entityData.get(DATA_CARRY_STATE).orElse((BlockState)null);
    }


    private boolean hurtWithCleanWater(DamageSource p_186273_, ThrownPotion p_186274_, float p_186275_) {
        ItemStack itemstack = p_186274_.getItem();
        Potion potion = PotionUtils.getPotion(itemstack);
        List<MobEffectInstance> list = PotionUtils.getMobEffects(itemstack);
        boolean flag = potion == Potions.WATER && list.isEmpty();
        return flag ? super.hurt(p_186273_, p_186275_) : false;
    }

    public boolean isCreepy() {
        return this.entityData.get(DATA_CREEPY);
    }

    public boolean hasBeenStaredAt() {
        return this.entityData.get(DATA_STARED_AT);
    }

    public void setBeingStaredAt() {
        this.entityData.set(DATA_STARED_AT, true);
    }

    public boolean requiresCustomPersistence() {
        return super.requiresCustomPersistence() || this.getCarriedBlock() != null;
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

    static class HerobrineLeaveBlockGoal extends Goal {
        private final net.leonard.violin.entity.custom.Herobrine herobrine;

        public HerobrineLeaveBlockGoal(net.leonard.violin.entity.custom.Herobrine p_32556_) {
            this.herobrine = p_32556_;
        }

        public boolean canUse() {
            if (this.herobrine.getCarriedBlock() == null) {
                return false;
            } else if (!net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.herobrine.level, this.herobrine)) {
                return false;
            } else {
                return this.herobrine.getRandom().nextInt(reducedTickDelay(2000)) == 0;
            }
        }

        public void tick() {
            RandomSource randomsource = this.herobrine.getRandom();
            Level level = this.herobrine.level;
            int i = Mth.floor(this.herobrine.getX() - 1.0D + randomsource.nextDouble() * 2.0D);
            int j = Mth.floor(this.herobrine.getY() + randomsource.nextDouble() * 2.0D);
            int k = Mth.floor(this.herobrine.getZ() - 1.0D + randomsource.nextDouble() * 2.0D);
            BlockPos blockpos = new BlockPos(i, j, k);
            BlockState blockstate = level.getBlockState(blockpos);
            BlockPos blockpos1 = blockpos.below();
            BlockState blockstate1 = level.getBlockState(blockpos1);
            BlockState blockstate2 = this.herobrine.getCarriedBlock();
            if (blockstate2 != null) {
                blockstate2 = Block.updateFromNeighbourShapes(blockstate2, this.herobrine.level, blockpos);
                if (this.canPlaceBlock(level, blockpos, blockstate2, blockstate, blockstate1, blockpos1) && !net.minecraftforge.event.ForgeEventFactory.onBlockPlace(herobrine, net.minecraftforge.common.util.BlockSnapshot.create(level.dimension(), level, blockpos1), net.minecraft.core.Direction.UP)) {
                    level.setBlock(blockpos, blockstate2, 3);
                    level.gameEvent(GameEvent.BLOCK_PLACE, blockpos, GameEvent.Context.of(this.herobrine, blockstate2));
                    this.herobrine.setCarriedBlock((BlockState)null);
                }

            }
        }

        private boolean canPlaceBlock(Level p_32559_, BlockPos p_32560_, BlockState p_32561_, BlockState p_32562_, BlockState p_32563_, BlockPos p_32564_) {
            return p_32562_.isAir() && !p_32563_.isAir() && !p_32563_.is(Blocks.BEDROCK) && !p_32563_.is(net.minecraftforge.common.Tags.Blocks.ENDERMAN_PLACE_ON_BLACKLIST) && p_32563_.isCollisionShapeFullBlock(p_32559_, p_32564_) && p_32561_.canSurvive(p_32559_, p_32560_) && p_32559_.getEntities(this.herobrine, AABB.unitCubeFromLowerCorner(Vec3.atLowerCornerOf(p_32560_))).isEmpty();
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
            this.pendingTarget = this.herobrine.level.getNearestPlayer(this.startAggroTargetConditions, this.herobrine);
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

        public void isTooCloseToMe() {
            //Equation of a sphere used to find radius, used for setting Herobrine's spherical agro distance
            double distance = Math.sqrt(Math.pow(target.getX() - this.herobrine.getX(), 2) + Math.pow(target.getZ() - this.herobrine.getZ(),2) + Math.pow(target.getY() - this.herobrine.getY(),2));
            System.out.println("Player: " + target.getX() + ", " + target.getY() + ", " + target.getZ());
            System.out.println("Herobrine: " + this.herobrine.getX() + ", " + this.herobrine.getY() + ", " + this.herobrine.getZ());
            if (distance < 1.5) {
                System.out.println("TOO CLOSE");
                this.herobrine.teleportTo(this.herobrine.getX(), -65, this.herobrine.getZ());
            }
        }

        public void tick() {
            System.out.println("tick");

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

    static class HerobrineTakeBlockGoal extends Goal {
        private final net.leonard.violin.entity.custom.Herobrine herobrine;

        public HerobrineTakeBlockGoal(net.leonard.violin.entity.custom.Herobrine p_32585_) {
            this.herobrine = p_32585_;
        }

        public boolean canUse() {
            if (this.herobrine.getCarriedBlock() != null) {
                return false;
            } else if (!net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.herobrine.level, this.herobrine)) {
                return false;
            } else {
                return this.herobrine.getRandom().nextInt(reducedTickDelay(20)) == 0;
            }
        }

        public void tick() {
            RandomSource randomsource = this.herobrine.getRandom();
            Level level = this.herobrine.level;
            int i = Mth.floor(this.herobrine.getX() - 2.0D + randomsource.nextDouble() * 4.0D);
            int j = Mth.floor(this.herobrine.getY() + randomsource.nextDouble() * 3.0D);
            int k = Mth.floor(this.herobrine.getZ() - 2.0D + randomsource.nextDouble() * 4.0D);
            BlockPos blockpos = new BlockPos(i, j, k);
            BlockState blockstate = level.getBlockState(blockpos);
            Vec3 vec3 = new Vec3((double)this.herobrine.getBlockX() + 0.5D, (double)j + 0.5D, (double)this.herobrine.getBlockZ() + 0.5D);
            Vec3 vec31 = new Vec3((double)i + 0.5D, (double)j + 0.5D, (double)k + 0.5D);
            BlockHitResult blockhitresult = level.clip(new ClipContext(vec3, vec31, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, this.herobrine));
            boolean flag = blockhitresult.getBlockPos().equals(blockpos);
            if (blockstate.is(BlockTags.ENDERMAN_HOLDABLE) && flag) {
                level.removeBlock(blockpos, false);
                level.gameEvent(GameEvent.BLOCK_DESTROY, blockpos, GameEvent.Context.of(this.herobrine, blockstate));
                this.herobrine.setCarriedBlock(blockstate.getBlock().defaultBlockState());
            }

        }
    }
}
