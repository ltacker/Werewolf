package fr.ensimag.loupsgarous.dao;

import fr.ensimag.loupsgarous.modele.Partie;
import fr.ensimag.loupsgarous.modele.Pouvoir;
import fr.ensimag.loupsgarous.modele.ProbaPouvoir;
import fr.ensimag.loupsgarous.modele.Role;
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
public class PartieDAO extends AbstractDataBaseDAO {
    
    public PartieDAO(DataSource ds) {
        super(ds);
    }
        
    public HashMap<Integer, Partie> getListPartiesDisponibles() {
        HashMap<Integer, Partie> result = new HashMap<>();
        try (
	     Connection conn = getConn();
	     PreparedStatement st = conn.prepareStatement("SELECT "
                     + "P.ID, P.NomPartie, P.DateDebut, P.DureeJour, P.DureeNuit, "
                     + "P.MinParticipants, P.MaxParticipants, P.ProportionLG, P.Vainqueurs, P.NombreParticipants "
                     + "FROM PartiesView P WHERE P.DateDebut > CURRENT_TIMESTAMP AND P.Vainqueurs IS NULL "
                     + "ORDER BY P.ID");
	     ) {
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                Partie p = new Partie(rs.getInt(1), rs.getString(2), rs.getTimestamp(3).toLocalDateTime(),
                    rs.getInt(4), rs.getInt(5),
                    rs.getInt(6), rs.getInt(7), rs.getFloat(8), Role.get(rs.getString(9)), rs.getInt(10));
                result.put(p.getId(), p);
            }
        } catch (SQLException e) {
            throw new DAOException("Error when listing all servers", e);
	}
	return result;
    }
    
    public HashMap<Integer, Partie> getListPartiesRejointes(String joueurID) {
        HashMap<Integer, Partie> result = new HashMap<>();
        try (
	     Connection conn = getConn();
	     PreparedStatement st = conn.prepareStatement("SELECT "
                     + "P.ID, P.NomPartie, P.DateDebut, P.DureeJour, P.DureeNuit, "
                     + "P.MinParticipants, P.MaxParticipants, P.ProportionLG, P.Vainqueurs, P.NombreParticipants "
                     + "FROM PartiesView P LEFT OUTER JOIN Villageois V ON V.Partie_ID = P.ID "
                     + "WHERE V.Joueur_ID = ? "
                     + "ORDER BY P.ID");
	     ) {
            st.setString(1, joueurID);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                Partie p = new Partie(rs.getInt(1), rs.getString(2), rs.getTimestamp(3).toLocalDateTime(),
                    rs.getInt(4), rs.getInt(5),
                    rs.getInt(6), rs.getInt(7), rs.getFloat(8), Role.get(rs.getString(9)), rs.getInt(10));
                result.put(p.getId(), p);
            }
        } catch (SQLException e) {
            throw new DAOException("Error when listing joined servers", e);
	}
	return result;
    }
    
    public Partie getPartie(int id) {
        try (
	     Connection conn = getConn();
	     PreparedStatement st = conn.prepareStatement("SELECT "
                     + "P.ID, P.NomPartie, P.DateDebut, P.DureeJour, P.DureeNuit, "
                     + "P.MinParticipants, P.MaxParticipants, P.ProportionLG, P.Vainqueurs, P.NombreParticipants "
                     + "FROM PartiesView P WHERE P.ID = ? ");
	     ) {
            st.setInt(1, id);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                return new Partie(rs.getInt(1), rs.getString(2), rs.getTimestamp(3).toLocalDateTime(),
                    rs.getInt(4), rs.getInt(5),
                    rs.getInt(6), rs.getInt(7), rs.getFloat(8), Role.get(rs.getString(9)), rs.getInt(10));
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new DAOException("Erreur when searching for server " + id, e);
	}
    }
    
    public HashMap<Pouvoir, ProbaPouvoir> getProbasPouvoirs(int idPartie) {
        HashMap<Pouvoir, ProbaPouvoir> result = new HashMap<>();
        try (
	     Connection conn = getConn();
	     PreparedStatement st = conn.prepareStatement("SELECT Partie_ID, "
                     + "Pouvoir, Proba FROM ProbasPouvoirs WHERE Partie_ID = ?");
	     ) {
            st.setInt(1, idPartie);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                ProbaPouvoir p = new ProbaPouvoir(rs.getInt(1), Pouvoir.get(rs.getString(2)), rs.getFloat(3));
                result.put(p.getPouvoir(), p);
            }
        } catch (SQLException e) {
            throw new DAOException("Error when searching for the power probabilities for server " + idPartie, e);
	}
	return result;
    }
    
    public boolean createPartie(Partie partie, HashMap<Pouvoir, ProbaPouvoir> probasPouvoirs) {
        try (
            Connection conn = getConn();
            PreparedStatement insertPartie = conn.prepareStatement("INSERT INTO Parties "
                    + "(NomPartie, DateDebut, DureeJour, DureeNuit, MinParticipants, MaxParticipants, ProportionLG) "
                    + "VALUES (?,?,?,?,?,?,?)");
            PreparedStatement getID = conn.prepareStatement("SELECT MAX(ID) FROM Parties WHERE NomPartie = ?");
            PreparedStatement insertProba = conn.prepareStatement("INSERT INTO ProbasPouvoirs (Partie_ID, Pouvoir, Proba) VALUES (?,?,?)");
        ) {
            insertPartie.setString(1, partie.getNomPartie());
            insertPartie.setTimestamp(2, Timestamp.valueOf(partie.getDateDebut()));
            insertPartie.setInt(3, partie.getDureeJour());
            insertPartie.setInt(4, partie.getDureeNuit());
            insertPartie.setInt(5, partie.getMinParticipants());
            insertPartie.setInt(6, partie.getMaxParticipants());
            insertPartie.setFloat(7, partie.getProportionLG());
            if (insertPartie.executeUpdate() != 1) {
                throw new SQLException("Could not insert server");
            }
            
            getID.setString(1, partie.getNomPartie());
            ResultSet rs = getID.executeQuery();
            if (! rs.next()) {
                throw new SQLException("Could not find inserted server");
            }
            int id = rs.getInt(1);
            
            for (ProbaPouvoir p : probasPouvoirs.values()) {
                insertProba.setInt(1, id);
                insertProba.setString(2, p.getPouvoir().toString());
                insertProba.setFloat(3, p.getProba());
                if (insertProba.executeUpdate() != 1) {
                    throw new SQLException("Could not insert probability");
                }
            }
            return true;
            
        } catch (SQLException e) {
            throw new DAOException("Error when creating server " + partie.getNomPartie(), e);
        }                
    }

    public boolean deletePartie(int id) {
        try (
                Connection conn = getConn();
                PreparedStatement st = conn.prepareStatement("DELETE FROM Parties WHERE ID = ? ");
	     ) {
            st.setInt(1, id);
            return (st.executeUpdate() != 0);
        } catch (SQLException e) {
            throw new DAOException("Error when deleting server " + id, e);
	}
    }

    public HashMap<Integer, Partie> getListPartiesADemarrer() {
        HashMap<Integer, Partie> result = new HashMap<>();
        try (
	     Connection conn = getConn();
	     PreparedStatement st = conn.prepareStatement("SELECT "
                     + "P.ID, P.NomPartie, P.DateDebut, P.DureeJour, P.DureeNuit, "
                     + "P.MinParticipants, P.MaxParticipants, P.ProportionLG, P.Vainqueurs, P.NombreParticipants "
                     + "FROM PartiesView P LEFT OUTER JOIN Chatrooms C ON C.Partie_ID = P.ID "
                     + "WHERE CURRENT_TIMESTAMP >= (P.DateDebut - INTERVAL '5' SECOND) AND P.Vainqueurs IS NULL "
                     + "GROUP BY P.ID, P.NomPartie, P.DateDebut, P.DureeJour, P.DureeNuit, "
                     + "P.MinParticipants, P.MaxParticipants, P.ProportionLG, P.Vainqueurs, P.NombreParticipants "
                     + "HAVING COUNT(C.ID) = 0");
	     ) {
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                Partie p = new Partie(rs.getInt(1), rs.getString(2), rs.getTimestamp(3).toLocalDateTime(),
                    rs.getInt(4), rs.getInt(5),
                    rs.getInt(6), rs.getInt(7), rs.getFloat(8), Role.get(rs.getString(9)), rs.getInt(10));
                result.put(p.getId(), p);
            }
        } catch (SQLException e) {
            throw new DAOException("Error when listing all new servers", e);
	}
	return result;
    }
    
    public boolean terminerPartie(int partieID, Role vainqueurs) {
        try (
                Connection conn = getConn();
                PreparedStatement st = conn.prepareStatement("UPDATE Parties SET Vainqueurs = ? WHERE ID = ? ");
	     ) {
            st.setString(1, vainqueurs.toString());
            st.setInt(2, partieID);
            return (st.executeUpdate() != 0);
        } catch (SQLException e) {
            throw new DAOException("Error when stopping game " + partieID, e);
	}
    }
}
