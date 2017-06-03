package fr.ensimag.loupsgarous.modele;

/**
 *
 * @author baplar
 */
public enum TypeChatroom {
    VILLAGE ("Village"),
    LOUPGAROU ("Loups-Garous"),
    MEDIUM ("Medium");
    
    private final String name;
    private TypeChatroom(String name) {
        this.name = name;
    }
    
    @Override
    public String toString() {
        return this.name;
    }

    public static TypeChatroom get(String name) {
        if (name == null) {
            return null;
        }
        switch (name) {
            case "Village":
                return VILLAGE;
            case "Loups-Garous":
                return LOUPGAROU;
            case "Medium":
                return MEDIUM;
            default:
                return null;
        }
    }
}
