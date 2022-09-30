package com.tom.createores.block.entity;

import static net.minecraft.ChatFormatting.*;

import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import com.simibubi.create.content.contraptions.base.IRotate.SpeedLevel;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.tileEntity.SmartTileEntity;
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;
import com.simibubi.create.foundation.utility.Lang;

import com.tom.createores.CreateOreExcavation;
import com.tom.createores.OreDataCapability;
import com.tom.createores.OreDataCapability.OreData;
import com.tom.createores.client.ClientUtil;
import com.tom.createores.recipe.ExcavatingRecipe;
import com.tom.createores.recipe.IRecipe;
import com.tom.createores.util.NumberFormatter;

public abstract class ExcavatingBlockEntity<R extends ExcavatingRecipe> extends SmartTileEntity implements MultiblockCapHandler, IDrill {
	protected int progress;
	protected Kinetic kinetic;
	protected ResourceLocation recipeClient;
	protected long resourceRemClient;
	protected boolean hasRotation;
	protected ItemStack drillStack;
	protected R current;
	protected OreData data;
	protected ExcavatorState state = ExcavatorState.NO_VEIN;

	protected ExcavatingBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		drillStack = ItemStack.EMPTY;
		setLazyTickRate(20);
	}

	protected abstract boolean instanceofCheck(Object rec);

	@SuppressWarnings("deprecation")
	@Override
	public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
		R rec = recipeClient != null ? level.getRecipeManager().byKey(recipeClient).filter(this::instanceofCheck).map(r -> (R) r).orElse(null) : null;
		Component vein = rec != null ? rec.getName() : new TranslatableComponent("chat.coe.veinFinder.nothing");
		tooltip.add(new TextComponent(spacing).append(new TranslatableComponent("chat.coe.veinFinder.found", vein)));
		if(!hasRotation) {
			tooltip.add(componentSpacing.plainCopy()
					.append(Lang.translateDirect("tooltip.speedRequirement")
							.withStyle(GOLD)));
			Component hint =
					Lang.translateDirect("gui.contraptions.not_fast_enough", I18n.get(getBlockState().getBlock()
							.getDescriptionId()));
			List<Component> cutString = TooltipHelper.cutTextComponent(hint, GRAY, ChatFormatting.WHITE);
			for (int i = 0; i < cutString.size(); i++)
				tooltip.add(componentSpacing.plainCopy()
						.append(cutString.get(i)));
		}
		if(drillStack.isEmpty())
			tooltip.add(new TextComponent(spacing).append(new TranslatableComponent("info.coe.drill.noDrill")));
		else
			tooltip.add(new TextComponent(spacing).append(new TranslatableComponent("info.coe.drill.installed", drillStack.getHoverName())));

		if(rec != null) {
			tooltip.add(new TextComponent(spacing).append(new TranslatableComponent("info.coe.drill.progress")).append(": [").append(ClientUtil.makeProgressBar(progress / (float) rec.getTicks())).append("]"));
			if(resourceRemClient != 0)tooltip.add(new TextComponent(spacing).append(new TranslatableComponent("info.coe.drill.resourceRemaining", NumberFormatter.formatNumber(resourceRemClient))));
			if(!rec.getDrill().test(drillStack)) {
				tooltip.add(new TextComponent(spacing).append(new TranslatableComponent("info.coe.drill.badDrill")));
			}
			addToGoggleTooltip(tooltip, rec);
		}
		if(state != ExcavatorState.NO_ERROR) {
			tooltip.add(new TextComponent(spacing).append(new TranslatableComponent("info.coe.drill.err_" + state.name().toLowerCase())));
		}

		return true;
	}

	public void addToGoggleTooltip(List<Component> tooltip, R recipe) {}

	private R getRecipe() {
		ChunkPos p = new ChunkPos(worldPosition);
		data = OreDataCapability.getData(level.getChunk(p.x, p.z));
		RecipeManager m = level.getRecipeManager();
		if(data != null) {
			IRecipe r = data.getRecipe(m);
			if (r != null && instanceofCheck(r)) {
				return (R) r;
			}
		}
		return null;
	}

	protected abstract boolean canExtract();
	protected abstract void onFinished();

	@Override
	public void tick() {
		super.tick();
		if(!level.isClientSide) {
			if(current != null && state == ExcavatorState.NO_ERROR) {
				if(kinetic != null)kinetic.setStress(current.getStress());
				if(canExtract() && kinetic != null && kinetic.getRotationSpeed() >= SpeedLevel.MEDIUM.getSpeedValue() &&
						current.getDrill().test(drillStack)) {
					float prg = kinetic.getRotationSpeed() / SpeedLevel.MEDIUM.getSpeedValue();
					progress += prg;
					if(progress >= current.getTicks()) {
						updateState();
						if(state == ExcavatorState.NO_ERROR) {
							onFinished();
							data.extract(1);
						}
						progress = 0;
					}
				}
			} else if(progress > 10) {
				progress = 0;
				updateState();
			} else {
				progress++;
			}
		}
	}

	private void updateState() {
		current = getRecipe();
		if(current == null) {
			state = ExcavatorState.NO_VEIN;
		} else if(!data.canExtract(level, worldPosition)) {
			state = ExcavatorState.TOO_MANY_EXCAVATORS;
		} else if(data.getResourcesRemaining(current) == -1) {
			state = ExcavatorState.VEIN_EMPTY;
		} else {
			state = ExcavatorState.NO_ERROR;
		}
		notifyUpdate();
	}

	@Override
	public void lazyTick() {
		notifyUpdate();
	}

	@Override
	protected void read(CompoundTag tag, boolean clientPacket) {
		super.read(tag, clientPacket);
		progress = tag.getInt("progress");
		drillStack = ItemStack.of(tag.getCompound("drill"));
		if(clientPacket) {
			state = ExcavatorState.VALUES[tag.getByte("state")];
			if(tag.contains("veinName")) {
				recipeClient = new ResourceLocation(tag.getString("veinName"));
				resourceRemClient = tag.getLong("resRem");
			} else
				recipeClient = null;
			hasRotation = tag.getBoolean("hasRot");
		}
	}

	@Override
	protected void write(CompoundTag tag, boolean clientPacket) {
		super.write(tag, clientPacket);
		tag.putInt("progress", progress);
		tag.put("drill", drillStack.serializeNBT());
		if(clientPacket) {
			tag.putByte("state", (byte) state.ordinal());
			if(current != null) {
				tag.putString("veinName", current.getRecipeId().toString());
				tag.putLong("resRem", data.getResourcesRemaining(current));
			}
			tag.putBoolean("hasRot", kinetic != null && kinetic.getRotationSpeed() >= SpeedLevel.MEDIUM.getSpeedValue());
		}
	}

	@Override
	public void initialize() {
		super.initialize();
		if(!level.isClientSide) {
			if(progress > 0 && current == null)
				current = getRecipe();
			if(current == null)progress = 0;
		}
	}

	@Override
	public boolean isActive() {
		return recipeClient != null;
	}

	@Override
	public ItemStack getDrill() {
		return drillStack;
	}

	@Override
	public BlockPos getBelow() {
		return worldPosition.below(2);
	}

	@Override
	public void addKinetic(Kinetic k) {
		this.kinetic = k;
	}

	@Override
	public void dropInv() {
		dropItemStack(drillStack);
	}

	public InteractionResult onClick(Player player, InteractionHand hand) {
		ItemStack item = player.getItemInHand(hand);
		if(!item.isEmpty() && item.is(CreateOreExcavation.DRILL_TAG)) {
			if(drillStack.isEmpty()) {
				if(!level.isClientSide)drillStack = item.split(1);
				notifyUpdate();
				return InteractionResult.SUCCESS;
			}
		} else if(item.isEmpty()) {
			if(!drillStack.isEmpty()) {
				if(!level.isClientSide) {
					if(player.addItem(drillStack)) {
						drillStack = ItemStack.EMPTY;
						notifyUpdate();
						return InteractionResult.CONSUME;
					}
					return InteractionResult.SUCCESS;
				} else return InteractionResult.SUCCESS;
			}
		}
		return InteractionResult.PASS;
	}

	@Override
	public void addBehaviours(List<TileEntityBehaviour> behaviours) {
	}

	protected void dropItemStack(ItemStack stackInSlot) {
		Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), stackInSlot);
	}

	@Override
	protected AABB createRenderBoundingBox() {
		return new AABB(worldPosition.offset(-1, -1, -1), worldPosition.offset(1, 0, 1));
	}

	public static enum ExcavatorState {
		NO_ERROR,
		NO_VEIN,
		VEIN_EMPTY,
		TOO_MANY_EXCAVATORS,
		;
		public static final ExcavatorState[] VALUES = values();
	}
}
