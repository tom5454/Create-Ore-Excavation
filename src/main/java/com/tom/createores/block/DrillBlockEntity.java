package com.tom.createores.block;

import static net.minecraft.ChatFormatting.*;

import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
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
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.CapabilityItemHandler;

import com.simibubi.create.content.contraptions.base.IRotate.SpeedLevel;
import com.simibubi.create.content.contraptions.processing.ProcessingOutput;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.tileEntity.SmartTileEntity;
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;
import com.simibubi.create.foundation.utility.Lang;

import com.tom.createores.CreateOreExcavation;
import com.tom.createores.OreDataCapability;
import com.tom.createores.OreDataCapability.OreData;
import com.tom.createores.block.IOBlock.Type;
import com.tom.createores.client.ClientUtil;
import com.tom.createores.recipe.DrillingRecipe;
import com.tom.createores.recipe.IRecipe;
import com.tom.createores.util.QueueInventory;

public class DrillBlockEntity extends SmartTileEntity implements MultiblockCapHandler {
	private int progress;
	private DrillingRecipe current;
	private Kinetic kinetic;
	private ResourceLocation recipeClient;
	private boolean hasRotation;
	private QueueInventory inventory;
	private FluidTank fluidTank;
	private LazyOptional<FluidTank> tankCap;
	private ItemStack drillStack;

	public DrillBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		inventory = new QueueInventory();
		fluidTank = new FluidTank(16000, v -> current != null && current.getDrillingFluid().test(v)) {

			@Override
			protected void onContentsChanged() {
				notifyUpdate();
			}
		};
		tankCap = LazyOptional.of(() -> fluidTank);
		drillStack = ItemStack.EMPTY;
		setLazyTickRate(20);
	}

	@Override
	public <T> LazyOptional<T> getCaps(Capability<T> cap, Type type) {
		if(type == Type.ITEM_OUT && cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return inventory.asCap();
		}

		if(type == Type.FLUID_IN && cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
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
				if(inventory.hasSpace() && kinetic != null && kinetic.getRotationSpeed() >= SpeedLevel.MEDIUM.getSpeedValue() &&
						current.getDrill().test(drillStack) && (current.getDrillingFluid().getRequiredAmount() == 0 ||
						(current.getDrillingFluid().test(fluidTank.getFluid()) &&
								fluidTank.getFluidAmount() >= current.getDrillingFluid().getRequiredAmount()))
						) {
					float prg = kinetic.getRotationSpeed() / SpeedLevel.MEDIUM.getSpeedValue();
					progress += prg;
					if(progress >= current.getTicks()) {
						current.getOutput().stream().map(ProcessingOutput::rollOutput).filter(i -> !i.isEmpty()).forEach(inventory::add);
						fluidTank.drain(current.getDrillingFluid().getRequiredAmount(), FluidAction.EXECUTE);
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
		} else {
			inventory.load(tag.getList("inv", Tag.TAG_COMPOUND));
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
		if(!clientPacket) {
			tag.put("inv", inventory.toTag());
		}
	}

	private DrillingRecipe getRecipe() {
		ChunkPos p = new ChunkPos(worldPosition);
		OreData data = OreDataCapability.getData(level.getChunk(p.x, p.z));
		RecipeManager m = level.getRecipeManager();
		if(data != null && data.getRecipe(m) != null) {
			IRecipe r = data.getRecipe(m);
			if (r instanceof DrillingRecipe) {
				return (DrillingRecipe) r;
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
		DrillingRecipe rec = recipeClient != null ? level.getRecipeManager().byKey(recipeClient).filter(r -> r instanceof DrillingRecipe).map(r -> (DrillingRecipe) r).orElse(null) : null;
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
			if(rec.getDrillingFluid().getRequiredAmount() != 0 && rec.getDrillingFluid().test(fluidTank.getFluid()) && fluidTank.getFluidAmount() >= rec.getDrillingFluid().getRequiredAmount()) {
				tooltip.add(new TextComponent(spacing).append(new TranslatableComponent("info.coe.drill.noFluid")));
			}
		}

		return true;
	}

	@Override
	public void setRemoved() {
		super.setRemoved();
		inventory.invalidate();
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
		for(int i = 0; i < inventory.getSlots(); ++i) {
			dropItemStack(inventory.getStackInSlot(i));
		}
	}

	private void dropItemStack(ItemStack stackInSlot) {
		Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), stackInSlot);
	}
}
