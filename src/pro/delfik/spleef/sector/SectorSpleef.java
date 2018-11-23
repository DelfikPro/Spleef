package pro.delfik.spleef.sector;

import implario.net.packet.PacketTopUpdate;
import implario.util.ByteZip;
import implario.util.ServerType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import pro.delfik.lmao.Connect;
import pro.delfik.lmao.outward.item.Ench;
import pro.delfik.lmao.outward.item.I;
import pro.delfik.lmao.outward.item.ItemBuilder;
import pro.delfik.lmao.user.Person;
import pro.delfik.lmao.util.Vec3i;
import pro.delfik.spleef.Cuboid;
import pro.delfik.spleef.Spleef;

import java.util.ArrayList;
import java.util.List;

import static org.bukkit.Material.AIR;
import static org.bukkit.Material.SNOW_BLOCK;

public class SectorSpleef extends Sector {

	private static final ItemStack spade = new ItemBuilder(Material.DIAMOND_SPADE).unbreakable().withDisplayName("§aСмеятся надо после слова...")
			.enchant(new Ench(Enchantment.DIG_SPEED, 10)).build();

	private volatile boolean gameStarted = false, gameEnd = false;
	private Cuboid cuboid;
	private final List<String> game = new ArrayList<>();
	private BukkitTask startGameTask, gameTask, trimTask;
	private final Vec3i spectators;
	private Cuboid center;
	private Cuboid primary;

	public SectorSpleef(Vec3i spawn, Vec3i spectators, Cuboid cuboid) {
		super(spawn, spade.getType());
		this.cuboid = primary = cuboid;
		this.spectators = spectators;
		int x = (cuboid.getTwo().x - cuboid.getOne().x) / 2 + cuboid.getOne().x;
		int z = (cuboid.getTwo().z - cuboid.getOne().z) / 2 + cuboid.getOne().z;
		int y = cuboid.getOne().y;
		center = new Cuboid(new Vec3i(x - 4, y, z - 4), new Vec3i(x + 4, y, z + 4));
		setSnow();
	}

	@Override
	public void onClick(String nick, Material material) {
		if (material == Spleef.teleportHub.getType())
			Sector.getSectorName("lobby").addPlayer(nick);
	}

	@Override
	public boolean canJoin(String nick) {
		return !gameEnd;
	}

	@Override
	public void onRespawn(String nick) {
		if (!gameStarted) return;
		playerDeath(nick, false);
	}

	@Override
	public void onJoin(String nick) {
		if (gameStarted) {
			if (game.size() < 2) {
				restartGame();
				return;
			}
			addSpectator(nick);
			return;
		}
		sendMessage(nick, "присоединился");
		game.add(nick);
		if (game.size() > 1) startGame();
	}

	@Override
	public void onLeave(String nick) {
		playerDeath(nick, true);
	}

	@Override
	public boolean onBreak(String nick, Player player, Block block) {
		return !(gameStarted && block.getType() == SNOW_BLOCK);
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
		updateStatistics(nick, false);
		if (!gameStarted) return;
		if (!leave) addSpectator(nick);
		if (game.size() != 1) return;
		if (gameStarted) {
			String win = game.get(0);
			updateStatistics(win, true);
			sendMessage(win, "победил");
			restartGame();
		} else if (leave) {
			startGameTask.cancel();
			gameTask.cancel();
			trimTask.cancel();
			sendMessage("Недостаточно игроков");
		}
	}

	private void addSpectator(String nick) {
		Person.get(nick).teleport(spectators.toLocation(Bukkit.getWorlds().get(0)));
	}

	private void startGame() {
		if (startGameTask != null) return;
		sendMessage("§eЧерез 5 секунд, игра начнётся");
		startGameTask = I.delay(() -> {
			gameStarted = true;
			sendMessage("§eИгра началась!");
			for (String nick : game)
				Person.get(nick).sendTitle("§aИгра началась!");
		}, 100);
		gameTask = I.delay(() -> {
			sendMessage("§eНикто не успел выиграть...");
			restartGame();
		}, 3600);
		trimTask = I.timer(() -> {
			sendMessage("§aАрена сужается!");
			boolean xmin = cuboid.getTwo().x - cuboid.getOne().x > 9;
			boolean zmin = cuboid.getTwo().z - cuboid.getOne().z > 9;
			cuboid = new Cuboid(cuboid.getOne().add(xmin ? 1 : 0, 0, zmin ? 1 : 0), cuboid.getTwo().add(xmin ? -1 : 0, 0, zmin ? -1 : 0));
			center.fill(SNOW_BLOCK, 0);
			primary.foreach(v -> {
				if (!cuboid.belongs(v)) v.toLocation(Bukkit.getWorlds().get(0)).getBlock().setType(AIR);
			});
		}, 200);
	}

	private void restartGame() {
		gameEnd = true;
		gameStarted = false;
		for (String nick : getPlayers()) {
			Player player = Bukkit.getPlayer(nick);
			teleport(player);
		}
		cuboid = primary;
		setSnow();
		game.clear();
		game.addAll(getPlayers());
		gameEnd = false;
		if (startGameTask != null) startGameTask.cancel();
		startGameTask = null;
		if (gameTask != null) gameTask.cancel();
		gameTask = null;
		if (trimTask != null) trimTask.cancel();
		trimTask = null;
		if (game.size() > 1) startGame();
	}

	private void setSnow() {
		cuboid.fill(SNOW_BLOCK, 0);
	}

	public static void updateStatistics(String nick, boolean win) {
		Connect.send(new PacketTopUpdate(ServerType.SPLEEF, new ByteZip().add(1).add(win ? 1 : 0).build(), nick));
	}

}
