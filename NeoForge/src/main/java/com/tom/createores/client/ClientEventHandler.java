package com.tom.createores.client;

import static net.minecraft.ChatFormatting.GRAY;

import java.util.List;

import net.createmod.catnip.lang.Lang;
import net.createmod.catnip.lang.LangBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

import com.simibubi.create.content.equipment.goggles.GogglesItem;
import com.simibubi.create.content.kinetics.base.IRotate.StressImpact;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.utility.CreateLang;

import com.tom.createores.CreateOreExcavation;
import com.tom.createores.Registration;

@EventBusSubscriber(value = Dist.CLIENT, modid = CreateOreExcavation.MODID)
public class ClientEventHandler {

	@SubscribeEvent
	public static void addToItemTooltip(ItemTooltipEvent event) {
		if (event.getEntity() == null)
			return;

		ItemStack stack = event.getItemStack();
		if(stack.getItem() == Registration.DRILL_BLOCK.get().asItem() || stack.getItem() == Registration.EXTRACTOR_BLOCK.get().asItem()) {
			appendVariableStress(event.getToolTip());
		}
	}

	public static void appendVariableStress(List<Component> tooltip) {
		boolean hasGoggles = GogglesItem.isWearingGoggles(Minecraft.getInstance().player);
		boolean hasStressImpact = StressImpact.isEnabled();
		LangBuilder rpmUnit = CreateLang.translate("generic.unit.rpm");

		if (hasStressImpact) {
			CreateLang.translate("tooltip.stressImpact")
			.style(GRAY)
			.addTo(tooltip);

			StressImpact impactId = StressImpact.HIGH;
			LangBuilder builder = CreateLang.builder()
					.add(CreateLang.text(TooltipHelper.makeProgressBar(3, impactId.ordinal() + 1))
							.style(impactId.getAbsoluteColor()));

			if (hasGoggles) {
				builder.add(CreateLang.text("1-?"))
				.text("x ")
				.add(rpmUnit)
				.add(Component.literal(" ").append(Component.translatable("tooltip.coe.variableImpact")))
				.addTo(tooltip);
			} else
				builder.translate("tooltip.stressImpact." + Lang.asId(impactId.name()))
				.addTo(tooltip);
		}
	}
}
