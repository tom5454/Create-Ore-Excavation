package com.tom.createores.client;

import static net.minecraft.ChatFormatting.GRAY;

import java.util.List;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import com.simibubi.create.content.contraptions.base.IRotate.StressImpact;
import com.simibubi.create.content.contraptions.goggles.GogglesItem;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.utility.Lang;
import com.simibubi.create.foundation.utility.LangBuilder;

import com.tom.createores.Registration;

import dev.architectury.registry.client.level.entity.EntityModelLayerRegistry;

public class CreateOreExcavationClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		ClientRegistration.register();
		EntityModelLayerRegistry.register(DrillRenderer.LAYER_LOCATION, DrillRenderer::createModel);

		ItemTooltipCallback.EVENT.register((stack, ctx, lines) -> {
			if(stack.getItem() == Registration.DRILL_BLOCK.get().asItem() || stack.getItem() == Registration.EXTRACTOR_BLOCK.get().asItem()) {
				appendVariableStress(lines);
			}
		});
	}

	public static void appendVariableStress(List<Component> tooltip) {
		boolean hasGoggles = GogglesItem.isWearingGoggles(Minecraft.getInstance().player);
		boolean hasStressImpact = StressImpact.isEnabled();
		LangBuilder rpmUnit = Lang.translate("generic.unit.rpm");

		if (hasStressImpact) {
			Lang.translate("tooltip.stressImpact")
			.style(GRAY)
			.addTo(tooltip);

			StressImpact impactId = StressImpact.HIGH;
			LangBuilder builder = Lang.builder()
					.add(Lang.text(ItemDescription.makeProgressBar(3, impactId.ordinal() + 1))
							.style(impactId.getAbsoluteColor()));

			if (hasGoggles) {
				builder.add(Lang.text("1-?"))
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
