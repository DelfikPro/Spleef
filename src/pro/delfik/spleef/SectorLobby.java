package pro.delfik.spleef;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import pro.delfik.lmao.util.Vec3i;

public class SectorLobby extends Sector{
	public SectorLobby(){
		super(new Vec3i(100, 100, 100));
	}

	@Override
	protected void onJoin(String nick) {
		setDefaultItems(Bukkit.getPlayer(nick));
	}

	@Override
	protected void onClick(String nick, Material material) {
		if(material == Spleef.SPADE)
			getSectorName("spleef_1").addPlayer(nick);
	}

	private void setDefaultItems(Player player){
		if(player == null)return;
		Inventory inventory = player.getInventory();
		inventory.clear();
		inventory.setItem(0, new ItemStack(Spleef.SPADE));
		inventory.setItem(8, new ItemStack(Material.COMPASS));
		inventory.setItem(7, new ItemStack(Material.EMERALD));
		player.updateInventory();
	}
}
