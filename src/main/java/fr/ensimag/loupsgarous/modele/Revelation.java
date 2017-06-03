package fr.ensimag.loupsgarous.modele;

/**
 *
 * @author baplar
 */
public class Revelation {
    private int partieID;
    private int chatroomID;
    private String voyantID;
    private String reveleID;
    private Role roleDecouvert;
    private Pouvoir pouvoirDecouvert;

    public Revelation(int partieID, int chatroomID, String voyantID, String reveleID, Role roleDecouvert, Pouvoir pouvoirDecouvert) {
        this.partieID = partieID;
        this.chatroomID = chatroomID;
        this.voyantID = voyantID;
        this.reveleID = reveleID;
        this.roleDecouvert = roleDecouvert;
        this.pouvoirDecouvert = pouvoirDecouvert;
    }

    public int getPartieID() {
        return partieID;
    }

    public int getChatroomID() {
        return chatroomID;
    }

    public String getVoyantID() {
        return voyantID;
    }

    public String getReveleID() {
        return reveleID;
    }

    public Role getRoleDecouvert() {
        return roleDecouvert;
    }

    public Pouvoir getPouvoirDecouvert() {
        return pouvoirDecouvert;
    }
    
    public String printRole() {
        String s = this.getRoleDecouvert().toString();
        if (this.getPouvoirDecouvert()!= null) {
            s += " - " + this.getPouvoirDecouvert().toString();
        }
        return s;
    }
}
