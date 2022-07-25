package com.tom.createores.block;

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

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import com.simibubi.create.content.contraptions.base.IRotate.SpeedLevel;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.tileEntity.SmartTileEntity;
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;
import com.simibubi.create.foundation.utility.Lang;

import com.tom.createores.CreateOreExcavation;
import com.tom.createores.OreDataCapability;
import com.tom.createores.OreDataCapability.OreData;
import com.tom.createores.block.IOBlock.Type;
import com.tom.createores.client.ClientUtil;
import com.tom.createores.recipe.ExtractorRecipe;
import com.tom.createores.recipe.IRecipe;

public class ExtractorBlockEntity extends SmartTileEntity implements MultiblockCapHandler {
	private int progress;
	private ExtractorRecipe current;
	private Kinetic kinetic;
	private ResourceLocation recipeClient;
	private boolean hasRotation;
	private Tank fluidTank;
	private LazyOptional<FluidTank> tankCap;
	private ItemStack drillStack;

	public ExtractorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		fluidTank = new Tank();
		tankCap = LazyOptional.of(() -> fluidTank);
		drillStack = ItemStack.EMPTY;
		setLazyTickRate(20);
	}

	@Override
	public <T> LazyOptional<T> getCaps(Capability<T> cap, Type type) {
		if(type == Type.FLUID_OUT && cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
			return tankCap.cast();
		}
		return LazyOptional.empty();
	}

	@Override
	public void addBehaviours(List<TileEntityBehaviour> behaviours) {}

	@Override
	public void tick() {
		super.tick();
		if(!level.isClientSide) {
			if(current != null) {
				if(kinetic != null)kinetic.setStress(current.getStress());
				if(kinetic != null && kinetic.getRotationSpeed() >= SpeedLevel.MEDIUM.getSpeedValue() &&
						current.getDrill().test(drillStack) &&
						fluidTank.fillInternal(current.getOutput(), FluidAction.SIMULATE) == current.getOutput().getAmount()
						) {
					float prg = kinetic.getRotationSpeed() / SpeedLevel.MEDIUM.getSpeedValue();
					progress += prg;
					if(progress >= current.getTicks()) {
						fluidTank.fillInternal(current.getOutput(), FluidAction.EXECUTE);
						progress = 0;
						current = getRecipe();
						notifyUpdate();
					}
				}
			} else if(progress > 10) {
				progress = 0;
				current = getRecipe();
				notifyUpdate();
			} else {
				progress++;
			}
		}
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
		fluidTank.readFromNBT(tag.getCompound("tank"));
		if(clientPacket) {
			if(tag.contains("veinName")) {
				recipeClient = new ResourceLocation(tag.getString("veinName"));
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
		tag.put("tank", fluidTank.writeToNBT(new CompoundTag()));
		if(clientPacket) {
			if(current != null) {
				tag.putString("veinName", current.getRecipeId().toString());
			}
			tag.putBoolean("hasRot", kinetic != null && kinetic.getRotationSpeed() >= SpeedLevel.MEDIUM.getSpeedValue());
		}
	}

	private ExtractorRecipe getRecipe() {
		ChunkPos p = new ChunkPos(worldPosition);
		OreData data = OreDataCapability.getData(level.getChunk(p.x, p.z));
		RecipeManager m = level.getRecipeManager();
		if(data != null && data.getRecipe(m) != null) {
			IRecipe r = data.getRecipe(m);
			if (r instanceof ExtractorRecipe) {
				return (ExtractorRecipe) r;
			}
		}
		return null;
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
	public void addKinetic(Kinetic k) {
		this.kinetic = k;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
		ExtractorRecipe rec = recipeClient != null ? level.getRecipeManager().byKey(recipeClient).filter(r -> r instanceof ExtractorRecipe).map(r -> (ExtractorRecipe) r).orElse(null) : null;
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
		containedFluidTooltip(tooltip, isPlayerSneaking, tankCap.cast());
		if(drillStack.isEmpty())
			tooltip.add(new TextComponent(spacing).append(new TranslatableComponent("info.coe.drill.noDrill")));
		else
			tooltip.add(new TextComponent(spacing).append(new TranslatableComponent("info.coe.drill.installed", drillStack.getHoverName())));

		if(rec != null) {
			tooltip.add(new TextComponent(spacing).append(new TranslatableComponent("info.coe.drill.progress")).append(": [").append(ClientUtil.makeProgressBar(progress / (float) rec.getTicks())).append("]"));
			if(!rec.getDrill().test(drillStack)) {
				tooltip.add(new TextComponent(spacing).append(new TranslatableComponent("info.coe.drill.badDrill")));
			}
		}

		return true;
	}

	@Override
	public void setRemoved() {
		super.setRemoved();
		tankCap.invalidate();
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
	public void dropInv() {
		dropItemStack(drillStack);
	}

	private void dropItemStack(ItemStack stackInSlot) {
		Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), stackInSlot);
	}

	private class Tank extends FluidTank {

		public Tank() {
			super(16000);
		}

		@Override
		protected void onContentsChanged() {
			notifyUpdate();
		}

		@Override
		public int fill(FluidStack resource, FluidAction action) {
			return 0;
		}

		public int fillInternal(FluidStack resource, FluidAction action) {
			return super.fill(resource, action);
		}
	}
}
