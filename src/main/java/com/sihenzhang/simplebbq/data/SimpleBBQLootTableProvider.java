package com.sihenzhang.simplebbq.data;

import com.google.common.collect.Iterables;
import com.sihenzhang.simplebbq.SimpleBBQRegistry;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class SimpleBBQLootTableProvider extends LootTableProvider {
    public SimpleBBQLootTableProvider(PackOutput packOutput) {
        super(packOutput,
                Set.of(SimpleBBQRegistry.GRILL_BLOCK.getId(), SimpleBBQRegistry.SKEWERING_TABLE_BLOCK.getId()),
                List.of(new SubProviderEntry(SimpleBBQBlockLootSubProvider::new, LootContextParamSets.BLOCK)));
    }

    @Override
    protected void validate(Map<ResourceLocation, LootTable> map, @NotNull ValidationContext validationcontext) {
        map.forEach((key, value) -> value.validate(validationcontext));
    }

    public static class SimpleBBQBlockLootSubProvider extends BlockLootSubProvider {
        public SimpleBBQBlockLootSubProvider() {
            super(Set.of(), FeatureFlags.REGISTRY.allFlags());
        }

        @Override
        protected void generate() {
            this.dropSelf(SimpleBBQRegistry.GRILL_BLOCK.get());
            this.dropSelf(SimpleBBQRegistry.SKEWERING_TABLE_BLOCK.get());
        }

        @Override
        protected @NotNull Iterable<Block> getKnownBlocks() {
            return Iterables.transform(SimpleBBQRegistry.BLOCKS.getEntries(), RegistryObject::get);
        }
    }
}
