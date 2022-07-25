package com.tom.createores.item;

import net.minecraft.world.item.Item;

import com.tom.createores.CreateOreExcavation;

public class DrillItem extends Item {

	public DrillItem() {
		super(new Item.Properties().tab(CreateOreExcavation.MOD_TAB).stacksTo(1));
	}

}
