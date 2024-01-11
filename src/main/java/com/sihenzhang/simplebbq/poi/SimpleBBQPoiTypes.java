package com.sihenzhang.simplebbq.poi;

import com.sihenzhang.simplebbq.util.RLUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.ai.village.poi.PoiType;

public class SimpleBBQPoiTypes {
    public static final ResourceKey<PoiType> SKEWERMAN = createKey("skewerman");

    private static ResourceKey<PoiType> createKey(String name) {
        return ResourceKey.create(Registries.POINT_OF_INTEREST_TYPE, RLUtils.createRL(name));
    }
}
