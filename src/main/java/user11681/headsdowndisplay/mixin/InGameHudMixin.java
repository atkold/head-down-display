package user11681.headsdowndisplay.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import user11681.headsdowndisplay.HDD;
import user11681.headsdowndisplay.config.HDDConfig;
import user11681.headsdowndisplay.config.hotbar.entry.HideLevel;
import user11681.headsdowndisplay.config.hotbar.entry.Triggers;

@Mixin(InGameHud.class)
abstract class InGameHudMixin {
    @Unique
    private static final KeyBinding[] hotbarKeys = MinecraftClient.getInstance().options.keysHotbar;

    @Final
    @Shadow
    private final MinecraftClient client = MinecraftClient.getInstance();

    @Shadow
    private int scaledHeight;

    @Unique
    private static int selectedSlot = -1;

    @Unique
    private static ItemStack selectedItem;

    @Unique
    private static int direction;

    @Unique
    private static int ticksSinceTop;

    @Unique
    private static final int minY = 0;

    @Unique
    private static int maxY;

    @Unique
    private static float y;

    @Unique
    private static float previousY;

    @Unique
    private static float frameY;

    @Unique
    private static int pressTicks;

    @Unique
    private static void translate(final MatrixStack matrices) {
        if (HDDConfig.instance.hotbar.lower) {
            matrices.push();
            matrices.translate(0, frameY, 0);
        }
    }

    @Unique
    private static void pop(final MatrixStack matrices) {
        if (HDDConfig.instance.hotbar.lower) {
            matrices.pop();
        }
    }

    @Inject(method = "tick", at = @At("RETURN"))
    public void tick(final CallbackInfo info) {
        if (HDDConfig.instance.hotbar.hideLevel == HideLevel.ALL) {
            maxY = this.scaledHeight;
        } else if (HDDConfig.instance.hotbar.hideLevel == HideLevel.CUSTOM) {
            maxY = HDDConfig.instance.hotbar.maxY;
        } else {
            maxY = HDDConfig.instance.hotbar.hideLevel.maxY;
        }

        final ClientPlayerEntity player = this.client.player;
        boolean reveal = false;

        if (player != null) {
            final Triggers triggers = HDDConfig.instance.hotbar.trigger;

            if (triggers.slot) {
                final PlayerInventory inventory = player.inventory;

                if (reveal = inventory != null && selectedSlot != inventory.selectedSlot) {
                    selectedSlot = inventory.selectedSlot;
                }
            }

            if (!reveal && triggers.item) {
                final ItemStack mainHandStack = player.getMainHandStack();

                if (reveal = selectedItem != mainHandStack) {
                    selectedItem = mainHandStack;
                }
            }

            if (!reveal && triggers.key) {
                for (final KeyBinding key : hotbarKeys) {
                    if (key.isPressed()) {
                        reveal = true;

                        break;
                    }
                }
            }
        }

        if (reveal) {
            direction = -1;
        }

        if (y == maxY) {
            ticksSinceTop = HDDConfig.instance.hotbar.lowerDelay;
        } else if (y == minY && direction == -1) {
            ticksSinceTop = 0;
            direction = 0;
        } else if (direction >= 0) {
            ++ticksSinceTop;
        } else {
            --ticksSinceTop;
        }

        if (HDD.toggleHotbarKey.isPressed()) {
            if (++pressTicks == 1) {
                if (y == minY) {
                    direction = 1;
                    ticksSinceTop = Math.max(HDDConfig.instance.hotbar.lowerDelay, HDDConfig.instance.hotbar.fadeDelay);
                } else {
                    direction = -1;
                    ticksSinceTop = HDDConfig.instance.hotbar.fadeEnd;
                }
            }
        } else {
            pressTicks = 0;
        }

        if (ticksSinceTop < HDDConfig.instance.hotbar.lowerDelay && y > minY) {
            direction = -1;
        } else if (HDDConfig.instance.hotbar.hideAutomatically && ticksSinceTop >= HDDConfig.instance.hotbar.lowerDelay && y < maxY && direction >= 0) {
            direction = 1;
        }

        previousY = y;
        y = (float) Math.pow(y + direction * HDDConfig.instance.hotbar.speed, 1 + direction * HDDConfig.instance.hotbar.acceleration);

        if (y >= maxY) {
            y = maxY;
        } else if (y < minY || y != y) {
            y = minY;
        }
    }

    @Inject(method = "render", at = @At("HEAD"))
    public void computeFramePosition(final MatrixStack matrices, final float tickDelta, final CallbackInfo info) {
        frameY = previousY + (y - previousY) * tickDelta;
    }

    @ModifyArg(method = "renderHotbar",
               index = 3,
               at = @At(value = "INVOKE",
                        target = "Lcom/mojang/blaze3d/systems/RenderSystem;color4f(FFFF)V"))
    protected float fadeHotbar(final float alpha) {
        if (HDDConfig.instance.hotbar.fade && direction >= 0) {
            final float fadeAlpha = (HDDConfig.instance.hotbar.fadeEnd - ticksSinceTop) / (float) HDDConfig.instance.hotbar.fadeDuration;

            if (fadeAlpha < 0) {
                return 0;
            }

            if (fadeAlpha > 1) {
                return 1;
            }

            return fadeAlpha;
        }

        return alpha;
    }

    @Inject(method = "renderHotbar", at = @At("HEAD"))
    protected void lowerHotbar(final float tickDelta, final MatrixStack matrices, final CallbackInfo info) {
        translate(matrices);
    }

    @Inject(method = "renderHotbar",
            at = @At(value = "FIELD",
                     target = "Lnet/minecraft/client/options/GameOptions;attackIndicator:Lnet/minecraft/client/options/AttackIndicator;"))
    protected void cleanUpHotbar(final float tickDelta, final MatrixStack matrices, final CallbackInfo info) {
        pop(matrices);
    }

    @Inject(method = "renderHotbarItem", at = @At("HEAD"))
    private void lowerItem(final int x, final int y0, final float tickDelta, final PlayerEntity playerEntity, final ItemStack itemStack, final CallbackInfo info) {
        if (HDDConfig.instance.hotbar.lower) {
            RenderSystem.pushMatrix();
            RenderSystem.translatef(0, frameY, 0);
        }
    }

    @Inject(method = "renderHotbarItem", at = @At("RETURN"))
    private void cleanUpItem(final int x, final int y0, final float tickDelta, final PlayerEntity playerEntity, final ItemStack itemStack, final CallbackInfo info) {
        if (HDDConfig.instance.hotbar.lower) {
            RenderSystem.popMatrix();
        }
    }

    @Inject(method = "renderStatusBars", at = @At("HEAD"))
    private void lowerStatusBars(final MatrixStack matrices, final CallbackInfo ci) {
        translate(matrices);
    }

    @Inject(method = "renderStatusBars", at = @At("RETURN"))
    private void cleanUpStatusBars(final MatrixStack matrices, final CallbackInfo ci) {
        pop(matrices);
    }

    @Inject(method = "renderMountHealth", at = @At("HEAD"))
    private void lowerMountHealth(final MatrixStack matrices, final CallbackInfo info) {
        translate(matrices);
    }

    @Inject(method = "renderMountHealth", at = @At("RETURN"))
    private void cleanUpMountHealth(final MatrixStack matrices, final CallbackInfo info) {
        pop(matrices);
    }

    @Inject(method = "renderMountJumpBar", at = @At("HEAD"))
    public void lowerMountJumpBar(final MatrixStack matrices, final int x, final CallbackInfo info) {
        translate(matrices);
    }

    @Inject(method = "renderMountJumpBar", at = @At("RETURN"))
    public void cleanUpJumpBar(final MatrixStack matrices, final int x, final CallbackInfo info) {
        pop(matrices);
    }

    @Inject(method = "renderExperienceBar", at = @At("HEAD"))
    public void lowerExperienceBar(final MatrixStack matrices, final int x, final CallbackInfo info) {
        translate(matrices);
    }

    @Inject(method = "renderExperienceBar", at = @At("RETURN"))
    public void cleanUpExperienceBar(final MatrixStack matrices, final int x, final CallbackInfo info) {
        pop(matrices);
    }

    @Inject(method = "renderHeldItemTooltip", at = @At("HEAD"))
    private void lowerTooltip(final MatrixStack matrices, final CallbackInfo info) {
        translate(matrices);
    }

    @Inject(method = "renderHeldItemTooltip", at = @At("RETURN"))
    private void cleanUpTooltip(final MatrixStack matrices, final CallbackInfo info) {
        pop(matrices);
    }
}
