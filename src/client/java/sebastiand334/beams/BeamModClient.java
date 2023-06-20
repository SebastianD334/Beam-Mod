package sebastiand334.beams;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
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

public class BeamModClient implements ClientModInitializer {
    @SuppressWarnings("deprecation")
    public static final SpriteIdentifier BEAM_TEXTURE = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, new Identifier("beam-mod", "entity/beam_preview"));

    private static final RenderLayer PREVIEW_LAYER = new PreviewLayer();

    @Override
    public void onInitializeClient() {
        WorldRenderEvents.AFTER_TRANSLUCENT.register(this::renderBeamPreview);
    }

    private void renderBeamPreview(WorldRenderContext context) {
        var start = BeamPlacement.getBeamStart();
        if (start == null) return;

        var mc = MinecraftClient.getInstance();
        assert mc.player != null;
        var placement = BeamPlacement.target(mc.player);

        var matrices = context.matrixStack();
        var camera = context.gameRenderer().getCamera().getPos();
        matrices.push();
        matrices.translate(-camera.x, -camera.y, -camera.z);

        matrices.push();
        matrices.translate(start.x, start.y, start.z);
        var consumers = context.consumers();
        assert consumers != null;
        mc.getItemRenderer().renderItem(new ItemStack(BeamMod.ROASTED_COCOA_BEAMS), ModelTransformationMode.GROUND, 255, 0, context.matrixStack(), consumers, mc.world, 0);
        matrices.pop();

        var corner = placement.corner1().toVector3f().mul(16f);
        var shape = placement.getShape().toVector3f().mul(16f);
        var beamParts = ModelPartBuilder.create().cuboid(
            corner.x, corner.y, corner.z,
            shape.x, shape.y, shape.z
        ).build();
        var buffer = consumers.getBuffer(PREVIEW_LAYER);
        for (var part : beamParts) {
            var cuboid = part.createCuboid(1, 1);
            cuboid.renderCuboid(matrices.peek(), buffer, 255, 0, 1f, 0.5f, 0f, 0.25f);
        }

        matrices.pop();
    }

    private static class PreviewLayer extends RenderLayer {
        private static final ImmutableList<RenderPhase> phases = ImmutableList.of(COLOR_PROGRAM, ALWAYS_DEPTH_TEST, DISABLE_CULLING, TRANSLUCENT_TRANSPARENCY);

        public PreviewLayer() {
            super(
                "beam_preview",
                VertexFormats.POSITION_COLOR,
                VertexFormat.DrawMode.QUADS,
                131072,
                false,
                true,
                () -> phases.forEach(RenderPhase::startDrawing),
                () -> phases.forEach(RenderPhase::endDrawing)
            );
        }
    }
}
