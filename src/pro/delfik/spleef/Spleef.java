package pro.delfik.spleef;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import pro.delfik.lmao.anticheat.AntiClicker;
import pro.delfik.lmao.outward.gui.GUI;
import pro.delfik.lmao.outward.gui.GeneralizedGUI;
import pro.delfik.lmao.outward.item.ItemBuilder;

public class Spleef extends JavaPlugin{
	public static final ItemStack teleportHub = new ItemBuilder(Material.EMERALD).withDisplayName("Вернуться в лобби").build();

	@Override
	public void onEnable(){
		Bukkit.getPluginManager().registerEvents(new Events(), this);
		Sector.addSector("pvp_1", new SectorPVP(-1000, -100));
		Sector.addSector("spleef_2", new SectorSpleef(-3000, -100));
		Sector.addSector("spleef_1", new SectorSpleef(-2000, -100));
		Sector.addSector("lobby", new SectorLobby());
		AntiClicker.isStart = false;
	}

	@Override
	public void onDisable(){

	}
}
