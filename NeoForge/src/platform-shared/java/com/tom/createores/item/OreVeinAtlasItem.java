package com.tom.createores.item;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;

import com.tom.createores.CreateOreExcavation;
import com.tom.createores.Registration;
import com.tom.createores.components.OreVeinAtlasDataComponent.OreVeinAtlasData;
import com.tom.createores.components.OreVeinAtlasDataComponent.OreVeinData;
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
	public static final String HIDE = "hide";

	public OreVeinAtlasItem(Properties p_41383_) {
		super(p_41383_);
	}

	public void addVein(Player player, ItemStack is, RecipeHolder<VeinRecipe> vein, DimChunkPos pos, float randomMul) {
		player.displayClientMessage(Component.translatable("chat.coe.sampleDrill.addedToAtlas"), false);

		OreVeinAtlasData comp = new OreVeinAtlasData(is.get(CreateOreExcavation.ORE_VEIN_ATLAS_DATA_COMPONENT));
		if (!comp.discovered().contains(vein.id())) {
			comp.addDiscovered(vein.id());
		}

		var vn = comp.veins().get(pos);
		if (vn == null || !vn.id().equals(vein.id())) {
			comp.addVein(pos, new OreVeinData(vein.id(), randomMul, false));
		}

		if (comp.isEdited()) {
			is.set(CreateOreExcavation.ORE_VEIN_ATLAS_DATA_COMPONENT, comp.finish());
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
		OreVeinAtlasData comp = new OreVeinAtlasData(is.get(CreateOreExcavation.ORE_VEIN_ATLAS_DATA_COMPONENT));

		switch (opt) {
		case ADD_EXCLUDE:
			comp.addExclude(id);
			break;

		case REMOVE_EXCLUDE:
			comp.removeExclude(id);
			break;

		case SET_TARGET:
			comp.setTarget(id);
			comp.removeExclude(id);
			break;

		default:
			break;
		}

		if (comp.isEdited()) {
			is.set(CreateOreExcavation.ORE_VEIN_ATLAS_DATA_COMPONENT, comp.finish());
		}
	}

	public void menuClicked2(ItemStack is, Option opt, int id) {
		OreVeinAtlasData comp = new OreVeinAtlasData(is.get(CreateOreExcavation.ORE_VEIN_ATLAS_DATA_COMPONENT));

		switch (opt) {
		case TOGGLE_HIDE:
			comp.toggleHide(id);
			break;

		case REMOVE_TARGET:
			comp.setTarget(null);
			break;

		default:
			break;
		}

		if (comp.isEdited()) {
			is.set(CreateOreExcavation.ORE_VEIN_ATLAS_DATA_COMPONENT, comp.finish());
		}
	}
}
