package com.sihenzhang.simplebbq.data;

import com.sihenzhang.simplebbq.SimpleBBQ;
import com.sihenzhang.simplebbq.SimpleBBQRegistry;
import com.sihenzhang.simplebbq.tag.SimpleBBQItemTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class SimpleBBQItemTagsProvider extends ItemTagsProvider {
    public SimpleBBQItemTagsProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider, BlockTagsProvider blockTagsProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(packOutput, lookupProvider, blockTagsProvider.contentsGetter(), SimpleBBQ.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        this.tag(SimpleBBQItemTags.SKEWER).addTag(Tags.Items.RODS_WOODEN).add(Items.BAMBOO);
        this.tag(SimpleBBQItemTags.SEASONING).add(Items.HONEY_BOTTLE, SimpleBBQRegistry.CHILI_POWDER.get(), SimpleBBQRegistry.CUMIN.get(), SimpleBBQRegistry.SALT_AND_PEPPER.get());

        var allRawSkewers = Set.of(
                SimpleBBQRegistry.BEEF_SKEWER.get(),
                SimpleBBQRegistry.CHICKEN_SKEWER.get(),
                SimpleBBQRegistry.MUTTON_SKEWER.get(),
                SimpleBBQRegistry.PORK_SKEWER.get(),
                SimpleBBQRegistry.RABBIT_SKEWER.get(),
                SimpleBBQRegistry.COD_SKEWER.get(),
                SimpleBBQRegistry.SALMON_SKEWER.get(),
                SimpleBBQRegistry.BREAD_SLICE_SKEWER.get(),
                SimpleBBQRegistry.MUSHROOM_SKEWER.get(),
                SimpleBBQRegistry.POTATO_SKEWER.get()
        );
        var cannotBeSeasonedByHoney = Set.of(
                SimpleBBQRegistry.COD_SKEWER.get(),
                SimpleBBQRegistry.SALMON_SKEWER.get()
        );
        var cannotBeSeasonedByChiliPowder = Set.of(SimpleBBQRegistry.BREAD_SLICE_SKEWER.get());
        var cannotBeSeasonedByCumin = Set.of(SimpleBBQRegistry.BREAD_SLICE_SKEWER.get());
        var cannotBeSeasonedBySaltAndPepper = Set.of(SimpleBBQRegistry.BREAD_SLICE_SKEWER.get());

        this.tag(SimpleBBQItemTags.CAN_BE_SEASONED_BY_HONEY).add(allRawSkewers.stream().filter(item -> !cannotBeSeasonedByHoney.contains(item)).toArray(Item[]::new));
        this.tag(SimpleBBQItemTags.CAN_BE_SEASONED_BY_CHILI_POWDER).add(allRawSkewers.stream().filter(item -> !cannotBeSeasonedByChiliPowder.contains(item)).toArray(Item[]::new));
        this.tag(SimpleBBQItemTags.CAN_BE_SEASONED_BY_CUMIN).add(allRawSkewers.stream().filter(item -> !cannotBeSeasonedByCumin.contains(item)).toArray(Item[]::new));
        this.tag(SimpleBBQItemTags.CAN_BE_SEASONED_BY_SALT_AND_PEPPER).add(allRawSkewers.stream().filter(item -> !cannotBeSeasonedBySaltAndPepper.contains(item)).toArray(Item[]::new));
    }
}
