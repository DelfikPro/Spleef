//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package pro.delfik.spleef.sector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import pro.delfik.lmao.outward.item.Ench;
import pro.delfik.lmao.outward.item.ItemBuilder;
import pro.delfik.lmao.util.Vec3i;
import pro.delfik.spleef.Cuboid;
import pro.delfik.spleef.Minigame;
import pro.delfik.spleef.Spleef;
import pro.delfik.spleef.modification.SectorInfo;

public class SectorPVP extends Sector {
	private static final ItemStack HELM;
	private static final ItemStack CHEST;
	private static final ItemStack LEG;
	private static final ItemStack BOOTS;
	private static final ItemStack SWORD;
	private static final ItemStack GAPPLE;
	private final List<String> players = new ArrayList();
	private final Map<String, String> lastDamage = new HashMap();
	private final Map<String, Integer> points = new HashMap();

	public SectorPVP(int x, int y) {
		super(new Vec3i(x, 40, y));
	}

	public void onRespawn(String nick) {
		this.giveDefaultItems(nick);
		this.playerDeath(Bukkit.getPlayer(nick), false);
	}

	public void onDeath(Player player) {
		this.giveDefaultItems(player);
		this.playerDeath(player, true);
	}

	public void onJoin(String nick) {
		this.sendMessage(nick, "присоединился");
	}

	public void onLeave(String nick) {
		this.points.remove(nick);
		this.players.remove(nick);
		this.lastDamage.remove(nick);
		this.sendMessage(nick, "ливнул");
	}

	public boolean onDamage(String nick, DamageCause cause) {
		if (cause == DamageCause.FALL) {
			Player player = Bukkit.getPlayer(nick);
			if (player.getLocation().getY() <= 30.0D && !this.players.contains(nick)) {
				this.addGamePlayer(player);
				return true;
			} else {
				return true;
			}
		} else {
			return false;
		}
	}

	public boolean onHit(String entity, String damager) {
		if (this.players.contains(entity) && this.players.contains(damager)) {
			this.lastDamage.put(entity, damager);
			return false;
		} else {
			return true;
		}
	}

	public void onClick(String nick, Material material) {
		if (material == Spleef.teleportHub.getType()) {
			Sector.getSectorName("lobby").addPlayer(nick);
		}

	}

	public Minigame getMinigame() {
		return Minigame.PVP;
	}

	protected void giveDefaultItems(Player player) {
		super.giveDefaultItems(player);
		Inventory inventory = player.getInventory();
		inventory.setItem(7, Spleef.teleportHub);
		player.updateInventory();
	}

	private void playerDeath(Player player, boolean death) {
		String nick = player.getName();
		this.points.remove(nick);
		this.players.remove(nick);
		String killer = (String)this.lastDamage.get(nick);
		this.lastDamage.remove(nick);
		if (killer != null) {
			if (death) {
				player.sendMessage("§eИгрок §c" + nick + " §eпогиб от руки §c" + killer);
			}

			this.sendMessage(nick, "погиб от руки §c" + killer);
			Integer points = (Integer)this.points.get(killer);
			if (points == null) {
				points = 0;
			}

			points = points + 1;
			this.points.put(killer, points);
			if (points > 4) {
				this.sendMessage(killer, "убил уже §c" + points + " §eигроков!");
			}

			if (points > 4 && death) {
				player.sendMessage("§eИгрок §c" + killer + " §eубил уже §c" + points + " §eигроков!");
			}

		}
	}

	private void addGamePlayer(Player player) {
		this.players.add(player.getName());
		this.giveSet(player);
	}

	private void giveSet(Player player) {
		PlayerInventory inventory = player.getInventory();
		inventory.setBoots(BOOTS);
		inventory.setLeggings(LEG);
		inventory.setChestplate(CHEST);
		inventory.setHelmet(HELM);
		inventory.setItem(0, SWORD);
		inventory.setItem(1, GAPPLE);
		player.updateInventory();
	}

	public SectorInfo getInfo() {
		return new SectorInfo(this.getNickname(), this.spawnPoint.toVec3i(), this.spawnPoint.toVec3i(), new Cuboid(this.spawnPoint, this.spawnPoint), Minigame.PVP);
	}

	static {
		HELM = ItemBuilder.create(Material.IRON_HELMET, "§dШапка-ушанка");
		CHEST = ItemBuilder.create(Material.IRON_CHESTPLATE, "§cПуленепробиваемый жилет");
		LEG = ItemBuilder.create(Material.IRON_LEGGINGS, "§dШтаны с подворотами");
		BOOTS = ItemBuilder.create(Material.IRON_BOOTS, "§dТуфли §fДжимми Чу");
		SWORD = (new ItemBuilder(Material.STONE_SWORD)).withDisplayName("§dХирургическая игла").unbreakable().withLore(new String[]{"§e§oПронзает противников со", "§e§oшвейцарской точностью!"}).enchant(new Ench[]{new Ench(Enchantment.DAMAGE_ALL, 1)}).build();
		GAPPLE = (new ItemBuilder(Material.GOLDEN_APPLE)).withDisplayName("§dНезачарованное незолотое неяблоко").enchant(new Ench[]{new Ench(Enchantment.KNOCKBACK, 1)}).build();
	}
}
