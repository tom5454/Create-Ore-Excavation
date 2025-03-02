package com.tom.createores.cc;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.ChunkPos;

import com.tom.createores.Config;
import com.tom.createores.OreDataCapability;
import com.tom.createores.OreDataCapability.OreData;
import com.tom.createores.recipe.VeinRecipe;
import com.tom.createores.util.ThreeState;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.AbstractTurtleUpgrade;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleCommand;
import dan200.computercraft.api.turtle.TurtleCommandResult;
import dan200.computercraft.api.turtle.TurtleSide;
import dan200.computercraft.api.turtle.TurtleUpgradeType;

public class OreVeinFinderTurtle extends AbstractTurtleUpgrade {

	protected OreVeinFinderTurtle(ResourceLocation id, ItemStack item) {
		super(id, TurtleUpgradeType.PERIPHERAL, item);
	}

	@Override
	public IPeripheral createPeripheral(ITurtleAccess turtle, TurtleSide side) {
		return new Peripheral(turtle);
	}

	public static class Peripheral implements IPeripheral {
		private final ITurtleAccess turtle;
		private AtomicLong time = new AtomicLong();

		public Peripheral(ITurtleAccess turtle) {
			this.turtle = turtle;
		}

		@Override
		public String getType() {
			return "coe_vein_finder";
		}

		@Override
		public boolean equals(IPeripheral other) {
			return other instanceof Peripheral;
		}

		@Override
		public Object getTarget() {
			return turtle;
		}

		@LuaFunction
		public final MethodResult search() throws LuaException {
			return turtle.executeCommand(new VeinFinderCommand(time));
		}

		@LuaFunction
		public final MethodResult getCooldown() throws LuaException {
			return turtle.executeCommand(new TurtleCommand() {

				@Override
				public TurtleCommandResult execute(ITurtleAccess turtle) {
					long t = turtle.getLevel().getGameTime();
					long dt = t - time.get();
					long rem = Math.max(0, Config.veinFinderCd - dt);
					return TurtleCommandResult.success(new Object[] {Double.valueOf(rem)});
				}
			});
		}
	}

	private static class VeinFinderCommand implements TurtleCommand {
		private AtomicLong time;

		public VeinFinderCommand(AtomicLong time) {
			this.time = time;
		}

		@Override
		public TurtleCommandResult execute(ITurtleAccess turtle) {
			long time = turtle.getLevel().getGameTime();
			if (time - this.time.get() < Config.veinFinderCd) {
				return TurtleCommandResult.failure("Ore Vein Finder on cooldown");
			}
			if (!turtle.getLevel().getBlockState(turtle.getPosition().below()).isCollisionShapeFullBlock(turtle.getLevel(), turtle.getPosition().below())) {
				return TurtleCommandResult.failure("Ore Vein Finder requires a full block below the turtle");
			}
			if (turtle.isFuelNeeded() && turtle.getFuelLevel() < 5) {
				return TurtleCommandResult.failure("Ouf of fuel");
			}
			ChunkPos center = new ChunkPos(turtle.getPosition());
			OreData d = OreDataCapability.getData(turtle.getLevel().getChunk(center.x, center.z));
			List<Object> result = new ArrayList<>();
			RecipeManager m = turtle.getLevel().getRecipeManager();
			if (d != null) {
				var rec = d.getRecipe(m);
				result.add(rec != null);
				if (rec != null) {
					result.add(rec.id.toString());
					result.add(Double.valueOf(getVeinSize(d, rec)));
				}
			} else {
				result.add(false);
			}
			if (turtle.isFuelNeeded())
				turtle.consumeFuel(5);
			this.time.set(time);

			return TurtleCommandResult.success(result.toArray());
		}

		private long getVeinSize(OreData d, VeinRecipe r) {
			if(r.isFinite() != ThreeState.NEVER) {
				if(r.isFinite() == ThreeState.DEFAULT && Config.defaultInfinite)return 0L;
				double mul = (r.getMaxAmount() - r.getMinAmount()) * d.getRandomMul() + r.getMinAmount();
				long am = Math.round(mul * Config.finiteAmountBase);
				return am;
			}
			return 0L;
		}
	}
}
