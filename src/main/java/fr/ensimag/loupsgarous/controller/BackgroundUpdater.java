package fr.ensimag.loupsgarous.controller;

import fr.ensimag.loupsgarous.dao.ChatroomDAO;
import fr.ensimag.loupsgarous.dao.DAOException;
import fr.ensimag.loupsgarous.dao.PartieDAO;
import fr.ensimag.loupsgarous.dao.VillageoisDAO;
import fr.ensimag.loupsgarous.modele.Chatroom;
import fr.ensimag.loupsgarous.modele.Message;
import fr.ensimag.loupsgarous.modele.Partie;
import fr.ensimag.loupsgarous.modele.Pouvoir;
import fr.ensimag.loupsgarous.modele.ProbaPouvoir;
import fr.ensimag.loupsgarous.modele.Role;
import fr.ensimag.loupsgarous.modele.TypeChatroom;
import fr.ensimag.loupsgarous.modele.Villageois;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;
import org.apache.taglibs.standard.tag.common.sql.DataSourceWrapper;

/**
 *
 * @author baplar
 */
public class BackgroundUpdater implements Runnable {
    private DataSource ds;
        
    public BackgroundUpdater(DataSource ds) {
        this.ds = ds;
    }

    @Override
    public void run() {
        startParties();
        updateChatrooms();
    }
    
    private void startParties() {
        PartieDAO dao = new PartieDAO(ds);
        try {
            HashMap<Integer, Partie> parties = dao.getListPartiesADemarrer();
            for (Partie p : parties.values()) {
                Logger.getLogger("loupsgarous").log(Level.INFO, "Launching server n°" + p.getId());                        
                repartirRoles(p, dao.getProbasPouvoirs(p.getId()));
            }
        } catch (DAOException e) {
            Logger.getLogger("loupsgarous").log(Level.WARNING, "DAO error during server initialization", e);                        
        } catch (Error | Exception e) {
            Logger.getLogger("loupsgarous").log(Level.SEVERE, "Unknown error during server initialization", e);
        }
    }
    
    private void repartirRoles(Partie p, HashMap<Pouvoir,ProbaPouvoir> probas) throws DAOException {
        VillageoisDAO dao = new VillageoisDAO(ds);
        ArrayList<Villageois> villageois = new ArrayList<>(dao.getListeVillageois(p.getId()).values());
        if (villageois.size() < p.getMinParticipants()) {
            // Trop peu de participants : partie annulée
            Logger.getLogger("loupsgarous").log(Level.WARNING, "Server n°" + p.getId() + " dropped by lack of players");
            PartieDAO daoP = new PartieDAO(ds);
            if (! daoP.deletePartie(p.getId())) {
                Logger.getLogger("loupsgarous").log(Level.SEVERE, "Server n°" + p.getId() + " could not be dropped");
            }
            return;
        }

        // Attribution au hasard des rôles et des pouvoirs spécifiques aux rôles
        Collections.shuffle(villageois);
        int nbLoupsGarous = (int) (villageois.size() * p.getProportionLG());
        if (nbLoupsGarous == 0) {nbLoupsGarous = 1;}
        for (int i = 0; i < nbLoupsGarous; i++) {
            villageois.get(i).setRoleVillageois(Role.LOUPGAROU);
        }
        for (int i = nbLoupsGarous; i < villageois.size(); i++) {
            villageois.get(i).setRoleVillageois(Role.HUMAIN);
        }
        
        if (Math.random() <= probas.get(Pouvoir.CONTAMINATION).getProba()) {
            villageois.get(0).setPouvoirVillageois(Pouvoir.CONTAMINATION);
        }
        if (Math.random() <= probas.get(Pouvoir.INSOMNIE).getProba()) {
            villageois.get(villageois.size() - 1).setPouvoirVillageois(Pouvoir.INSOMNIE);
        }
        
        // Attribution au hasard des rôles non spécifiques
        Collections.shuffle(villageois);
        if (Math.random() <= probas.get(Pouvoir.VOYANCE).getProba()) {
            int i = 0;
            while (i < villageois.size() && ! villageois.get(i).setPouvoirVillageois(Pouvoir.VOYANCE)) {
                i++;
            }
            if (i == villageois.size()) {
                Logger.getLogger("loupsgarous").log(Level.WARNING, "Could not attribute : VOYANCE");                                        
            }
        }        
        if (Math.random() <= probas.get(Pouvoir.SPIRITISME).getProba()) {
            int i = 0;
            while (i < villageois.size() && ! villageois.get(i).setPouvoirVillageois(Pouvoir.SPIRITISME)) {
                i++;
            }
            if (i == villageois.size()) {
                Logger.getLogger("loupsgarous").log(Level.WARNING, "Could not attribute : SPIRITISME");                                        
            }
        }
        
        dao.setRolesPouvoirs(villageois);
        Logger.getLogger("loupsgarous").log(Level.WARNING, "Server n°" + p.getId() + " initialized");
    }

    private void updateChatrooms() {
        ChatroomDAO dao = new ChatroomDAO(ds);
        try {
            ArrayList<Chatroom> chatrooms = dao.getChatroomsToCreate();
            for (Chatroom c : chatrooms) { 
                dao.createChatroom(c);
                if (c.getElimineID() != null) {
                    VillageoisDAO daoV = new VillageoisDAO(ds);
                    daoV.tuerVillageois(c.getPartieID(), c.getElimineID());
                    
                    Chatroom newChatroom = dao.getCurrentChatrooms(c.getPartieID()).get(TypeChatroom.VILLAGE);
                    if (newChatroom != null) {
                        dao.sendMessage(new Message(newChatroom.getId(), "ANNONCE", LocalDateTime.now(), "Le villageois <b>" + c.getElimineID() + "</b> a été tué dans son sommeil !"));
                    }
                    
                    checkVictoire(c.getPartieID());
                }
            }
        } catch (DAOException e) {
            Logger.getLogger("loupsgarous").log(Level.WARNING, "DAO error during chatroom creation", e);                        
        }
    }
        
    public void checkVictoire(int partieID) {
        VillageoisDAO daoV = new VillageoisDAO(ds);
        PartieDAO daoP = new PartieDAO(ds);
        int nbVivants = daoV.getNombreVillageois(partieID);
        int nbLG = daoV.getNombreLG(partieID);
        if (nbLG == 0) {
            daoP.terminerPartie(partieID, Role.HUMAIN);
        }
        if (nbLG == nbVivants) {
            daoP.terminerPartie(partieID, Role.LOUPGAROU);
        }
    }    

}
