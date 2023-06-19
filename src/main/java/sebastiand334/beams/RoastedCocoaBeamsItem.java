package sebastiand334.beams;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.*;
import net.minecraft.util.ActionResult;

public class RoastedCocoaBeamsItem extends Item {
	public RoastedCocoaBeamsItem() {
		super(new FabricItemSettings());
	}
	
	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		var placement = new ItemPlacementContext(context);
		var world = placement.getWorld();
		BeamMod.beamStart = placement.getHitPos();
		System.out.println(placement.getHitPos());
		return ActionResult.success(true);
	}
}
