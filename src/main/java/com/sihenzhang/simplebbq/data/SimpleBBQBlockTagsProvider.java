package com.sihenzhang.simplebbq.data;

import com.sihenzhang.simplebbq.SimpleBBQ;
import com.sihenzhang.simplebbq.SimpleBBQRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class SimpleBBQBlockTagsProvider extends BlockTagsProvider {
    public SimpleBBQBlockTagsProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(packOutput, lookupProvider, SimpleBBQ.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(SimpleBBQRegistry.GRILL_BLOCK.get());
        this.tag(BlockTags.NEEDS_STONE_TOOL).add(SimpleBBQRegistry.GRILL_BLOCK.get());
        this.tag(BlockTags.MINEABLE_WITH_AXE).add(SimpleBBQRegistry.SKEWERING_TABLE_BLOCK.get());
    }

    @Override
    public @NotNull String getName() {
        return "SimpleBBQ Block Tags";
    }


}
