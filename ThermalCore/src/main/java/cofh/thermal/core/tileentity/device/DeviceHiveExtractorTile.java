package cofh.thermal.core.tileentity.device;

import cofh.core.fluid.FluidStorageCoFH;
import cofh.core.inventory.ItemStorageCoFH;
import cofh.core.network.packet.client.TileControlPacket;
import cofh.thermal.core.inventory.container.device.DeviceHiveExtractorContainer;
import cofh.thermal.core.tileentity.ThermalTileBase;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.BeehiveTileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nullable;

import static cofh.core.util.StorageGroup.OUTPUT;
import static cofh.core.util.constants.Constants.TANK_MEDIUM;
import static cofh.core.util.helpers.ItemHelper.cloneStack;
import static cofh.core.util.references.CoreReferences.FLUID_HONEY;
import static cofh.thermal.core.common.ThermalConfig.deviceAugments;
import static cofh.thermal.core.init.TCoreReferences.DEVICE_HIVE_EXTRACTOR_TILE;

public class DeviceHiveExtractorTile extends ThermalTileBase {

    private static final int COMB_AMOUNT = 2;
    private static final int HONEY_AMOUNT = 250;

    protected ItemStorageCoFH outputSlot = new ItemStorageCoFH();
    protected FluidStorageCoFH outputTank = new FluidStorageCoFH(TANK_MEDIUM);

    public DeviceHiveExtractorTile() {

        super(DEVICE_HIVE_EXTRACTOR_TILE);

        inventory.addSlot(outputSlot, OUTPUT);

        tankInv.addTank(outputTank, OUTPUT);

        addAugmentSlots(deviceAugments);
        initHandlers();
    }

    protected void extractProducts(BlockPos above) {

        if (world == null) {
            return;
        }
        BlockState hive = world.getBlockState(above);
        Block hiveBlock = hive.getBlock();
        if (hiveBlock instanceof BeehiveBlock && BeehiveTileEntity.getHoneyLevel(hive) >= 5) {
            world.setBlockState(above, hive.with(BeehiveBlock.HONEY_LEVEL, 0), 3);
            if (outputSlot.isEmpty()) {
                outputSlot.setItemStack(cloneStack(Items.HONEYCOMB, COMB_AMOUNT));
            } else {
                outputSlot.modify(Math.min(COMB_AMOUNT, outputSlot.getSpace()));
            }
            if (outputTank.isEmpty()) {
                outputTank.setFluidStack(new FluidStack(FLUID_HONEY, HONEY_AMOUNT));
            } else {
                outputTank.modify(Math.min(HONEY_AMOUNT, outputTank.getSpace()));
            }
        }
    }

    protected void updateActiveState() {

        boolean curActive = isActive;
        isActive = redstoneControl().getState() && world.getBlockState(pos.up()).hasProperty(BeehiveBlock.HONEY_LEVEL);
        updateActiveState(curActive);
    }

    @Override
    public void neighborChanged(Block blockIn, BlockPos fromPos) {

        super.neighborChanged(blockIn, fromPos);

        if (fromPos.equals(pos.up())) {
            if (redstoneControl.getState()) {
                extractProducts(pos.up());
            }
        }
        updateActiveState();
    }

    @Override
    public boolean onActivatedDelegate(World world, BlockPos pos, BlockState state, PlayerEntity player, Hand hand, BlockRayTraceResult result) {

        ItemStack stack = player.getHeldItem(hand);
        if (stack.getItem() == Items.GLASS_BOTTLE && outputTank.getAmount() >= HONEY_AMOUNT) {
            outputTank.drain(HONEY_AMOUNT, IFluidHandler.FluidAction.EXECUTE);
            stack.shrink(1);
            world.playSound(player, player.getPosX(), player.getPosY(), player.getPosZ(), SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.NEUTRAL, 1.0F, 1.0F);
            if (stack.isEmpty()) {
                player.setHeldItem(hand, new ItemStack(Items.HONEY_BOTTLE));
            } else if (!player.inventory.addItemStackToInventory(new ItemStack(Items.HONEY_BOTTLE))) {
                player.dropItem(new ItemStack(Items.HONEY_BOTTLE), false);
            }
            return true;
        }
        return super.onActivatedDelegate(world, pos, state, player, hand, result);
    }

    @Override
    public void onPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack
            stack) {

        super.onPlacedBy(worldIn, pos, state, placer, stack);
        if (redstoneControl.getState()) {
            extractProducts(pos.up());
        }
        updateActiveState();
    }

    @Nullable
    @Override
    public Container createMenu(int i, PlayerInventory inventory, PlayerEntity player) {

        return new DeviceHiveExtractorContainer(i, world, pos, inventory, player);
    }

    @Override
    public void handleControlPacket(PacketBuffer buffer) {

        super.handleControlPacket(buffer);
        if (redstoneControl.getState()) {
            extractProducts(pos.up());
        }
    }

    @Override
    public void handleStatePacket(PacketBuffer buffer) {

        super.handleStatePacket(buffer);
        if (redstoneControl.getState()) {
            extractProducts(pos.up());
        }
    }

    // region ITileCallback
    @Override
    public void onControlUpdate() {

        updateActiveState();
        TileControlPacket.sendToClient(this);
    }
    // endregion
}
