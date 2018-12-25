//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package pro.delfik.spleef.sector;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import pro.delfik.lmao.outward.item.ItemBuilder;
import pro.delfik.lmao.util.Vec5d;
import pro.delfik.spleef.GameSelector;
import pro.delfik.spleef.Minigame;
import pro.delfik.spleef.modification.SectorInfo;

public class SectorLobby extends Sector {
	private static final ItemStack changeServer;

	public SectorLobby() {
		super(new Vec5d(-330.5D, 4.5D, 63.5D, 90.0D, 0.0D));
	}

	protected String getDisplayName() {
		return "§eЛобби";
	}

	public void onClick(String nick, Material material) {
		if (material == changeServer.getType()) {
			GameSelector.openTo(Bukkit.getPlayer(nick));
		}

	}

	protected void giveDefaultItems(Player player) {
		super.giveDefaultItems(player);
		Inventory inventory = player.getInventory();
		inventory.setItem(0, changeServer);
		player.updateInventory();
	}

	public Minigame getMinigame() {
		return null;
	}

	public SectorInfo getInfo() {
		return null;
	}

	static {
		changeServer = (new ItemBuilder(Material.WATCH)).withDisplayName("§f>> §a§lИграть §f<<").build();
	}
}
