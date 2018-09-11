package pro.delfik.spleef;

import implario.util.Rank;
import org.bukkit.command.CommandSender;
import pro.delfik.lmao.command.handle.LmaoCommand;

public class CmdSpleef extends LmaoCommand{
	public CmdSpleef() {
		super("spleef", Rank.DEV, "LolKek");
	}

	@Override
	protected void run(CommandSender sender, String command, String[] args) {
		((SectorTNT)Sector.getSector(sender.getName())).startGame();
	}
}
