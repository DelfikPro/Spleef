//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package pro.delfik.spleef;

import __google_.util.ByteUnzip;
import __google_.util.ByteZip;
import java.util.function.Consumer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import pro.delfik.lmao.Lmao;
import pro.delfik.lmao.util.Vec;
import pro.delfik.lmao.util.Vec3i;

public class Cuboid {
	private Vec3i one;
	private Vec3i two;

	public Cuboid(Vec one, Vec two) {
		this.one = one.toVec3i();
		this.two = two.toVec3i();
	}

	public Vec3i getOne() {
		return this.one;
	}

	public Vec3i getTwo() {
		return this.two;
	}

	public void foreach(Consumer<Vec3i> consumer) {
		for(int x = this.one.x; x <= this.two.x; ++x) {
			for(int y = this.one.y; y <= this.two.y; ++y) {
				for(int z = this.one.z; z <= this.two.z; ++z) {
					consumer.accept(new Vec3i(x, y, z));
				}
			}
		}

	}

	public boolean belongs(Vec3i vec) {
		return this.one.x <= vec.x && vec.x <= this.two.x && this.one.y <= vec.y && vec.y <= this.two.y && this.one.z <= vec.z && vec.z <= this.two.z;
	}

	public int getSize() {
		return Math.abs(this.one.x - this.two.x) * Math.abs(this.one.y - this.two.y) * Math.abs(this.one.z - this.two.z);
	}

	public void fill(Material m, int data) {
		Bukkit.getScheduler().runTask(Lmao.plugin, () -> {
			this.foreach((vec) -> {
				Block block = (new Location((World)Bukkit.getWorlds().get(0), (double)vec.x, (double)vec.y, (double)vec.z)).getBlock();
				block.setType(m);
				if (data != 0) {
					block.setData((byte)data);
				}

			});
		});
	}

	public byte[] toByteArray() {
		return (new ByteZip()).add(this.one.toByteArray()).add(this.two.toByteArray()).build();
	}

	public static Cuboid read(byte[] array) {
		ByteUnzip unzip = new ByteUnzip(array);
		return new Cuboid(Vec3i.read(unzip.getBytes()), Vec3i.read(unzip.getBytes()));
	}
}
