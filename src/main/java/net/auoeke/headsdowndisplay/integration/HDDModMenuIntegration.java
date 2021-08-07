package net.auoeke.headsdowndisplay.integration;

import net.auoeke.headsdowndisplay.config.HDDConfig;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import net.minecraft.client.gui.screen.Screen;

public class HDDModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return (Screen parent) -> AutoConfig.getConfigScreen(HDDConfig.class, parent).get();
    }
}
