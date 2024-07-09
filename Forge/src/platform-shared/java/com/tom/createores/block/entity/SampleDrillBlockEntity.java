package com.tom.createores.block.entity;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import com.simibubi.create.AllEnchantments;
import com.simibubi.create.content.equipment.armor.BacktankBlockEntity;
import com.simibubi.create.content.equipment.armor.BacktankUtil;
import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.equipment.goggles.IHaveHoveringInformation;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import com.tom.createores.OreDataCapability;
import com.tom.createores.OreDataCapability.OreData;
import com.tom.createores.Registration;
import com.tom.createores.client.ClientUtil;
import com.tom.createores.recipe.VeinRecipe;
import com.tom.createores.util.DimChunkPos;
import com.tom.createores.util.NumberFormatter;

public class SampleDrillBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation, IHaveHoveringInformation, IDrill {
	public static final int DRILL_TIME = 200;
	private ResourceLocation veinClient;
	private long resourceRemClient;
	private VeinRecipe vein;
	private OreData data;
	private int progress = 0;
	private boolean drilling = false;
	private float airTankLevel = -1f;

	public SampleDrillBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
		super(typeIn, pos, state);
		setLazyTickRate(20);
	}

	@Override
	public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
		VeinRecipe veinR = veinClient != null ? level.getRecipeManager().byKey(veinClient).filter(e -> e instanceof VeinRecipe).map(r -> (VeinRecipe) r).orElse(null) : null;
		Component vein = veinR != null ? veinR.getName() : Component.translatable("chat.coe.veinFinder.nothing");
		tooltip.add(Component.literal(spacing).append(Component.translatable("chat.coe.veinFinder.found", vein)));

		if(!level.getBlockState(worldPosition.below()).isCollisionShapeFullBlock(level, worldPosition.below())) {
			tooltip.add(Component.literal(spacing).append(Component.translatable("info.coe.drill.noGround")));
		}

		if (drilling) {
			if (progress < DRILL_TIME)
				tooltip.add(Component.literal(spacing).append(Component.translatable("info.coe.drill.progress")).append(": [").append(ClientUtil.makeProgressBar(progress / (float) DRILL_TIME)).append("]"));
			else
				tooltip.add(Component.literal(spacing).append(Component.translatable("info.coe.sample_drill.done")));
		} else if(airTankLevel > 0.2f) {
			tooltip.add(Component.literal(spacing).append(Component.translatable("info.coe.sample_drill.click_to_start")));
		}
		if(resourceRemClient != 0)tooltip.add(Component.literal(spacing).append(Component.translatable("info.coe.drill.resourceRemaining", NumberFormatter.formatNumber(resourceRemClient))));
		if (airTankLevel < 0)
			tooltip.add(Component.literal(spacing).append(Component.translatable("info.coe.sample_drill.no_air")));
		else if (airTankLevel < 0.21f)
			tooltip.add(Component.literal(spacing).append(Component.translatable("info.coe.sample_drill.low_air")));
		else
			tooltip.add(Component.literal(spacing).append(Component.translatable("info.coe.sample_drill.air")).append(": [").append(ClientUtil.makeProgressBar(airTankLevel)).append("]"));
		return true;
	}

	private void updateVein() {
		ChunkPos p = new ChunkPos(worldPosition);
		data = OreDataCapability.getData(level.getChunk(p.x, p.z));
		RecipeManager m = level.getRecipeManager();
		if(data != null) {
			vein = data.getRecipe(m);
		}
	}

	@Override
	public void tick() {
		super.tick();
		if (!level.isClientSide && drilling && progress < DRILL_TIME &&
				level.getBlockState(worldPosition.below()).isCollisionShapeFullBlock(level, worldPosition.below())) {
			BlockEntity be = level.getBlockEntity(worldPosition.above());
			if (be instanceof BacktankBlockEntity t) {
				int air = t.getAirLevel();
				int level = getTagEnchantmentLevel(AllEnchantments.CAPACITY.get(), t.getEnchantmentTag());
				int max = BacktankUtil.maxAir(level);
				if (air > max / 5) {
					int op = Mth.clamp(air / 80, 1, 4);
					t.setAirLevel(air - op);
					progress += op;
					notifyUpdate();
				}
			}
		}
	}

	@Override
	public void lazyTick() {
		if (level.isClientSide)return;
		updateVein();
		BlockEntity be = level.getBlockEntity(worldPosition.above());
		if (be instanceof BacktankBlockEntity t) {
			int level = getTagEnchantmentLevel(AllEnchantments.CAPACITY.get(), t.getEnchantmentTag());
			int max = BacktankUtil.maxAir(level);
			airTankLevel = t.getAirLevel() / (float) max;
		} else {
			airTankLevel = -1f;
		}
		notifyUpdate();
	}

	private static int getTagEnchantmentLevel(Enchantment p_44844_, ListTag listtag) {
		if (listtag == null || listtag.isEmpty())return 0;
		ResourceLocation resourcelocation = EnchantmentHelper.getEnchantmentId(p_44844_);

		for(int i = 0; i < listtag.size(); ++i) {
			CompoundTag compoundtag = listtag.getCompound(i);
			ResourceLocation resourcelocation1 = EnchantmentHelper.getEnchantmentId(compoundtag);
			if (resourcelocation1 != null && resourcelocation1.equals(resourcelocation)) {
				return EnchantmentHelper.getEnchantmentLevel(compoundtag);
			}
		}

		return 0;
	}

	@Override
	protected void write(CompoundTag compound, boolean clientPacket) {
		super.write(compound, clientPacket);
		compound.putInt("progress", progress);
		compound.putBoolean("drilling", drilling);
		if(clientPacket) {
			if(vein != null) {
				compound.putString("veinId", vein.getId().toString());
				compound.putLong("resRem", data.getResourcesRemaining(vein));
			}
			compound.putFloat("air", airTankLevel);
		}
	}

	@Override
	protected void read(CompoundTag compound, boolean clientPacket) {
		super.read(compound, clientPacket);
		progress = compound.getInt("progress");
		drilling = compound.getBoolean("drilling");
		if(clientPacket) {
			if(compound.contains("veinId")) {
				veinClient = new ResourceLocation(compound.getString("veinId"));
				resourceRemClient = compound.getLong("resRem");
			} else
				veinClient = null;
			airTankLevel = compound.getFloat("air");
		}
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
	}

	public void clicked(Player player) {
		if (progress >= DRILL_TIME) {
			if (vein == null) {
				player.displayClientMessage(Component.translatable("chat.coe.veinFinder.found", Component.translatable("chat.coe.veinFinder.nothing")), true);
			} else {
				ItemStack is = player.getMainHandItem();
				if (is.getItem() == Registration.VEIN_ATLAS_ITEM.get()) {
					Registration.VEIN_ATLAS_ITEM.get().addVein(player, is, vein, new DimChunkPos(level, worldPosition), data.getRandomMul());
					return;
				}
				player.displayClientMessage(Component.translatable("chat.coe.sampleDrill.noAtlas"), true);
			}
		} else if(!drilling) {
			if(airTankLevel > 0.2f) {
				progress = 0;
				drilling = true;
				player.displayClientMessage(Component.translatable("chat.coe.sampleDrill.start"), true);
			} else if(airTankLevel < 0) {
				player.displayClientMessage(Component.translatable("chat.coe.sampleDrill.noAir"), true);
			} else {
				player.displayClientMessage(Component.translatable("chat.coe.sampleDrill.lowAir"), true);
			}
		} else {
			player.displayClientMessage(Component.translatable("chat.coe.sampleDrill.running"), true);
		}
	}

	@Override
	public ItemStack getDrill() {
		return Registration.NORMAL_DRILL_ITEM.asStack();
	}

	@Override
	public BlockPos getBelow() {
		return worldPosition.below();
	}

	@Override
	public Direction getFacing() {
		return Direction.SOUTH;
	}

	@Override
	public boolean shouldRenderRubble() {
		return progress > 0;
	}

	@Override
	public float getYOffset() {
		return 1.7f;
	}

	@Override
	public float getDrillOffset() {
		return progress >= DRILL_TIME ? 0f : (progress / (float) DRILL_TIME) * 0.5f;
	}

	@Override
	public float getRotation() {
		if (progress >= DRILL_TIME)return 0f;
		if (progress > 0 && airTankLevel > 0.2f) {
			long ticks = getLevel().getGameTime();
			float rot = (ticks * 20) % 360;
			return rot;
		}
		return 0f;
	}

	@Override
	public float getPrevRotation() {
		if (progress >= DRILL_TIME)return 0f;
		if (progress > 0 && airTankLevel > 0.2f) {
			long ticks = getLevel().getGameTime() - 1;
			float rot = (ticks * 20) % 360;
			return rot;
		}
		return 0f;
	}

	@Override
	public boolean shouldRenderShaft() {
		return (progress / (float) DRILL_TIME) > 0.5f && progress < DRILL_TIME;
	}

	public void setDrilling(boolean drilling) {
		this.drilling = drilling;
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}

	public void setAirTankLevel(float airTankLevel) {
		this.airTankLevel = airTankLevel;
	}
}
