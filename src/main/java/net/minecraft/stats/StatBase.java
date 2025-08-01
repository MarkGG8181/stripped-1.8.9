package net.minecraft.stats;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import net.minecraft.event.HoverEvent;
import net.minecraft.scoreboard.IScoreObjectiveCriteria;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.IJsonSerializable;

public class StatBase
{
    /** The Stat ID */
    public final String statId;

    /** The Stat name */
    private final IChatComponent statName;
    public boolean isIndependent;
    private final IStatType type;
    private final IScoreObjectiveCriteria objectiveCriteria;
    private Class <? extends IJsonSerializable > serializableClazz;
    private static NumberFormat numberFormat = NumberFormat.getIntegerInstance(Locale.US);
    public static IStatType simpleStatType = new IStatType()
    {
        public String format(int number)
        {
            return StatBase.numberFormat.format((long)number);
        }
    };
    private static DecimalFormat decimalFormat = new DecimalFormat("########0.00");
    public static IStatType timeStatType = new IStatType()
    {
        public String format(int number)
        {
            double d0 = (double)number / 20.0D;
            double d1 = d0 / 60.0D;
            double d2 = d1 / 60.0D;
            double d3 = d2 / 24.0D;
            double d4 = d3 / 365.0D;
            return d4 > 0.5D ? StatBase.decimalFormat.format(d4) + " y" : (d3 > 0.5D ? StatBase.decimalFormat.format(d3) + " d" : (d2 > 0.5D ? StatBase.decimalFormat.format(d2) + " h" : (d1 > 0.5D ? StatBase.decimalFormat.format(d1) + " m" : d0 + " s")));
        }
    };
    public static IStatType distanceStatType = new IStatType()
    {
        public String format(int number)
        {
            double d0 = (double)number / 100.0D;
            double d1 = d0 / 1000.0D;
            return d1 > 0.5D ? StatBase.decimalFormat.format(d1) + " km" : (d0 > 0.5D ? StatBase.decimalFormat.format(d0) + " m" : number + " cm");
        }
    };
    public static IStatType divideByTen = new IStatType()
    {
        public String format(int number)
        {
            return StatBase.decimalFormat.format((double)number * 0.1D);
        }
    };

    public StatBase(String statIdIn, IChatComponent statNameIn, IStatType typeIn)
    {
        this.statId = statIdIn;
        this.statName = statNameIn;
        this.type = typeIn;
        this.objectiveCriteria = new ObjectiveStat(this);
        IScoreObjectiveCriteria.INSTANCES.put(this.objectiveCriteria.getName(), this.objectiveCriteria);
    }

    public StatBase(String statIdIn, IChatComponent statNameIn)
    {
        this(statIdIn, statNameIn, simpleStatType);
    }

    /**
     * Initializes the current stat as independent (i.e., lacking prerequisites for being updated) and returns the
     * current instance.
     */
    public StatBase initIndependentStat()
    {
        this.isIndependent = true;
        return this;
    }

    /**
     * Register the stat into StatList.
     */
    public StatBase registerStat()
    {
        if (StatList.oneShotStats.containsKey(this.statId))
        {
            throw new RuntimeException("Duplicate stat id: \"" + ((StatBase)StatList.oneShotStats.get(this.statId)).statName + "\" and \"" + this.statName + "\" at id " + this.statId);
        }
        else
        {
            StatList.allStats.add(this);
            StatList.oneShotStats.put(this.statId, this);
            return this;
        }
    }

    /**
     * Returns whether or not the StatBase-derived class is a statistic (running counter) or an achievement (one-shot).
     */
    public boolean isAchievement()
    {
        return false;
    }

    public String format(int p_75968_1_)
    {
        return this.type.format(p_75968_1_);
    }

    public IChatComponent getStatName()
    {
        IChatComponent ichatcomponent = this.statName.createCopy();
        ichatcomponent.getChatStyle().setColor(EnumChatFormatting.GRAY);
        ichatcomponent.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ACHIEVEMENT, new ChatComponentText(this.statId)));
        return ichatcomponent;
    }

    /**
     * 1.8.9
     */
    public IChatComponent createChatComponent()
    {
        IChatComponent ichatcomponent = this.getStatName();
        IChatComponent ichatcomponent1 = (new ChatComponentText("[")).appendSibling(ichatcomponent).appendText("]");
        ichatcomponent1.setChatStyle(ichatcomponent.getChatStyle());
        return ichatcomponent1;
    }

    public boolean equals(Object p_equals_1_)
    {
        if (this == p_equals_1_)
        {
            return true;
        }
        else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass())
        {
            StatBase statbase = (StatBase)p_equals_1_;
            return this.statId.equals(statbase.statId);
        }
        else
        {
            return false;
        }
    }

    public int hashCode()
    {
        return this.statId.hashCode();
    }

    public String toString()
    {
        return "Stat{id=" + this.statId + ", nameId=" + this.statName + ", awardLocallyOnly=" + this.isIndependent + ", formatter=" + this.type + ", objectiveCriteria=" + this.objectiveCriteria + '}';
    }

    /**
     * 1.8.9
     */
    public IScoreObjectiveCriteria getCriteria()
    {
        return this.objectiveCriteria;
    }

    public Class <? extends IJsonSerializable > func_150954_l()
    {
        return this.serializableClazz;
    }

    public StatBase func_150953_b(Class <? extends IJsonSerializable > p_150953_1_)
    {
        this.serializableClazz = p_150953_1_;
        return this;
    }
}
