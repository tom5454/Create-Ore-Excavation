# Create-Ore-Excavation
Extract resources using machines powered by Rotational Force

Download: [CurseForge](https://www.curseforge.com/minecraft/mc-mods/create-ore-excavation), [Modrinth](https://modrinth.com/mod/create-ore-excavation)

## KubeJS
You can add custom ore veins using KubeJS.  
 
```js
onEvent('recipes', event => {

	//Drilling recipes (Items)
	//Spawning weight the bigger the number the higher chance to spawn.
	//See server config for empty chunk weight.
	//Set the recipe id with .id('...')
	//Arguments: output item(s), ore vein name in Text Component, spawning weight, extraction time in ticks at 32 RPM.
	event.recipes.createoreexcavation.drilling('minecraft:redstone', '{"text": "My redstone vein"}', 10, 100).id("my_vein1");
	
	//Coal vein with 5% chance for diamond and require a diamond drill and lava for drilling
	event.recipes.createoreexcavation.drilling([Item.of('minecraft:coal'), Item.of('minecraft:diamond').withChance(0.05)], '{"text": "My coal vein"}', 2, 1000).drill('createoreexcavation:diamond_drill').fluid('minecraft:lava').id("my_vein2");
	
	//Iron vein only in overworld and a stress requirement of 512 xRPM (default is 256 xRPM)
	event.recipes.createoreexcavation.drilling('minecraft:raw_iron', '{"text": "My iron vein"}', 10, 100).biomeWhitelist('forge:is_overworld').stress(512).id("my_vein3");
	//biomeBlacklist is also available

	//Fluid extractor recipes (Fluids)
	event.recipes.createoreexcavation.extracting(Fluid.of('minecraft:water', 400), '{"text": "Water well"}', 10, 100).id("my_vein4");
	//The biome, stress and drill settings are the same as the drilling recipe
})
```