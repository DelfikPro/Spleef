package pro.delfik.spleef;

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
import pro.delfik.lmao.core.Person;
import pro.delfik.lmao.outward.item.Ench;
import pro.delfik.lmao.outward.item.I;
import pro.delfik.lmao.outward.item.ItemBuilder;
import pro.delfik.lmao.util.Vec3i;

import java.util.ArrayList;
import java.util.List;

public class SectorSpleef extends Sector{
	private static final ItemStack spade = new ItemBuilder(Material.DIAMOND_SPADE).unbreakable().withDisplayName("§сЛАМАААААААТЬ")
			.enchant(new Ench(Enchantment.DIG_SPEED, 10)).build();
	private static final ItemStack teleportHub = new ItemBuilder(Material.EMERALD).withDisplayName("Вернуться в лобби").build();

	private volatile boolean gameStarted = false, gameEnd = false;
	private final Vec3i one, two;
	private final List<String> game = new ArrayList<>();
	private volatile List<String> blockBreak = new ArrayList<>();
	private int startGameTask = 0, gameTask = 0;

	public SectorSpleef(int x, int z){
		super(new Vec3i(x, 3, z));
		Location location = getSpawnPoint();
		one = new Vec3i(location.getBlockX() - 15, location.getBlockY() - 2, location.getBlockZ() - 15);
		two = new Vec3i(location.getBlockX() + 15, location.getBlockY() - 2, location.getBlockZ() + 15);
		setSnow();
		Scheduler.addTask(new Scheduler.RunTask(50, this::run));
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
	protected void onClick(String nick, Material material) {
		if(material == teleportHub.getType())
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
		giveDefaultItems(Bukkit.getPlayer(nick));
		if(gameStarted)addSpectator(nick);
		else {
			sendMessage("§eИгрок §c" + nick + " §eприсоединился");
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
		if(gameStarted && block.getType() == Material.SNOW_BLOCK){
			if(!blockBreak.contains(nick))blockBreak.add(nick);
			return false;
		}
		return true;
	}

	@Override
	protected void giveDefaultItems(Player player) {
		super.giveDefaultItems(player);
		Inventory inventory = player.getInventory();
		inventory.setItem(0, spade);
		inventory.setItem(7, teleportHub);
		player.updateInventory();
	}

	private void playerDeath(String nick, boolean leave) {
		if(!gameStarted)return;
		sendMessage("§eИгрок §c" + nick + " §e" + (leave ? "ливнул" : "упал"));
		game.remove(nick);
		if(!leave) addSpectator(nick);
		if(game.size() == 1){
			if(gameStarted){
				sendMessage("§eИгрок §c" + game.get(0) + " §eпобедил");
				restartGame();
			}else if(leave){
				Bukkit.getScheduler().cancelTask(startGameTask);
				Bukkit.getScheduler().cancelTask(gameTask);
				sendMessage("Недостаточно игроков");
			}
		}
	}

	private void addSpectator(String nick){
		Bukkit.getPlayer(nick).setGameMode(GameMode.SPECTATOR);
	}

	private void startGame(){
		if(startGameTask != 0)return;
		sendMessage("§eЧерез 5 секунд, игра начнётся");
		startGameTask = I.delay(() -> {
			gameStarted = true;
			sendMessage("§eИгра началась!");
			for (String nick : game){
				Person.get(nick).sendTitle("§aИгра началась!");
			}
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
		setSnow();
		blockBreak.clear();
		blockBreak.addAll(getPlayers());
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
		for(int x = one.x; x < two.x; x++)
			for(int z = one.z; z < two.z; z++)
				new Location(Bukkit.getWorlds().get(0), x, one.y, z)
						.getBlock().setType(Material.SNOW_BLOCK);
	}
}
