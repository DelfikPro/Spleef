//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package pro.delfik.spleef.sector;

import implario.net.packet.PacketTopUpdate;
import implario.util.ByteZip;
import implario.util.ServerType;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
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
import pro.delfik.spleef.GameSelector;
import pro.delfik.spleef.Minigame;
import pro.delfik.spleef.Spleef;
import pro.delfik.spleef.modification.SectorInfo;

public class SectorSpleef extends Sector {
	private static final ItemStack spade;
	private volatile boolean gameStarted = false;
	private volatile boolean gameEnd = false;
	private Cuboid cuboid;
	private final List<String> game = new ArrayList();
	private BukkitTask startGameTask;
	private BukkitTask gameTask;
	private BukkitTask trimTask;
	private final Vec3i spectators;
	private Cuboid center;
	private Cuboid primary;
	private long startTime;

	public SectorSpleef(Vec3i spawn, Vec3i spectators, Cuboid cuboid) {
		super(spawn);
		this.cuboid = this.primary = cuboid;
		this.spectators = spectators;
		int x = (cuboid.getTwo().x - cuboid.getOne().x) / 2 + cuboid.getOne().x;
		int z = (cuboid.getTwo().z - cuboid.getOne().z) / 2 + cuboid.getOne().z;
		int y = cuboid.getOne().y;
		this.center = new Cuboid(new Vec3i(x - 4, y, z - 4), new Vec3i(x + 4, y, z + 4));
		this.setSnow();
	}

	public void onClick(String nick, Material material) {
		if (material == Spleef.teleportHub.getType()) {
			Sector.getSectorName("lobby").addPlayer(nick);
		}

	}

	public boolean canJoin(String nick) {
		return !this.gameEnd;
	}

	public Minigame getMinigame() {
		return Minigame.SPLEEF;
	}

	public void onRespawn(String nick) {
		if (this.gameStarted) {
			this.playerDeath(nick, false);
		}
	}

	public void onJoin(String nick) {
		if (this.gameStarted) {
			if (this.game.size() < 2) {
				this.restartGame();
			} else {
				this.addSpectator(nick);
			}
		} else {
			this.sendMessage(nick, "присоединился");
			this.game.add(nick);
			GameSelector.update(this);
			if (this.game.size() > 1) {
				this.startGame();
			}

		}
	}

	public void onLeave(String nick) {
		this.playerDeath(nick, true);
	}

	public boolean onBreak(String nick, Player player, Block block) {
		return !this.gameStarted || block.getType() != Material.SNOW_BLOCK;
	}

	public void clearPlayer(String nick) {
		super.clearPlayer(nick);
		if (this.getPlayers().size() < 2) {
			this.stopGame();
		}

	}

	protected void giveDefaultItems(Player player) {
		super.giveDefaultItems(player);
		Inventory inventory = player.getInventory();
		inventory.setItem(0, spade);
		inventory.setItem(7, Spleef.teleportHub);
		player.updateInventory();
	}

	private void playerDeath(String nick, boolean leave) {
		this.game.remove(nick);
		GameSelector.update(this);
		this.sendMessage(nick, leave ? "ливнул" : "упал");
		updateStatistics(nick, false);
		if (this.gameStarted) {
			if (!leave) {
				this.addSpectator(nick);
			}

			if (this.game.size() == 1) {
				if (this.gameStarted) {
					if (System.currentTimeMillis() - this.startTime < 20000L) {
						this.sendMessage("§cИгра слишком короткая и не будет засчитана.");
					} else {
						String win = (String)this.game.get(0);
						updateStatistics(win, true);
						this.sendMessage(win, "победил");
					}

					this.restartGame();
				} else if (leave) {
					this.startGameTask.cancel();
					this.gameTask.cancel();
					this.trimTask.cancel();
					this.sendMessage("Недостаточно игроков");
				}

			}
		}
	}

	private void addSpectator(String nick) {
		Person.get(nick).teleport(this.spectators.toLocation((World)Bukkit.getWorlds().get(0)));
	}

	private void startGame() {
		if (this.startGameTask == null) {
			this.sendMessage("§eЧерез 5 секунд, игра начнётся");
			this.startGameTask = I.delay(() -> {
				this.gameStarted = true;
				this.sendMessage("§eИгра началась!");
				this.startTime = System.currentTimeMillis();
				Iterator var1 = this.game.iterator();

				while(var1.hasNext()) {
					String nick = (String)var1.next();
					Person.get(nick).sendTitle("§aИгра началась!");
				}

			}, 100);
			this.gameTask = I.delay(() -> {
				this.sendMessage("§eНикто не успел выиграть...");
				this.restartGame();
			}, 3600);
			this.trimTask = I.timer(() -> {
				this.sendMessage("§aАрена сужается!");
				boolean xmin = this.cuboid.getTwo().x - this.cuboid.getOne().x > 9;
				boolean zmin = this.cuboid.getTwo().z - this.cuboid.getOne().z > 9;
				this.cuboid = new Cuboid(this.cuboid.getOne().add(xmin ? 1 : 0, 0, zmin ? 1 : 0), this.cuboid.getTwo().add(xmin ? -1 : 0, 0, zmin ? -1 : 0));
				this.center.fill(Material.SNOW_BLOCK, 0);
				this.primary.foreach((v) -> {
					if (!this.cuboid.belongs(v)) {
						v.toLocation((World)Bukkit.getWorlds().get(0)).getBlock().setType(Material.AIR);
					}

				});
			}, 200);
		}
	}

	private void stopGame() {
		if (this.startGameTask != null) {
			this.startGameTask.cancel();
		}

		if (this.trimTask != null) {
			this.trimTask.cancel();
		}

		if (this.gameTask != null) {
			this.gameTask.cancel();
		}

		this.startTime = 0L;
		this.startGameTask = null;
		this.gameTask = null;
		this.trimTask = null;
		this.sendMessage("§cИгра остановлена.");
	}

	private void restartGame() {
		this.gameEnd = true;
		this.gameStarted = false;
		Iterator var1 = this.getPlayers().iterator();

		while(var1.hasNext()) {
			String nick = (String)var1.next();
			Player player = Bukkit.getPlayer(nick);
			this.teleport(player);
		}

		this.cuboid = this.primary;
		this.setSnow();
		this.game.clear();
		this.game.addAll(this.getPlayers());
		this.gameEnd = false;
		if (this.startGameTask != null) {
			this.startGameTask.cancel();
		}

		this.startGameTask = null;
		if (this.gameTask != null) {
			this.gameTask.cancel();
		}

		this.gameTask = null;
		if (this.trimTask != null) {
			this.trimTask.cancel();
		}

		this.trimTask = null;
		if (this.game.size() > 1) {
			this.startGame();
		}

	}

	private void setSnow() {
		this.cuboid.fill(Material.SNOW_BLOCK, 0);
	}

	public static void updateStatistics(String nick, boolean win) {
		Connect.send(new PacketTopUpdate(ServerType.SPLEEF, (new ByteZip()).add(1).add(win ? 1 : 0).build(), nick));
	}

	public SectorInfo getInfo() {
		return new SectorInfo(this.getNickname(), this.spawnPoint.toVec3i(), this.spectators.toVec3i(), this.cuboid, this.getMinigame());
	}

	static {
		spade = (new ItemBuilder(Material.DIAMOND_SPADE)).unbreakable().withDisplayName("§aСмеятся надо после слова...").enchant(new Ench[]{new Ench(Enchantment.DIG_SPEED, 10)}).build();
	}
}
