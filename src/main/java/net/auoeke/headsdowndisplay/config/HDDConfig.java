package net.auoeke.headsdowndisplay.config;

import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config.Gui.Background;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry.Category;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry.Gui.Excluded;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry.Gui.TransitiveObject;
import net.auoeke.headsdowndisplay.HDD;
import net.auoeke.headsdowndisplay.config.arm.ArmConfiguration;
import net.auoeke.headsdowndisplay.config.hotbar.Hotbar;

@Config(name = HDD.ID)
@Background("textures/block/andesite.png")
public class HDDConfig implements ConfigData {
    @Excluded
    public static transient HDDConfig instance;

    @TransitiveObject
    @Category("default")
    public Hotbar hotbar = new Hotbar();

    @TransitiveObject
    @Category("arm")
    public ArmConfiguration arm = new ArmConfiguration();

    @Override
    public void validatePostLoad() {
        this.hotbar.fadeEnd = this.hotbar.fadeDelay + this.hotbar.fadeDuration;
    }
}
