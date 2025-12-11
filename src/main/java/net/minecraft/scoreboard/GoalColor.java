package net.minecraft.scoreboard;

import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;

public class GoalColor implements IScoreObjectiveCriteria
{
    private final String goalName;

    public GoalColor(String identifier, EnumChatFormatting formatting)
    {
        this.goalName = identifier + formatting.getFriendlyName();
        IScoreObjectiveCriteria.INSTANCES.put(this.goalName, this);
    }

    public String getName()
    {
        return this.goalName;
    }

    public int setScore(List<EntityPlayer> players)
    {
        return 0;
    }

    public boolean isReadOnly()
    {
        return false;
    }

    public IScoreObjectiveCriteria.EnumRenderType getRenderType()
    {
        return IScoreObjectiveCriteria.EnumRenderType.INTEGER;
    }
}
