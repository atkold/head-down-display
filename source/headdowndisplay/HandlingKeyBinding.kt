package headdowndisplay

import net.minecraft.client.option.KeyBinding
import java.lang.Runnable

class HandlingKeyBinding(translationKey: String, private val handler: Runnable) : KeyBinding(translationKey, -1, "key.category.head-down-display") {
    override fun setPressed(pressed: Boolean) {
        if (pressed && !this.isPressed) {
            handler.run()
        }

        super.setPressed(pressed)
    }
}
