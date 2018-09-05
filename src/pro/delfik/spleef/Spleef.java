package pro.delfik.spleef;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;
import pro.delfik.lmao.anticheat.AntiClicker;
import pro.delfik.lmao.outward.gui.GUI;
import pro.delfik.lmao.outward.gui.GeneralizedGUI;

public class Spleef extends JavaPlugin{

	@Override
	public void onEnable(){
		Bukkit.getPluginManager().registerEvents(new Events(), this);
		Sector.addSector("spleef_1", new SectorSpleef(-100, -100));
		Sector.addSector("lobby", new SectorLobby());
		AntiClicker.isStart = false;
	}

	@Override
	public void onDisable(){

	}
}
