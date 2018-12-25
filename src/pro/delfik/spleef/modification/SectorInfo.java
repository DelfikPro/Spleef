package pro.delfik.spleef.modification;

import __google_.util.ByteUnzip;
import __google_.util.ByteZip;
import __google_.util.Byteable;
import pro.delfik.lmao.util.Vec;
import pro.delfik.lmao.util.Vec3i;
import pro.delfik.spleef.Cuboid;
import pro.delfik.spleef.Minigame;

public class SectorInfo implements Byteable {
    private String name;
    private Vec3i spawn;
    private Vec3i spectators;
    private Cuboid cuboid;
    private Minigame game;

    public SectorInfo(ByteUnzip unzip) {
        this.name = unzip.getString();
        this.spawn = Vec3i.read(unzip.getBytes());
        this.spectators = Vec3i.read(unzip.getBytes());
        this.game = Minigame.values()[unzip.getInt()];
        this.cuboid = Cuboid.read(unzip.getBytes());
    }

    public SectorInfo(String name, Vec3i spawn, Vec3i spectators, Cuboid cuboid, Minigame game) {
        this.name = name;
        this.spawn = spawn;
        this.spectators = spectators;
        this.cuboid = cuboid;
        this.game = game;
    }

    public String getName() {
        return this.name;
    }

    public Cuboid getCuboid() {
        return this.cuboid;
    }

    public Vec getSpawn() {
        return this.spawn;
    }

    public Vec getSpectators() {
        return this.spectators;
    }

    public Minigame getGame() {
        return this.game;
    }

    public ByteZip toByteZip() {
        return (new ByteZip()).add(this.name).add(byteVec(this.spawn)).add(byteVec(this.spectators)).add(this.game.ordinal()).add(this.cuboid.toByteArray());
    }

    public static byte[] byteVec(Vec3i vec3i) {
        ByteZip z = (new ByteZip()).add(vec3i.x).add(vec3i.y).add(vec3i.z);
        return z.build();
    }
}
