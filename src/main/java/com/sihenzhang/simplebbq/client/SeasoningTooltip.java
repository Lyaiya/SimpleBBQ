package com.sihenzhang.simplebbq.client;

import com.sihenzhang.simplebbq.SimpleBBQ;
import com.sihenzhang.simplebbq.util.I18nUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = SimpleBBQ.MOD_ID)
public class SeasoningTooltip {

    @SubscribeEvent
    public static void onTooltip(final ItemTooltipEvent event) {
        var itemStack = event.getItemStack();
        var seasoningTag = itemStack.getTagElement("Seasoning");
        if (seasoningTag != null && seasoningTag.contains("SeasoningList", Tag.TAG_LIST)) {
            var seasoningList = seasoningTag.getList("SeasoningList", Tag.TAG_STRING);
            var hasEffect = seasoningTag.getBoolean("HasEffect");
            if (!seasoningList.isEmpty()) {
                event.getToolTip().add(I18nUtils.createComponent("tooltip", "seasoned_with").withStyle(ChatFormatting.GRAY));
            }
            for (var i = 0; i < seasoningList.size(); i++) {
                event.getToolTip().add(CommonComponents.space().append(I18nUtils.createComponent("tooltip", "seasoning." + seasoningList.getString(i)).withStyle(hasEffect ? ChatFormatting.YELLOW : ChatFormatting.DARK_GRAY)));
            }
            if (!seasoningList.isEmpty() && !hasEffect) {
                event.getToolTip().add(I18nUtils.createComponent("tooltip", "seasoning_no_effect").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
            }
        }
    }
}
