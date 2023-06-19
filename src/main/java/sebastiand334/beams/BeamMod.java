package sebastiand334.beams;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BeamMod implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("beam-mod");
	
	@Nullable
	public static Vec3d beamStart;
	
	public static final Item ROASTED_COCOA_BEAMS = new RoastedCocoaBeamsItem();
	
	@Override
	public void onInitialize() {
		Registry.register(Registries.ITEM, new Identifier("beam-mod", "roasted_cocoa_beams"), ROASTED_COCOA_BEAMS);
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS).register(entries -> entries.add(ROASTED_COCOA_BEAMS));
	}
}
