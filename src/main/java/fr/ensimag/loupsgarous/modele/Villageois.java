package fr.ensimag.loupsgarous.modele;

/**
 *
 * @author lartigab
 */
public class Villageois {
    private String joueurID;
    private int partieID;
    private Role roleVillageois = null;
    private Pouvoir pouvoirVillageois = null;
    private boolean vivant = true;

    public Villageois(String joueurID, int partieID, Role roleVillageois, Pouvoir pouvoirVillageois, boolean vivant) {
        this.joueurID = joueurID;
        this.partieID = partieID;
        this.roleVillageois = roleVillageois;
        this.pouvoirVillageois = pouvoirVillageois;
        this.vivant = vivant;
    }

    public Villageois(String joueurID, int partieID) {
        this.joueurID = joueurID;
        this.partieID = partieID;
    }

    public String getJoueurID() {
        return joueurID;
    }

    public int getPartieID() {
        return partieID;
    }

    public Role getRoleVillageois() {
        return roleVillageois;
    }

    public Pouvoir getPouvoirVillageois() {
        return pouvoirVillageois;
    }
    
    public boolean getVivant() {
        return vivant;
    }
    
    public boolean setRoleVillageois(Role roleVillageois) {
        this.roleVillageois = roleVillageois;
        return true;
    }
    
    public boolean setPouvoirVillageois(Pouvoir pouvoirVillageois) {
        if (this.pouvoirVillageois != null) {
            return false;
        }
        if ((this.roleVillageois == Role.HUMAIN && this.pouvoirVillageois == Pouvoir.CONTAMINATION)
                || (this.roleVillageois == Role.LOUPGAROU && this.pouvoirVillageois == Pouvoir.INSOMNIE)) {
            return false;
        }
        this.pouvoirVillageois = pouvoirVillageois;
        return true;
    }
    
    public boolean canRead(Chatroom c) {
        if (! vivant) {
            return true;
        }
        switch (c.getTypeChatroom()) {
            case VILLAGE:
                return true;
            case LOUPGAROU:
                return (roleVillageois == Role.LOUPGAROU) || (pouvoirVillageois == Pouvoir.INSOMNIE);
            case MEDIUM:
                return pouvoirVillageois == Pouvoir.SPIRITISME;
            default:
                return false;
        }
    }
    
    public boolean canWrite(Chatroom c) {
        if (! vivant) {
            return (c.getTypeChatroom() == TypeChatroom.MEDIUM && joueurID.equals(c.getEspritAppeleID()));
        }
        switch (c.getTypeChatroom()) {
            case VILLAGE:
                return true;
            case LOUPGAROU:
                return roleVillageois == Role.LOUPGAROU;
            case MEDIUM:
                return pouvoirVillageois == Pouvoir.SPIRITISME;
            default:
                return false;
        }
    }

    public String printRole() {
        String s = this.getRoleVillageois().toString();
        if (this.getPouvoirVillageois() != null) {
            s += " - " + this.getPouvoirVillageois().toString();
        }
        return s;
    }
}
