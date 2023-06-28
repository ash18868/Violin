/*package net.leonard.violin.entity.custom;

import net.leonard.violin.entity.ModEntities;
import net.leonard.violin.item.ModItems;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class Poop extends ThrowableItemProjectile {
    public Poop(EntityType<? extends net.leonard.violin.entity.custom.Poop> p_37391_, Level p_37392_) {
        super(p_37391_, p_37392_);
    }

    public Poop(Level p_37399_, LivingEntity p_37400_) {
        super(ModEntities.POOP, p_37400_, p_37399_);
    }

    public Poop(Level p_37394_, double p_37395_, double p_37396_, double p_37397_) {
        super(EntityType.SNOWBALL, p_37395_, p_37396_, p_37397_, p_37394_);
    }



    protected Item getDefaultItem() {
        return Items.SNOWBALL;
    }//TODO Change this to poop

    private ParticleOptions getParticle() {
        ItemStack itemstack = this.getItemRaw();
        return (ParticleOptions)(itemstack.isEmpty() ? ParticleTypes.ITEM_SNOWBALL : new ItemParticleOption(ParticleTypes.ITEM, itemstack));
    }

    public void handleEntityEvent(byte p_37402_) {
        if (p_37402_ == 3) {
            ParticleOptions particleoptions = this.getParticle();

            for(int i = 0; i < 8; ++i) {
                this.level.addParticle(particleoptions, this.getX(), this.getY(), this.getZ(), 0.0D, 0.0D, 0.0D);
            }
        }

    }

    protected void onHitEntity(EntityHitResult p_37404_) {
        super.onHitEntity(p_37404_);
        Entity entity = p_37404_.getEntity();
        int i = entity instanceof Blaze ? 3 : 0;
        entity.hurt(this.damageSources().thrown(this, this.getOwner()), (float)i);
    }

    protected void onHit(HitResult p_37406_) {
        super.onHit(p_37406_);
        if (!this.level.isClientSide) {
            this.level.broadcastEntityEvent(this, (byte)3);
            this.discard();
        }

    }
}*/