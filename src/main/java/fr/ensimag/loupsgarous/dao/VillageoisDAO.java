package fr.ensimag.loupsgarous.dao;

import fr.ensimag.loupsgarous.modele.Pouvoir;
import fr.ensimag.loupsgarous.modele.Role;
import fr.ensimag.loupsgarous.modele.Villageois;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import javax.sql.DataSource;

/**
 *
 * @author baplar
 */
public class VillageoisDAO extends AbstractDataBaseDAO {
    
    public VillageoisDAO(DataSource ds) {
        super(ds);
    }
    
    public Villageois getVillageois(String joueurID, int partieID) {
        try (
            Connection conn = getConn();
            PreparedStatement st = conn.prepareStatement("SELECT RoleVillageois, PouvoirVillageois, Vivant FROM Villageois WHERE Joueur_ID = ? AND Partie_ID = ?");
        ) {
            st.setString(1, joueurID);
            st.setInt(2, partieID);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                return new Villageois(joueurID, partieID, Role.get(rs.getString(1)), Pouvoir.get(rs.getString(2)), rs.getInt(3) > 0);
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new DAOException("Error when getting character from server " + partieID, e);
        }
    }
    
    public ArrayList<Villageois> getListePersonnages(String joueurID) {
        ArrayList<Villageois> result = new ArrayList<>();
        try (
            Connection conn = getConn();
            PreparedStatement st = conn.prepareStatement("SELECT Partie_ID, RoleVillageois, PouvoirVillageois, Vivant FROM Villageois WHERE Joueur_ID = ? ORDER BY Partie_ID");
        ) {
            st.setString(1, joueurID);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                result.add(new Villageois(joueurID, rs.getInt(1), Role.get(rs.getString(2)), Pouvoir.get(rs.getString(3)), rs.getInt(4) > 0));
            }
            return result;
        } catch (SQLException e) {
            throw new DAOException("Error when looking up characters of " + joueurID, e);
        }
    }
    
    public HashMap<String, Villageois> getListeVillageois(int idPartie) {
        HashMap<String, Villageois> result = new HashMap<>();
        try (
            Connection conn = getConn();
            PreparedStatement st = conn.prepareStatement("SELECT Joueur_ID, RoleVillageois, PouvoirVillageois, Vivant FROM Villageois WHERE Partie_ID = ? ORDER BY Joueur_ID");
        ) {
            st.setInt(1, idPartie);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                result.put(rs.getString(1), new Villageois(rs.getString(1), idPartie, Role.get(rs.getString(2)), Pouvoir.get(rs.getString(3)), rs.getInt(4) > 0));
            }
            return result;
        } catch (SQLException e) {
            throw new DAOException("Error when looking up villagers of server " + idPartie, e);
        }
    }
    
    public int getNombreVillageois(int partieID) {
        try (
            Connection conn = getConn();
            PreparedStatement st = conn.prepareStatement("SELECT COUNT(Joueur_ID) FROM Villageois WHERE Partie_ID = ? AND Vivant > 0");
                )
        {
            st.setInt(1, partieID);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                return 0;
            }
        }
        catch (SQLException e) {
            throw new DAOException("Error when getting villager number", e);
        }
    }
    
    public int getNombreLG(int partieID) {
        try (
            Connection conn = getConn();
            PreparedStatement st = conn.prepareStatement("SELECT COUNT(Joueur_ID) FROM Villageois WHERE Partie_ID = ? AND Vivant > 0 AND RoleVillageois = 'Loup-Garou'");
                )
        {
            st.setInt(1, partieID);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                return 0;
            }
        }
        catch (SQLException e) {
            throw new DAOException("Error when getting werewolf number", e);
        }
    }
    
    public int getNombreVotants(int partieID, int chatroomID) {
        try (
            Connection conn = getConn();
            PreparedStatement st = conn.prepareStatement("SELECT TypeChatroom FROM Chatrooms WHERE ID = ?");
                )
        {
            st.setInt(1, chatroomID);
            ResultSet rs = st.executeQuery();
            if (! rs.next()) {
                throw new SQLException("The chatroom does not exist");
            }
            switch (rs.getString(1)) {
            case "Village":
                return getNombreVillageois(partieID);
            case "Loups-Garous":
                return getNombreLG(partieID);
            default:
                return 0;
            }
        }
        catch (SQLException e) {
            throw new DAOException("Error when creating vote", e);
        }
    }

    public boolean rejoindrePartie(String joueurID, int partieID) {
        Villageois v = new Villageois(joueurID, partieID);
        try (
            Connection conn = getConn();
            PreparedStatement st = conn.prepareStatement("INSERT INTO Villageois (Joueur_ID, Partie_ID) VALUES (?, ?)");
        ) {
            st.setString(1, v.getJoueurID());
            st.setInt(2, v.getPartieID());
            if (st.executeUpdate() != 1) {
                throw new SQLException("Could not insert villager");
            }
            return true;
        } catch (SQLException e) {
            throw new DAOException("Error when joining server " + partieID, e);
        }
    }
        
    
    public void setRolesPouvoirs(ArrayList<Villageois> villageois) {
        try (
            Connection conn = getConn();
            PreparedStatement st = conn.prepareStatement("UPDATE Villageois SET RoleVillageois = ? , PouvoirVillageois = ? WHERE Joueur_ID = ? AND Partie_ID = ?");
        ) {
            for (Villageois v : villageois) {
                st.setString(1, v.getRoleVillageois().toString());
                if (v.getPouvoirVillageois() == null) {
                    st.setNull(2, java.sql.Types.NULL);
                } else {
                    st.setString(2, v.getPouvoirVillageois().toString());
                }
                st.setString(3, v.getJoueurID());
                st.setInt(4, v.getPartieID());
                if (st.executeUpdate() != 1) {
                    throw new SQLException("Could not update villager");
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error when updating villager roles", e);
        }
    }
    
    public void tuerVillageois(int partieID, String joueurID) {
        try (
            Connection conn = getConn();
            PreparedStatement st = conn.prepareStatement("UPDATE Villageois SET Vivant = 0 WHERE Joueur_ID = ? AND Partie_ID = ?");
        ) {
            st.setString(1, joueurID);
            st.setInt(2, partieID);
            st.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Error when killing villager", e);
        }
    }    
}
