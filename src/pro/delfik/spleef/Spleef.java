package pro.delfik.spleef;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import pro.delfik.lmao.ev.EvInteract;
import pro.delfik.lmao.util.Registrar;
import pro.delfik.lmao.outward.item.ItemBuilder;
import pro.delfik.spleef.sector.Sector;
import pro.delfik.spleef.sector.SectorLobby;
import pro.delfik.spleef.sector.SectorPVP;
import pro.delfik.spleef.sector.SectorSpleef;

public class Spleef extends JavaPlugin{
	public static final ItemStack teleportHub = new ItemBuilder(Material.EMERALD).withDisplayName("Вернуться в лобби").build();

	@Override
	public void onEnable(){
		Registrar reg = new Registrar(this);
		reg.regEvent(new Events());
		reg.regCommand(new CmdSector());
		Sector.addSector("pvp_1", new SectorPVP(-1000, -100));
		Sector.addSector("spleef_2", new SectorSpleef(-3000, -100));
		Sector.addSector("spleef_1", new SectorSpleef(-2000, -100));
		Sector.addSector("lobby", new SectorLobby());
		EvInteract.isStart = false;
	}

	@Override
	public void onDisable(){

	}
}
