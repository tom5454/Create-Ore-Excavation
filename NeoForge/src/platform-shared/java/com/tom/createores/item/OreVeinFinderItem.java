package com.tom.createores.item;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

import com.mojang.datafixers.util.Pair;

import com.tom.createores.Config;
import com.tom.createores.OreDataCapability;
import com.tom.createores.OreDataCapability.OreData;
import com.tom.createores.OreVeinGenerator;
import com.tom.createores.Registration;
import com.tom.createores.network.NetworkHandler;
import com.tom.createores.network.OreVeinInfoPacket;
import com.tom.createores.recipe.VeinRecipe;
import com.tom.createores.util.ComponentJoiner;
import com.tom.createores.util.RandomSpreadGenerator;

public class OreVeinFinderItem extends Item {

	public OreVeinFinderItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
		if(!level.isClientSide)
			detect(level, player.blockPosition(), player);

		return InteractionResultHolder.success(player.getItemInHand(interactionHand));
	}

	@Override
	public InteractionResult useOn(UseOnContext ctx) {
		if(!ctx.getLevel().isClientSide)
			detect(ctx.getLevel(), ctx.getClickedPos(), ctx.getPlayer());

		return InteractionResult.SUCCESS;
	}

	@Override
	public void appendHoverText(ItemStack pStack, Level pLevel, List<Component> pTooltipComponents,
			TooltipFlag pIsAdvanced) {
		if (pStack.hasTag() && pStack.getTag().getBoolean("isFiltered")) {
			pTooltipComponents.add(Component.translatable("tooltip.coe.vein_finder.filtered"));
		}
	}

	@Override
	public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
		if (!pLevel.isClientSide && pEntity instanceof Player player && pLevel.getGameTime() % 20 == 10) {
			boolean hasAtlas = false;
			for (int i = 0;i < player.getInventory().getContainerSize(); i++) {
				ItemStack is = player.getInventory().getItem(i);
				if (is.getItem() == Registration.VEIN_ATLAS_ITEM.get()) {
					hasAtlas = true;
					break;
				}
			}
			pStack.getOrCreateTag().putBoolean("isFiltered", hasAtlas);
		}
	}

	@Override
	public boolean isFoil(ItemStack pStack) {
		return (pStack.hasTag() && pStack.getTag().getBoolean("isFiltered")) || super.isFoil(pStack);
	}

	private void detect(Level level, BlockPos pos, Player player) {
		ItemStack atlas = ItemStack.EMPTY;
		for (int i = 0;i < player.getInventory().getContainerSize(); i++) {
			ItemStack is = player.getInventory().getItem(i);
			if (is.getItem() == Registration.VEIN_ATLAS_ITEM.get()) {
				atlas = is;
				break;
			}
		}
		CompoundTag atlasTag = atlas.getTag();
		Predicate<VeinRecipe> filter = atlasTag != null ? makeFilter(atlasTag) : a -> true;

		ChunkPos center = new ChunkPos(pos);
		OreData found = null;
		List<OreData> nearby = new ArrayList<>();
		int near = Config.veinFinderNear;
		for(int x = -near;x <= near;x++) {
			for(int z = -near;z <= near;z++) {
				OreData d = OreDataCapability.getData(level.getChunk(center.x + x, center.z + z));
				if(x == 0 && z == 0)found = d;
				else nearby.add(d);
			}
		}
		player.displayClientMessage(Component.translatable("chat.coe.veinFinder.info"), false);
		player.displayClientMessage(Component.translatable("chat.coe.veinFinder.pos", center.x, center.z), false);
		RecipeManager m = level.getRecipeManager();
		Component f;
		Component nothing = Component.translatable("chat.coe.veinFinder.nothing");
		Component comma = Component.literal(", ");
		if(found != null && found.getRecipe(m) != null)f = found.getRecipe(m).getName();
		else f = nothing;
		player.displayClientMessage(Component.translatable("chat.coe.veinFinder.found", f), false);

		CompoundTag infoTag = new CompoundTag();
		ResourceLocation id = found.getRecipeId();
		if (id != null)infoTag.putString("found", id.toString());
		infoTag.putInt("x", pos.getX());
		infoTag.putInt("z", pos.getZ());

		ResourceLocation rl = nearby.stream().map(d -> d.getRecipe(m)).filter(r -> r != null).map(VeinRecipe::getId).findFirst().orElse(null);
		if (rl != null) {
			infoTag.putString("nearby", rl.toString());
		}

		f = nearby.stream().map(d -> d.getRecipe(m)).filter(r -> r != null).map(r -> r.getName()).collect(ComponentJoiner.joining(nothing, comma));
		player.displayClientMessage(Component.translatable("chat.coe.veinFinder.nearby", f), false);

		Pair<BlockPos, VeinRecipe> nearest = OreVeinGenerator.getPicker((ServerLevel) level).locate(pos, (ServerLevel) level, 16, filter);
		if(nearest != null) {
			BlockPos at = nearest.getFirst();
			int i = Math.round(RandomSpreadGenerator.distance2d(at, pos) / Config.veinFinderFar) * Config.veinFinderFar;
			player.displayClientMessage(Component.translatable("chat.coe.veinFinder.far", Component.translatable("chat.coe.veinFinder.distance", nearest.getSecond().getName(), i)), false);
			infoTag.putString("far", nearest.getSecond().getId().toString());
			infoTag.putInt("dist", i);
		} else {
			player.displayClientMessage(Component.translatable("chat.coe.veinFinder.far", nothing), false);
		}
		NetworkHandler.sendTo((ServerPlayer) player, new OreVeinInfoPacket(infoTag));
		player.getCooldowns().addCooldown(this, Config.veinFinderCd);
	}

	private Predicate<VeinRecipe> makeFilter(CompoundTag atlasTag) {
		String vt = atlasTag.getString(OreVeinAtlasItem.TARGET);
		ResourceLocation target = ResourceLocation.tryParse(vt);
		if (!vt.isEmpty() && target != null) {
			return v -> target.equals(v.getId());
		}
		Set<ResourceLocation> exclude = new HashSet<>();
		ListTag ex = atlasTag.getList(OreVeinAtlasItem.EXCLUDE, Tag.TAG_STRING);
		for (int i = 0;i < ex.size(); i++) {
			ResourceLocation v = ResourceLocation.tryParse(ex.getString(i));
			if (v != null)exclude.add(v);
		}
		if (exclude.isEmpty())return a -> true;
		return v -> {
			return !exclude.contains(v.getId());
		};
	}
}
