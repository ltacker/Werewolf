package fr.ensimag.loupsgarous.modele;

/**
 *
 * @author baplar
 */
public class Bulletin {
    private int partieID;
    private int chatroomID;
    private String votantID;
    private String cibleID;

    public Bulletin(int partieID, int chatroomID, String votantID, String cibleID) {
        this.partieID = partieID;
        this.chatroomID = chatroomID;
        this.votantID = votantID;
        this.cibleID = cibleID;
    }

    public int getPartieID() {
        return partieID;
    }

    public int getChatroomID() {
        return chatroomID;
    }

    public String getVotantID() {
        return votantID;
    }

    public String getCibleID() {
        return cibleID;
    }
}
