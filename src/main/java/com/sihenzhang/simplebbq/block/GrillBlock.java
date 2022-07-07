package com.sihenzhang.simplebbq.block;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableSet;
import com.sihenzhang.simplebbq.SimpleBBQRegistry;
import com.sihenzhang.simplebbq.block.entity.GrillBlockEntity;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.*;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.Set;
import java.util.function.Supplier;

public class GrillBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {
    protected static final VoxelShape OUTLINE_SHAPE = Shapes.or(
            Block.box(0.0D, 0.0D, 0.0D, 1.0D, 10.0D, 1.0D),
            Block.box(0.0D, 0.0D, 15.0D, 1.0D, 10.0D, 16.0D),
            Block.box(15.0D, 0.0D, 15.0D, 16.0D, 10.0D, 16.0D),
            Block.box(15.0D, 0.0D, 0.0D, 16.0D, 10.0D, 1.0D),
            Block.box(0.0D, 10.0D, 0.0D, 16.0D, 16.0D, 16.0D)
    );
    protected static final VoxelShape COLLISION_SHAPE = Shapes.join(
            OUTLINE_SHAPE,
            Block.box(1.0D, 15.5D, 1.0D, 15.0D, 16.0D, 15.0D),
            BooleanOp.ONLY_FIRST
    );
    protected static final Supplier<Set<Item>> CAMPFIRE_ITEMS = Suppliers.memoize(() -> ForgeRegistries.ITEMS.getValues().stream().filter(item -> isCampfire(item.getDefaultInstance())).collect(ImmutableSet.toImmutableSet()));
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public GrillBlock() {
        super(Properties.of(Material.METAL, MaterialColor.NONE).requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.LANTERN).lightLevel(state -> state.getValue(LIT) ? 15 : 0).noOcclusion());
        this.registerDefaultState(stateDefinition.any().setValue(LIT, false).setValue(WATERLOGGED, false).setValue(FACING, Direction.NORTH));
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        var blockEntity = pLevel.getBlockEntity(pPos);
        if (blockEntity instanceof GrillBlockEntity grillBlockEntity) {
            var stackInHand = pPlayer.getItemInHand(pHand);
            var campfireData = grillBlockEntity.getCampfireData();

            // try to place the campfire
            if (campfireData.toBlockState().isAir() && isCampfire(stackInHand)) {
                // TODO: set campfire state with stack nbt
                var campfireState = ((BlockItem) stackInHand.getItem()).getBlock().getStateForPlacement(new BlockPlaceContext(pLevel, pPlayer, pHand, stackInHand, pHit));
                var newCampfireData = new GrillBlockEntity.CampfireData(campfireState);
                grillBlockEntity.setCampfireData(newCampfireData);
                pLevel.setBlockAndUpdate(pPos, pState.setValue(LIT, campfireData.lit));
                if (pPlayer instanceof ServerPlayer serverPlayer) {
                    CriteriaTriggers.PLACED_BLOCK.trigger(serverPlayer, pPos, stackInHand);
                }
                pLevel.gameEvent(pPlayer, GameEvent.BLOCK_PLACE, pPos);
                var campfireSoundType = campfireState.getSoundType(pLevel, pPos, pPlayer);
                pLevel.playSound(pPlayer, pPos, campfireSoundType.getPlaceSound(), SoundSource.BLOCKS, (campfireSoundType.getVolume() + 1.0F) / 2.0F, campfireSoundType.getPitch() * 0.8F);
                if (!pPlayer.getAbilities().instabuild) {
                    stackInHand.shrink(1);
                }
                return InteractionResult.sidedSuccess(pLevel.isClientSide());
            }

            // try to light the grill
            if (pState.hasProperty(LIT) && !pState.getValue(LIT) && pState.hasProperty(WATERLOGGED) && !pState.getValue(WATERLOGGED) && isCampfire(campfireData.toBlockState()) && !campfireData.lit) {
                if (stackInHand.getItem() instanceof FlintAndSteelItem) {
                    pLevel.playSound(pPlayer, pPos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, pLevel.getRandom().nextFloat() * 0.4F + 0.8F);
                    var newCampfireData = campfireData.copy();
                    newCampfireData.lit = true;
                    grillBlockEntity.setCampfireData(newCampfireData);
                    pLevel.setBlockAndUpdate(pPos, pState.setValue(LIT, true));
                    pLevel.gameEvent(pPlayer, GameEvent.BLOCK_PLACE, pPos);
                    stackInHand.hurtAndBreak(1, pPlayer, player -> player.broadcastBreakEvent(pHand));
                    return InteractionResult.sidedSuccess(pLevel.isClientSide());
                }
                if (stackInHand.getItem() instanceof FireChargeItem) {
                    var random = pLevel.getRandom();
                    pLevel.playSound(null, pPos, SoundEvents.FIRECHARGE_USE, SoundSource.BLOCKS, 1.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F);
                    var newCampfireData = campfireData.copy();
                    newCampfireData.lit = true;
                    grillBlockEntity.setCampfireData(newCampfireData);
                    pLevel.setBlockAndUpdate(pPos, pState.setValue(LIT, true));
                    pLevel.gameEvent(pPlayer, GameEvent.BLOCK_PLACE, pPos);
                    if (!pPlayer.getAbilities().instabuild) {
                        stackInHand.shrink(1);
                    }
                    return InteractionResult.sidedSuccess(pLevel.isClientSide());
                }
            }

            // try to dowse the grill
            if (pState.hasProperty(LIT) && pState.getValue(LIT) && isCampfire(campfireData.toBlockState()) && campfireData.lit && stackInHand.getItem() instanceof ShovelItem) {
                if (!pLevel.isClientSide()) {
                    pLevel.levelEvent(null, 1009, pPos, 0);
                }
                dowse(pPlayer, pLevel, pPos, pState);
                pLevel.setBlockAndUpdate(pPos, pState.setValue(LIT, false));
                stackInHand.hurtAndBreak(1, pPlayer, player -> player.broadcastBreakEvent(pHand));
                return InteractionResult.sidedSuccess(pLevel.isClientSide());
            }

            // try to cook
            var optionalRecipe = grillBlockEntity.getCookableRecipe(stackInHand);
            if (optionalRecipe.isPresent()) {
                if (!pLevel.isClientSide() && grillBlockEntity.placeFood(pPlayer.getAbilities().instabuild ? stackInHand.copy() : stackInHand, optionalRecipe.get().getCookingTime())) {
                    return InteractionResult.SUCCESS;
                }
                return InteractionResult.CONSUME;
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public void stepOn(Level pLevel, BlockPos pPos, BlockState pState, Entity pEntity) {
        if (!pEntity.fireImmune() && pState.getValue(LIT) && pEntity instanceof LivingEntity livingEntity && !EnchantmentHelper.hasFrostWalker(livingEntity)) {
            var damage = 1.0F;
            var blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof GrillBlockEntity grillBlockEntity) {
                var block = grillBlockEntity.getCampfireData().toBlockState().getBlock();
                if (block instanceof CampfireBlock campfireBlock) {
                    damage = (float) campfireBlock.fireDamage;
                }
            }
            pEntity.hurt(DamageSource.HOT_FLOOR, damage);
        }
        super.stepOn(pLevel, pPos, pState, pEntity);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (!pState.is(pNewState.getBlock())) {
            var blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof GrillBlockEntity grillBlockEntity) {
                Containers.dropContents(pLevel, pPos, new RecipeWrapper(grillBlockEntity.getInventory()));
                var campfireState = grillBlockEntity.getCampfireData().toBlockState();
                if (isCampfire(campfireState)) {
                    if (pNewState.isAir() || pNewState.getFluidState().getType() == Fluids.WATER) {
                        if (campfireState.hasProperty(BlockStateProperties.SIGNAL_FIRE)) {
                            campfireState = campfireState.setValue(BlockStateProperties.SIGNAL_FIRE, pLevel.getBlockState(pPos.below()).is(Blocks.HAY_BLOCK));
                        }
                        if (campfireState.hasProperty(BlockStateProperties.WATERLOGGED)) {
                            campfireState = campfireState.setValue(BlockStateProperties.WATERLOGGED, pNewState.getFluidState().getType() == Fluids.WATER);
                        }
                        pNewState = campfireState;
                        pLevel.setBlockAndUpdate(pPos, pNewState);
                    } else {
                        Containers.dropItemStack(pLevel, pPos.getX(), pPos.getY(), pPos.getZ(), campfireState.getBlock().asItem().getDefaultInstance());
                    }
                }
            }
            super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
        }
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(LIT, false).setValue(WATERLOGGED, pContext.getLevel().getFluidState(pContext.getClickedPos()).getType() == Fluids.WATER).setValue(FACING, pContext.getHorizontalDirection());
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState updateShape(BlockState pState, Direction pDirection, BlockState pNeighborState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pNeighborPos) {
        if (pState.getValue(WATERLOGGED)) {
            pLevel.scheduleTick(pCurrentPos, Fluids.WATER, Fluids.WATER.getTickDelay(pLevel));
        }
        return super.updateShape(pState, pDirection, pNeighborState, pLevel, pCurrentPos, pNeighborPos);
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return this.getShapeWithCampfire(COLLISION_SHAPE, pState, pLevel, pPos, pContext);
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        if (pLevel.getBlockEntity(pPos) instanceof GrillBlockEntity grillBlockEntity && grillBlockEntity.getCampfireData().toBlockState().isAir()) {
            if (pContext instanceof EntityCollisionContext entityCollisionContext) {
                if (isCampfire(entityCollisionContext.heldItem)) {
                    return Shapes.block();
                }
            } else {
                // this code block will not be invoked in theory, keep it just in case
                if (CAMPFIRE_ITEMS.get().stream().anyMatch(pContext::isHoldingItem)) {
                    return Shapes.block();
                }
            }
        }
        return this.getShapeWithCampfire(OUTLINE_SHAPE, pState, pLevel, pPos, pContext);
    }

    @SuppressWarnings("deprecation")
    private VoxelShape getShapeWithCampfire(VoxelShape pBaseShape, BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        if (pLevel.getBlockEntity(pPos) instanceof GrillBlockEntity grillBlockEntity) {
            var campfireState = grillBlockEntity.getCampfireData().toBlockState();
            if (isCampfire(campfireState)) {
                return Shapes.or(pBaseShape, campfireState.getBlock().getShape(pState, pLevel, pPos, pContext));
            }
        }
        return pBaseShape;
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getInteractionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        return COLLISION_SHAPE;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean canBeReplaced(BlockState pState, BlockPlaceContext pUseContext) {
        return !pUseContext.isSecondaryUseActive() && isCampfire(pUseContext.getItemInHand()) || super.canBeReplaced(pState, pUseContext);
    }

    public static boolean isCampfire(BlockState pState) {
        return pState.is(BlockTags.CAMPFIRES) && pState.hasProperty(BlockStateProperties.LIT);
    }

    public static boolean isCampfire(ItemStack pStack) {
        return pStack.getItem() instanceof BlockItem blockItem && isCampfire(blockItem.getBlock().defaultBlockState());
    }

    @Override
    @SuppressWarnings("deprecation")
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, Random pRandom) {
        if (pState.getValue(LIT)) {
            if (pRandom.nextInt(10) == 0) {
                pLevel.playLocalSound((double) pPos.getX() + 0.5D, (double) pPos.getY() + 0.5D, (double) pPos.getZ() + 0.5D, SoundEvents.CAMPFIRE_CRACKLE, SoundSource.BLOCKS, 0.5F + pRandom.nextFloat(), pRandom.nextFloat() * 0.7F + 0.6F, false);
            }

//            if (this.spawnParticles && pRand.nextInt(5) == 0) {
//                for(int i = 0; i < pRand.nextInt(1) + 1; ++i) {
//                    pLevel.addParticle(ParticleTypes.LAVA, (double)pPos.getX() + 0.5D, (double)pPos.getY() + 0.5D, (double)pPos.getZ() + 0.5D, (double)(pRand.nextFloat() / 2.0F), 5.0E-5D, (double)(pRand.nextFloat() / 2.0F));
//                }
//            }
        }
    }

    public static void dowse(@Nullable Entity pEntity, LevelAccessor pLevel, BlockPos pPos, BlockState pState) {
        if (pLevel.isClientSide()) {
            for (int i = 0; i < 20; i++) {
                CampfireBlock.makeParticles((Level) pLevel, pPos, false, true);
            }
        }
        var blockEntity = pLevel.getBlockEntity(pPos);
        if (blockEntity instanceof GrillBlockEntity grillBlockEntity) {
            var newCampfireData = grillBlockEntity.getCampfireData().copy();
            newCampfireData.lit = false;
            grillBlockEntity.setCampfireData(newCampfireData);
        }
        pLevel.gameEvent(pEntity, GameEvent.BLOCK_CHANGE, pPos);
    }

    @Override
    public boolean placeLiquid(LevelAccessor pLevel, BlockPos pPos, BlockState pState, FluidState pFluidState) {
        if (!pState.getValue(WATERLOGGED) && pFluidState.getType() == Fluids.WATER) {
            if (pState.getValue(LIT)) {
                if (!pLevel.isClientSide()) {
                    pLevel.playSound(null, pPos, SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.BLOCKS, 1.0F, 1.0F);
                }
                dowse(null, pLevel, pPos, pState);
            }
            pLevel.setBlock(pPos, pState.setValue(WATERLOGGED, true).setValue(LIT, false), Block.UPDATE_ALL);
            pLevel.scheduleTick(pPos, pFluidState.getType(), pFluidState.getType().getTickDelay(pLevel));
            return true;
        } else {
            return false;
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onProjectileHit(Level pLevel, BlockState pState, BlockHitResult pHit, Projectile pProjectile) {
        var pos = pHit.getBlockPos();
        var blockEntity = pLevel.getBlockEntity(pos);
        if (blockEntity instanceof GrillBlockEntity grillBlockEntity) {
            var campfireData = grillBlockEntity.getCampfireData();
            if (!pLevel.isClientSide() && pProjectile.isOnFire() && pProjectile.mayInteract(pLevel, pos) && !pState.getValue(WATERLOGGED) && isCampfire(campfireData.toBlockState()) && !campfireData.lit) {
                var newCampfireData = campfireData.copy();
                newCampfireData.lit = true;
                grillBlockEntity.setCampfireData(newCampfireData);
                pLevel.setBlockAndUpdate(pos, pState.setValue(LIT, true));
            }
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public FluidState getFluidState(BlockState pState) {
        return pState.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(pState);
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState pState, Rotation pRot) {
        return pState.setValue(FACING, pRot.rotate(pState.getValue(FACING)));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(LIT, WATERLOGGED, FACING);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new GrillBlockEntity(pPos, pState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return pLevel.isClientSide() ? null : createTickerHelper(pBlockEntityType, SimpleBBQRegistry.GRILL_BLOCK_ENTITY.get(), GrillBlockEntity::serverTick);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isPathfindable(BlockState pState, BlockGetter pLevel, BlockPos pPos, PathComputationType pType) {
        return false;
    }
}
