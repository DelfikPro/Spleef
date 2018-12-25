//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package pro.delfik.spleef;

import __google_.util.FileIO;
import java.io.File;
import java.util.Iterator;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import pro.delfik.lmao.ev.EvInteract;
import pro.delfik.lmao.outward.item.ItemBuilder;
import pro.delfik.lmao.util.Registrar;
import pro.delfik.spleef.modification.SectorInfo;
import pro.delfik.spleef.sector.Sector;
import pro.delfik.spleef.sector.SectorLobby;

public class Spleef extends JavaPlugin {
	public static final ItemStack teleportHub;

	public Spleef() {
	}

	public void onEnable() {
		Registrar reg = new Registrar(this);
		reg.regEvent(new Events());
		reg.regCommand(new CmdSector());

		try {
			initSectors();
		} catch (Exception var3) {
			var3.printStackTrace();
		}

		Sector.addSector("lobby", new SectorLobby());
		EvInteract.isStart = false;
	}

	private static void initSectors() {
		File dir = new File("sectors/");
		if (!dir.exists()) {
			dir.mkdir();
		}

		File[] var1 = dir.listFiles();
		int var2 = var1.length;

		for(int var3 = 0; var3 < var2; ++var3) {
			File file = var1[var3];
			SectorInfo info = (SectorInfo)FileIO.readByteable(file, SectorInfo.class);
			if (info == null) {
				file.delete();
			} else {
				Sector.addSector(file.getName(), info.getGame().createSector(info));
			}
		}

	}

	public void onDisable() {
		Iterator var1 = ((World)Bukkit.getWorlds().get(0)).getEntities().iterator();

		while(var1.hasNext()) {
			Entity e = (Entity)var1.next();
			if (e.getType() == EntityType.ARMOR_STAND) {
				e.remove();
			}
		}

	}

	static {
		teleportHub = (new ItemBuilder(Material.EMERALD)).withDisplayName("Вернуться в лобби").build();
	}
}
