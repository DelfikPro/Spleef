package pro.delfik.spleef;

import pro.delfik.lmao.util.Vec;
import pro.delfik.lmao.util.Vec3i;

import java.util.function.Consumer;

public class Cuboid {
	private final Vec3i one, two;

	public Cuboid(Vec one, Vec two){
		this.one = one.toVec3i();
		this.two = two.toVec3i();
	}

	public Vec getOne() {
		return one;
	}

	public Vec getTwo() {
		return two;
	}

	public void foreach(Consumer<Vec3i> consumer){
		for(int x = one.x; x <= two.x; x++)
			for(int y = one.y; y <= two.y; y++)
				for(int z = one.z; z <= two.z; z++)
					consumer.accept(new Vec3i(x, y, z));
	}
}
