package net.minecraft.entity.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityEndermite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class EntityEnderPearl extends EntityThrowable
{
    private EntityLivingBase thrower;

    public EntityEnderPearl(World worldIn)
    {
        super(worldIn);
    }

    public EntityEnderPearl(World worldIn, EntityLivingBase p_i1783_2_)
    {
        super(worldIn, p_i1783_2_);
        this.thrower = p_i1783_2_;
    }

    public EntityEnderPearl(World worldIn, double x, double y, double z)
    {
        super(worldIn, x, y, z);
    }

    /**
     * Called when this EntityThrowable hits a block or entity.
     */
    protected void onImpact(MovingObjectPosition p_70184_1_)
    {
        EntityLivingBase entitylivingbase = this.getThrower();

        if (p_70184_1_.entityHit != null)
        {
            if (p_70184_1_.entityHit == this.thrower)
            {
                return;
            }

            p_70184_1_.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, entitylivingbase), 0.0F);
        }

        for (int i = 0; i < 32; ++i)
        {
            this.worldObj.spawnParticle(EnumParticleTypes.PORTAL, this.posX, this.posY + this.rand.nextDouble() * 2.0D, this.posZ, this.rand.nextGaussian(), 0.0D, this.rand.nextGaussian(), new int[0]);
        }

        if (!this.worldObj.isRemote)
        {
            if (entitylivingbase instanceof EntityPlayerMP)
            {
                EntityPlayerMP entityplayermp = (EntityPlayerMP)entitylivingbase;

                if (entityplayermp.playerNetServerHandler.getNetworkManager().isChannelOpen() && entityplayermp.worldObj == this.worldObj && !entityplayermp.isPlayerSleeping())
                {
                    if (this.rand.nextFloat() < 0.05F && this.worldObj.getGameRules().getBoolean("doMobSpawning"))
                    {
                        EntityEndermite entityendermite = new EntityEndermite(this.worldObj);
                        entityendermite.setSpawnedByPlayer(true);
                        entityendermite.setLocationAndAngles(entitylivingbase.posX, entitylivingbase.posY, entitylivingbase.posZ, entitylivingbase.rotationYaw, entitylivingbase.rotationPitch);
                        this.worldObj.spawnEntityInWorld(entityendermite);
                    }

                    if (entitylivingbase.isRiding())
                    {
                        entitylivingbase.mountEntity((Entity)null);
                    }

                    entitylivingbase.setPositionAndUpdate(this.posX, this.posY, this.posZ);
                    entitylivingbase.fallDistance = 0.0F;
                    entitylivingbase.attackEntityFrom(DamageSource.fall, 5.0F);
                }
            }
            else if (entitylivingbase != null)
            {
                entitylivingbase.setPositionAndUpdate(this.posX, this.posY, this.posZ);
                entitylivingbase.fallDistance = 0.0F;
            }

            this.setDead();
        }
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        EntityLivingBase entitylivingbase = this.getThrower();

        if (entitylivingbase != null && entitylivingbase instanceof EntityPlayer && !entitylivingbase.isEntityAlive())
        {
            this.setDead();
        }
        else
        {
            super.onUpdate();
        }
    }
}
