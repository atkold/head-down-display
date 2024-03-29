package headdowndisplay.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import headdowndisplay.HDD;
import headdowndisplay.config.HDDConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
abstract class InGameHudMixin {
    @Shadow @Final private final MinecraftClient client = MinecraftClient.getInstance();
    @Shadow private int scaledHeight;

    @Unique private static final KeyBinding[] hotbarKeys = MinecraftClient.getInstance().options.keysHotbar;
    @Unique private static final int minY = 0;
    @Unique private static int selectedSlot = -1;
    @Unique private static ItemStack selectedItem;
    @Unique private static int direction;
    @Unique private static int ticksSinceTop;
    @Unique private static int maxY;
    @Unique private static float y;
    @Unique private static float previousY;
    @Unique private static float frameY;
    @Unique private static int pressTicks;

    @Unique
    private static void translate(MatrixStack matrices) {
        if (HDDConfig.instance.hotbar.lower) {
            matrices.push();
            matrices.translate(0, frameY, 0);
        }
    }

    @Unique
    private static void pop(MatrixStack matrices) {
        if (HDDConfig.instance.hotbar.lower) {
            matrices.pop();
        }
    }

    @Inject(method = "tick(Z)V", at = @At("RETURN"))
    public void tick(CallbackInfo info) {
        maxY = switch (HDDConfig.instance.hotbar.hideLevel) {
            case ALL -> this.scaledHeight;
            case CUSTOM -> HDDConfig.instance.hotbar.maxY;
            default -> HDDConfig.instance.hotbar.hideLevel.maxY;
        };

        var player = this.client.player;
        var reveal = false;

        if (player != null) {
            var triggers = HDDConfig.instance.hotbar.trigger;

            if (triggers.slot) {
                var inventory = player.getInventory();

                if (reveal = inventory != null && selectedSlot != inventory.selectedSlot) {
                    selectedSlot = inventory.selectedSlot;
                }
            }

            if (!reveal && triggers.item) {
                var mainHandStack = player.getMainHandStack();

                if (reveal = selectedItem != mainHandStack) {
                    selectedItem = mainHandStack;
                }
            }

            if (!reveal && triggers.key) {
                for (var key : hotbarKeys) {
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
    public void computeFramePosition(MatrixStack matrices, float tickDelta, CallbackInfo info) {
        frameY = previousY + (y - previousY) * tickDelta;
    }

    @ModifyArg(method = "renderHotbar",
               index = 3,
               at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderColor(FFFF)V"))
    protected float fadeHotbar(float alpha) {
        if (HDDConfig.instance.hotbar.fade && direction >= 0) {
            return MathHelper.clamp((HDDConfig.instance.hotbar.fadeEnd - ticksSinceTop) / (float) HDDConfig.instance.hotbar.fadeDuration, 0, 1);
        }

        return alpha;
    }

    @Inject(method = "renderHotbar", at = @At("HEAD"))
    protected void lowerHotbar(float tickDelta, MatrixStack matrices, CallbackInfo info) {
        translate(matrices);
    }

    @Inject(method = "renderHotbar", at = @At(value = "FIELD", target = "Lnet/minecraft/client/option/GameOptions;attackIndicator:Lnet/minecraft/client/option/AttackIndicator;"))
    protected void cleanUpHotbar(float tickDelta, MatrixStack matrices, CallbackInfo info) {
        pop(matrices);
    }

    @Inject(method = "renderHotbarItem", at = @At("HEAD"))
    private void lowerItem(int x, int y, float tickDelta, PlayerEntity player, ItemStack stack, int seed, CallbackInfo ci) {
        if (HDDConfig.instance.hotbar.lower) {
            var matrixes = RenderSystem.getModelViewStack();
            matrixes.push();
            matrixes.translate(0, frameY, 0);
        }
    }

    @Inject(method = "renderHotbarItem", at = @At("RETURN"))
    private void cleanUpItem(int x, int y, float tickDelta, PlayerEntity player, ItemStack stack, int seed, CallbackInfo ci) {
        if (HDDConfig.instance.hotbar.lower) {
            RenderSystem.getModelViewStack().pop();
            RenderSystem.applyModelViewMatrix();
        }
    }

    @Inject(method = "renderStatusBars", at = @At("HEAD"))
    private void lowerStatusBars(MatrixStack matrices, CallbackInfo ci) {
        translate(matrices);
    }

    @Inject(method = "renderStatusBars", at = @At("RETURN"))
    private void cleanUpStatusBars(MatrixStack matrices, CallbackInfo ci) {
        pop(matrices);
    }

    @Inject(method = "renderMountHealth", at = @At("HEAD"))
    private void lowerMountHealth(MatrixStack matrices, CallbackInfo info) {
        translate(matrices);
    }

    @Inject(method = "renderMountHealth", at = @At("RETURN"))
    private void cleanUpMountHealth(MatrixStack matrices, CallbackInfo info) {
        pop(matrices);
    }

    @Inject(method = "renderMountJumpBar", at = @At("HEAD"))
    public void lowerMountJumpBar(MatrixStack matrices, int x, CallbackInfo info) {
        translate(matrices);
    }

    @Inject(method = "renderMountJumpBar", at = @At("RETURN"))
    public void cleanUpJumpBar(MatrixStack matrices, int x, CallbackInfo info) {
        pop(matrices);
    }

    @Inject(method = "renderExperienceBar", at = @At("HEAD"))
    public void lowerExperienceBar(MatrixStack matrices, int x, CallbackInfo info) {
        translate(matrices);
    }

    @Inject(method = "renderExperienceBar", at = @At("RETURN"))
    public void cleanUpExperienceBar(MatrixStack matrices, int x, CallbackInfo info) {
        pop(matrices);
    }

    @Inject(method = "renderHeldItemTooltip", at = @At("HEAD"))
    private void lowerTooltip(MatrixStack matrices, CallbackInfo info) {
        translate(matrices);
    }

    @Inject(method = "renderHeldItemTooltip", at = @At("RETURN"))
    private void cleanUpTooltip(MatrixStack matrices, CallbackInfo info) {
        pop(matrices);
    }
}
