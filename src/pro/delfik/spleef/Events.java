package pro.delfik.spleef;

import org.bukkit.GameMode;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class Events implements Listener{
	@EventHandler
	public void event(PlayerJoinEvent event){
		event.setJoinMessage("");
		event.getPlayer().setGameMode(GameMode.SURVIVAL);
		Sector.getSectorName("lobby").addPlayer(event.getPlayer().getName());
	}

	@EventHandler
	public void event(PlayerQuitEvent event){
		Sector.getSector(event.getPlayer().getName()).clearPlayer(event.getPlayer().getName());
		event.setQuitMessage("");
	}

	@EventHandler
	public void event(BlockBreakEvent event){
		Sector.getSector(event.getPlayer().getName()).onBreak(event);
	}

	@EventHandler
	public void event(PlayerInteractEvent event){
		if(event.getAction() == Action.PHYSICAL)return;
		Sector.getSector(event.getPlayer().getName()).onClick(event);
	}

	@EventHandler
	public void event(PlayerDeathEvent event){
		event.setDeathMessage("");
	}

	@EventHandler
	public void event(InventoryClickEvent event){
		event.setCancelled(event.getWhoClicked().getGameMode() == GameMode.SURVIVAL);
	}

	@EventHandler
	public void event(PlayerDropItemEvent event){
		if(event.getPlayer().getGameMode() == GameMode.SURVIVAL)
			event.setCancelled(true);
	}

	@EventHandler
	public void event(EntityDamageEvent event){
		if(event.getEntity().getType() == EntityType.PLAYER){
			event.setCancelled(true);
			if(event.getCause() == EntityDamageEvent.DamageCause.VOID)
				Sector.getSector(event.getEntity().getName()).onRespawn((Player)event.getEntity());
		}
	}
}