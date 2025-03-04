package com.tom.createores.client;

import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.WorldSectionElement;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import com.simibubi.create.foundation.ponder.CreateSceneBuilder;

import com.tom.createores.Registration;
import com.tom.createores.block.entity.SampleDrillBlockEntity;

public class PonderScenes {

	public static void oreFinder(SceneBuilder builder, SceneBuildingUtil util) {
		CreateSceneBuilder scene = new CreateSceneBuilder(builder);
		scene.title("find_ores", "Find ore veins using the Vein Finder");
		scene.configureBasePlate(0, 0, 9);
		scene.scaleSceneView(.65f);
		scene.setSceneOffsetY(-1);

		ElementLink<WorldSectionElement> groundElement =
				scene.world().showIndependentSection(util.select().fromTo(0, 0, 1, 8, 0, 9), Direction.UP);

		BlockPos ground = util.grid().at(5, 0, 5);

		scene.overlay().showText(60)
		.attachKeyFrame()
		.text("Use an Ore Vein Finder to locate ores")
		.pointAt(util.vector().blockSurface(ground, Direction.WEST))
		.placeNearTarget();
		scene.idle(60);

		scene.overlay().showControls(util.vector().topOf(ground), Pointing.DOWN, 30).rightClick()
		.withItem(new ItemStack(Registration.VEIN_FINDER_ITEM.get()));

		scene.idle(10);

		scene.overlay().showText(50)
		.text("Found: Nothing")
		.pointAt(util.vector().blockSurface(ground, Direction.WEST))
		.placeNearTarget();
		scene.idle(50);

		scene.world().moveSection(groundElement, util.vector().of(12, 0, 0), 40);
		scene.idle(15);
		scene.world().hideIndependentSection(groundElement, null);

		scene.idle(15);

		groundElement = scene.world().showIndependentSection(util.select().fromTo(0, 0, 1, 8, 0, 9), null);
		scene.world().moveSection(groundElement, util.vector().of(-8, 0, 0), 0);
		scene.world().moveSection(groundElement, util.vector().of(8, 0, 0), 40);

		scene.idle(40);

		scene.overlay().showControls(util.vector().topOf(ground), Pointing.DOWN, 30).rightClick()
		.withItem(new ItemStack(Registration.VEIN_FINDER_ITEM.get()));

		scene.idle(10);

		scene.overlay().showText(50)
		.text("Found: Copper Ore")
		.pointAt(util.vector().blockSurface(ground, Direction.WEST))
		.placeNearTarget();
		scene.idle(60);
	}

	public static void drillingMachine(SceneBuilder builder, SceneBuildingUtil util) {
		CreateSceneBuilder scene = new CreateSceneBuilder(builder);
		scene.title("drilling", "Start drilling");
		scene.configureBasePlate(0, 0, 9);
		scene.scaleSceneView(.65f);
		scene.setSceneOffsetY(-1);

		scene.world().showSection(util.select().layer(0), Direction.UP);

		BlockPos ground = util.grid().at(5, 0, 5);
		BlockPos drill = util.grid().at(5, 2, 5);

		scene.overlay().showText(70)
		.attachKeyFrame()
		.text("Place a Drilling Machine or a Fluid Extractor to harvest the resources")
		.pointAt(util.vector().blockSurface(ground, Direction.WEST))
		.placeNearTarget();
		scene.idle(20);

		scene.world().showSection(util.select().fromTo(3, 1, 4, 5, 2, 6), Direction.DOWN);
		scene.world().setKineticSpeed(util.select().position(4, 1, 4), 0);
		scene.idle(60);

		scene.overlay().showText(40)
		.text("Put in a Drill")
		.pointAt(util.vector().blockSurface(drill, Direction.WEST))
		.placeNearTarget();
		scene.idle(10);

		scene.overlay().showControls(util.vector().topOf(drill), Pointing.DOWN, 30).rightClick()
		.withItem(new ItemStack(Registration.NORMAL_DRILL_ITEM.get()));
		scene.idle(30);

		scene.overlay().showText(70)
		.attachKeyFrame()
		.text("The machines require Rotational Force to operate")
		.pointAt(util.vector().blockSurface(util.grid().at(5, 0, 2), Direction.WEST))
		.placeNearTarget();
		scene.idle(20);

		scene.world().showSection(util.select().fromTo(4, 1, 0, 4, 1, 3), Direction.DOWN);
		scene.world().showSection(util.select().position(5, 0, 0), Direction.NORTH);
		scene.idle(20);
		scene.world().setKineticSpeed(util.select().position(4, 1, 4), -32);
		scene.idle(40);

		scene.overlay().showText(70)
		.attachKeyFrame()
		.text("Extract the results")
		.pointAt(util.vector().blockSurface(util.grid().at(2, 1, 5), Direction.WEST))
		.placeNearTarget();
		scene.idle(20);

		scene.world().showSection(util.select().fromTo(0, 1, 5, 2, 2, 5), Direction.DOWN);
		scene.world().showSection(util.select().fromTo(2, 1, 6, 2, 1, 10), Direction.DOWN);
		scene.world().showSection(util.select().position(1, 0, 10), Direction.SOUTH);

		scene.idle(40);
		scene.world().flapFunnel(util.grid().at(2, 2, 5), true);
		scene.world().createItemOnBelt(util.grid().at(2, 1, 5), Direction.EAST, new ItemStack(Items.RAW_COPPER));

		scene.idle(60);

		scene.overlay().showText(70)
		.attachKeyFrame()
		.text("Some ore veins require drilling fluid")
		.pointAt(util.vector().blockSurface(util.grid().at(6, 1, 5), Direction.WEST))
		.placeNearTarget();
		scene.idle(10);

		scene.rotateCameraY(70);
		scene.idle(10);

		scene.world().showSection(util.select().fromTo(6, 1, 5, 7, 1, 10), Direction.DOWN);
		scene.world().showSection(util.select().position(6, 0, 10), Direction.SOUTH);

		scene.idle(60);
	}

	public static void sampleDrill(SceneBuilder builder, SceneBuildingUtil util) {
		CreateSceneBuilder scene = new CreateSceneBuilder(builder);
		scene.title("sample_drill", "Find an existing ore vein");
		scene.configureBasePlate(0, 0, 5);
		scene.scaleSceneView(.65f);
		scene.setSceneOffsetY(-1);
		scene.showBasePlate();

		BlockPos ground = util.grid().at(3, 0, 3);
		BlockPos drillAt = util.grid().at(2, 1, 2);

		scene.overlay().showControls(util.vector().topOf(ground), Pointing.DOWN, 30).rightClick()
		.withItem(new ItemStack(Registration.VEIN_FINDER_ITEM.get()));

		scene.idle(10);

		scene.overlay().showText(50)
		.text("Found: Copper Ore")
		.pointAt(util.vector().blockSurface(ground, Direction.WEST))
		.placeNearTarget();
		scene.idle(60);

		scene.overlay().showText(50)
		.attachKeyFrame()
		.text("Place the Sample Drill")
		.pointAt(util.vector().blockSurface(ground, Direction.WEST))
		.placeNearTarget();
		scene.idle(20);

		scene.world().showSection(util.select().fromTo(2, 1, 2, 2, 1, 2), Direction.DOWN);
		scene.idle(40);

		scene.overlay().showText(50)
		.text("Place a Coppper Backtank on top")
		.pointAt(util.vector().blockSurface(ground.above(), Direction.WEST))
		.placeNearTarget();
		scene.idle(20);

		scene.world().showSection(util.select().fromTo(2, 2, 2, 2, 2, 2), Direction.DOWN);
		scene.idle(40);

		scene.overlay().showText(40)
		.attachKeyFrame()
		.text("Activate the drill")
		.pointAt(util.vector().blockSurface(ground, Direction.WEST))
		.placeNearTarget();
		scene.idle(10);

		scene.overlay().showControls(util.vector().topOf(ground.above()), Pointing.DOWN, 30).rightClick();
		scene.world().modifyBlockEntity(drillAt, SampleDrillBlockEntity.class, be -> {
			be.setDrilling(true);
			be.setAirTankLevel(1f);
		});
		scene.idle(60);

		scene.overlay().showText(40)
		.text("Wait for the drilling")
		.pointAt(util.vector().blockSurface(ground, Direction.WEST))
		.placeNearTarget();
		scene.world().modifyBlockEntity(drillAt, SampleDrillBlockEntity.class, be -> {
			be.setProgress(SampleDrillBlockEntity.DRILL_TIME * 3 / 4);
		});
		scene.idle(60);

		scene.world().modifyBlockEntity(drillAt, SampleDrillBlockEntity.class, be -> {
			be.setProgress(SampleDrillBlockEntity.DRILL_TIME);
		});
		scene.overlay().showText(40)
		.attachKeyFrame()
		.text("Then click it again with the atlas, once it's finished")
		.pointAt(util.vector().blockSurface(ground, Direction.WEST))
		.placeNearTarget();
		scene.idle(10);

		scene.overlay().showControls(util.vector().topOf(ground.above()), Pointing.DOWN, 30).withItem(new ItemStack(Registration.VEIN_ATLAS_ITEM.get()));
		scene.idle(60);

		scene.overlay().showText(40)
		.text("Your Ore Vein Atlas now has all of the vein details, allowing you to filter with this vein");
		scene.idle(100);

		scene.world().hideSection(util.select().fromTo(2, 1, 2, 2, 2, 2), Direction.DOWN);

		ElementLink<WorldSectionElement> drill =
				scene.world().showIndependentSection(util.select().fromTo(0, 4, 0, 5, 5, 5), Direction.DOWN);

		scene.world().moveSection(drill, util.vector().of(0, -3, 0), 0);

		scene.overlay().showText(40)
		.attachKeyFrame()
		.pointAt(util.vector().blockSurface(ground.above(), Direction.WEST))
		.text("Or click an already running drilling machine, to collect a sample")
		.placeNearTarget();
		scene.idle(20);

		scene.overlay().showControls(util.vector().topOf(ground.above()), Pointing.DOWN, 30).rightClick().withItem(new ItemStack(Registration.VEIN_ATLAS_ITEM.get()));
		scene.idle(80);
	}
}
