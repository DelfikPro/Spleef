//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package pro.delfik.spleef;

import org.bukkit.Material;
import pro.delfik.spleef.modification.SectorInfo;
import pro.delfik.spleef.sector.Sector;
import pro.delfik.spleef.sector.SectorSpleef;

public enum Minigame {
    SPLEEF("spleef", "Сплиф-арена", Material.DIAMOND_SPADE, (info) -> {
        return new SectorSpleef(info.getSpawn().toVec3i(), info.getSpectators().toVec3i(), info.getCuboid());
    }),
    PVP("pvp", "PvP-арена", Material.IRON_SWORD, (info) -> {
        return null;
    });

    private final Material material;
    private final String title;
    private final Minigame.Unwrapper unwrapper;
    private final String code;
    public volatile int counter = 0;

    private Minigame(String code, String title, Material material, Minigame.Unwrapper unwrapper) {
        this.code = code;
        this.title = title;
        this.material = material;
        this.unwrapper = unwrapper;
    }

    public String getTitle() {
        return this.title;
    }

    public Material getMaterial() {
        return this.material;
    }

    public Sector createSector(SectorInfo info) {
        return this.unwrapper.unwrap(info);
    }

    private interface Unwrapper {
        Sector unwrap(SectorInfo var1);
    }
}
