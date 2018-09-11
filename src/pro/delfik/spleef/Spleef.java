package pro.delfik.spleef;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import pro.delfik.lmao.anticheat.AntiClicker;
import pro.delfik.lmao.core.Registrar;
import pro.delfik.lmao.outward.item.ItemBuilder;
import pro.delfik.lmao.util.Vec3i;

public class Spleef extends JavaPlugin{
	public static final ItemStack teleportHub = new ItemBuilder(Material.EMERALD).withDisplayName("Вернуться в лобби").build();

	@Override
	public void onEnable(){
		Registrar reg = new Registrar(this);
		reg.regEvent(new Events());
		reg.regCommand(new CmdSpleef());
		Sector.addSector("pvp_1", new SectorPVP(-1000, -100));
		Sector.addSector("spleef_2", new SectorSpleef(-3000, -100));
		Sector.addSector("spleef_1", new SectorSpleef(-2000, -100));
		Sector.addSector("tnt_1", new SectorTNT(new Vec3i(1000, 15, -100),
				new Cuboid(new Vec3i(990, 10, -110), new Vec3i(1010, 10, -90)),
				new Cuboid(new Vec3i(990, 5, -110), new Vec3i(1010, 5, -90))));
		Sector.addSector("lobby", new SectorLobby());
		AntiClicker.isStart = false;
	}

	@Override
	public void onDisable(){

	}
}
