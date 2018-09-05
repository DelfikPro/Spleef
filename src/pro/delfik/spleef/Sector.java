package pro.delfik.spleef;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import pro.delfik.lmao.util.U;
import pro.delfik.lmao.util.Vec;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Sector {
	private static final Map<String, Sector> sectors = new HashMap<>();

	private final List<String> players = new ArrayList<>();

	private final Vec spawnPoint;

	protected Sector(Vec vec){
		this.spawnPoint = vec;
	}

	public List<String> getPlayers(){
		return players;
	}

	public void clearPlayer(String nick){
		if(!containsPlayer(nick))return;
		players.remove(nick);
		onLeave(nick);
	}

	public void onBreak(BlockBreakEvent event){
		if(event.getPlayer().getGameMode() != GameMode.SURVIVAL)return;
		event.setCancelled(onBreak(event.getPlayer().getName(), event.getPlayer(), event.getBlock()));
	}

	public void onClick(PlayerInteractEvent event){
		String nick = event.getPlayer().getName();
		Material material = event.getMaterial();
		if(material == Material.COMPASS) U.send(nick, "LOBBY_1");
		else onClick(nick, material);
	}

	public void onRespawn(Player player){
		String nick = player.getName();
		teleport(nick);
		onRespawn(nick);
	}

	public void addPlayer(String nick){
		if(!canJoin(nick))return;
		Sector sector = getSector(nick);
		if(sector != null)sector.clearPlayer(nick);
		players.add(nick);
		teleport(nick);
		onJoin(nick);
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

	protected boolean canJoin(String nick){
		return true;
	}

	protected void onClick(String nick, Material material){
	}

	protected void onRespawn(String nick){}

	protected void onJoin(String nick){}

	protected void onLeave(String nick){}

	protected boolean onBreak(String nick, Player player, Block block){
		return true;
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
}
