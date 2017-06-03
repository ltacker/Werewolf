package fr.ensimag.loupsgarous.modele;

import java.time.LocalDateTime;

/**
 *
 * @author baplar
 */
public class Partie {
    private int id = -1;
    private String nomPartie;
    private LocalDateTime dateDebut;
    private int dureeJour;
    private int dureeNuit;
    private int minParticipants;
    private int maxParticipants;
    private float proportionLG;
    private Role vainqueurs = null;
    private int nombreParticipants = 0;
   
    public Partie(int id, String nomPartie, LocalDateTime dateDebut, int dureeJour, int dureeNuit, int minParticipants, int maxParticipants, float proportionLG, Role vainqueurs, int nombreParticipants) {
        this.id = id;
        this.nomPartie = nomPartie;
        this.dateDebut = dateDebut;
        this.dureeJour = dureeJour;
        this.dureeNuit = dureeNuit;
        this.minParticipants = minParticipants;
        this.maxParticipants = maxParticipants;
        this.proportionLG = proportionLG;
        this.vainqueurs = vainqueurs;
        this.nombreParticipants = nombreParticipants;
    }

    public Partie(String nomPartie, LocalDateTime dateDebut, int dureeJour, int dureeNuit, int minParticipants, int maxParticipants, float proportionLG) {
        this.nomPartie = nomPartie;
        this.dateDebut = dateDebut;
        this.dureeJour = dureeJour;
        this.dureeNuit = dureeNuit;
        this.minParticipants = minParticipants;
        this.maxParticipants = maxParticipants;
        this.proportionLG = proportionLG;
    }

    public int getId() {
        return id;
    }

    public String getNomPartie() {
        return nomPartie;
    }

    public LocalDateTime getDateDebut() {
        return dateDebut;
    }

    public int getDureeJour() {
        return dureeJour;
    }

    public int getDureeNuit() {
        return dureeNuit;
    }

    public int getMinParticipants() {
        return minParticipants;
    }

    public int getMaxParticipants() {
        return maxParticipants;
    }

    public float getProportionLG() {
        return proportionLG;
    }
    
    public Role getVainqueurs() {
        return vainqueurs;
    }

    public int getnombreParticipants() {
        return nombreParticipants;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Partie other = (Partie) obj;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }
     
    
}
