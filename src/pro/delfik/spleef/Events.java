//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package pro.delfik.spleef;

import implario.net.Packet;
import implario.net.packet.PacketCreateTop;
import org.bukkit.GameMode;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import pro.delfik.lmao.ev.added.PacketEvent;
import pro.delfik.spleef.sector.Sector;

public class Events implements Listener {
	public Events() {
	}

	@EventHandler
	public void event(PlayerJoinEvent event) {
		event.setJoinMessage("");
		event.getPlayer().setGameMode(GameMode.SURVIVAL);
		Sector.getSectorName("lobby").addPlayer(event.getPlayer());
	}

	@EventHandler
	public void event(PlayerQuitEvent event) {
		Sector.getSector(event.getPlayer().getName()).clearPlayer(event.getPlayer().getName());
		event.setQuitMessage("");
	}

	@EventHandler
	public void event(BlockBreakEvent event) {
		Sector.getSector(event.getPlayer().getName()).onBreak(event);
	}

	@EventHandler
	public void event(PlayerInteractEvent event) {
		if (event.getAction() != Action.PHYSICAL) {
			Sector sector = Sector.getSector(event.getPlayer().getName());
			if (sector != null) {
				sector.onClick(event);
			}
		}
	}

	@EventHandler
	public void event(PlayerDeathEvent event) {
		event.setDeathMessage("");
	}

	@EventHandler
	public void event(InventoryClickEvent event) {
		if (event.getWhoClicked().getGameMode() != GameMode.CREATIVE) {
			event.setCancelled(true);
		}

	}

	@EventHandler
	public void event(PlayerDropItemEvent event) {
		if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
			event.setCancelled(true);
		}

	}

	@EventHandler
	public void event(PlayerRespawnEvent event) {
		Sector.getSector(event.getPlayer().getName()).onDeath(event);
	}

	@EventHandler
	public void event(PlayerMoveEvent event) {
		Sector.getSector(event.getPlayer().getName()).onMove(event);
	}

	@EventHandler
	public void event(EntityDamageByEntityEvent event) {
		if (event.getEntity().getType() == EntityType.PLAYER && event.getDamager().getType() == EntityType.PLAYER) {
			Sector.getSector(event.getEntity().getName()).onHit(event);
		}
	}

	@EventHandler
	public void event(EntityDamageEvent event) {
		if (event.getEntity().getType() == EntityType.PLAYER) {
			if (event.getCause() == DamageCause.VOID) {
				event.setCancelled(true);
				Sector.getSector(event.getEntity().getName()).onRespawn((Player)event.getEntity());
			} else {
				Sector.getSector(event.getEntity().getName()).onDamage(event);
			}

		}
	}

	@EventHandler
	public void event(PacketEvent event) {
		Packet packet = event.getPacket();
		if (packet instanceof PacketCreateTop) {
			Top.update(((PacketCreateTop)packet).getTop());
		}

	}
}
