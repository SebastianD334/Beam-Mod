package sebastiand334.beams;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import org.joml.Vector3f;

public class BeamModClient implements ClientModInitializer {
    @SuppressWarnings("deprecation")
    public static final SpriteIdentifier BEAM_TEXTURE = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, new Identifier("beam-mod", "entity/beam_preview"));

    private static final RenderLayer PREVIEW_LAYER_1 = new PreviewLayer("beam_preview_1", true);
    private static final RenderLayer PREVIEW_LAYER_2 = new PreviewLayer("beam_preview_2", false);

    @Override
    public void onInitializeClient() {
        WorldRenderEvents.AFTER_TRANSLUCENT.register(this::renderBeamPreview);
    }

    public static void onLeftClick() {
        if (isHoldingBeamItem()) {
            BeamPlacement.stopPlacingBeam();
        }
    }

    public static boolean isHoldingBeamItem() {
        var player = MinecraftClient.getInstance().player;
        if (player == null) return false;
        return player.getMainHandStack().getItem() == BeamMod.ROASTED_COCOA_BEAMS;
    }

    private void renderBeamPreview(WorldRenderContext context) {
        if (!isHoldingBeamItem()) return;
        
        var mc = MinecraftClient.getInstance();
        assert mc.player != null;

        var placement = BeamPlacement.target(mc.player);

        var matrices = context.matrixStack();
        var camera = context.gameRenderer().getCamera().getPos();
        matrices.push();
        matrices.translate(-camera.x, -camera.y, -camera.z);

        var consumers = context.consumers();
        assert consumers != null;

        var corner1 = placement.corner1().toVector3f().mul(16f);
        var corner2 = placement.corner2().toVector3f().mul(16f);
        var min = corner1.min(corner2, new Vector3f());
        var max = corner1.max(corner2); // avoid allocation by reusing corner1

        var epsilon = 1e-3f; // avoid z-fighting
        var beamParts = ModelPartBuilder.create().cuboid(
            min.x - epsilon,
            min.y - epsilon,
            min.z - epsilon,
            max.x - min.x + 2 * epsilon,
            max.y - min.y + 2 * epsilon,
            max.z - min.z + 2 * epsilon
        ).build();
        assert beamParts.size() == 1;
        var cuboid = beamParts.get(0).createCuboid(1, 1);

        // draw faintly, ignoring depth
        cuboid.renderCuboid(
            matrices.peek(),
            consumers.getBuffer(PREVIEW_LAYER_1),
            255, 0,
            1f, 0.4f, 0.2f, 0.1f
        );
        // draw more brightly, respecting depth
        cuboid.renderCuboid(
            matrices.peek(),
            consumers.getBuffer(PREVIEW_LAYER_2),
            255, 0,
            1f, 0.6f, 0f, 0.2f
        );

        matrices.pop();
    }

    private static class PreviewLayer extends RenderLayer {
        private static final ImmutableList<RenderPhase> phases = ImmutableList.of(COLOR_PROGRAM, ALWAYS_DEPTH_TEST, DISABLE_CULLING, TRANSLUCENT_TRANSPARENCY);

        private final boolean ignoresDepth;

        public PreviewLayer(String name, boolean ignoresDepth) {
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
            this.ignoresDepth = ignoresDepth;
        }

        @Override
        public void startDrawing() {
            super.startDrawing();
            if (this.ignoresDepth) {
                RenderSystem.disableDepthTest();
            }
        }
    }
}
