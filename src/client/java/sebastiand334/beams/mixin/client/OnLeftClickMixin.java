package sebastiand334.beams.mixin.client;

import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import sebastiand334.beams.BeamModClient;

@Mixin(MinecraftClient.class)
public class OnLeftClickMixin {
    @Inject(at = @At("HEAD"), method = "doAttack")
    private void doAttack(CallbackInfoReturnable<Boolean> cir) {
        BeamModClient.onLeftClick();
    }
}
