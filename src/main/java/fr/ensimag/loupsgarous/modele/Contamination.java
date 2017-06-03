package fr.ensimag.loupsgarous.modele;

/**
 *
 * @author baplar
 */
public class Contamination {
    private int partieID;
    private int chatroomID;
    private String contaminateurID;
    private String contamineID;

    public Contamination(int partieID, int chatroomID, String contaminateurID, String contamineID) {
        this.partieID = partieID;
        this.chatroomID = chatroomID;
        this.contaminateurID = contaminateurID;
        this.contamineID = contamineID;
    }

    public int getPartieID() {
        return partieID;
    }

    public int getChatroomID() {
        return chatroomID;
    }

    public String getContaminateurID() {
        return contaminateurID;
    }

    public String getContamineID() {
        return contamineID;
    }
}
