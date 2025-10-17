package net.minecraft.entity.passive;

import net.minecraft.entity.EntityLiving;
import net.minecraft.world.World;

public abstract class EntityAmbientCreature extends EntityLiving implements IAnimals
{
    protected EntityAmbientCreature(World worldIn)
    {
        super(worldIn);
    }

    public boolean allowLeashing()
    {
        return false;
    }

}
