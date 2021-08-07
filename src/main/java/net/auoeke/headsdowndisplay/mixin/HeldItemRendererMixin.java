package net.auoeke.headsdowndisplay.mixin;

import net.auoeke.headsdowndisplay.config.HDDConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(HeldItemRenderer.class)
abstract class HeldItemRendererMixin {
    @Inject(method = "renderFirstPersonItem",
            cancellable = true,
            at = @At("HEAD"))
    private void disableArm(final AbstractClientPlayerEntity player,
                            final float tickDelta,
                            final float pitch,
                            final Hand hand,
                            final float swingProgress,
                            final ItemStack item,
                            final float equipProgress,
                            final MatrixStack matrices,
                            final VertexConsumerProvider vertexConsumers,
                            final int light,
                            final CallbackInfo info) {
        if (HDDConfig.instance.arm.shouldHide(tickDelta, player, hand)) {
            info.cancel();
        }
    }
}
