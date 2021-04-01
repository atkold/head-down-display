package user11681.headsdowndisplay;

import net.minecraft.client.options.KeyBinding;

public class HandlingKeyBinding extends KeyBinding {
    private final Runnable handler;

    public HandlingKeyBinding(String translationKey, Runnable handler) {
        super(translationKey, -1, "key.category.headsdowndisplay");

        this.handler = handler;
    }

    @Override
    public void setPressed(boolean pressed) {
        if (pressed && !this.isPressed()) {
            this.handler.run();
        }

        super.setPressed(pressed);
    }
}
