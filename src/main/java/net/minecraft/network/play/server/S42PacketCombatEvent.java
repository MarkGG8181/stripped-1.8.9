package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.CombatTracker;

public class S42PacketCombatEvent implements Packet<INetHandlerPlayClient>
{
    public S42PacketCombatEvent.Event eventType;
    public int playerId;
    public int entityId;
    public int duration;
    public String deathMessage;

    public S42PacketCombatEvent()
    {
    }

    @SuppressWarnings("incomplete-switch")
    public S42PacketCombatEvent(CombatTracker combatTrackerIn, S42PacketCombatEvent.Event combatEventType)
    {
        this.eventType = combatEventType;
        EntityLivingBase entitylivingbase = combatTrackerIn.func_94550_c();

        if (combatEventType == S42PacketCombatEvent.Event.END_COMBAT) {
            this.duration = combatTrackerIn.func_180134_f();
            this.entityId = entitylivingbase == null ? -1 : entitylivingbase.getEntityId();
        }
        else if (combatEventType == S42PacketCombatEvent.Event.ENTITY_DIED) {
            this.playerId = combatTrackerIn.getFighter().getEntityId();
            this.entityId = entitylivingbase == null ? -1 : entitylivingbase.getEntityId();
            this.deathMessage = combatTrackerIn.getDeathMessage().getUnformattedText();
        }
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buf) throws IOException
    {
        this.eventType = (S42PacketCombatEvent.Event)buf.readEnumValue(S42PacketCombatEvent.Event.class);

        if (this.eventType == S42PacketCombatEvent.Event.END_COMBAT)
        {
            this.duration = buf.readVarIntFromBuffer();
            this.entityId = buf.readInt();
        }
        else if (this.eventType == S42PacketCombatEvent.Event.ENTITY_DIED)
        {
            this.playerId = buf.readVarIntFromBuffer();
            this.entityId = buf.readInt();
            this.deathMessage = buf.readStringFromBuffer(32767);
        }
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buf) throws IOException
    {
        buf.writeEnumValue(this.eventType);

        if (this.eventType == S42PacketCombatEvent.Event.END_COMBAT)
        {
            buf.writeVarIntToBuffer(this.duration);
            buf.writeInt(this.entityId);
        }
        else if (this.eventType == S42PacketCombatEvent.Event.ENTITY_DIED)
        {
            buf.writeVarIntToBuffer(this.playerId);
            buf.writeInt(this.entityId);
            buf.writeString(this.deathMessage);
        }
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandlerPlayClient handler)
    {
        handler.handleCombatEvent(this);
    }

    public enum Event
    {
        ENTER_COMBAT,
        END_COMBAT,
        ENTITY_DIED
    }
}
