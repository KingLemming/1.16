package cofh.thermal.core.tileentity.storage;

import cofh.core.energy.EnergyStorageAdjustable;
import cofh.core.network.packet.client.TileStatePacket;
import cofh.core.util.helpers.BlockHelper;
import cofh.thermal.core.inventory.container.storage.EnergyCellContainer;
import cofh.thermal.core.tileentity.CellTileBase;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static cofh.core.client.renderer.model.ModelUtils.*;
import static cofh.thermal.core.common.ThermalConfig.storageAugments;
import static cofh.thermal.core.init.TCoreReferences.ENERGY_CELL_TILE;

public class EnergyCellTile extends CellTileBase implements ITickableTileEntity {

    public static final int BASE_CAPACITY = 1000000;
    public static final int BASE_RECV = 1000;
    public static final int BASE_SEND = 1000;

    public EnergyCellTile() {

        super(ENERGY_CELL_TILE);

        energyStorage = new EnergyStorageAdjustable(BASE_CAPACITY, BASE_RECV, BASE_SEND)
                .setTransferLimits(() -> amountInput, () -> amountOutput);

        amountInput = energyStorage.getMaxReceive();
        amountOutput = energyStorage.getMaxExtract();

        addAugmentSlots(storageAugments);
        initHandlers();
    }

    //    @Override
    //    public void neighborChanged(Block blockIn, BlockPos fromPos) {
    //
    //        super.neighborChanged(blockIn, fromPos);
    //
    //        // TODO: Handle caching of neighbor caps.
    //    }

    @Override
    public void tick() {

        if (redstoneControl.getState()) {
            transferRF();
        }
        if (timeCheck()) {
            updateTrackers(true);
        }
    }

    @Override
    public int getLightValue() {

        return levelTracker;
    }

    protected void transferRF() {

        if (amountOutput <= 0 || energyStorage.isEmpty()) {
            return;
        }
        for (int i = outputTracker; i < 6 && energyStorage.getEnergyStored() > 0; ++i) {
            if (reconfigControl.getSideConfig(i).isOutput()) {
                attemptTransferRF(Direction.byIndex(i));
            }
        }
        for (int i = 0; i < outputTracker && energyStorage.getEnergyStored() > 0; ++i) {
            if (reconfigControl.getSideConfig(i).isOutput()) {
                attemptTransferRF(Direction.byIndex(i));
            }
        }
        ++outputTracker;
        outputTracker %= 6;
    }

    protected void attemptTransferRF(Direction side) {

        TileEntity adjTile = BlockHelper.getAdjacentTileEntity(this, side);
        if (adjTile != null) {
            Direction opposite = side.getOpposite();
            int maxTransfer = Math.min(amountOutput, energyStorage.getEnergyStored());
            adjTile.getCapability(CapabilityEnergy.ENERGY, opposite)
                    .ifPresent(e -> energyStorage.modify(-e.receiveEnergy(maxTransfer, false)));
        }
    }

    @Override
    protected boolean keepEnergy() {

        return true;
    }

    @Override
    public int getMaxInput() {

        return energyStorage.getMaxReceive();
    }

    @Override
    public int getMaxOutput() {

        return energyStorage.getMaxExtract();
    }

    @Nullable
    @Override
    public Container createMenu(int i, PlayerInventory inventory, PlayerEntity player) {

        return new EnergyCellContainer(i, world, pos, inventory, player);
    }

    @Nonnull
    @Override
    public IModelData getModelData() {

        return new ModelDataMap.Builder()
                .withInitial(SIDES, reconfigControl().getRawSideConfig())
                .withInitial(FACING, reconfigControl.getFacing())
                .withInitial(LEVEL, levelTracker)
                .build();
    }

    @Override
    protected void updateTrackers(boolean send) {

        prevLight = getLightValue();

        int curScale = energyStorage.getEnergyStored() > 0 ? 1 + (int) (energyStorage.getRatio() * 14) : 0;
        if (curScale != compareTracker) {
            compareTracker = curScale;
            if (send) {
                markDirty();
            }
        }
        curScale = energyStorage.getEnergyStored() > 0 ? 1 + Math.min((int) (energyStorage.getRatio() * 8), 7) : 0;
        if (levelTracker != curScale) {
            levelTracker = curScale;
            if (send) {
                TileStatePacket.sendToClient(this);
            }
        }
    }

    // region CAPABILITIES
    @Override
    protected void updateHandlers() {

        LazyOptional<?> oldCap = energyCap;
        energyCap = LazyOptional.of(() -> energyStorage);
        oldCap.invalidate();
    }

    @Override
    protected <T> LazyOptional<T> getEnergyCapability(@Nullable Direction side) {

        if (side == null || reconfigControl.getSideConfig(side.ordinal()) != SideConfig.SIDE_NONE) {
            return super.getEnergyCapability(side);
        }
        return LazyOptional.empty();
    }
    // endregion
}