package pro.delfik.spleef;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import pro.delfik.lmao.command.handle.LmaoCommand;
import implario.util.Rank;

public class CommandSpleef extends LmaoCommand{
	public CommandSpleef(){
		super("spleef", Rank.DEV, "");
	}

	@Override
	protected void run(CommandSender sender, String command, String[] args) {
		Events.start();
	}
}
