package pro.delfik.spleef;

import implario.util.Scheduler;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import pro.delfik.lmao.core.Person;
import pro.delfik.lmao.outward.item.I;
import pro.delfik.lmao.util.Vec;

import java.util.ArrayList;
import java.util.List;

public class SectorTNT extends Sector{
	private static final Material block = Material.EMERALD_BLOCK;

	private final Cuboid cuboids[];
	private final List<String> game = new ArrayList<>();
	private volatile int gameTask = 0, startGameTask = 0;
	private volatile boolean gameStarted = false, gameEnd = false;
	private volatile List<String> blockBreak = new ArrayList<>();

	public SectorTNT(Vec spawn, Cuboid... cuboids) {
		super(spawn, Material.TNT);
		this.cuboids = cuboids;
		setBlocks();
		Scheduler.addTask(new Scheduler.RunTask(15, this::run));
	}

	private void run(){
		for(String nick : game){
			if(!gameStarted)break;
			if(!blockBreak.contains(nick))
				checkPlayer(Bukkit.getPlayer(nick));
		}
		blockBreak = new ArrayList<>();
	}

	private void checkPlayer(Player player){
		I.delay(() -> {
			if(player == null)return;
			if(player.getHealth() < 19) playerDeath(player.getName(), false);
			else player.setHealth(player.getHealth() - 18);
		}, 0);
	}

	@Override
	public void onMove(String nick, Location in, Location out) {
		if(!gameStarted)return;
		int i = getBlock(in, 0);
		if(i == -1)return;
		Block block = new Location(Bukkit.getWorlds().get(0), in.getBlockX(), i, in.getBlockZ()).getBlock();
		if(block.getType() != SectorTNT.block)return;
		if(!blockBreak.contains(nick))blockBreak.add(nick);
		I.delay(() -> breakBlock(block), 6);
	}

	private int getBlock(Location in, int i){
		if(cuboids.length <= i)return -1;
		Cuboid cuboid = cuboids[i];
		int y = in.getBlockY() - cuboid.getOne().toVec3i().y;
		if(y > 0 && y < 1.5)return cuboid.getOne().toVec3i().y;
		return getBlock(in, i + 1);
	}

	@Override
	public void onClick(String nick, Material material) {
		if(material == Spleef.teleportHub.getType())
			Sector.getSectorName("lobby").addPlayer(nick);
	}

	@Override
	public boolean canJoin(String nick) {
		return !gameEnd;
	}

	@Override
	public void onRespawn(String nick) {
		if(!gameStarted)return;
		playerDeath(nick, false);
	}

	@Override
	public void onJoin(String nick) {
		if(gameStarted){
			addSpectator(nick);
			return;
		}
		sendMessage(nick, "присоединился");
		game.add(nick);
		if(game.size() > 1) startGame();
	}

	@Override
	public void onLeave(String nick) {
		playerDeath(nick, true);
	}

	@Override
	protected void giveDefaultItems(Player player) {
		super.giveDefaultItems(player);
		Inventory inventory = player.getInventory();
		inventory.setItem(7, Spleef.teleportHub);
		player.updateInventory();
	}

	private void breakBlock(Block block){
		if(!gameStarted)return;
		block.setType(Material.AIR);
	}

	private void playerDeath(String nick, boolean leave) {
		game.remove(nick);
		sendMessage(nick, leave ? "ливнул" : "упал");
		if(!gameStarted) return;
		if(!leave) addSpectator(nick);
		if(game.size() != 1) return;
		if(gameStarted){
			sendMessage(game.get(0), "победил");
			restartGame();
		}else if(leave){
			Bukkit.getScheduler().cancelTask(startGameTask);
			Bukkit.getScheduler().cancelTask(gameTask);
			sendMessage("Недостаточно игроков");
		}
	}

	private void addSpectator(String nick){
		Bukkit.getPlayer(nick).setGameMode(GameMode.SPECTATOR);
	}

	public void startGame(){
		if(startGameTask != 0)return;
		sendMessage("§eЧерез 5 секунд, игра начнётся");
		startGameTask = I.delay(() -> {
			gameStarted = true;
			sendMessage("§eИгра началась!");
			for (String nick : game)
				Person.get(nick).sendTitle("§aИгра началась!");
		}, 100).getTaskId();
		gameTask = I.delay(() -> {
			sendMessage("§eНикто не успел выиграть...");
			restartGame();
		}, 3600).getTaskId();
	}

	private void restartGame(){
		gameEnd = true;
		gameStarted = false;
		for(String nick : getPlayers()){
			Player player = Bukkit.getPlayer(nick);
			teleport(player);
			player.setGameMode(GameMode.SURVIVAL);
		}
		setBlocks();
		game.clear();
		game.addAll(getPlayers());
		if(startGameTask != 0)I.s().cancelTask(startGameTask);
		startGameTask = 0;
		if(gameTask != 0)I.s().cancelTask(gameTask);
		gameTask = 0;
		gameEnd = false;
		if(game.size() > 1)startGame();
	}

	private void setBlocks(){
		for(Cuboid cuboid : cuboids)
			cuboid.foreach((vec) -> {
				Block block = new Location(Bukkit.getWorlds().get(0), vec.x, vec.y, vec.z).getBlock();
				if(block.getType() != Material.AIR)return;
				block.setType(SectorTNT.block);
			});
	}
}
