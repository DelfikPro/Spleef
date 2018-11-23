package pro.delfik.spleef.sector;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import pro.delfik.lmao.outward.inventory.SlotGUI;
import pro.delfik.lmao.outward.item.ItemBuilder;
import pro.delfik.lmao.util.Vec5d;

import java.util.Map;

public class SectorLobby extends Sector {

	private static final ItemStack changeServer = new ItemBuilder(Material.WATCH).withDisplayName("§f>> §a§lИграть §f<<").build();
	private static SlotGUI gui;

	public SectorLobby() {
		super(new Vec5d(-330.5, 4.5, 63.5, 90, 0));
		if (gui != null) return;
		gui = new SlotGUI("Выбор сервера", 1, (player, slot, item) -> {
			Sector sector = getSectorName(item
					.getItemMeta()
					.getDisplayName());
			if (sector == null) return;
			sector.addPlayer(player);
		}, null);
		for (Map.Entry<String, Sector> entry : getEntries())
			if (entry.getValue().visible())
				gui.dummy(entry.getValue().getMaterial(), entry.getKey());
	}

	@Override
	public void onClick(String nick, Material material) {
		if (material == changeServer.getType())
			Bukkit.getPlayer(nick).openInventory(gui.inv());
	}

	@Override
	protected void giveDefaultItems(Player player) {
		super.giveDefaultItems(player);
		Inventory inventory = player.getInventory();
		inventory.setItem(0, changeServer);
		player.updateInventory();
	}

}
