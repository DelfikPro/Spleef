package pro.delfik.spleef;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import pro.delfik.lmao.ev.EvInteract;
import pro.delfik.lmao.util.Registrar;
import pro.delfik.lmao.outward.item.ItemBuilder;
import pro.delfik.lmao.util.Vec3i;
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
		Sector.addSector("spleef_2", new SectorSpleef(
				new Vec3i(-397, 4, 100), new Vec3i(-402, 4, 120),
				new Cuboid(new Vec3i(-413, 3, 87), new Vec3i(-382, 3, 112))
		));
		Sector.addSector("spleef_1", new SectorSpleef(
				new Vec3i(-334, 4, 28), new Vec3i(-334, 11, 28),
				new Cuboid(new Vec3i(-344, 3, 22), new Vec3i(-326, 3, 34))
		));
		Sector.addSector("lobby", new SectorLobby());
		EvInteract.isStart = false;
	}

	@Override
	public void onDisable(){

	}
}
