package net.minecraft.scoreboard;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class Team {
    /**
     * Same as ==
     */
    public boolean isSameTeam(Team other) {
        return other == null ? false : this == other;
    }

    /**
     * Retrieve the name by which this team is registered in the scoreboard
     */
    public abstract String getRegisteredName();

    public abstract String formatString(String input);

    public abstract boolean getSeeFriendlyInvisiblesEnabled();

    public abstract boolean getAllowFriendlyFire();

    public abstract Team.EnumVisible getNameTagVisibility();

    public abstract Collection<String> getMembershipCollection();

    public abstract Team.EnumVisible getDeathMessageVisibility();

    public enum EnumVisible {
        ALWAYS("always", 0),
        NEVER("never", 1),
        HIDE_FOR_OTHER_TEAMS("hideForOtherTeams", 2),
        HIDE_FOR_OWN_TEAM("hideForOwnTeam", 3);

        private static final Map<String, Team.EnumVisible> visibilities = new HashMap<>();
        public final String internalName;
        public final int id;

        public static String[] func_178825_a() {
            return visibilities.keySet().toArray(new String[0]);
        }

        public static Team.EnumVisible func_178824_a(String p_178824_0_) {
            return visibilities.get(p_178824_0_);
        }

        EnumVisible(String internalName, int id) {
            this.internalName = internalName;
            this.id = id;
        }

        static {
            for (Team.EnumVisible team$enumvisible : values()) {
                visibilities.put(team$enumvisible.internalName, team$enumvisible);
            }
        }
    }
}
