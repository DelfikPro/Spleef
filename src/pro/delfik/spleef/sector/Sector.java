//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package pro.delfik.spleef.sector;

import implario.util.Converter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import pro.delfik.lmao.outward.item.ItemBuilder;
import pro.delfik.lmao.util.U;
import pro.delfik.lmao.util.Vec;
import pro.delfik.spleef.GameSelector;
import pro.delfik.spleef.Minigame;
import pro.delfik.spleef.modification.SectorInfo;

public abstract class Sector implements SectorMethods {
	private static final Map<String, Sector> sectors = new HashMap();
	private static final ItemStack teleportLobby;
	private static volatile int idCounter;
	private final List<String> players = new ArrayList();
	protected final Vec spawnPoint;
	private int id;
	private int localid;
	private String nickname;

	protected Sector(Vec vec) {
		this.spawnPoint = vec;
		if (this.getMinigame() != null) {
			this.localid = ++this.getMinigame().counter;
		}

	}

	public abstract Minigame getMinigame();

	protected String getDisplayName() {
		return "§e" + this.getMinigame() + " §f#" + this.localid;
	}

	public String getNickname() {
		return this.nickname;
	}

	public ItemStack getItem() {
		int players = this.getPlayers().size();
		return (new ItemBuilder(this.getMaterial())).withAmount(players == 0 ? 1 : players).withDisplayName(this.getDisplayName()).withLore(new String[]{"§e[§f" + players + " игрок" + Converter.plural(players, "", "а", "ов") + "§e]", "§f", "§f>> §a§lВойти§f <<"}).build();
	}

	public Material getMaterial() {
		return this.getMinigame().getMaterial();
	}

	public List<String> getPlayers() {
		return this.players;
	}

	public void onMove(PlayerMoveEvent event) {
		this.onMove(event.getPlayer().getName(), event.getFrom(), event.getTo());
	}

	public void clearPlayer(String nick) {
		if (this.containsPlayer(nick)) {
			this.players.remove(nick);
			this.onLeave(nick);
		}
	}

	public void onDeath(PlayerRespawnEvent event) {
		event.setRespawnLocation(this.getSpawnPoint());
		this.onDeath(event.getPlayer());
	}

	public void onHit(EntityDamageByEntityEvent event) {
		event.setCancelled(this.onHit(event.getEntity().getName(), event.getDamager().getName()));
	}

	public void onDamage(EntityDamageEvent event) {
		DamageCause cause = event.getCause();
		String nick = event.getEntity().getName();
		if (cause != DamageCause.ENTITY_ATTACK) {
			event.setCancelled(this.onDamage(nick, cause));
		}
	}

	public void onBreak(BlockBreakEvent event) {
		if (event.getPlayer().getGameMode() == GameMode.SURVIVAL) {
			event.setCancelled(this.onBreak(event.getPlayer().getName(), event.getPlayer(), event.getBlock()));
		}
	}

	public void onClick(PlayerInteractEvent event) {
		String nick = event.getPlayer().getName();
		Material material = event.getMaterial();
		if (material == teleportLobby.getType()) {
			U.send(nick, "LOBBY_1");
		} else {
			this.onClick(nick, material);
		}

	}

	public void onRespawn(Player player) {
		String nick = player.getName();
		this.teleport(player);
		this.onRespawn(nick);
	}

	public void addPlayer(Player player) {
		String nick = player.getName();
		if (this.canJoin(nick)) {
			Sector sector = getSector(nick);
			if (sector != null) {
				sector.clearPlayer(nick);
			}

			this.players.add(nick);
			this.teleport(player);
			this.giveDefaultItems(player);
			this.onJoin(nick);
		}
	}

	public void addPlayer(String nick) {
		this.addPlayer(Bukkit.getPlayer(nick));
	}

	public boolean containsPlayer(String nick) {
		return this.players.contains(nick);
	}

	public void sendMessage(String message) {
		Iterator var2 = this.players.iterator();

		while(var2.hasNext()) {
			String nick = (String)var2.next();
			Player player = Bukkit.getPlayer(nick);
			if (player != null) {
				player.sendMessage(message);
			}
		}

	}

	public void sendMessage(String nick, String message) {
		this.sendMessage("§eИгрок §c" + nick + " §e" + message);
	}

	protected void teleport(String nick) {
		this.teleport(Bukkit.getPlayer(nick));
	}

	protected void teleport(Player player) {
		if (player != null) {
			player.teleport(this.getSpawnPoint());
		}
	}

	protected Location getSpawnPoint() {
		return this.spawnPoint.toLocation((World)Bukkit.getWorlds().get(0));
	}

	protected void giveDefaultItems(Player player) {
		PlayerInventory inventory = player.getInventory();
		inventory.setBoots((ItemStack)null);
		inventory.setLeggings((ItemStack)null);
		inventory.setChestplate((ItemStack)null);
		inventory.setHelmet((ItemStack)null);
		inventory.clear();
		inventory.setItem(8, teleportLobby);
	}

	protected void giveDefaultItems(String nick) {
		this.giveDefaultItems(Bukkit.getPlayer(nick));
	}

	public static Sector getSectorName(String sector) {
		Iterator var1 = sectors.entrySet().iterator();

		Entry entry;
		do {
			if (!var1.hasNext()) {
				return null;
			}

			entry = (Entry)var1.next();
		} while(!((String)entry.getKey()).equals(sector));

		return (Sector)entry.getValue();
	}

	public static Sector getSector(String nick) {
		Iterator var1 = sectors.values().iterator();

		Sector sector;
		do {
			if (!var1.hasNext()) {
				return null;
			}

			sector = (Sector)var1.next();
		} while(!sector.containsPlayer(nick));

		return sector;
	}

	public static void addSector(String sectorName, Sector sector) {
		sectors.put(sectorName, sector);
		sector.nickname = sectorName;
		sector.id = idCounter++;
		if (!(sector instanceof SectorLobby)) {
			GameSelector.register(sector);
		}

	}

	public static Collection<String> getNames() {
		return sectors.keySet();
	}

	public static Collection<Sector> getSectors() {
		return sectors.values();
	}

	public static Collection<Entry<String, Sector>> getEntries() {
		return sectors.entrySet();
	}

	public int getID() {
		return this.id;
	}

	public abstract SectorInfo getInfo();

	static {
		teleportLobby = (new ItemBuilder(Material.COMPASS)).withDisplayName("§eВернуться в лобби").build();
		idCounter = 0;
	}
}
