package pro.delfik.spleef.sector;

import implario.util.Scheduler;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import pro.delfik.lmao.Lmao;
import pro.delfik.lmao.user.Person;
import pro.delfik.lmao.outward.item.Ench;
import pro.delfik.lmao.outward.item.I;
import pro.delfik.lmao.outward.item.ItemBuilder;
import pro.delfik.lmao.util.Vec3i;
import pro.delfik.spleef.Cuboid;
import pro.delfik.spleef.Spleef;

import java.util.ArrayList;
import java.util.List;

public class SectorSpleef extends Sector{
	private static final ItemStack spade = new ItemBuilder(Material.DIAMOND_SPADE).unbreakable().withDisplayName("§сЛАМАААААААТЬ")
			.enchant(new Ench(Enchantment.DIG_SPEED, 10)).build();

	private volatile boolean gameStarted = false, gameEnd = false;
	private final Cuboid cuboid;
	private final List<String> game = new ArrayList<>();
	private int startGameTask = 0, gameTask = 0;
	private final Vec3i spectators;

	public SectorSpleef(Vec3i spawn, Vec3i spectators, Cuboid cuboid){
		super(spawn, spade.getType());
		this.cuboid = cuboid;
		this.spectators = spectators;
		setSnow();
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
			if(game.size() < 2){
				restartGame();
				return;
			}
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
	public boolean onBreak(String nick, Player player, Block block) {
		return !(gameStarted && block.getType() == Material.SNOW_BLOCK);
	}

	@Override
	protected void giveDefaultItems(Player player) {
		super.giveDefaultItems(player);
		Inventory inventory = player.getInventory();
		inventory.setItem(0, spade);
		inventory.setItem(7, Spleef.teleportHub);
		player.updateInventory();
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
		Person.get(nick).teleport(spectators.toLocation(Bukkit.getWorlds().get(0)));
	}

	private void startGame(){
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
		}
		setSnow();
		game.clear();
		game.addAll(getPlayers());
		gameEnd = false;
		if(startGameTask != 0)I.s().cancelTask(startGameTask);
		startGameTask = 0;
		if(gameTask != 0)I.s().cancelTask(gameTask);
		gameTask = 0;
		if(game.size() > 1)startGame();
	}

	private void setSnow(){
		Bukkit.getScheduler().runTask(Lmao.plugin, () ->{cuboid.foreach((vec) -> {
			new Location(Bukkit.getWorlds().get(0), vec.x, vec.y, vec.z)
					.getBlock().setType(Material.SNOW_BLOCK);
		});});

	}
}
