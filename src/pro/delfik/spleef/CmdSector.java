//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package pro.delfik.spleef;

import __google_.util.FileIO;
import implario.util.Converter;
import implario.util.Rank;
import java.io.File;
import org.bukkit.command.CommandSender;
import pro.delfik.lmao.command.handle.LmaoCommand;
import pro.delfik.lmao.util.Vec3i;
import pro.delfik.spleef.modification.SectorInfo;

public class CmdSector extends LmaoCommand {
	public CmdSector() {
		super("sector", Rank.ADMIN, "LolKek");
	}

	protected void run(CommandSender sender, String command, String[] args) {
		requireArgs(args, 1, "[spleef|pvp]");
		String var4 = args[0];
		byte var5 = -1;
		switch(var4.hashCode()) {
			case -895862857:
				if (var4.equals("spleef")) {
					var5 = 0;
				}
			default:
				switch(var5) {
					case 0:
						requireArgs(args, 5, "spleef [x,y,z спавна] [x,y,z зрителей] [x,y,z pos1] [x,y,z pos2]");
						Vec3i spawn = parseVec3i(args[1]);
						Vec3i spect = parseVec3i(args[2]);
						Vec3i pos1c = parseVec3i(args[3]);
						Vec3i pos2c = parseVec3i(args[4]);
						Cuboid c = new Cuboid(pos1c, pos2c);
						SectorInfo info = new SectorInfo("spleef_" + Minigame.SPLEEF.counter + 1, spawn, spect, c, Minigame.SPLEEF);
						FileIO.writeByteable(new File("sectors/" + info.getName()), info);
						sender.sendMessage("§aСектор §e" + info.getName() + "§a успешно создан и записан на диск.");
						return;
					default:
						sender.sendMessage("§cНеизвестная игра.");
				}
		}
	}

	public static Vec3i parseVec3i(String s) {
		String[] split = s.split(",");
		return new Vec3i(Converter.toInt(split[0]), Converter.toInt(split[1]), Converter.toInt(split[2]));
	}
}
