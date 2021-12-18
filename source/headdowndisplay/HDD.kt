package headdowndisplay

import com.terraformersmc.modmenu.api.ConfigScreenFactory
import com.terraformersmc.modmenu.api.ModMenuApi
import me.shedaniel.autoconfig.AutoConfig
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer
import net.fabricmc.api.ClientModInitializer
import headdowndisplay.config.HDDConfig
import net.fabricmc.fabric.impl.client.keybinding.KeyBindingRegistryImpl
import headdowndisplay.config.arm.entry.HideCondition
import net.minecraft.client.option.KeyBinding
import java.lang.reflect.Modifier

object HDD : ClientModInitializer {
    const val ID = "head-down-display"

    @JvmField
    val toggleHotbarKey = KeyBinding("key.head-down-display.toggleHotbar", -1, "key.category.head-down-display")

    @JvmField
    val toggleMainArmKey: KeyBinding = HandlingKeyBinding("key.head-down-display.toggleMain") {
        HDDConfig.instance.arm.mainHandHideCondition = when (HDDConfig.instance.arm.mainHandHideCondition) {
            HideCondition.ALWAYS -> HideCondition.NEVER
            else -> HideCondition.ALWAYS
        }
    }

    @JvmField
    val toggleSecondaryArmKey: KeyBinding = HandlingKeyBinding("key.head-down-display.toggleSecondary") {
        HDDConfig.instance.arm.offHandHideCondition = when (HDDConfig.instance.arm.offHandHideCondition) {
            HideCondition.ALWAYS -> HideCondition.NEVER
            else -> HideCondition.ALWAYS
        }
    }

    @JvmField
    val toggleArmsKey: KeyBinding = HandlingKeyBinding("key.head-down-display.toggleArms") {
        val config = HDDConfig.instance.arm
        config.offHandHideCondition = if (config.mainHandHideCondition == HideCondition.ALWAYS) HideCondition.NEVER else HideCondition.ALWAYS
        config.mainHandHideCondition = config.offHandHideCondition
    }

    override fun onInitializeClient() {
        HDDConfig.instance = AutoConfig.register(HDDConfig::class.java, ::GsonConfigSerializer).get()

        AutoConfig.getGuiRegistry(HDDConfig::class.java).registerPredicateProvider({_, _, _, _, _ -> listOf()}) {
            it.modifiers and (Modifier.STATIC or Modifier.TRANSIENT or 0x1000) != 0
        }

        KeyBindingRegistryImpl.registerKeyBinding(toggleHotbarKey)
        KeyBindingRegistryImpl.registerKeyBinding(toggleMainArmKey)
        KeyBindingRegistryImpl.registerKeyBinding(toggleSecondaryArmKey)
        KeyBindingRegistryImpl.registerKeyBinding(toggleArmsKey)
    }

    object ModMenu : ModMenuApi {
        override fun getModConfigScreenFactory(): ConfigScreenFactory<*> = ConfigScreenFactory {AutoConfig.getConfigScreen(HDDConfig::class.java, it).get()}
    }
}
