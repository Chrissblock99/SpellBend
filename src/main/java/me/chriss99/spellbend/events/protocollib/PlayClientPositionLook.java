package me.chriss99.spellbend.events.protocollib;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import me.chriss99.spellbend.SpellBend;
import me.chriss99.spellbend.data.PlayerSessionData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.lang.reflect.InvocationTargetException;

public class PlayClientPositionLook extends PacketAdapter {
    public PlayClientPositionLook() {
        super(SpellBend.getInstance(), ListenerPriority.HIGHEST, PacketType.Play.Client.POSITION_LOOK);
        SpellBend.registerPacketListener(this);
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        Player player = event.getPlayer();
        Bukkit.getLogger().info("Play.Client.POSITION_LOOK " + player.getName() + " " + event.getPacket().toString());
        StructureModifier<Double> eventPacketDoubles = event.getPacket().getDoubles();
        StructureModifier<Float> eventPacketFloats = event.getPacket().getFloat();
        Location location = player.getLocation();

        if (!PlayerSessionData.getPlayerSession(player).getIsMovementStunned().valueIsLargerZero() ||
                location.toVector().equals(new Vector(eventPacketDoubles.read(0), eventPacketDoubles.read(1), eventPacketDoubles.read(2)))) {
            Bukkit.getLogger().info("not cancelling");
            return;
        }

        event.setCancelled(true);

        ProtocolManager manager = SpellBend.getProtocolManager();
        PacketContainer packet = manager.createPacket(PacketType.Play.Server.POSITION);

        packet.getDoubles().write(0, location.getX());
        packet.getDoubles().write(1, location.getY());
        packet.getDoubles().write(2, location.getZ());
        packet.getFloat().write(0, eventPacketFloats.read(0));
        packet.getFloat().write(1, eventPacketFloats.read(1));

        try {
            manager.sendServerPacket(player, packet, false);
        } catch (InvocationTargetException e) {
            Bukkit.getLogger().info("HAHA PACKET SENDING DIDN'T WORK");
        }
    }
}
