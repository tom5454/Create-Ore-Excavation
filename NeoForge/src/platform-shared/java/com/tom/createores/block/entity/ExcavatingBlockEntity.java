package com.tom.createores.block.entity;

import static net.minecraft.ChatFormatting.GOLD;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;

import java.util.Comparator;
import java.util.List;

import net.createmod.catnip.lang.FontHelper.Palette;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import com.simibubi.create.content.kinetics.base.IRotate.SpeedLevel;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.utility.CreateLang;

import com.tom.createores.CreateOreExcavation;
import com.tom.createores.OreData;
import com.tom.createores.OreDataAttachment;
import com.tom.createores.Registration;
import com.tom.createores.client.ClientUtil;
import com.tom.createores.recipe.ExcavatingRecipe;
import com.tom.createores.recipe.VeinRecipe;
import com.tom.createores.util.DimChunkPos;
import com.tom.createores.util.NumberFormatter;
import com.tom.createores.util.TooltipUtil;

public abstract class ExcavatingBlockEntity<R extends ExcavatingRecipe> extends SmartBlockEntity implements MultiblockCapHandler, IDrill {
	protected int progress;
	protected Kinetic kinetic;
	protected ResourceLocation veinClient, recipeClient;
	protected long resourceRemClient;
	protected boolean hasRotation;
	protected ItemStack drillStack;
	protected RecipeHolder<R> current;
	protected RecipeHolder<VeinRecipe> vein;
	protected OreData data;
	protected ExcavatorState state = ExcavatorState.NO_VEIN;
	protected boolean completedOneCycle;

	protected ExcavatingBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		drillStack = ItemStack.EMPTY;
		setLazyTickRate(20);
	}

	@SuppressWarnings({ "unchecked" })
	@Override
	public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
		VeinRecipe veinR = veinClient != null ? level.getRecipeManager().byKey(veinClient).filter(e -> e.value() instanceof VeinRecipe).map(r -> (VeinRecipe) r.value()).orElse(null) : null;
		Component vein = veinR != null ? veinR.getName() : Component.translatable("chat.coe.veinFinder.nothing");
		R rec = recipeClient != null ? level.getRecipeManager().byKey(recipeClient).filter(r -> r.value().getType() == getRecipeType()).map(r -> (R) r.value()).orElse(null) : null;
		TooltipUtil.forGoggles(tooltip, Component.translatable("chat.coe.veinFinder.found", vein));
		if(!hasRotation) {
			TooltipUtil.forGoggles(tooltip, CreateLang.translateDirect("tooltip.speedRequirement")
					.withStyle(GOLD));
			Component hint =
					CreateLang.translateDirect("gui.contraptions.not_fast_enough", I18n.get(getBlockState().getBlock()
							.getDescriptionId()));
			List<Component> cutString = TooltipHelper.cutTextComponent(hint, Palette.GRAY_AND_WHITE);
			for (int i = 0; i < cutString.size(); i++)
				TooltipUtil.forGoggles(tooltip, cutString.get(i).copy());
		}
		if(drillStack.isEmpty())
			TooltipUtil.forGoggles(tooltip, Component.translatable("info.coe.drill.noDrill"));
		else
			TooltipUtil.forGoggles(tooltip, Component.translatable("info.coe.drill.installed", drillStack.getHoverName()));

		if(!level.getBlockState(getBelow()).isCollisionShapeFullBlock(level, getBelow())) {
			TooltipUtil.forGoggles(tooltip, Component.translatable("info.coe.drill.noGround"));
		}

		if(rec != null) {
			TooltipUtil.forGoggles(tooltip, Component.translatable("info.coe.drill.progress").append(": [").append(ClientUtil.makeProgressBar(progress / (float) rec.getTicks())).append("]"));
			if(resourceRemClient != 0)TooltipUtil.forGoggles(tooltip, Component.translatable("info.coe.drill.resourceRemaining", NumberFormatter.formatNumber(resourceRemClient)));
			if(!rec.getDrill().test(drillStack)) {
				TooltipUtil.forGoggles(tooltip, Component.translatable("info.coe.drill.badDrill"));
			}
			addToGoggleTooltip(tooltip, rec);
		}
		if(state != ExcavatorState.NO_ERROR) {
			TooltipUtil.forGoggles(tooltip, Component.translatable("info.coe.drill.err_" + state.name().toLowerCase()));
		}

		return true;
	}

	public void addToGoggleTooltip(List<Component> tooltip, R recipe) {}

	public void updateRecipe() {
		vein = null;
		current = null;

		ChunkPos p = new ChunkPos(worldPosition);
		data = OreDataAttachment.getData(level.getChunk(p.x, p.z));
		RecipeManager m = level.getRecipeManager();
		if(data != null) {
			vein = data.getRecipe(m);
			if(vein != null) {
				List<RecipeHolder<R>> rec = m.getAllRecipesFor(getRecipeType()).stream().filter(r -> r.value().veinId.equals(vein.id())).sorted(Comparator.comparingInt(r -> r.value().priority)).toList();
				if(rec.size() == 1)current = rec.get(0);
				else if(rec.size() > 1) {
					for (RecipeHolder<R> r : rec) {
						if(validateRecipe(r.value())) {
							current = r;
						}
					}
					if(current == null)current = rec.get(0);
				}
			}
		}
	}

	protected boolean validateRecipe(R recipe) {
		return recipe.getDrill().test(drillStack);
	}

	protected abstract RecipeType<R> getRecipeType();
	protected abstract boolean canExtract();
	protected abstract void onFinished();

	@Override
	public void tick() {
		super.tick();
		if(!level.isClientSide) {
			if(current != null && state == ExcavatorState.NO_ERROR) {
				if(kinetic != null)kinetic.setStress(current.value().getStress());
				if(canExtract() && kinetic != null && kinetic.getRotationSpeed() >= SpeedLevel.MEDIUM.getSpeedValue() &&
						current.value().getDrill().test(drillStack) && level.getBlockState(getBelow()).isCollisionShapeFullBlock(level, getBelow())) {
					float prg = kinetic.getRotationSpeed() / SpeedLevel.MEDIUM.getSpeedValue();
					progress += prg;
					if(progress >= current.value().getTicks()) {
						updateState();
						if(state == ExcavatorState.NO_ERROR) {
							onFinished();
							data.extract(1);
							completedOneCycle = true;
							setChanged();
						}
						progress = 0;
					}
				} else if(!current.value().getDrill().test(drillStack)) {
					RecipeHolder<R> old = current;
					updateRecipe();
					if (old != current)
						progress = 0;
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
		updateRecipe();
		if(vein == null) {
			state = ExcavatorState.NO_VEIN;
		} else if(!data.canExtract(level, worldPosition)) {
			state = ExcavatorState.TOO_MANY_EXCAVATORS;
		} else if(data.getResourcesRemaining(vein.value()) == -1) {
			state = ExcavatorState.VEIN_EMPTY;
		} else if(current == null) {
			state = ExcavatorState.NO_RECIPE;
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
	protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
		super.read(tag, registries, clientPacket);
		progress = tag.getInt("progress");
		drillStack = ItemStack.parseOptional(registries, tag.getCompound("drill"));
		completedOneCycle = tag.getBoolean("finishedOnce");
		if(clientPacket) {
			state = ExcavatorState.VALUES[tag.getByte("state")];
			if(tag.contains("veinId")) {
				veinClient = ResourceLocation.tryParse(tag.getString("veinId"));
				resourceRemClient = tag.getLong("resRem");
			} else
				veinClient = null;

			if(tag.contains("currentRecipeId")) {
				recipeClient = ResourceLocation.tryParse(tag.getString("currentRecipeId"));
			} else
				recipeClient = null;

			hasRotation = tag.getBoolean("hasRot");
		}
	}

	@Override
	public void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
		super.write(tag, registries, clientPacket);
		tag.putInt("progress", progress);
		if (!drillStack.isEmpty())tag.put("drill", drillStack.save(registries));
		tag.putBoolean("finishedOnce", completedOneCycle);
		if(clientPacket) {
			tag.putByte("state", (byte) state.ordinal());
			if(vein != null) {
				tag.putString("veinId", vein.id().toString());
				tag.putLong("resRem", data.getResourcesRemaining(vein.value()));
			}
			if(current != null) {
				tag.putString("currentRecipeId", current.id().toString());
			}
			tag.putBoolean("hasRot", kinetic != null && kinetic.getRotationSpeed() >= SpeedLevel.MEDIUM.getSpeedValue());
		}
	}

	@Override
	public void initialize() {
		super.initialize();
		if(!level.isClientSide) {
			if(progress > 0 && current == null)
				updateRecipe();
			if(current == null)progress = 0;
		}
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

	public ItemInteractionResult onClick(Player player, InteractionHand hand) {
		ItemStack item = player.getItemInHand(hand);
		if (item.getItem() == Registration.VEIN_ATLAS_ITEM.get()) {
			if(!level.isClientSide) {
				if (completedOneCycle) {
					Registration.VEIN_ATLAS_ITEM.get().addVein(player, item, vein, new DimChunkPos(level, worldPosition), data.getRandomMul());
				} else {
					player.displayClientMessage(Component.translatable("chat.coe.sampleDrill.notDone"), true);
				}
			}
			return ItemInteractionResult.SUCCESS;
		} else if(item.is(CreateOreExcavation.DRILL_TAG)) {
			if (drillStack.isEmpty()) {
				if(!level.isClientSide)drillStack = item.split(1);
				notifyUpdate();
				return ItemInteractionResult.SUCCESS;
			}
		}
		return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
	}

	public InteractionResult onClick(Player player) {
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
		return InteractionResult.PASS;
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
	}

	protected void dropItemStack(ItemStack stackInSlot) {
		Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), stackInSlot);
	}

	@Override
	protected AABB createRenderBoundingBox() {
		return AABB.encapsulatingFullBlocks(worldPosition.offset(-1, -1, -1), worldPosition.offset(1, 0, 1));
	}

	@Override
	public Direction getFacing() {
		return getBlockState().getValue(HORIZONTAL_FACING);
	}

	@Override
	public boolean shouldRenderRubble() {
		return completedOneCycle || (recipeClient != null && progress > 0);
	}

	@Override
	public float getYOffset() {
		return 0.8f;
	}

	@Override
	public float getDrillOffset() {
		return isActive() ? 0.2f : 0.0f;
	}

	private boolean isActive() {
		return recipeClient != null && hasRotation;
	}

	@Override
	public float getRotation() {
		if (isActive()) {
			long ticks = getLevel().getGameTime();
			float rot = (ticks * 20) % 360;
			return rot;
		}
		return 0f;
	}

	@Override
	public float getPrevRotation() {
		if (isActive()) {
			long ticks = getLevel().getGameTime() - 1;
			float rot = (ticks * 20) % 360;
			return rot;
		}
		return 0f;
	}

	@Override
	public boolean shouldRenderShaft() {
		return true;
	}

	public static enum ExcavatorState {
		NO_ERROR,
		NO_VEIN,
		VEIN_EMPTY,
		TOO_MANY_EXCAVATORS,
		NO_RECIPE,
		;
		public static final ExcavatorState[] VALUES = values();
	}
}
