package headdowndisplay.config.arm

import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.EnumHandler
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.Tooltip
import headdowndisplay.config.arm.entry.HideCondition
import headdowndisplay.config.arm.entry.HideType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.Hand

class ArmConfiguration {
    @EnumHandler(option = EnumHandler.EnumDisplayOption.BUTTON)
    @Tooltip
    var mainHandHideCondition = HideCondition.ALWAYS

    @EnumHandler(option = EnumHandler.EnumDisplayOption.BUTTON)
    @Tooltip
    var mainHandHideType = HideType.ARM

    @EnumHandler(option = EnumHandler.EnumDisplayOption.BUTTON)
    @Tooltip
    var offHandHideCondition = HideCondition.ALWAYS

    fun shouldHide(tickDelta: Float, player: PlayerEntity, hand: Hand): Boolean {
        val condition = when (hand) {
            Hand.MAIN_HAND -> mainHandHideCondition
            else -> offHandHideCondition
        }

        return when {
            player.getHandSwingProgress(tickDelta) == 0F && condition == HideCondition.REST || condition == HideCondition.ALWAYS || condition == HideCondition.SWING && player.getHandSwingProgress(tickDelta) != 0F -> {
                when (hand) {
                    Hand.OFF_HAND -> true
                    else -> when {
                        player.mainHandStack.isEmpty -> {
                            mainHandHideType != HideType.ITEM
                        }
                        else -> mainHandHideType >= HideType.ITEM
                    }
                }
            }
            else -> false
        }
    }
}
