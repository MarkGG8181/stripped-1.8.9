package net.minecraft.event;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.util.IChatComponent;

public record HoverEvent(Action action, IChatComponent value) {

    /**
     * Gets the action to perform when this event is raised.
     */
    @Override
    public Action action() {
        return this.action;
    }

    /**
     * Gets the value to perform the action on when this event is raised.  For example, if the action is "show item",
     * this would be the item to show.
     */
    @Override
    public IChatComponent value() {
        return this.value;
    }

    public boolean equals(Object p_equals_1_) {
        if (this == p_equals_1_) {
            return true;
        }
        else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass()) {
            HoverEvent hoverevent = (HoverEvent)p_equals_1_;

            if (this.action != hoverevent.action) {
                return false;
            }
            else {
                if (this.value != null) {
                    if (!this.value.equals(hoverevent.value)) {
                        return false;
                    }
                }
                else if (hoverevent.value != null) {
                    return false;
                }

                return true;
            }
        }
        else {
            return false;
        }
    }

    public String toString() {
        return "HoverEvent{action=" + this.action + ", value=\'" + this.value + '\'' + '}';
    }

    public static enum Action {
        SHOW_TEXT("show_text", true),
        SHOW_ACHIEVEMENT("show_achievement", true),
        SHOW_ITEM("show_item", true),
        SHOW_ENTITY("show_entity", true);

        private static final Map<String, Action> nameMapping = new HashMap<>();
        private final boolean allowedInChat;
        private final String canonicalName;

        private Action(String canonicalNameIn, boolean allowedInChatIn) {
            this.canonicalName = canonicalNameIn;
            this.allowedInChat = allowedInChatIn;
        }

        public boolean shouldAllowInChat() {
            return this.allowedInChat;
        }

        public String getCanonicalName() {
            return this.canonicalName;
        }

        public static Action getValueByCanonicalName(String canonicalNameIn) {
            return (Action)nameMapping.get(canonicalNameIn);
        }

        static {
            for (Action hoverevent$action : values()) {
                nameMapping.put(hoverevent$action.getCanonicalName(), hoverevent$action);
            }
        }
    }
}
