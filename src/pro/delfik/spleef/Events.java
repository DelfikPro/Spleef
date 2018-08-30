package pro.delfik.spleef;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pro.delfik.lmao.core.Lmao;
import pro.delfik.lmao.util.U;
import implario.util.Scheduler;

import java.util.HashMap;
import java.util.Map;

public class Events implements Listener{
	private volatile static boolean started = false, schedule = false, start = false;
	public volatile static int id = 0;
	private volatile static Map<String, Long> map = new HashMap<>(4);

	@EventHandler
	public void event(BlockBreakEvent event){
		if(event.getPlayer().getGameMode() == GameMode.CREATIVE)return;
		if(started && event.getBlock().getType() == Material.SNOW_BLOCK){
			Player player = event.getPlayer();
			map.put(player.getName(), System.currentTimeMillis());
			return;
		}
		event.setCancelled(true);
	}

	@EventHandler
	public void event(InventoryClickEvent event){
		if(event.getWhoClicked().getGameMode() == GameMode.CREATIVE)return;
		event.setCancelled(true);
	}

	@EventHandler
	public void event(PlayerInteractEvent event){
		Action action = event.getAction();
		if(event.isBlockInHand())return;
		if((event.getMaterial() == null || event.getMaterial() != Material.COMPASS))return;
		if(action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK)return;
		U.send(event.getPlayer().getName(), "LOBBY_1");
	}

	@EventHandler
	public void event(EntityDamageByEntityEvent event){
		event.setCancelled(true);
	}

	@EventHandler
	public void event(PlayerMoveEvent event){
		if(event.getTo().getBlockY() > 255 || event.getTo().getBlockX() > 255 || event.getTo().getBlockZ() > 255 ||
				event.getTo().getBlockY() < -255 || event.getTo().getBlockZ() < -255)
			event.getPlayer().teleport(hub());
	}

	@EventHandler
	public void event(EntityDamageEvent event){
		if(!(event.getEntity() instanceof Player))return;
		EntityDamageEvent.DamageCause cause = event.getCause();
		if(cause == EntityDamageEvent.DamageCause.FALL)event.setCancelled(true);
		else if(cause == EntityDamageEvent.DamageCause.VOID){
			Player player = (Player)event.getEntity();
			if(player.getGameMode() == GameMode.SURVIVAL && started){
				Bukkit.broadcastMessage("§eИгрок §c" + player.getName() + " §eупал");
				player.setGameMode(GameMode.SPECTATOR);
				int players = 0;
				Player last = null;
				for(Player pl : Bukkit.getOnlinePlayers())
					if(pl.getGameMode() == GameMode.SURVIVAL){
						players++;
						last = pl;
					}
				if(players == 1){
					Bukkit.broadcastMessage("§eВау, игрок §c" + last.getName() + " §eпобедил!");
					Bukkit.broadcastMessage("§cЧерез пару секунд сервер перезагрузится");
					Bukkit.reload();
				}
			}
			player.teleport(hub());
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void event(PlayerJoinEvent event){
		Player player = event.getPlayer();
		player.teleport(hub());
		player.getInventory().clear();
		player.getInventory().setItem(8, new ItemStack(Material.COMPASS));
		ItemStack stack = new ItemStack(Material.DIAMOND_SPADE);
		ItemMeta meta = stack.getItemMeta();
		meta.addEnchant(Enchantment.getById(32), 10, true);
		stack.setItemMeta(meta);
		player.getInventory().setItem(0, stack);
		player.setGameMode(start ? GameMode.SPECTATOR : GameMode.SURVIVAL);
		event.setJoinMessage("");
		if(!schedule && Bukkit.getOnlinePlayers().size() > 1){
			Bukkit.broadcastMessage("§eЧерез 10 секунд начнётся игра!");
			schedule = true;
			Bukkit.getScheduler().runTaskLater(Lmao.plugin, () -> {
				if(Bukkit.getOnlinePlayers().size() < 2){
					schedule = false;
					Bukkit.broadcastMessage("§eНе достаточно игроков");
					return;
				}
				Events.start();
			}, 200L);
		}
	}

	@EventHandler
	public void event(PlayerRespawnEvent event){
		Player player = event.getPlayer();
		if(player.getGameMode() == GameMode.SURVIVAL && started){
			Bukkit.broadcastMessage("§eИгрок §c" + player.getName() + " §eупал");
			player.setGameMode(GameMode.SPECTATOR);
			int players = 0;
			Player last = null;
			for(Player pl : Bukkit.getOnlinePlayers())
				if(pl.getGameMode() == GameMode.SURVIVAL){
					players++;
					last = pl;
				}
			if(players == 1){
				Bukkit.broadcastMessage("§eВау, игрок §c" + last.getName() + " §eпобедил!");
				Bukkit.broadcastMessage("§cЧерез пару секунд сервер перезагрузится");
				Bukkit.reload();
			}
		}
		event.setRespawnLocation(hub());
	}

	@EventHandler
	public void event(PlayerDropItemEvent event){
		if(event.getPlayer().getGameMode() == GameMode.CREATIVE)return;
		event.setCancelled(true);
	}

	@EventHandler
	public void event(PlayerQuitEvent event){
		event.setQuitMessage("");
	}

	public static Location hub(){
		return new Location(Bukkit.getWorlds().get(0), 100, 103, 100);
	}

	public static void start(){
		start = true;
		schedule = true;
		for(Player player : Bukkit.getOnlinePlayers()){
			if(player.getGameMode() != GameMode.SURVIVAL)continue;
			player.teleport(new Location(Bukkit.getWorlds().get(0), 100, 102, 100));
		}
		Scheduler.addTask(new Scheduler.Task(10) {
			@Override
			public void run() {
				if(started)
					for (Player player : Bukkit.getOnlinePlayers()){
						if(player.getGameMode() != GameMode.SURVIVAL) continue;
						Long l = map.get(player.getName());
						if(l == null)continue;
						if(l + 3000 > System.currentTimeMillis())continue;
						if(player.getHealth() < 3.1){
							player.teleport(new Location(player.getWorld(), 0, -100, 0));
						}else player.setHealth(player.getHealth() - 3);
					}
			}
		});
		id = Bukkit.getScheduler().runTaskLater(Lmao.plugin, () -> {
			Bukkit.broadcastMessage("§eНикто не успел выиграть..");
			Bukkit.reload();
		}, 3000L).getTaskId();
		Bukkit.getScheduler().runTaskLater(Lmao.plugin, () -> started = true, 100L);
		Bukkit.broadcastMessage("§eДа начнётся сплиф!");
	}
}
