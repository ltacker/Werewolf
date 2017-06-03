package fr.ensimag.loupsgarous.modele;

/**
 *
 * @author baplar
 */
public enum Pouvoir {
    CONTAMINATION ("Contamination"),
    INSOMNIE ("Insomnie"),
    VOYANCE ("Voyance"),
    SPIRITISME ("Spiritisme");
    
    private final String name;
    private Pouvoir(String name) {
        this.name = name;
    }
    
    @Override
    public String toString() {
        return this.name;
    }

    public static Pouvoir get(String name) {
        if (name == null) {
            return null;
        }
        switch (name) {
            case "Contamination":
                return CONTAMINATION;
            case "Insomnie":
                return INSOMNIE;
            case "Voyance":
                return VOYANCE;
            case "Spiritisme":
                return SPIRITISME;
            default:
                return null;
        }
    }
}
