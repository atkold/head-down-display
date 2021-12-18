package headdowndisplay.config.hotbar.entry

enum class HideLevel(@JvmField val maxY: Int) {
    HOTBAR(23),
    EXPERIENCE(29),
    CUSTOM,
    ALL;

    constructor() : this(Int.MIN_VALUE)
}
