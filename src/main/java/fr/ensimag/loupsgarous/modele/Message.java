package fr.ensimag.loupsgarous.modele;

import java.time.LocalDateTime;

/**
 *
 * @author lartigab
 */
public class Message {
    private int id;
    private int chatroomID;
    private String envoyeurID;
    private LocalDateTime dateEnvoi;
    private String contenu;

    public Message(int id, int chatroomID, String envoyeurID, LocalDateTime dateEnvoi, String contenu) {
        this.id = id;
        this.chatroomID = chatroomID;
        this.envoyeurID = envoyeurID;
        this.dateEnvoi = dateEnvoi;
        this.contenu = contenu;
    }
    
    public Message(int chatroomID, String envoyeurID, LocalDateTime dateEnvoi, String contenu) {
        this.chatroomID = chatroomID;
        this.envoyeurID = envoyeurID;
        this.dateEnvoi = dateEnvoi;
        this.contenu = contenu;
    }

    public int getId() {
        return id;
    }

    public int getChatroomID() {
        return chatroomID;
    }

    public String getEnvoyeurID() {
        return envoyeurID;
    }

    public LocalDateTime getDateEnvoi() {
        return dateEnvoi;
    }

    public String getContenu() {
        return contenu;
    }
    
}
