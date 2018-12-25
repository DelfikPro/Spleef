package pro.delfik.spleef.cosmetics;

public enum Gender {
    MASCULINE("ый", ""),
    FEMININE("ая", "а"),
    NEUTER("ое", "о"),
    PLURAL("ые", "и");

    private final String adjective;
    private final String verbPast;

    private Gender(String adjective, String verbPast) {
        this.adjective = adjective;
        this.verbPast = verbPast;
    }

    public String getAdjective() {
        return this.adjective;
    }

    public String getVerbPast() {
        return this.verbPast;
    }
}
