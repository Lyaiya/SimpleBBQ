package com.sihenzhang.simplebbq.data;

import com.sihenzhang.simplebbq.SimpleBBQ;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SimpleBBQ.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGen {
    @SubscribeEvent
    public static void gatherData(final GatherDataEvent event) {
        var generator = event.getGenerator();
        var lookupProvider = event.getLookupProvider();
        var helper = event.getExistingFileHelper();
        var packOutput = generator.getPackOutput();
        if (event.includeServer()) {
            var blockTagsProvider = new SimpleBBQBlockTagsProvider(packOutput, lookupProvider, helper);
            generator.addProvider(true, blockTagsProvider);
            generator.addProvider(true, new SimpleBBQItemTagsProvider(packOutput, lookupProvider, blockTagsProvider, helper));
            generator.addProvider(true, new SimpleBBQLootTableProvider(packOutput));
            generator.addProvider(true, new SimpleBBQRecipeProvider(packOutput));
            generator.addProvider(true, new SimpleBBQPoiTypeTagsProvider(packOutput, lookupProvider, helper));
        }
        if (event.includeClient()) {
            var blockStateProvider = new SimpleBBQBlockStateProvider(generator, helper);
            generator.addProvider(true, blockStateProvider);
            generator.addProvider(true, new SimpleBBQItemModelProvider(generator, blockStateProvider.models().existingFileHelper));
        }
    }
}
