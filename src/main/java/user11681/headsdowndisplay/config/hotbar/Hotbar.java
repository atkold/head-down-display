package user11681.headsdowndisplay.config.hotbar;

import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry.Gui.CollapsibleObject;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry.Gui.EnumHandler;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry.Gui.Excluded;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry.Gui.Tooltip;
import user11681.headsdowndisplay.config.hotbar.entry.HideLevel;
import user11681.headsdowndisplay.config.hotbar.entry.Triggers;

public class Hotbar {
    @Excluded
    public transient int fadeEnd;

    public boolean fade = false;

    public boolean lower = true;

    public boolean revealAutomatically = true;

    public boolean hideAutomatically = true;

    @EnumHandler(option = EnumHandler.EnumDisplayOption.BUTTON)
    @Tooltip
    public HideLevel hideLevel = HideLevel.HOTBAR;

    @Tooltip
    public int maxY = 23;

    @Tooltip
    public int lowerDelay = 30;

    @Tooltip
    public int fadeDelay = 28;

    @Tooltip
    public int fadeDuration = 5;

    @Tooltip
    public float speed = 2;

    @Tooltip
    public float acceleration = 0.2F;

    @CollapsibleObject
    @Tooltip
    public Triggers trigger = new Triggers();
}
