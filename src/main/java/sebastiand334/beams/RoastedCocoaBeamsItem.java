package sebastiand334.beams;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class RoastedCocoaBeamsItem extends Item {
    public RoastedCocoaBeamsItem() {
        super(new FabricItemSettings());
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        var placement = new ItemPlacementContext(context);
        BeamPlacement.startPlacingBeam(placement.getHitPos());
        return ActionResult.success(true);
    }

    @Override
    public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
        return false;
    }
}
