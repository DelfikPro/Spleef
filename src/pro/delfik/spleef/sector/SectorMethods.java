//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package pro.delfik.spleef.sector;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public interface SectorMethods {
	default void onClick(String nick, Material material) {
	}

	default void onDeath(Player player) {
	}

	default void onRespawn(String nick) {
	}

	default void onJoin(String nick) {
	}

	default void onLeave(String nick) {
	}

	default void onMove(String nick, Location in, Location out) {
	}

	default boolean canJoin(String nick) {
		return true;
	}

	default boolean onDamage(String nick, DamageCause cause) {
		return true;
	}

	default boolean onHit(String entity, String damager) {
		return true;
	}

	default boolean onBreak(String nick, Player player, Block block) {
		return true;
	}

	default boolean visible() {
		return true;
	}
}
