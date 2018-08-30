package pro.delfik.spleef;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.plugin.java.JavaPlugin;
import pro.delfik.lmao.anticheat.AntiClicker;
import implario.util.Scheduler;

import java.util.Locale;

public class Spleef extends JavaPlugin{
	@Override
	public void onEnable(){
		Bukkit.getPluginCommand("spleef").setExecutor(new CommandSpleef());
		Bukkit.getPluginManager().registerEvents(new Events(), this);
		AntiClicker.isStart = false;
		for(int z = 83; z < 117; z++)
			for(int x = 88; x < 113; x++)
				new Location(Bukkit.getWorlds().get(0), x, 100, z).getBlock().setType(Material.SNOW_BLOCK);
	}

	@Override
	public void onDisable(){
		if(Events.id != 0)Bukkit.getScheduler().cancelTask(Events.id);
	}
}
