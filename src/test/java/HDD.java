import asm.HDDMixinConfigPlugin;
import it.unimi.dsi.fastutil.floats.Float2FloatFunction;

public class HDD {
    public static void registerQuadAlphaRedirector(final Float2FloatFunction newAlpha) {
        if (HDDMixinConfigPlugin.quadAlphaRedirector == null) {
            HDDMixinConfigPlugin.quadAlphaRedirector = newAlpha;
            HDDMixinConfigPlugin.applyRedirector = true;
        } else {
            HDDMixinConfigPlugin.quadAlphaRedirector = (final float alpha) -> newAlpha.get(HDDMixinConfigPlugin.quadAlphaRedirector.get(alpha));
        }
    }
}
