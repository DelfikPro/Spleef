package pro.delfik.spleef;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import pro.delfik.lmao.outward.item.I;
import pro.delfik.lmao.util.Vec3i;

import java.util.ArrayList;
import java.util.List;

public class SectorSpleef extends Sector{
	private volatile boolean gameStarted = false, gameEnd = false;
	private final Vec3i one, two;
	private final List<String> game = new ArrayList<>();
	private int task = 0;

	public SectorSpleef(){
		super(new Vec3i(200, 10, 200));
		Location location = getSpawnPoint();
		one = new Vec3i(location.getBlockX() - 15, location.getBlockY() - 2, location.getBlockZ() - 15);
		two = new Vec3i(location.getBlockX() + 15, location.getBlockY() - 2, location.getBlockZ() + 15);
		setSnow();
	}

	@Override
	protected void onClick(String nick, Material material) {
		if(material == Material.EMERALD)
			Sector.getSectorName("lobby").addPlayer(nick);
	}

	@Override
	protected boolean canJoin(String nick) {
		return !gameEnd;
	}

	@Override
	protected void onRespawn(String nick) {
		if(!gameStarted)return;
		playerDeath(nick, false);
	}

	@Override
	protected void onJoin(String nick) {
		if(gameStarted)addSpectator(nick);
		else {
			game.add(nick);
			if(game.size() > 1) startGame();
		}
	}

	@Override
	protected void onLeave(String nick) {
		playerDeath(nick, true);
	}

	@Override
	protected boolean onBreak(String nick, Player player, Block block) {
		return !(gameStarted && block.getType() == Material.SNOW_BLOCK);
	}

	private void playerDeath(String nick, boolean leave){
		sendMessage("§eИгрок §c" + nick + " §e" + (leave ? "ливнул" : "упал"));
		game.remove(nick);
		if(!leave) addSpectator(nick);
		if(game.size() == 1){
			if(gameStarted){
				sendMessage("§eИгрок §c" + game.get(0) + " §eпобедил");
				restartGame();
			}else if(leave){
				Bukkit.getScheduler().cancelTask(task);
				sendMessage("Недостаточно игроков");
			}
		}
	}

	private void addSpectator(String nick){
		Bukkit.getPlayer(nick).setGameMode(GameMode.SPECTATOR);
	}

	private void startGame(){
		if(task != 0)return;
		sendMessage("§eЧерез 5 секунд, игра начнётся");
		task = I.delay(() -> {
			gameStarted = true;
			sendMessage("§eИгра началась!");
		}, 100).getTaskId();
	}

	private void restartGame(){
		gameEnd = true;
		gameStarted = false;
		for(String nick : getPlayers()){
			Player player = Bukkit.getPlayer(nick);
			teleport(player);
			player.setGameMode(GameMode.SURVIVAL);
		}
		setSnow();
		game.clear();
		game.addAll(getPlayers());
		gameEnd = false;
		task = 0;
		if(game.size() > 1)startGame();
	}

	private void setSnow(){
		for(int x = one.x; x < two.x; x++)
			for(int z = one.z; z < two.z; z++)
				new Location(Bukkit.getWorlds().get(0), x, one.y, z)
						.getBlock().setType(Material.SNOW_BLOCK);
	}
}
