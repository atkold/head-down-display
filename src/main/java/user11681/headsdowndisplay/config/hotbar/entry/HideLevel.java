package user11681.headsdowndisplay.config.hotbar.entry;

public enum HideLevel {
    HOTBAR(23),
    EXPERIENCE(29),
    CUSTOM,
    ALL;

    public final int maxY;

    HideLevel(final int maxY) {
        this.maxY = maxY;
    }

    HideLevel() {
        this.maxY = Integer.MIN_VALUE;
    }
}
