package fr.ensimag.loupsgarous.modele;

/**
 *
 * @author baplar
 */
public enum Role {
    HUMAIN ("Humain", "Humains"),
    LOUPGAROU ("Loup-Garou", "Loups-Garous");
    
    private final String name;
    private final String pluriel;
    private Role(String name, String pluriel) {
        this.name = name;
        this.pluriel = pluriel;
    }
    
    @Override
    public String toString() {
        return this.name;
    }
    
    public String toPluriel() {
        return this.pluriel;
    }
    
    public static Role get(String name) {
        if (name == null) {
            return null;
        }
        switch (name) {
            case "Humain":
                return HUMAIN;
            case "Loup-Garou":
                return LOUPGAROU;
            default:
                return null;
        }
    }
}
