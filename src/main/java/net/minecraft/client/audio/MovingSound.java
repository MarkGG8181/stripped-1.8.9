package net.minecraft.client.audio;

import net.minecraft.util.ResourceLocation;

public abstract class MovingSound extends PositionedSound implements ITickableSound
{
    protected boolean donePlaying;

    protected MovingSound(ResourceLocation location)
    {
        super(location);
    }

    public boolean isDonePlaying()
    {
        return this.donePlaying;
    }
}
