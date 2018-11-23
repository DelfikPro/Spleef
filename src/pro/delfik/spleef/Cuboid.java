package pro.delfik.spleef;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import pro.delfik.lmao.Lmao;
import pro.delfik.lmao.util.Vec;
import pro.delfik.lmao.util.Vec3i;

import java.util.function.Consumer;

public class Cuboid {
	private final Vec3i one, two;

	public Cuboid(Vec one, Vec two){
		this.one = one.toVec3i();
		this.two = two.toVec3i();
	}

	public Vec3i getOne() {
		return one;
	}

	public Vec3i getTwo() {
		return two;
	}

	public void foreach(Consumer<Vec3i> consumer){
		for(int x = one.x; x <= two.x; x++)
			for(int y = one.y; y <= two.y; y++)
				for(int z = one.z; z <= two.z; z++)
					consumer.accept(new Vec3i(x, y, z));
	}

	public boolean belongs(Vec3i vec) {
		//		Bukkit.broadcastMessage("vec: " + vec.x + " | " + vec.y + " | " + vec.z);
//		Bukkit.broadcastMessage("one: " + one.x + " | " + one.y + " | " + one.z);
//		Bukkit.broadcastMessage("two: " + two.x + " | " + two.y + " | " + two.z);
//		Bukkit.broadcastMessage("Принадлежность " + Converter.representBoolean(b) + "а");
		return  one.x <= vec.x && vec.x <= two.x &&
				one.y <= vec.y && vec.y <= two.y &&
				one.z <= vec.z && vec.z <= two.z;
	}

	public int getSize() {
		return Math.abs(one.x - two.x) * Math.abs(one.y - two.y) * Math.abs(one.z - two.z);
	}

	public void fill(Material m, int data) {
		Bukkit.getScheduler().runTask(Lmao.plugin, () -> {
			foreach(vec -> {
				Block block = new Location(Bukkit.getWorlds().get(0), vec.x, vec.y, vec.z).getBlock();
				block.setType(m);
				if (data != 0) block.setData((byte) data);
			});
		});
	}

}
