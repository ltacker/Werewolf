package fr.ensimag.loupsgarous.dao;

import fr.ensimag.loupsgarous.modele.Bulletin;
import fr.ensimag.loupsgarous.modele.Chatroom;
import fr.ensimag.loupsgarous.modele.Contamination;
import fr.ensimag.loupsgarous.modele.Message;
import fr.ensimag.loupsgarous.modele.Pouvoir;
import fr.ensimag.loupsgarous.modele.Revelation;
import fr.ensimag.loupsgarous.modele.Role;
import fr.ensimag.loupsgarous.modele.TypeChatroom;
import fr.ensimag.loupsgarous.modele.Villageois;
import fr.ensimag.loupsgarous.modele.Vote;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import javax.sql.DataSource;

/**
 *
 * @author baplar
 */
public class ChatroomDAO extends AbstractDataBaseDAO {
    
    public ChatroomDAO(DataSource ds) {
        super(ds);
    }
    
    public ArrayList<Chatroom> getChatroomsToCreate() {
        ArrayList<Chatroom> result = new ArrayList<>();
        try (
            Connection conn = getConn();
            PreparedStatement st = conn.prepareStatement("WITH LastChatrooms AS ("
                    + "    SELECT Partie_ID, MAX(DateFin) AS MaxDateFin "
                    + "    FROM Chatrooms GROUP BY Partie_ID) " 
                    + "SELECT P.ID, P.DateDebut, P.DureeJour, P.DureeNuit, "
                    + "C.ID, C.TypeChatroom, C.NumeroJour, C.DateDebut, C.DateFin, C.Elimine_ID, C.EspritAppele_ID "
                    + "FROM Parties P LEFT OUTER JOIN LastChatrooms LC ON LC.Partie_ID = P.ID "
                    + "LEFT OUTER JOIN Chatrooms C ON C.Partie_ID = P.ID AND LC.MaxDateFin = C.DateFin AND C.TypeChatroom <> 'Medium' "
                    + "WHERE P.Vainqueurs IS NULL "
                    + "AND CURRENT_TIMESTAMP >= (NVL(C.DateFin, P.DateDebut) - INTERVAL '5' SECOND)");
            ) {
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                int id = rs.getInt(5);
                int partieID = rs.getInt(1);
                TypeChatroom tc;
                int numeroJour;
                LocalDateTime dateDebut;
                LocalDateTime dateFin;
                String elimineID = rs.getString(10);
                String espritAppeleID = rs.getString(11);

                int dureeJour = rs.getInt(3);
                int dureeNuit = rs.getInt(4);
                if (rs.getTimestamp(8) == null) {
                    // La partie vient de démarrer
                    dateFin = rs.getTimestamp(2).toLocalDateTime();
                    dateDebut = dateFin.minusMinutes(dureeNuit);
                    tc = TypeChatroom.LOUPGAROU; // Avant le 1er matin
                    numeroJour = 0;
                } else {
                    // La partie a au moins une chatroom, et sa dernière vient d'expirer
                    dateDebut = rs.getTimestamp(8).toLocalDateTime();
                    dateFin = rs.getTimestamp(9).toLocalDateTime();
                    tc = TypeChatroom.get(rs.getString(6));
                    numeroJour = rs.getInt(7);
                }
                
                // Mise à jour des dates
                while (LocalDateTime.now().isAfter(dateFin.minusSeconds(5))) {
                    dateDebut = dateFin;
                    if (tc == TypeChatroom.VILLAGE) {
                        dateFin = dateFin.plusMinutes(dureeNuit);
                        tc = TypeChatroom.LOUPGAROU;
                    } else {
                        dateFin = dateFin.plusMinutes(dureeJour);
                        tc = TypeChatroom.VILLAGE;
                        numeroJour++;
                    }
                }
                result.add(new Chatroom(id, partieID, tc, numeroJour, dateDebut, dateFin, elimineID, espritAppeleID));
                if (tc == TypeChatroom.LOUPGAROU) {
                    result.add(new Chatroom(partieID, TypeChatroom.MEDIUM, numeroJour, dateDebut, dateFin));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error when listing all chatrooms to create", e);
	}
	return result;
    }
    
    public HashMap<TypeChatroom, Chatroom> getCurrentChatrooms(int partieID) {
        HashMap<TypeChatroom, Chatroom> result = new HashMap<>();
        try (
	     Connection conn = getConn();
	     PreparedStatement st = conn.prepareStatement("SELECT C.ID, C.Partie_ID, C.TypeChatroom, C.NumeroJour, C.DateDebut, C.DateFin, C.Elimine_ID, C.EspritAppele_ID "
                     + "FROM Chatrooms C "
                     + "WHERE C.Partie_ID = ? "
                     + "AND CURRENT_TIMESTAMP >= C.DateDebut AND CURRENT_TIMESTAMP < C.DateFin "
                     + "ORDER BY C.ID");
	     ) {
            st.setInt(1, partieID);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                Chatroom c = new Chatroom(rs.getInt(1), rs.getInt(2), TypeChatroom.get(rs.getString(3)), rs.getInt(4),
                        rs.getTimestamp(5).toLocalDateTime(), rs.getTimestamp(6).toLocalDateTime(), rs.getString(7), rs.getString(8));
                c.setMessages(getListMessages(c.getId()));
                result.put(c.getTypeChatroom(), c);
            }
            return result;
        } catch (SQLException e) {
            throw new DAOException("Error when listing all chatrooms", e);
	}
    }
    
    public ArrayList<Chatroom> getListChatrooms(int partieID) {
        ArrayList<Chatroom> result = new ArrayList<>();
        try (
	     Connection conn = getConn();
	     PreparedStatement st = conn.prepareStatement("SELECT C.ID, C.Partie_ID, C.TypeChatroom, C.NumeroJour, C.DateDebut, C.DateFin, C.Elimine_ID, C.EspritAppele_ID "
                     + "FROM Chatrooms C "
                     + "WHERE C.Partie_ID = ? "
                     + "ORDER BY C.ID");
	     ) {
            st.setInt(1, partieID);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                Chatroom c = new Chatroom(rs.getInt(1), rs.getInt(2), TypeChatroom.get(rs.getString(3)), rs.getInt(4),
                        rs.getTimestamp(5).toLocalDateTime(), rs.getTimestamp(6).toLocalDateTime(), rs.getString(7), rs.getString(8));
                c.setMessages(getListMessages(c.getId()));
                result.add(c);
            }
            return result;
        } catch (SQLException e) {
            throw new DAOException("Error when listing all chatrooms", e);
	}
    }
    
    public void createChatroom(Chatroom c) {
        try (
            Connection conn = getConn();
            PreparedStatement st = conn.prepareStatement("INSERT INTO Chatrooms (Partie_ID, TypeChatroom, NumeroJour, DateDebut, DateFin) "
                    + "VALUES (?,?,?,?,?)");
                )
        {
            st.setInt(1, c.getPartieID());
            st.setString(2, c.getTypeChatroom().toString());
            st.setInt(3, c.getNumeroJour());
            st.setTimestamp(4, Timestamp.valueOf(c.getDateDebut()));
            st.setTimestamp(5, Timestamp.valueOf(c.getDateFin()));
            if (st.executeUpdate() != 1) {
                throw new SQLException("Could not insert");
            }
        }
        catch (SQLException e) {
            throw new DAOException("Error when creating chatroom", e);
        }
    }
    
    public ArrayList<Message> getListMessages(int chatroomID) {
        ArrayList<Message> result = new ArrayList<>();
        try (
            Connection conn = getConn();
            PreparedStatement st = conn.prepareStatement("SELECT Envoyeur_ID, DateEnvoi, Contenu FROM Messages WHERE Chatroom_ID = ? ORDER BY DateEnvoi");
                )
        {
            st.setInt(1, chatroomID);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                result.add(new Message(chatroomID, rs.getString(1), rs.getTimestamp(2).toLocalDateTime(), rs.getString(3)));
            }
            return result;
        }
        catch (SQLException e) {
            throw new DAOException("Error when listing messages from chatroom " + chatroomID, e);
        }
    }
    
    public boolean sendMessage(Message m) {
        try (
            Connection conn = getConn();
            PreparedStatement st = conn.prepareStatement("INSERT INTO Messages (Chatroom_ID, Envoyeur_ID, DateEnvoi, Contenu) VALUES (?,?,?,?)");
                )
        {
            st.setInt(1, m.getChatroomID());
            st.setString(2, m.getEnvoyeurID());
            st.setTimestamp(3, Timestamp.valueOf(m.getDateEnvoi()));
            st.setString(4, m.getContenu());
            if (st.executeUpdate() != 1) {
                throw new SQLException("Could not insert message");
            }
            return true;
        }
        catch (SQLException e) {
            throw new DAOException("Error when creating message", e);
        }
    }
    
    public int castVote(int partieID, int chatroomID, String votantID, String cibleID) {
        try (
            Connection conn = getConn();
            PreparedStatement st = conn.prepareStatement("INSERT INTO Bulletins (Partie_ID, Chatroom_ID, Votant_ID, Cible_ID) VALUES (?,?,?,?)");
            PreparedStatement st2 = conn.prepareStatement("SELECT NombreVotes FROM Votes WHERE Partie_ID = ? AND Chatroom_ID = ? AND Cible_ID = ?");
                )
        {
            st.setInt(1, partieID);
            st.setInt(2, chatroomID);
            st.setString(3, votantID);
            st.setString(4, cibleID);
            if (st.executeUpdate() != 1) {
                throw new SQLException("Could not insert vote");
            }
            
            st2.setInt(1, partieID);
            st2.setInt(2, chatroomID);
            st2.setString(3, cibleID);
            ResultSet rs = st2.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                return 0;
            }            
        }
        catch (SQLException e) {
            throw new DAOException("Error when creating vote", e);
        }
    }

    public void sentence(int partieID, int chatroomID, String elimineID) {
        try (
            Connection conn = getConn();
            PreparedStatement st = conn.prepareStatement("UPDATE Villageois SET Vivant = 2 WHERE Joueur_ID = ? AND Partie_ID = ?");
            PreparedStatement st2 = conn.prepareStatement("UPDATE Chatrooms SET Elimine_ID = ? WHERE ID = ?");
                )
        {
            st.setString(1, elimineID);
            st.setInt(2, partieID);
            if (st.executeUpdate() != 1) {
                throw new SQLException("Could not eliminate player");
            }
            st2.setString(1, elimineID);
            st2.setInt(2, chatroomID);
            if (st2.executeUpdate() != 1) {
                throw new SQLException("Could not eliminate player");
            }
        }
        catch (SQLException e) {
            throw new DAOException("Error when eliminating player", e);
        }
    }

    public void cancelVote(int partieID, int chatroomID, String votantID, String cibleID) {
        try (
            Connection conn = getConn();
            PreparedStatement st = conn.prepareStatement("DELETE FROM Bulletins WHERE Partie_ID = ? AND Chatroom_ID = ? AND Votant_ID = ? AND Cible_ID = ?");
                )
        {
            st.setInt(1, partieID);
            st.setInt(2, chatroomID);
            st.setString(3, votantID);
            st.setString(4, cibleID);
            if (st.executeUpdate() != 1) {
                throw new SQLException("Could not delete vote");
            }
        }
        catch (SQLException e) {
            throw new DAOException("Error when deleting vote", e);
        }
    }
    
    public HashMap<String, Bulletin> getListBulletins(int chatroomID, String votantID) {
        HashMap<String, Bulletin> resultats = new HashMap<>();
        try (
            Connection conn = getConn();
            PreparedStatement st = conn.prepareStatement("SELECT Partie_ID, Cible_ID FROM Bulletins WHERE Chatroom_ID = ? AND Votant_ID = ?");
                )
        {
            st.setInt(1, chatroomID);
            st.setString(2, votantID);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                resultats.put(rs.getString(2), new Bulletin(rs.getInt(1), chatroomID, votantID, rs.getString(2)));
            }
            return resultats;
        }
        catch (SQLException e) {
            throw new DAOException("Error when getting all bulletins", e);
        }        
    }
    
    public HashMap<String, Vote> getListVotes(int chatroomID) {
        HashMap<String, Vote> resultats = new HashMap<>();
        try (
            Connection conn = getConn();
            PreparedStatement st = conn.prepareStatement("SELECT Partie_ID, Chatroom_ID, Cible_ID, NombreVotes FROM Votes WHERE Chatroom_ID = ? ORDER BY Cible_ID");
                )
        {
            st.setInt(1, chatroomID);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                resultats.put(rs.getString(3), new Vote(rs.getInt(1), rs.getInt(2), rs.getString(3), rs.getInt(4)));
            }
            return resultats;
        }
        catch (SQLException e) {
            throw new DAOException("Error when getting all votes", e);
        }        
    }

    public Revelation reveal(int partieID, int chatroomID, String voyantID, String reveleID) {
        try (
            Connection conn = getConn();
            PreparedStatement st = conn.prepareStatement("SELECT RoleVillageois, PouvoirVillageois FROM Villageois WHERE Partie_ID = ? AND Joueur_ID = ?");
            PreparedStatement st2 = conn.prepareStatement("INSERT INTO Revelations (Partie_ID, Chatroom_ID, Voyant_ID, Revele_ID, RoleDecouvert, PouvoirDecouvert) VALUES (?,?,?,?,?,?)");
                )
        {
            st.setInt(1, partieID);
            st.setString(2, reveleID);
            ResultSet rs = st.executeQuery();
            Revelation revelation;
            if (rs.next()) {
                revelation = new Revelation(partieID, chatroomID, voyantID, reveleID, Role.get(rs.getString(1)), Pouvoir.get(rs.getString(2)));
            } else {
                throw new SQLException("Could not verify role");
            }
            
            st2.setInt(1, partieID);
            st2.setInt(2, chatroomID);
            st2.setString(3, voyantID);
            st2.setString(4, reveleID);
            st2.setString(5, revelation.getRoleDecouvert().toString());
            st2.setString(6, revelation.getPouvoirDecouvert().toString());
            if (st2.executeUpdate() != 1) {
                throw new SQLException("Could not insert revelation");
            }
            
            return revelation;
        }
        catch (SQLException e) {
            throw new DAOException("Error when revealing role", e);
        }
    }
    
    public HashMap<String, Revelation> getListRevelations(int partieID, String voyantID) {
        HashMap<String, Revelation> resultats = new HashMap<>();
        try (
            Connection conn = getConn();
            PreparedStatement st = conn.prepareStatement(""
                    + "SELECT Chatroom_ID, Revele_ID, RoleDecouvert, PouvoirDecouvert FROM Revelations WHERE Partie_ID = ? AND Voyant_ID = ?");
                )
        {
            st.setInt(1, partieID);
            st.setString(2, voyantID);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                resultats.put(rs.getString(2), new Revelation(partieID, rs.getInt(1), voyantID, rs.getString(2), Role.get(rs.getString(3)), Pouvoir.get(rs.getString(4))));
            }
            return resultats;
        }
        catch (SQLException e) {
            throw new DAOException("Error when getting revelations", e);
        }        
    }

    public void contaminate(int partieID, int chatroomID, String contaminateurID, String contamineID) {
        try (
            Connection conn = getConn();
            PreparedStatement st = conn.prepareStatement("UPDATE Villageois SET RoleVillageois = 'Loup-Garou' WHERE Partie_ID = ? AND Joueur_ID = ?");
            PreparedStatement st2 = conn.prepareStatement("INSERT INTO Contaminations (Partie_ID, Chatroom_ID, Contaminateur_ID, Contamine_ID) VALUES (?,?,?,?)");
                )
        {
            st.setInt(1, partieID);
            st.setString(2, contamineID);
            if (st.executeUpdate() != 1) {
                throw new SQLException("Could not update villager");
            }
            st2.setInt(1, partieID);
            st2.setInt(2, chatroomID);
            st2.setString(3, contaminateurID);
            st2.setString(4, contamineID);
            ResultSet rs = st2.executeQuery();
            if (st.executeUpdate() != 1) {
                throw new SQLException("Could not insert contamination");
            }
        }
        catch (SQLException e) {
            throw new DAOException("Error when contaminating", e);
        }
    }
    
    public HashMap<String, Contamination> getListContaminations(int partieID, String contaminateurID) {
        HashMap<String, Contamination> resultats = new HashMap<>();
        try (
            Connection conn = getConn();
            PreparedStatement st = conn.prepareStatement(""
                    + "SELECT Chatroom_ID, Contamine_ID FROM Contaminations WHERE Partie_ID = ? AND Contaminateur_ID = ?");
                )
        {
            st.setInt(1, partieID);
            st.setString(2, contaminateurID);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                resultats.put(rs.getString(2), new Contamination(partieID, rs.getInt(1), contaminateurID, rs.getString(2)));
            }
            return resultats;
        }
        catch (SQLException e) {
            throw new DAOException("Error when getting contaminations", e);
        }        
    }
    
    public void callSpirit(int chatroomID, String appeleID) {
        try (
            Connection conn = getConn();
            PreparedStatement st = conn.prepareStatement("UPDATE Chatrooms SET EspritAppele_ID = ? WHERE ID = ?");
                )
        {
            st.setString(1, appeleID);
            st.setInt(2, chatroomID);
            if (st.executeUpdate() != 1) {
                throw new SQLException("Could not update chatrooms");
            }
        }
        catch (SQLException e) {
            throw new DAOException("Error when calling spirit", e);
        }
    }

}
