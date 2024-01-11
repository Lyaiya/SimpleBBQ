package com.sihenzhang.simplebbq.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.sihenzhang.simplebbq.block.GrillBlock;
import com.sihenzhang.simplebbq.block.entity.GrillBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;

public class GrillRenderer implements BlockEntityRenderer<GrillBlockEntity> {
    public GrillRenderer(BlockEntityRendererProvider.Context pContext) {
    }

    @Override
    public void render(GrillBlockEntity blockEntity, float partialTick, @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        var mc = Minecraft.getInstance();

        var direction = blockEntity.getBlockState().getValue(GrillBlock.FACING);
        var inventory = blockEntity.getInventory();
        var posInt = (int) blockEntity.getBlockPos().asLong();
        for (var i = 0; i < inventory.getSlots(); i++) {
            var itemStack = inventory.getStackInSlot(i);
            if (!itemStack.isEmpty()) {
                var itemRenderer = mc.getItemRenderer();
                var isBlockItem = itemRenderer.getModel(itemStack, blockEntity.getLevel(), null, posInt).isGui3d();
                poseStack.pushPose();
                // center the item/block on the grill
                if (isBlockItem) {
                    poseStack.translate(0.5D, 1.11885D, 0.5D);
                } else {
                    poseStack.translate(0.5D, 0.98285D, 0.5D);
                }
                // rotate the item/block to face the grill
                poseStack.mulPose(Axis.YN.rotationDegrees(direction.toYRot()));
                // rotate the item to lay down on the grill
                if (!isBlockItem) {
                    poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
                }
                // move the item/block to the specified position
                poseStack.translate(0.2D - 0.4D * i, 0.0D, 0.0D);
                // resize the item/block
                if (isBlockItem) {
                    poseStack.scale(0.6F, 0.6F, 0.6F);
                } else {
                    poseStack.scale(0.45F, 0.45F, 0.45F);
                }
                // render the item/block on the grill
                itemRenderer.renderStatic(itemStack, ItemDisplayContext.FIXED, packedLight, packedOverlay, poseStack, bufferSource, null, posInt + i);
                poseStack.popPose();
            }
        }

        // render campfire block
        var blockState = blockEntity.getCampfireData().toBlockState();
        poseStack.pushPose();
        mc.getBlockRenderer().renderSingleBlock(blockState, poseStack, bufferSource, packedLight, packedOverlay, ModelData.EMPTY, RenderType.cutout());
        poseStack.popPose();
    }
}
