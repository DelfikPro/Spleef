package pro.delfik.spleef.sector;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import pro.delfik.lmao.outward.item.ItemBuilder;
import pro.delfik.lmao.util.U;
import pro.delfik.lmao.util.Vec;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Sector implements SectorMethods{
	private static final Map<String, Sector> sectors = new HashMap<>();
	private static final ItemStack teleportLobby = new ItemBuilder(Material.COMPASS).withDisplayName("§eВернуться в лобби").build();

	private final List<String> players = new ArrayList<>();
	private final Vec spawnPoint;
	private final Material material;

	protected Sector(Vec vec, Material material){
		this.spawnPoint = vec;
		this.material = material;
	}

	protected Sector(Vec vec){
		this(vec, Material.DIAMOND_SPADE);
	}

	public Material getMaterial() {
		return material;
	}

	public List<String> getPlayers(){
		return players;
	}

	public void onMove(PlayerMoveEvent event){
		onMove(event.getPlayer().getName(), event.getFrom(), event.getTo());
	}

	public void clearPlayer(String nick){
		if(!containsPlayer(nick))return;
		players.remove(nick);
		onLeave(nick);
	}

	public void onDeath(PlayerRespawnEvent event){
		event.setRespawnLocation(getSpawnPoint());
		onDeath(event.getPlayer());
	}

	public void onHit(EntityDamageByEntityEvent event){
		event.setCancelled(onHit(event.getEntity().getName(), event.getDamager().getName()));
	}

	public void onDamage(EntityDamageEvent event){
		EntityDamageEvent.DamageCause cause = event.getCause();
		String nick = event.getEntity().getName();
		if(cause == EntityDamageEvent.DamageCause.ENTITY_ATTACK)return;
		event.setCancelled(onDamage(nick, cause));
	}

	public void onBreak(BlockBreakEvent event){
		if(event.getPlayer().getGameMode() != GameMode.SURVIVAL)return;
		event.setCancelled(onBreak(event.getPlayer().getName(), event.getPlayer(), event.getBlock()));
	}

	public void onClick(PlayerInteractEvent event){
		String nick = event.getPlayer().getName();
		Material material = event.getMaterial();
		if(material == teleportLobby.getType()) U.send(nick, "LOBBY_1");
		else onClick(nick, material);
	}

	public void onRespawn(Player player){
		String nick = player.getName();
		teleport(player);
		onRespawn(nick);
	}

	public void addPlayer(Player player){
		String nick = player.getName();
		if(!canJoin(nick))return;
		Sector sector = getSector(nick);
		if(sector != null)sector.clearPlayer(nick);
		players.add(nick);
		teleport(player);
		giveDefaultItems(player);
		onJoin(nick);
	}

	public void addPlayer(String nick){
		addPlayer(Bukkit.getPlayer(nick));
	}

	public boolean containsPlayer(String nick){
		return players.contains(nick);
	}

	public void sendMessage(String message){
		for(String nick : players){
			Player player = Bukkit.getPlayer(nick);
			if(player == null)continue;
			player.sendMessage(message);
		}
	}

	public void sendMessage(String nick, String message){
		sendMessage("§eИгрок §c" + nick + " §e" + message);
	}

	protected void teleport(String nick){
		teleport(Bukkit.getPlayer(nick));
	}

	protected void teleport(Player player){
		if(player == null)return;
		player.teleport(getSpawnPoint());
	}

	protected Location getSpawnPoint(){
		return spawnPoint.toLocation(Bukkit.getWorlds().get(0));
	}

	protected void giveDefaultItems(Player player){
		PlayerInventory inventory = player.getInventory();
		inventory.setBoots(null);
		inventory.setLeggings(null);
		inventory.setChestplate(null);
		inventory.setHelmet(null);
		inventory.clear();
		inventory.setItem(8, teleportLobby);
	}

	protected void giveDefaultItems(String nick){
		giveDefaultItems(Bukkit.getPlayer(nick));
	}

	public static Sector getSectorName(String sector){
		for(Map.Entry<String, Sector> entry : sectors.entrySet())
			if(entry.getKey().equals(sector))return entry.getValue();
		return null;
	}

	public static Sector getSector(String nick){
		for(Sector sector : sectors.values())
			if(sector.containsPlayer(nick))return sector;
		return null;
	}

	public static void addSector(String sectorName, Sector sector){
		sectors.put(sectorName, sector);
	}

	public static Collection<String> getNames(){
		return sectors.keySet();
	}

	public static Collection<Sector> getSectors(){
		return sectors.values();
	}

	public static Collection<Map.Entry<String, Sector>> getEntries(){
		return sectors.entrySet();
	}
}
