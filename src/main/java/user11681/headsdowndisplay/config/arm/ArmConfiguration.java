package user11681.headsdowndisplay.config.arm;

import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry.Gui.EnumHandler;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry.Gui.Tooltip;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import user11681.headsdowndisplay.config.arm.entry.HideCondition;
import user11681.headsdowndisplay.config.arm.entry.HideType;

public class ArmConfiguration {
    @EnumHandler(option = EnumHandler.EnumDisplayOption.BUTTON)
    @Tooltip
    public HideCondition mainHandHideCondition = HideCondition.ALWAYS;

    @EnumHandler(option = EnumHandler.EnumDisplayOption.BUTTON)
    @Tooltip
    public HideType mainHandHideType = HideType.ARM;

    @EnumHandler(option = EnumHandler.EnumDisplayOption.BUTTON)
    @Tooltip
    public HideCondition offHandHideCondition = HideCondition.ALWAYS;

    public boolean shouldHide(final float tickDelta, final PlayerEntity player, final Hand hand) {
        final HideCondition condition = hand == Hand.MAIN_HAND ? this.mainHandHideCondition : this.offHandHideCondition;

        if (player.getHandSwingProgress(tickDelta) == 0
            && condition == HideCondition.REST
            || condition == HideCondition.ALWAYS
            || condition == HideCondition.SWING
            && player.getHandSwingProgress(tickDelta) != 0) {
            if (hand == Hand.OFF_HAND) {
                return true;
            }

            if (player.getMainHandStack().isEmpty()) {
                return this.mainHandHideType != HideType.ITEM;
            }

            return this.mainHandHideType.compareTo(HideType.ITEM) >= 0;
        }

        return false;
    }
}
