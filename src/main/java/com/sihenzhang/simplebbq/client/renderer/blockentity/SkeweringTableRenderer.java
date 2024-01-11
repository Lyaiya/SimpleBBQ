package com.sihenzhang.simplebbq.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.sihenzhang.simplebbq.block.SkeweringTableBlock;
import com.sihenzhang.simplebbq.block.entity.SkeweringTableBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class SkeweringTableRenderer implements BlockEntityRenderer<SkeweringTableBlockEntity> {
    public SkeweringTableRenderer(BlockEntityRendererProvider.Context pContext) {
    }

    @Override
    public void render(SkeweringTableBlockEntity blockEntity, float partialTick, @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        var direction = blockEntity.getBlockState().getValue(SkeweringTableBlock.FACING);
        var inventory = blockEntity.getInventory();
        var posInt = (int) blockEntity.getBlockPos().asLong();
        var itemStack = inventory.getStackInSlot(0);
        if (!itemStack.isEmpty()) {
            var itemRenderer = Minecraft.getInstance().getItemRenderer();
            var count = getRenderCount(itemStack);
            var isBlockItem = itemRenderer.getModel(itemStack, blockEntity.getLevel(), null, posInt).isGui3d();
            var randomSource = RandomSource.create(posInt);
            poseStack.pushPose();
            // center the item/block on the table
            if (isBlockItem) {
                poseStack.translate(0.5D, 1.19D, 0.5D);
            } else {
                poseStack.translate(0.5D, 1.02D, 0.5D);
            }
            // rotate the item/block to face the table
            poseStack.mulPose(Axis.YN.rotationDegrees(direction.toYRot()));
            // rotate the item to lay down on the table
            if (!isBlockItem) {
                poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
            }
            // resize the item/block on the table
            if (isBlockItem) {
                poseStack.scale(0.75F, 0.75F, 0.75F);
            } else {
                poseStack.scale(0.6F, 0.6F, 0.6F);
            }
            for (var i = 0; i < count; i++) {
                poseStack.pushPose();
                // stack the items/blocks on the table
                if (isBlockItem) {
                    poseStack.translate(i > 0 ? Mth.nextDouble(randomSource, -0.0625D, 0.0625D) : 0.0D, 0.0625D * i, i > 0 ? Mth.nextDouble(randomSource, -0.0625D, 0.0625D) : 0.0D);
                } else {
                    poseStack.translate(i > 0 ? Mth.nextDouble(randomSource, -0.075D, 0.075D) : 0.0D, i > 0 ? Mth.nextDouble(randomSource, -0.075D, 0.075D) : 0.0D, -0.06D * i);
                    poseStack.mulPose(Axis.ZP.rotationDegrees(i > 0 ? Mth.nextFloat(randomSource, -15.0F, 15.0F) : 0.0F));
                }
                // render the item/block on the table
                itemRenderer.renderStatic(itemStack, ItemDisplayContext.FIXED, packedLight, packedOverlay, poseStack, bufferSource, null, posInt);
                poseStack.popPose();
            }
            poseStack.popPose();
        }
    }

    private static int getRenderCount(ItemStack stack) {
        if (stack.isEmpty()) {
            return 0;
        }
        if (stack.getCount() > 48) {
            return 5;
        }
        if (stack.getCount() > 32) {
            return 4;
        }
        if (stack.getCount() > 16) {
            return 3;
        }
        if (stack.getCount() > 1) {
            return 2;
        }
        return 1;
    }
}
