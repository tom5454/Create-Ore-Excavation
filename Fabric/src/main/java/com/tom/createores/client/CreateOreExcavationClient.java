package com.tom.createores.client;

import static net.minecraft.ChatFormatting.GRAY;

import java.util.List;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import com.simibubi.create.content.equipment.goggles.GogglesItem;
import com.simibubi.create.content.kinetics.base.IRotate.StressImpact;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.utility.Lang;
import com.simibubi.create.foundation.utility.LangBuilder;

import com.tom.createores.CreateOreExcavation;
import com.tom.createores.Registration;
import com.tom.createores.jm.JMEventListener;
import com.tom.createores.network.ClientNetwork;

public class CreateOreExcavationClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		ClientRegistration.register();
		EntityModelLayerRegistry.registerModelLayer(DrillRenderer.LAYER_LOCATION, DrillRenderer::createModel);
		ClientNetwork.init();

		ItemTooltipCallback.EVENT.register((stack, ctx, lines) -> {
			if(stack.getItem() == Registration.DRILL_BLOCK.get().asItem() || stack.getItem() == Registration.EXTRACTOR_BLOCK.get().asItem()) {
				appendVariableStress(lines);
			}
		});

		CreateOreExcavation.journeyMap = FabricLoader.getInstance().isModLoaded("journeymap");
		if (CreateOreExcavation.journeyMap)
			JMEventListener.init();
	}

	public static void appendVariableStress(List<Component> tooltip) {
		if (Minecraft.getInstance().player == null) return;
		boolean hasGoggles = GogglesItem.isWearingGoggles(Minecraft.getInstance().player);
		boolean hasStressImpact = StressImpact.isEnabled();
		LangBuilder rpmUnit = Lang.translate("generic.unit.rpm");

		if (hasStressImpact) {
			Lang.translate("tooltip.stressImpact")
			.style(GRAY)
			.addTo(tooltip);

			StressImpact impactId = StressImpact.HIGH;
			LangBuilder builder = Lang.builder()
					.add(Lang.text(TooltipHelper.makeProgressBar(3, impactId.ordinal() + 1))
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
