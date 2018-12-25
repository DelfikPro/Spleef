package pro.delfik.spleef.cosmetics;

public enum Tier {
    COMMON("§fОбычн", "§e", "ы"),
    SPECIAL("§eОсоб", "§e", "ы"),
    RARE("§6Редк", "§6", "и"),
    EPIC("§bЭпическ", "§b", "и"),
    LEGENDARY("§cЛегендарн", "§c", "ы"),
    IMPLARIO("§d#implario", "§d", null);

    private final String title;
    private final String prefix;
    private final String softiness;

    private Tier(String title, String prefix, String softiness) {
        this.title = title;
        this.prefix = prefix;
        this.softiness = softiness;
    }

    public String getCaption(Gender gender) {
        return this.softiness == null ? this.title : this.title + (gender == Gender.MASCULINE ? this.softiness + "й" : gender.getAdjective());
    }

    public String getColor() {
        return this.prefix;
    }
}
