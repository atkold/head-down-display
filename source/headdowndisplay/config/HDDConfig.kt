package headdowndisplay.config

import me.shedaniel.autoconfig.ConfigData
import me.shedaniel.autoconfig.annotation.Config
import headdowndisplay.HDD
import me.shedaniel.autoconfig.annotation.Config.Gui.Background
import me.shedaniel.autoconfig.annotation.ConfigEntry.Category
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.TransitiveObject
import headdowndisplay.config.hotbar.Hotbar
import headdowndisplay.config.arm.ArmConfiguration

@Config(name = HDD.ID)
@Background("textures/block/andesite.png")
class HDDConfig : ConfigData {
    @JvmField
    @TransitiveObject
    @Category("default")
    var hotbar = Hotbar()

    @JvmField
    @TransitiveObject
    @Category("arm")
    var arm = ArmConfiguration()

    override fun validatePostLoad() {
        hotbar.fadeEnd = hotbar.fadeDelay + hotbar.fadeDuration
    }

    companion object {
        @Transient
        lateinit var instance: HDDConfig
    }
}
