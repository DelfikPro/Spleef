package pro.delfik.spleef;

import implario.util.Rank;
import org.bukkit.command.CommandSender;
import pro.delfik.lmao.command.handle.LmaoCommand;
import pro.delfik.spleef.sector.SectorSpleef;

public class CmdSector extends LmaoCommand{
	public CmdSector() {
		super("sector", Rank.DEV, "LolKek");
	}

	@Override
	protected void run(CommandSender sender, String command, String[] args) {
		SectorSpleef.updateStatistics(sender.getName(), args.length == 1);
	}
}
