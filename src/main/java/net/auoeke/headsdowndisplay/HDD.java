package net.auoeke.headsdowndisplay;

import net.auoeke.headsdowndisplay.config.HDDConfig;
import net.auoeke.headsdowndisplay.config.arm.entry.HideCondition;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.impl.client.keybinding.KeyBindingRegistryImpl;
import net.minecraft.client.option.KeyBinding;
import net.auoeke.headsdowndisplay.config.arm.ArmConfiguration;

@SuppressWarnings({"unused", "RedundantSuppression"})
public class HDD implements ClientModInitializer {
    public static final String ID = "headsdowndisplay";

    public static final KeyBinding toggleHotbarKey = new KeyBinding("key.headsdowndisplay.toggleHotbar", -1, "key.category.headsdowndisplay");

    public static final KeyBinding toggleMainArmKey = new HandlingKeyBinding(
        "key.headsdowndisplay.toggleMain",
        () -> HDDConfig.instance.arm.mainHandHideCondition = HDDConfig.instance.arm.mainHandHideCondition == HideCondition.ALWAYS ? HideCondition.NEVER : HideCondition.ALWAYS
    );

    public static final KeyBinding toggleSecondaryArmKey = new HandlingKeyBinding(
        "key.headsdowndisplay.toggleSecondary",
        () -> HDDConfig.instance.arm.offHandHideCondition = HDDConfig.instance.arm.offHandHideCondition == HideCondition.ALWAYS ? HideCondition.NEVER : HideCondition.ALWAYS
    );

    public static final KeyBinding toggleArmsKey = new HandlingKeyBinding("key.headsdowndisplay.toggleArms", () -> {
        ArmConfiguration config = HDDConfig.instance.arm;
        config.mainHandHideCondition = config.offHandHideCondition = config.mainHandHideCondition == HideCondition.ALWAYS ? HideCondition.NEVER : HideCondition.ALWAYS;
    });

    @Override
    public void onInitializeClient() {
        HDDConfig.instance = AutoConfig.register(HDDConfig.class, GsonConfigSerializer::new).get();

        KeyBindingRegistryImpl.registerKeyBinding(toggleHotbarKey);
        KeyBindingRegistryImpl.registerKeyBinding(toggleMainArmKey);
        KeyBindingRegistryImpl.registerKeyBinding(toggleSecondaryArmKey);
        KeyBindingRegistryImpl.registerKeyBinding(toggleArmsKey);
    }
}
