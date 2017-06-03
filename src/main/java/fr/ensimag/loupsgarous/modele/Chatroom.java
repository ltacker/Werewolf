package fr.ensimag.loupsgarous.modele;

import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 *
 * @author lartigab
 */
public class Chatroom {
    private int id;
    private int partieID;
    private TypeChatroom typeChatroom;
    private int numeroJour;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private String elimineID = null;
    private String espritAppeleID = null;
    private ArrayList<Message> messages = null;

    public Chatroom(int partieID, TypeChatroom typeChatroom, int numeroJour, LocalDateTime dateDebut, LocalDateTime dateFin) {
        this.partieID = partieID;
        this.typeChatroom = typeChatroom;
        this.numeroJour = numeroJour;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
    }
    
    public Chatroom(int id, int partieID, TypeChatroom typeChatroom, int numeroJour, LocalDateTime dateDebut, LocalDateTime dateFin, String elimineID, String espritAppeleID) {
        if (espritAppeleID != null && typeChatroom != TypeChatroom.MEDIUM) {
            throw new IllegalArgumentException("Esprit appelé pour une chatroom autre que Medium");
        }
        if (elimineID != null && typeChatroom == TypeChatroom.MEDIUM) {
            throw new IllegalArgumentException("Elimination pour une chatroom Medium");
        }
        this.id = id;
        this.partieID = partieID;
        this.typeChatroom = typeChatroom;
        this.numeroJour = numeroJour;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.elimineID = elimineID;
        this.espritAppeleID = espritAppeleID;
    }

    public int getId() {
        return id;
    }

    public int getPartieID() {
        return partieID;
    }

    public TypeChatroom getTypeChatroom() {
        return typeChatroom;
    }
    
    public int getNumeroJour() {
        return numeroJour;
    }

    public LocalDateTime getDateDebut() {
        return dateDebut;
    }

    public LocalDateTime getDateFin() {
        return dateFin;
    }

    public String getEspritAppeleID() {
        return espritAppeleID;
    }
    
    public String getElimineID() {
        return elimineID;
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }
    
    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }

}
