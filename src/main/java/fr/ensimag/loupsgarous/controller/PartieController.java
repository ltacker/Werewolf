/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.ensimag.loupsgarous.controller;

import fr.ensimag.loupsgarous.dao.ChatroomDAO;
import fr.ensimag.loupsgarous.dao.DAOException;
import fr.ensimag.loupsgarous.dao.PartieDAO;
import fr.ensimag.loupsgarous.dao.VillageoisDAO;
import fr.ensimag.loupsgarous.modele.Bulletin;
import fr.ensimag.loupsgarous.modele.Chatroom;
import fr.ensimag.loupsgarous.modele.Contamination;
import fr.ensimag.loupsgarous.modele.Message;
import fr.ensimag.loupsgarous.modele.Partie;
import fr.ensimag.loupsgarous.modele.Pouvoir;
import fr.ensimag.loupsgarous.modele.Revelation;
import fr.ensimag.loupsgarous.modele.Role;
import fr.ensimag.loupsgarous.modele.TypeChatroom;
import fr.ensimag.loupsgarous.modele.Utilisateur;
import fr.ensimag.loupsgarous.modele.Villageois;
import fr.ensimag.loupsgarous.modele.Vote;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

@WebServlet(name = "Partie", urlPatterns = {"/loupsgarous/village"})
public class PartieController extends HttpServlet {

    @Resource(name = "jdbc/loupsgarous")
    private DataSource ds;

    @Override
    public void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws IOException, ServletException {
        request.setCharacterEncoding("UTF-8");
        Utilisateur user = (Utilisateur) request.getSession().getAttribute("logged-in");
        if (user == null) {
            request.getRequestDispatcher("/loupsgarous").forward(request, response);
        }
        
        if (request.getParameter("id") != null) {
            int partieID;
            try {
                partieID = Integer.parseInt(request.getParameter("id"));
            } catch (NumberFormatException e) {
                ErrorManager.errorPage(request, response, "Erreur de lecture de l'identifiant de partie");
                return;
            }
            try {
                PartieDAO dao = new PartieDAO(ds);
                Partie partie = dao.getListPartiesRejointes(user.getLogin()).get(partieID);
                if (partie == null) {
                    ErrorManager.errorPage(request, response, "Vous n'êtes pas un joueur de cette partie.");
                }
                request.getSession().setAttribute("partie-courante", partie);
            } catch (DAOException e) {
                Logger.getLogger("loupsgarous").log(Level.SEVERE, "Could not get server info for '" + user.getLogin() + "'", e);
                ErrorManager.errorPage(request, response, "Erreur lors de l'accès à la partie.");
                return;
            }
        }
        
        Partie partie = (Partie) request.getSession().getAttribute("partie-courante");
        if (partie == null) {
            ErrorManager.errorPage(request, response, "Aucune partie sélectionnée.");
            return;
        }
        
        String action = request.getParameter("action");
        if (action == null) {
            afficherVillage(request, response);
            return;
        }
        switch (action) {
            case "archives":
                afficherArchives(request, response);
                break;
            default:
                ErrorManager.errorPage(request, response, "Action impossible : " + action);                
        }
    }

    
    @Override
    public void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws IOException, ServletException {
        request.setCharacterEncoding("UTF-8");
        Utilisateur user = (Utilisateur) request.getSession().getAttribute("logged-in");
        if (user == null) {
            request.getRequestDispatcher("/loupsgarous").forward(request, response);
        }
        
        if (request.getParameter("id") != null) {
            int partieID;
            try {
                partieID = Integer.parseInt(request.getParameter("id"));
            } catch (NumberFormatException e) {
                ErrorManager.errorPage(request, response, "Erreur de lecture de l'identifiant de partie");
                return;
            }
            try {
                PartieDAO dao = new PartieDAO(ds);
                Partie partie = dao.getListPartiesRejointes(user.getLogin()).get(partieID);
                if (partie == null) {
                    ErrorManager.errorPage(request, response, "Vous n'êtes pas un joueur de cette partie.");
                }
                request.getSession().setAttribute("partie-courante", partie);
            } catch (DAOException e) {
                Logger.getLogger("loupsgarous").log(Level.SEVERE, "Could not get server info for '" + user.getLogin() + "'", e);
                ErrorManager.errorPage(request, response, "Erreur lors de l'accès à la partie.");
                return;
            }
        }
        
        Partie partie = (Partie) request.getSession().getAttribute("partie-courante");
        if (partie == null) {
            ErrorManager.errorPage(request, response, "Aucune partie sélectionnée.");
            return;
        }
        
        String action = request.getParameter("action");
        if (action == null) {
            ErrorManager.errorPage(request, response, "Aucune action précisée sur une requête POST");        
            return;
        }
        switch (action) {
            case "envoyerMessage":
                actionEnvoyerMessage(request, response);
                break;
            case "voter":
                actionVoter(request, response);
                break;
            case "reveler":
                actionDecouvrirRole(request, response);
                break;
            case "appelerEsprit":
                actionAppelerEsprit(request, response);
                break;
            case "contaminer":
                actionContaminer(request, response);
                break;
            default:
                ErrorManager.errorPage(request, response, "Action impossible : " + action);                
        }
    }
    
    public void afficherVillage(HttpServletRequest request,
            HttpServletResponse response)
            throws IOException, ServletException {
        Utilisateur user = (Utilisateur) request.getSession().getAttribute("logged-in");
        request.setAttribute("utilisateur", user);
        Partie partie = (Partie) request.getSession().getAttribute("partie-courante");

        try {
            PartieDAO daoP = new PartieDAO(ds);
            partie = daoP.getPartie(partie.getId());
            request.setAttribute("partie", partie);
            
            VillageoisDAO daoV = new VillageoisDAO(ds);
            Villageois v = daoV.getVillageois(user.getLogin(), partie.getId());
            request.setAttribute("villageois", v);
            
            HashMap<String, Villageois> allVillageois = daoV.getListeVillageois(partie.getId());
            request.setAttribute("allVillageois", allVillageois);
            
            ChatroomDAO daoC = new ChatroomDAO(ds);
            HashMap<TypeChatroom, Chatroom> chatrooms = daoC.getCurrentChatrooms(partie.getId());
            request.setAttribute("chatrooms", chatrooms);

            HashMap<String, Revelation> revelations = null;
            if (v.getPouvoirVillageois() == Pouvoir.VOYANCE) {
                revelations = daoC.getListRevelations(partie.getId(), v.getJoueurID());
                request.setAttribute("revelations", revelations);
            }
            
            HashMap<String, Contamination> contaminations = null;
            if (v.getPouvoirVillageois() == Pouvoir.CONTAMINATION) {
                contaminations = daoC.getListContaminations(partie.getId(), v.getJoueurID());
                request.setAttribute("contaminations", contaminations);
            }
            
            for (Chatroom c : chatrooms.values()) {
                if (v.canRead(c) && c.getTypeChatroom() != TypeChatroom.MEDIUM) {
                    // Chatroom de vote
                    HashMap<String, Vote> votes = daoC.getListVotes(c.getId());
                    request.setAttribute("votes", votes);
                    if (c.getElimineID() == null && v.canWrite(c)) {
                        request.setAttribute("canVote", c.getId());
                        HashMap<String, Bulletin> bulletins = daoC.getListBulletins(c.getId(), v.getJoueurID());
                        request.setAttribute("bulletins", bulletins);
                    }
                }
                if (v.getPouvoirVillageois() == Pouvoir.CONTAMINATION && c.getTypeChatroom() == TypeChatroom.LOUPGAROU) {
                    // Peut contaminer
                    request.setAttribute("canContaminate", c.getId());
                    for (Contamination co : contaminations.values()) {
                        if (co.getChatroomID() == c.getId()) {
                            request.removeAttribute("canContaminate");
                        }
                    }
                }
                if (v.getPouvoirVillageois() == Pouvoir.VOYANCE && c.getTypeChatroom() == TypeChatroom.LOUPGAROU) {
                    // Peut révéler un rôle
                    request.setAttribute("canReveal", c.getId());
                    for (Revelation r : revelations.values()) {
                        if (r.getChatroomID() == c.getId()) {
                            request.removeAttribute("canReveal");
                        }
                    }
                }
                if (v.getPouvoirVillageois() == Pouvoir.SPIRITISME && c.getTypeChatroom() == TypeChatroom.MEDIUM && c.getEspritAppeleID() == null) {
                    // Peut appeler un esprit
                    request.setAttribute("canCallSpirit", c.getId());
                }
            }
            
        } catch (DAOException e) {
            Logger.getLogger("loupsgarous").log(Level.SEVERE, "Could not display game page for server '" + partie.getNomPartie() + "'", e);
            ErrorManager.errorPage(request, response, e);
        }
        request.getRequestDispatcher("/WEB-INF/village.jsp").forward(request, response);
    }

    public void afficherArchives(HttpServletRequest request,
            HttpServletResponse response)
            throws IOException, ServletException {
        Utilisateur user = (Utilisateur) request.getSession().getAttribute("logged-in");
        Partie partie = (Partie) request.getSession().getAttribute("partie-courante");
        request.setAttribute("partie", partie);
        
        try {
            VillageoisDAO daoV = new VillageoisDAO(ds);
            Villageois v = daoV.getVillageois(user.getLogin(), partie.getId());
            if (v == null) {
                throw new DAOException("Villager not found");
            }
            if (v.getVivant() && (partie.getVainqueurs() == null)) {
                request.setAttribute("alert", "<h3 class='alert alert-danger'>Vous n'avez pas encore accès aux archives de la partie</h3>");
                afficherVillage(request, response);
                return;
            }
        
            ChatroomDAO dao = new ChatroomDAO(ds);
            ArrayList<Chatroom> chatrooms = dao.getListChatrooms(partie.getId());
            request.setAttribute("chatrooms", chatrooms);

            HashSet<Integer> numerosJours = new HashSet<>();
            for (Chatroom c : chatrooms) {
                numerosJours.add(c.getNumeroJour());
            }
            request.setAttribute("numerosJours", numerosJours);
        } catch (DAOException e) {
            Logger.getLogger("loupsgarous").log(Level.SEVERE, "Could not display archives for server '" + partie.getNomPartie() + "'", e);
            ErrorManager.errorPage(request, response, e);
        }
        request.getRequestDispatcher("/WEB-INF/archives.jsp").forward(request, response);
    }
    
    public void actionEnvoyerMessage(HttpServletRequest request,
            HttpServletResponse response)
            throws IOException, ServletException {
        Utilisateur user = (Utilisateur) request.getSession().getAttribute("logged-in");
        int chatroomID = Integer.parseInt(request.getParameter("chatroomID"));
        String contenu = request.getParameter("nouveauMessage");
        Message nouveauMessage = new Message(chatroomID, user.getLogin(), LocalDateTime.now(), contenu);
        try {
            ChatroomDAO dao = new ChatroomDAO(ds);
            if (! dao.sendMessage(nouveauMessage)) {
                throw new DAOException();
            }
        } catch (DAOException e) {
            Logger.getLogger("loupsgarous").log(Level.WARNING, "Failed creation of message from '" + user.getLogin() + "'", e);
            request.setAttribute("alert", "<h3 class='alert alert-danger'>Erreur : Le message n'a pas pu être envoyé</h3>");
        }
        afficherVillage(request, response);
    }
    
    public void actionVoter(HttpServletRequest request,
            HttpServletResponse response)
            throws IOException, ServletException {
        Utilisateur user = (Utilisateur) request.getSession().getAttribute("logged-in");
        Partie partie = (Partie) request.getSession().getAttribute("partie-courante");
        int chatroomID = Integer.parseInt(request.getParameter("chatroomID"));
        String cibleID = request.getParameter("cibleID");
        String annuler = request.getParameter("annuler");
        try {
            ChatroomDAO daoC = new ChatroomDAO(ds);
            HashMap<TypeChatroom, Chatroom> chatrooms = daoC.getCurrentChatrooms(partie.getId());
            Chatroom c = chatrooms.get(TypeChatroom.VILLAGE);
            if (c == null) {
                c = chatrooms.get(TypeChatroom.LOUPGAROU);
                if (c == null) {
                    throw new DAOException("Could not find voting chatroom");
                }
            }
            if (c.getElimineID() != null) {
                throw new DAOException("Le vote est clôturé.");
            }
            
            if (! "1".equals(annuler)) {
                int nbVotes = daoC.castVote(partie.getId(), chatroomID, user.getLogin(), cibleID);
                VillageoisDAO daoV = new VillageoisDAO(ds);
                int nbVotants = daoV.getNombreVotants(partie.getId(), chatroomID);
                if ( (nbVotes * 2) > nbVotants) {
                    Logger.getLogger("loupsgarous").log(Level.INFO, nbVotes + " votes sur " + nbVotants + " votants : élimination de " + cibleID);
                    request.setAttribute("alert", "<h3 class='alert alert-success'>Votre vote a fait éliminer " + cibleID + "</h3>");
                    
                    if (c.getTypeChatroom() == TypeChatroom.VILLAGE) {
                        daoC.sendMessage(new Message(c.getId(), "ANNONCE", LocalDateTime.now(), "Vote terminé ! Vous avez décidé d'éliminer <b>" + cibleID + "</b>."));
                        daoV.tuerVillageois(partie.getId(), cibleID);
                    } else if (c.getTypeChatroom() == TypeChatroom.LOUPGAROU) {
                        daoC.sendMessage(new Message(c.getId(), "ANNONCE", LocalDateTime.now(), "Vote terminé ! Le villageois <b>" + cibleID + "</b> sera éliminé à la fin de la journée."));
                        daoC.sentence(partie.getId(), chatroomID, cibleID);
                    }
                }
            } else {
                daoC.cancelVote(partie.getId(), chatroomID, user.getLogin(), cibleID);
            }
        } catch (DAOException e) {
            Logger.getLogger("loupsgarous").log(Level.WARNING, "Failed creation of vote for '" + user.getLogin() + "'", e);
            request.setAttribute("alert", "<h3 class='alert alert-danger'>Erreur : Le vote n'a pas pu être enregistré</h3>");
        }
        afficherVillage(request, response);
    }
    
    public void actionDecouvrirRole(HttpServletRequest request,
            HttpServletResponse response)
            throws IOException, ServletException {
        Utilisateur user = (Utilisateur) request.getSession().getAttribute("logged-in");
        Partie partie = (Partie) request.getSession().getAttribute("partie-courante");
        int chatroomID = Integer.parseInt(request.getParameter("chatroomID"));
        String reveleID = request.getParameter("reveleID");
        try {
            ChatroomDAO dao = new ChatroomDAO(ds);
            Revelation revele = dao.reveal(partie.getId(), chatroomID, user.getLogin(), reveleID);
            request.setAttribute("alert", "<h3 class='alert alert-success'>Vous avez découvert le rôle de " + reveleID + " : <br />"
                    + revele.printRole() + "</h3>");
        } catch (DAOException e) {
            Logger.getLogger("loupsgarous").log(Level.WARNING, "Failed revelation of role of '" + reveleID + "'", e);
            request.setAttribute("alert", "<h3 class='alert alert-danger'>Erreur : Le rôle n'a pas pu être révélé</h3>");
        }
        afficherVillage(request, response);
    }
    
    public void actionContaminer(HttpServletRequest request,
            HttpServletResponse response)
            throws IOException, ServletException {
        Utilisateur user = (Utilisateur) request.getSession().getAttribute("logged-in");
        Partie partie = (Partie) request.getSession().getAttribute("partie-courante");
        int chatroomID = Integer.parseInt(request.getParameter("chatroomID"));
        String contamineID = request.getParameter("contamineID");
        try {
            ChatroomDAO dao = new ChatroomDAO(ds);
            dao.contaminate(partie.getId(), chatroomID, user.getLogin(), contamineID);
            request.setAttribute("alert", "<h3 class='alert alert-success'>Vous avez contaminé " + contamineID + "</h3>");
        } catch (DAOException e) {
            Logger.getLogger("loupsgarous").log(Level.WARNING, "Failed contamination of '" + contamineID + "'", e);
            request.setAttribute("alert", "<h3 class='alert alert-danger'>Erreur : La contamination n'a pas pu être effectuée</h3>");
        }
        afficherVillage(request, response);
    }
    
    public void actionAppelerEsprit(HttpServletRequest request,
            HttpServletResponse response)
            throws IOException, ServletException {
        int chatroomID = Integer.parseInt(request.getParameter("chatroomID"));
        String appeleID = request.getParameter("appeleID");
        try {
            ChatroomDAO dao = new ChatroomDAO(ds);
            dao.callSpirit(chatroomID, appeleID);
            request.setAttribute("alert", "<h3 class='alert alert-success'>Vous avez appelé " + appeleID + " à votre séance de spiritisme.</h3>");
        } catch (DAOException e) {
            Logger.getLogger("loupsgarous").log(Level.WARNING, "Failed spirit calling on '" + appeleID + "'" + e);
            request.setAttribute("alert", "<h3 class='alert alert-danger'>Erreur : L'esprit n'a pas pu être appelé</h3>");
        }
        afficherVillage(request, response);
    }
}