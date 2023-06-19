package sebastiand334.beams;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.render.*;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

public class BeamModClient implements ClientModInitializer {
	@SuppressWarnings("deprecation")
	public static final SpriteIdentifier BEAM_TEXTURE = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, new Identifier("beam-mod", "entity/beam_preview"));
	
	private static final RenderLayer layer = RenderLayer.of(
		"beam_preview",
		VertexFormats.POSITION_COLOR,
		VertexFormat.DrawMode.QUADS,
		131072,
		RenderLayer.MultiPhaseParameters.builder()
			.program(RenderPhase.COLOR_PROGRAM)
			.transparency(RenderPhase.TRANSLUCENT_TRANSPARENCY)
			.cull(RenderPhase.DISABLE_CULLING)
			.build(false)
	);
	
	@Override
	public void onInitializeClient() {
		WorldRenderEvents.BEFORE_ENTITIES.register(this::renderBeamPreview);
	}
	
	private void renderBeamPreview(WorldRenderContext context) {
		var start = BeamMod.beamStart;
		if (start == null) return;
		
		var mc = MinecraftClient.getInstance();
		
		assert mc.player != null;
		var pos = mc.player.getClientCameraPosVec(context.tickDelta());
		var dir = mc.player.getRotationVec(context.tickDelta());
		var distance = (start.y - pos.y) / dir.y;
		var target = pos.add(dir.multiply(distance));
		var delta = target.subtract(start);
		Vector3f beam;
		if (Math.abs(delta.x) > Math.abs(delta.z)) {
			beam = new Vector3f((float) delta.x, 0f, 0f);
		} else {
			beam = new Vector3f(0f, 0f, (float) delta.z);
		}
		
		var matrices = context.matrixStack();
		var camera = context.gameRenderer().getCamera().getPos();
		matrices.push();
		matrices.translate(-camera.x, -camera.y, -camera.z);
		matrices.translate(start.x, start.y, start.z);
		var consumers = context.consumers();
		assert consumers != null;
		mc.getItemRenderer().renderItem(new ItemStack(BeamMod.ROASTED_COCOA_BEAMS), ModelTransformationMode.GROUND, 255, 0, context.matrixStack(), consumers, mc.world, 0);
		var beamParts = ModelPartBuilder.create().cuboid(
			0, 0, 0,
			4 + 16 * beam.x, 4 + 16 * beam.y, 4 + 16 * beam.z
		).build();
		var buffer = BEAM_TEXTURE.getVertexConsumer(consumers, RenderLayer::getEntityTranslucentCull);
		matrices.push();
		for (var part : beamParts) {
			var cuboid = part.createCuboid(16, 16);
			cuboid.renderCuboid(matrices.peek(), buffer, 255, 0, 1f, 1f, 1f, 0.5f);
		}
		matrices.pop();
		
		matrices.translate(beam.x, beam.y, beam.z);
		mc.getItemRenderer().renderItem(new ItemStack(BeamMod.ROASTED_COCOA_BEAMS), ModelTransformationMode.GROUND, 255, 0, context.matrixStack(), consumers, mc.world, 0);
		matrices.pop();
	}
}
