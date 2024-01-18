# Create Ore Excavation

Extract resources using machines powered by Rotational Force  
Download: [CurseForge](https://www.curseforge.com/minecraft/mc-mods/create-ore-excavation), [Modrinth](https://modrinth.com/mod/create-ore-excavation)

## KubeJS

### 1.20

```js
ServerEvents.recipes((event) => {
  //Adding veins
  //.placement(spacing, separation, salt)
  //If all three values match the veins overwrite each other
  //Use .priority(<value>) to set the vein generation priority
  event.recipes.createoreexcavation
    .vein('{"text": "My redstone vein"}', "minecraft:redstone")
    .placement(1024, 128, 64825185)
    .id("kubejs:my_redstone_vein");

  //Drilling recipes (Items)
  //Arguments: output item(s), ore vein id, extraction time in ticks at 32 RPM.
  event.recipes.createoreexcavation
    .drilling("minecraft:redstone", "kubejs:my_redstone_vein", 100)
    .id("kubejs:my_vein1");

  //Coal vein with 5% chance for diamond and require a diamond drill and lava for drilling
  //Always finite 5x-8x base
  //Use .priority(<value>) for duplicate recipes with different inputs, higher values take priority
  event.recipes.createoreexcavation
    .vein('{"text": "My coal vein"}', "minecraft:coal")
    .placement(2048, 128, 64457512)
    .alwaysFinite()
    .veinSize(5, 8)
    .id("kubejs:my_coal_vein");
  event.recipes.createoreexcavation
    .drilling("minecraft:coal", "kubejs:my_coal_vein", 1000)
    .id("kubejs:my_coal1");
  event.recipes.createoreexcavation
    .drilling(
      [
        Item.of("minecraft:coal_block"),
        Item.of("minecraft:diamond").withChance(0.05),
      ],
      "kubejs:my_coal_vein",
      500
    )
    .drill("createoreexcavation:diamond_drill")
    .fluid("minecraft:lava")
    .priority(1)
    .id("kubejs:my_coal2");

  //Iron vein only in overworld and a stress requirement of 512 xRPM (default is 256 xRPM)
  //With a finite vein size between 3x-8.5x base (if finite veins are enabled)
  event.recipes.createoreexcavation
    .vein('{"text": "My iron vein"}', "minecraft:iron_ore")
    .placement(1024, 128, 6894685)
    .veinSize(3, 8.5)
    .biomeWhitelist("forge:is_overworld")
    .id("kubejs:my_iron_vein");
  event.recipes.createoreexcavation
    .drilling("minecraft:raw_iron", "kubejs:my_iron_vein", 100)
    .stress(512)
    .id("kubejs:my_vein3");
  //biomeBlacklist is also available

  //Fluid extractor recipes (Fluids)
  //Lava as drilling fluid
  event.recipes.createoreexcavation
    .vein('{"text": "Water well"}', "minecraft:water_bucket")
    .placement(1024, 128, 64630185)
    .alwaysInfinite()
    .id("kubejs:my_water_well");
  event.recipes.createoreexcavation
    .extracting("minecraft:water 2000", "kubejs:my_water_well", 10)
    .fluid("minecraft:lava 10")
    .id("kubejs:test");
  //The drilling fluid, stress and drill settings are the same as the drilling recipe

  //Set base value in config for finite veins
});

//Add any new drill items to #createoreexcavation:drills item tag
//Place a drill texture under assets/<item mod id>/textures/entity/drill/<item name>.png
//See assets/createoreexcavation/textures/entity/drill/drill.png

```

### 1.19.2 or Older

[https://github.com/tom5454/Create-Ore-Excavation/blob/1.19/README.md#kubejs](https://github.com/tom5454/Create-Ore-Excavation/blob/1.19/README.md#kubejs)
