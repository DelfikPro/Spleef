//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package pro.delfik.spleef;

import implario.util.ArrayUtils;
import java.util.Iterator;
import java.util.Map.Entry;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pro.delfik.lmao.outward.inventory.SlotGUI;
import pro.delfik.lmao.outward.inventory.SlotGUI.Action;
import pro.delfik.spleef.sector.Sector;

public class GameSelector {
    public static final SlotGUI gui = new SlotGUI("Выбор игры", 1, GameSelector::click);
    private static final String[] ids = new String[9];

    public GameSelector() {
    }

    private static void click(Player player, int slot, ItemStack itemStack) {
        if (slot < ids.length && slot >= 0) {
            String s = ids[slot];
            int var14;
            if (s == null) {
                System.out.println("Что-то здесь блядь не так...");
                StringBuilder b = new StringBuilder();
                String[] var11 = ids;
                int var13 = var11.length;

                for(var14 = 0; var14 < var13; ++var14) {
                    String id = var11[var14];
                    b.append(id).append(" ");
                }

                System.out.println(b.toString());
            } else {
                Sector sector = Sector.getSectorName(s);
                if (sector != null) {
                    sector.addPlayer(player);
                } else {
                    System.out.println("Что-то здесь блядь не так... " + s);
                    StringBuilder b = new StringBuilder();
                    Iterator var6 = Sector.getEntries().iterator();

                    while(var6.hasNext()) {
                        Entry<String, Sector> entry = (Entry)var6.next();
                        b.append(entry.getValue() == null ? "NULL" : ((Sector)entry.getValue()).getNickname()).append(" ");
                    }

                    System.out.println(b.toString());
                    b = new StringBuilder();
                    String[] var12 = ids;
                    var14 = var12.length;

                    for(int var8 = 0; var8 < var14; ++var8) {
                        String id = var12[var8];
                        b.append(id).append(" ");
                    }

                    System.out.println(b.toString());
                }
            }
        }
    }

    public static void register(Sector sector) {
        int slot = ArrayUtils.firstEmpty(ids);
        ids[slot] = sector.getNickname();
        gui.inv().setItem(slot, sector.getItem());
    }

    public static void update(Sector sector) {
        gui.inv().setItem(sector.getID(), sector.getItem());
    }

    public static void openTo(Player player) {
        player.openInventory(gui.inv());
    }
}
