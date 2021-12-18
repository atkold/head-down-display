package headdowndisplay.config.hotbar

import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.*
import headdowndisplay.config.hotbar.entry.HideLevel
import headdowndisplay.config.hotbar.entry.Triggers

class Hotbar {
    @JvmField
    @Transient
    var fadeEnd: Int = 0

    @JvmField
    var fade: Boolean = false

    @JvmField
    var lower: Boolean = true

    @JvmField
    var revealAutomatically: Boolean = true

    @JvmField
    var hideAutomatically: Boolean = true

    @JvmField
    @EnumHandler(option = EnumHandler.EnumDisplayOption.BUTTON)
    @Tooltip
    var hideLevel: HideLevel = HideLevel.HOTBAR

    @JvmField
    @Tooltip
    var maxY: Int = 23

    @JvmField
    @Tooltip
    var lowerDelay: Int = 30

    @JvmField
    @Tooltip
    var fadeDelay: Int = 28

    @JvmField
    @Tooltip
    var fadeDuration: Int = 5

    @JvmField
    @Tooltip
    var speed: Float = 2F

    @JvmField
    @Tooltip
    var acceleration: Float = 0.2F

    @JvmField
    @CollapsibleObject
    @Tooltip
    var trigger = Triggers()
}
