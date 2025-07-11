package net.minecraft.util;

public class Vec4b
{
    private byte type;
    private byte x;
    private byte y;
    private byte rotation;

    public Vec4b(byte p_i45555_1_, byte p_i45555_2_, byte p_i45555_3_, byte p_i45555_4_)
    {
        this.type = p_i45555_1_;
        this.x = p_i45555_2_;
        this.y = p_i45555_3_;
        this.rotation = p_i45555_4_;
    }

    public Vec4b(Vec4b p_i45556_1_)
    {
        this.type = p_i45556_1_.type;
        this.x = p_i45556_1_.x;
        this.y = p_i45556_1_.y;
        this.rotation = p_i45556_1_.rotation;
    }

    public byte func_176110_a()
    {
        return this.type;
    }

    public byte func_176112_b()
    {
        return this.x;
    }

    public byte func_176113_c()
    {
        return this.y;
    }

    public byte func_176111_d()
    {
        return this.rotation;
    }

    public boolean equals(Object p_equals_1_)
    {
        if (this == p_equals_1_)
        {
            return true;
        }
        else if (!(p_equals_1_ instanceof Vec4b))
        {
            return false;
        }
        else
        {
            Vec4b vec4b = (Vec4b)p_equals_1_;
            return this.type != vec4b.type ? false : (this.rotation != vec4b.rotation ? false : (this.x != vec4b.x ? false : this.y == vec4b.y));
        }
    }

    public int hashCode()
    {
        int i = this.type;
        i = 31 * i + this.x;
        i = 31 * i + this.y;
        i = 31 * i + this.rotation;
        return i;
    }
}
