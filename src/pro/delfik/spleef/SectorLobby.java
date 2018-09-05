package pro.delfik.spleef;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import pro.delfik.lmao.outward.inventory.SlotGUI;
import pro.delfik.lmao.outward.item.ItemBuilder;
import pro.delfik.lmao.util.Vec3i;

public class SectorLobby extends Sector{
	private static final ItemStack changeServer = new ItemBuilder(Material.WATCH)
			.withDisplayName("§f>> §a§lИграть §f<<").build();;
	private static SlotGUI gui;

	public SectorLobby(){
		super(new Vec3i(100, 100, 100));
		if(gui != null)return;
		gui = new SlotGUI("Выбор сервера", 1, (player, slot, item) -> {
			Sector sector = getSectorName(item
					.getItemMeta()
					.getDisplayName());
			if(sector == null)return;
			sector.addPlayer(player.getName());
		}, null);
		for(String sector : getNames())
			gui.dummy(Material.DIAMOND_SPADE, sector);
	}

	@Override
	protected void onJoin(String nick) {
		giveDefaultItems(Bukkit.getPlayer(nick));
	}

	@Override
	protected void onClick(String nick, Material material) {
		if(material == changeServer.getType())
			Bukkit.getPlayer(nick).openInventory(gui.inv());
	}

	@Override
	protected void giveDefaultItems(Player player){
		if(player == null)return;
		super.giveDefaultItems(player);
		Inventory inventory = player.getInventory();
		inventory.setItem(0, changeServer);
		player.updateInventory();
	}
}
