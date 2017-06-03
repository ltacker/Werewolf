package fr.ensimag.loupsgarous.modele;

/**
 *
 * @author lartigab
 */
public class Vote {
    private int partieID;
    private int chatroomID;
    private String cibleID;
    private int NombreVotes;

    public Vote(int partieID, int chatroomID, String cibleID, int NombreVotes) {
        this.partieID = partieID;
        this.chatroomID = chatroomID;
        this.cibleID = cibleID;
        this.NombreVotes = NombreVotes;
    }

    public int getPartieID() {
        return partieID;
    }

    public int getChatroomID() {
        return chatroomID;
    }

    public String getCibleID() {
        return cibleID;
    }

    public int getNombreVotes() {
        return NombreVotes;
    }
}
