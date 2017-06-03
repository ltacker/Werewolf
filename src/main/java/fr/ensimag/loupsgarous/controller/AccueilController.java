package fr.ensimag.loupsgarous.controller;

import fr.ensimag.loupsgarous.dao.DAOException;
import fr.ensimag.loupsgarous.dao.PartieDAO;
import fr.ensimag.loupsgarous.dao.UtilisateurDAO;
import fr.ensimag.loupsgarous.dao.VillageoisDAO;
import fr.ensimag.loupsgarous.modele.Partie;
import fr.ensimag.loupsgarous.modele.Pouvoir;
import fr.ensimag.loupsgarous.modele.ProbaPouvoir;
import fr.ensimag.loupsgarous.modele.Utilisateur;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.sql.DataSource;

@WebServlet(name = "Accueil", urlPatterns = {"/loupsgarous"})
public class AccueilController extends HttpServlet {

    @Resource(name = "jdbc/loupsgarous")
    private DataSource ds;

    @Override
    public void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws IOException, ServletException {
        request.setCharacterEncoding("UTF-8");

        Utilisateur user = (Utilisateur) request.getSession().getAttribute("logged-in");
        if (user == null) {
            afficherConnexion(request, response);
        }
        String action = request.getParameter("action");
        if (action == null) {
            afficherLobby(request, response);
        } else {
            switch (action) {                
                case "join":
                    actionRejoindre(request, response);
                    break;
                case "logout":
                    actionDeconnexion(request, response);
                    break;
                default:
                    ErrorManager.errorPage(request, response, "Action impossible : " + action);
            }
        }
    }

    
    @Override
    public void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws IOException, ServletException {

        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        if (action == null) {
            ErrorManager.errorPage(request, response, "Aucune action précisée sur une requête POST");        
            return;
        }
        switch (action) {                
            case "connexion":
                actionConnexion(request, response);
                break;
            case "inscription":
                actionInscription(request, response);
                break;
            case "creerServeur":
                actionCreerServeur(request, response);
                break;
            default:
                ErrorManager.errorPage(request, response, "Action impossible : " + action);
        }
    }

    public void afficherConnexion(HttpServletRequest request, 
            HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/login.jsp").forward(request, response);
    }
    
    public void actionConnexion(HttpServletRequest request, 
            HttpServletResponse response)
            throws ServletException, IOException {
        String login = request.getParameter("login");
        String password = request.getParameter("password");
        try {
            UtilisateurDAO dao = new UtilisateurDAO(ds);
            Utilisateur user = dao.getUtilisateur(login);
            if (user == null) {
                // Utilisateur non existant
                Logger.getLogger("loupsgarous").log(Level.INFO, "Connexion tried with unknown username '" + login + "'");
                request.setAttribute("alert", "<h3 class='alert alert-danger'>Utilisateur inconnu</h3>");
                afficherConnexion(request, response);
            } else if (user.checkPassword(password)) {
                // Utilisateur connecté avec succès
                Logger.getLogger("loupsgarous").log(Level.INFO, "User '" + login + "' successfully connected");
                request.getSession().setAttribute("logged-in", user);
                afficherLobby(request, response);
            } else {
                // Mot de passe incorrect
                Logger.getLogger("loupsgarous").log(Level.INFO, "Bad password for user '" + login + "'");
                request.setAttribute("alert", "<h3 class='alert alert-danger'>Mot de passe incorrect</h3>");
                afficherConnexion(request, response);
            }
        } catch (DAOException e) {
            Logger.getLogger("loupsgarous").log(Level.INFO, "Unknown login error for user '" + login + "'", e);
            request.setAttribute("alert", "<h3 class='alert alert-danger'>Erreur inattendue : " + e.getMessage() + "</h3>");
            afficherConnexion(request, response);
        }
    }

    public void actionInscription(HttpServletRequest request, 
            HttpServletResponse response)
            throws ServletException, IOException {
        String login = request.getParameter("login");
        String password = request.getParameter("password");
        try {
            UtilisateurDAO dao = new UtilisateurDAO(ds);
            Utilisateur u = new Utilisateur(login, password);
            if (dao.createUtilisateur(u)) {
                // Utilisateur créé avec succès
                Logger.getLogger("loupsgarous").log(Level.INFO, "User '" + login + "' created");
                request.setAttribute("alert", "<h3 class='alert alert-success'>Compte créé avec succès</h3>");
            } else {
                // Utilisateur n'a pas pu être créé
                throw new DAOException("Utilisateur existant");
            }
        } catch (DAOException e) {
            Logger.getLogger("loupsgarous").log(Level.WARNING, "Failed creation of user '" + login + "'", e);
            request.setAttribute("alert", "<h3 class='alert alert-danger'>Erreur : Ce compte existe déjà</h3>");
        }
        afficherConnexion(request, response);
    }

    private void afficherLobby(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        Utilisateur user = (Utilisateur) request.getSession().getAttribute("logged-in");
        
        try {
            request.setAttribute("utilisateur", user);
            PartieDAO dao = new PartieDAO(ds);
            HashMap<Integer, Partie> partiesDisponibles = dao.getListPartiesDisponibles();
            HashMap<Integer, Partie> partiesAttente = dao.getListPartiesRejointes(user.getLogin());
            HashMap<Integer, Partie> partiesCourantes = new HashMap<>();
            HashMap<Integer, Partie> partiesTerminees = new HashMap<>();
            
            for (Partie p : partiesAttente.values()) {
                // Retirer des parties disponibles
                partiesDisponibles.remove(p.getId());
                // Vérifier si la partie a démarré
                if (LocalDateTime.now().isAfter(p.getDateDebut())) {
                    // Vérifier si la partie est terminée
                    if (p.getVainqueurs() == null) {
                        partiesCourantes.put(p.getId(), p);
                    } else {
                        partiesTerminees.put(p.getId(), p);
                    }
                }
            }
            for (int i : partiesCourantes.keySet()) {
                partiesAttente.remove(i);
            }
            for (int i : partiesTerminees.keySet()) {
                partiesAttente.remove(i);
            }
            
            request.setAttribute("partiesDisponibles", partiesDisponibles);
            request.setAttribute("partiesAttente", partiesAttente);
            request.setAttribute("partiesCourantes", partiesCourantes);
            request.setAttribute("partiesTerminees", partiesTerminees);
            
            request.getRequestDispatcher("/WEB-INF/lobby.jsp").forward(request, response);
        } catch (DAOException e) {
            Logger.getLogger("loupsgarous").log(Level.SEVERE, "Could not display lobby for '" + user.getLogin() + "'", e);
            ErrorManager.errorPage(request, response, e);
        }
    }

    private void actionCreerServeur(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        String dateDebutString = request.getParameter("dateDebut") + " " + request.getParameter("heureDebut");
        LocalDateTime dateDebut = LocalDateTime.parse(dateDebutString, DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm"));
        Partie p = new Partie(request.getParameter("nomPartie"), dateDebut,
                Integer.parseInt(request.getParameter("dureeJour")), Integer.parseInt(request.getParameter("dureeNuit")), 
                Integer.parseInt(request.getParameter("minParticipants")), Integer.parseInt(request.getParameter("maxParticipants")), Float.parseFloat(request.getParameter("proportionLG"))/100);
        HashMap<Pouvoir, ProbaPouvoir> pp = ProbaPouvoir.initProbas(Float.parseFloat(request.getParameter("probaContamination"))/100, Float.parseFloat(request.getParameter("probaInsomnie"))/100,
                Float.parseFloat(request.getParameter("probaVoyance"))/100, Float.parseFloat(request.getParameter("probaSpiritisme"))/100);
        try {
            PartieDAO dao = new PartieDAO(ds);
            if (dao.createPartie(p, pp)) {
                // Partie créée avec succès
                Logger.getLogger("loupsgarous").log(Level.INFO, "Server '" + p.getNomPartie() + "' created");
                request.setAttribute("alertCreate", "<h3 class='alert alert-success'>Partie créée avec succès</h3>");
            } else {
                // Utilisateur n'a pas pu être créé
                throw new DAOException();
            }
        } catch (DAOException e) {
            Logger.getLogger("loupsgarous").log(Level.WARNING, "Failed creation of user server '" + p.getNomPartie() + "'", e);
            request.setAttribute("alertCreate", "<h3 class='alert alert-danger'>Erreur : La partie n'a pas pu être créée</h3>");
        }
        afficherLobby(request, response);
    }
    
    private void actionRejoindre(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        
        int partieID;
        try {
            partieID = Integer.parseInt(request.getParameter("id"));
        } catch (NumberFormatException e) {
            ErrorManager.errorPage(request, response, "Erreur de lecture de l'identifiant de partie");
            return;
        }
        
        Utilisateur user = (Utilisateur) request.getSession().getAttribute("logged-in");
        
        try {
            VillageoisDAO dao = new VillageoisDAO(ds);
            if (dao.rejoindrePartie(user.getLogin(), partieID)) {
                // Partie rejointe avec succès
                Logger.getLogger("loupsgarous").log(Level.INFO, "User '" + user.getLogin() + "' joined server n°" + partieID);
                request.setAttribute("alertList", "<h3 class='alert alert-success'>Partie rejointe avec succès</h3>");
            } else {
                // Partie non rejointe
                throw new DAOException();
            }
        } catch (DAOException e) {
            Logger.getLogger("loupsgarous").log(Level.WARNING, "User '" + user.getLogin() + "' could not join server n°" + partieID, e);
            request.setAttribute("alertList", "<h3 class='alert alert-danger'>Erreur : La partie n'a pas pu être rejointe</h3>");                
        }
        afficherLobby(request, response);
    }

    public void actionDeconnexion(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        Utilisateur user = (Utilisateur) request.getSession().getAttribute("logged-in");
        Logger.getLogger("loupsgarous").log(Level.INFO, "User '" + user.getLogin() + "' disconnected");
        request.getSession().invalidate();
        afficherConnexion(request, response);
    }
}
