package com.tom.createores.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import com.tom.createores.Registration;
import com.tom.createores.menu.OreVeinAtlasMenu;
import com.tom.createores.network.OreVeinAtlasClickPacket.Option;
import com.tom.createores.recipe.VeinRecipe;
import com.tom.createores.util.DimChunkPos;
import com.tom.createores.util.PlatformMenuProvider;

public class OreVeinAtlasItem extends Item implements PlatformMenuProvider {
	public static final String DISCOVERED = "discovered";
	public static final String EXCLUDE = "exclude";
	public static final String VEINS = "veins";
	public static final String TARGET = "veinTarget";
	public static final String SIZE = "size";
	public static final String DIMENSION = "dim";
	public static final String POS_X = "x";
	public static final String POS_Z = "z";
	public static final String VEIN_ID = "id";

	public OreVeinAtlasItem(Properties p_41383_) {
		super(p_41383_);
	}

	public void addVein(Player player, ItemStack is, VeinRecipe vein, DimChunkPos pos, float randomMul) {
		player.displayClientMessage(Component.translatable("chat.coe.sampleDrill.addedToAtlas"), false);

		var tag = is.getOrCreateTag();
		ListTag disc = tag.getList(DISCOVERED, Tag.TAG_STRING);
		tag.put(DISCOVERED, disc);
		String id = vein.getId().toString();
		boolean found = false;
		for (int i = 0;i < disc.size(); i++) {
			var v = disc.getString(i);
			if (id.equals(v)) {
				found = true;
				break;
			}
		}
		if (!found) {
			disc.add(StringTag.valueOf(id));
		}

		ListTag veins = tag.getList(VEINS, Tag.TAG_COMPOUND);
		tag.put(VEINS, veins);
		String dimId = pos.dimension.location().toString();
		found = false;
		for (int i = 0;i < veins.size(); i++) {
			var v = veins.getCompound(i);
			int x = v.getInt(POS_X);
			int z = v.getInt(POS_Z);
			var vid = v.getString(VEIN_ID);
			var dim = v.getString(DIMENSION);
			if (dimId.equals(dim) && x == pos.x && z == pos.z) {
				if(!vid.equals(id))
					v.putString(VEIN_ID, id);
				v.putFloat(SIZE, randomMul);
				found = true;
				break;
			}
		}
		if (!found) {
			var t = new CompoundTag();
			t.putInt(POS_X, pos.x);
			t.putInt(POS_Z, pos.z);
			t.putString(DIMENSION, dimId);
			t.putString(VEIN_ID, id);
			veins.add(t);
		}
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
		if (!world.isClientSide) {
			player.openMenu(this);
		}
		return InteractionResultHolder.pass(player.getItemInHand(hand));
	}

	@Override
	public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
		ItemStack heldItem = pPlayer.getMainHandItem();
		return new OreVeinAtlasMenu(Registration.VEIN_ATLAS_MENU.get(), pContainerId, pPlayerInventory, heldItem);
	}

	@Override
	public Component getDisplayName() {
		return getDescription();
	}

	public void menuClicked(ItemStack is, Option opt, ResourceLocation id) {
		var tag = is.getOrCreateTag();

		switch (opt) {
		case ADD_EXCLUDE:
		{
			ListTag ex = tag.getList(EXCLUDE, Tag.TAG_STRING);
			tag.put(EXCLUDE, ex);
			ex.add(StringTag.valueOf(id.toString()));
		}
		break;

		case REMOVE_EXCLUDE:
		{
			ListTag ex = tag.getList(EXCLUDE, Tag.TAG_STRING);
			tag.put(EXCLUDE, ex);
			String st = id.toString();
			ex.removeIf(t -> t instanceof StringTag s && s.getAsString().equals(st));
		}
		break;

		case REMOVE_TARGET:
			tag.remove(TARGET);
			break;

		case SET_TARGET:
			tag.putString(TARGET, id.toString());
			ListTag ex = tag.getList(EXCLUDE, Tag.TAG_STRING);
			tag.put(EXCLUDE, ex);
			String st = id.toString();
			ex.removeIf(t -> t instanceof StringTag s && s.getAsString().equals(st));
			break;

		default:
			break;
		}
	}
}
