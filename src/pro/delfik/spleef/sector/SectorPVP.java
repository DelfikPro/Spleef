package pro.delfik.spleef.sector;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import pro.delfik.lmao.outward.item.Ench;
import pro.delfik.lmao.outward.item.ItemBuilder;
import pro.delfik.lmao.util.Vec3i;
import pro.delfik.spleef.Spleef;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SectorPVP extends Sector{
	private static final ItemStack HELM = ItemBuilder.create(Material.IRON_HELMET, "§dШапка-ушанка");
	private static final ItemStack CHEST = ItemBuilder.create(Material.IRON_CHESTPLATE, "§cПуленепробиваемый жилет");
	private static final ItemStack LEG = ItemBuilder.create(Material.IRON_LEGGINGS, "§dШтаны с подворотами");
	private static final ItemStack BOOTS = ItemBuilder.create(Material.IRON_BOOTS, "§dТуфли §fДжимми Чу");
	private static final ItemStack SWORD = new ItemBuilder(Material.STONE_SWORD).withDisplayName("§dХирургическая игла").unbreakable()
			.withLore("§e§oПронзает противников со", "§e§oшвейцарской точностью!").enchant(new Ench(Enchantment.DAMAGE_ALL, 1)).build();
	private static final ItemStack GAPPLE = new ItemBuilder(Material.GOLDEN_APPLE).withDisplayName("§dНезачарованное незолотое неяблоко")
			.enchant(new Ench(Enchantment.KNOCKBACK, 1)).build();

	public SectorPVP(int x, int y) {
		super(new Vec3i(x, 40, y), SWORD.getType());
	}

	private final List<String> players = new ArrayList<>();
	private final Map<String, String> lastDamage = new HashMap<>();
	private final Map<String, Integer> points = new HashMap<>();

	@Override
	public void onRespawn(String nick) {
		giveDefaultItems(nick);
		playerDeath(Bukkit.getPlayer(nick), false);
	}

	@Override
	public void onDeath(Player player) {
		giveDefaultItems(player);
		playerDeath(player, true);
	}

	@Override
	public void onJoin(String nick) {
		sendMessage(nick, "присоединился");
	}

	@Override
	public void onLeave(String nick) {
		points.remove(nick);
		players.remove(nick);
		lastDamage.remove(nick);
		sendMessage(nick, "ливнул");
	}

	@Override
	public boolean onDamage(String nick, EntityDamageEvent.DamageCause cause) {
		if(cause == EntityDamageEvent.DamageCause.FALL){
			Player player = Bukkit.getPlayer(nick);
			if(player.getLocation().getY() > 30 || players.contains(nick))return true;
			addGamePlayer(player);
			return true;
		}
		return false;
	}

	@Override
	public boolean onHit(String entity, String damager) {
		if(players.contains(entity) && players.contains(damager)){
			lastDamage.put(entity, damager);
			return false;
		}
		return true;
	}

	@Override
	public void onClick(String nick, Material material) {
		if(material == Spleef.teleportHub.getType())
			Sector.getSectorName("lobby").addPlayer(nick);
	}

	@Override
	protected void giveDefaultItems(Player player) {
		super.giveDefaultItems(player);
		Inventory inventory = player.getInventory();
		inventory.setItem(7, Spleef.teleportHub);
		player.updateInventory();
	}

	private void playerDeath(Player player, boolean death){
		String nick = player.getName();
		points.remove(nick);
		players.remove(nick);
		String killer = lastDamage.get(nick);
		lastDamage.remove(nick);
		if(killer == null)return;
		if(death) player.sendMessage("§eИгрок §c" + nick + " §eпогиб от руки §c" + killer);
		sendMessage(nick, "погиб от руки §c" + killer);
		Integer points = this.points.get(killer);
		if(points == null)points = 0;
		points++;
		this.points.put(killer, points);
		if(points > 4)sendMessage(killer, "убил уже §c" + points + " §eигроков!");
		if(points > 4 && death) player.sendMessage("§eИгрок §c" + killer + " §eубил уже §c" + points + " §eигроков!");
	}

	private void addGamePlayer(Player player){
		players.add(player.getName());
		giveSet(player);
	}

	private void giveSet(Player player){
		PlayerInventory inventory = player.getInventory();
		inventory.setBoots(BOOTS);
		inventory.setLeggings(LEG);
		inventory.setChestplate(CHEST);
		inventory.setHelmet(HELM);
		inventory.setItem(0, SWORD);
		inventory.setItem(1, GAPPLE);
		player.updateInventory();
	}
}
