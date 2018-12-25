//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package pro.delfik.spleef;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import pro.delfik.lmao.outward.Texteria;
import pro.delfik.lmao.outward.Texteria.Text;

public class Top {
	public static Text numbers = null;
	public static Text names = null;
	public static Text games = null;
	public static Text wins = null;

	public static void update(String[] array) {
		World w = Bukkit.getWorlds().get(0);
		String[] names = new String[array.length + 1];
		String[] wins = new String[array.length + 1];
		String[] games = new String[array.length + 1];
		Location first = new Location(w, -348.0D, 8.0D, 65.0D);
		if (numbers == null) {
			numbers = Texteria.create(first.clone(), new String[]{"§d§l#", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15"});
		}

		names[0] = "§d§lИмя";
		games[0] = "§d§lИгры";
		wins[0] = "§d§lПобеды";

		for(int i = 1; i < array.length + 1; ++i) {
			String[] split = array[i - 1].split(" ");
			boolean isNull = split[0].equals("null");
			names[i] = isNull ? "§7§o- Пусто -" : split[0];
			wins[i] = isNull ? "§7-" : "§a" + split[1];
			games[i] = isNull ? "§7-" : "§a" + split[2];
		}

		if (Top.names == null) {
			Top.names = Texteria.create(first.clone().add(0.0D, 0.0D, -1.0D), names);
		} else {
			Top.names.setLines(names);
		}

		if (Top.wins == null) {
			Top.wins = Texteria.create(first.clone().add(0.0D, 0.0D, -3.0D), games);
		} else {
			Top.wins.setLines(games);
		}

		if (Top.games == null) {
			Top.games = Texteria.create(first.clone().add(0.0D, 0.0D, -2.0D), wins);
		} else {
			Top.games.setLines(wins);
		}

	}
}
