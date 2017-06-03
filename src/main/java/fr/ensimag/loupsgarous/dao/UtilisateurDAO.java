package fr.ensimag.loupsgarous.dao;

import fr.ensimag.loupsgarous.modele.Utilisateur;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;

/**
 *
 * @author baplar
 */
public class UtilisateurDAO extends AbstractDataBaseDAO {
    
    public UtilisateurDAO(DataSource ds) {
        super(ds);
    }
    
    public Utilisateur getUtilisateur(String login) {
        try (
            Connection conn = getConn();
            PreparedStatement st = conn.prepareStatement("SELECT Salt, HashedPassword FROM Utilisateurs WHERE Login = ?");
        ) {
            st.setString(1, login);
            ResultSet r = st.executeQuery();
            if (r.next()) {
               return new Utilisateur(login, r.getBytes(1), r.getBytes(2));
            } else {
               return null;
            }
        } catch (SQLException e) {
            throw new DAOException("Error when searching for user " + login, e);
        }
    }
    
    public boolean createUtilisateur(Utilisateur u) {
        try (
            Connection conn = getConn();
            PreparedStatement st = conn.prepareStatement("INSERT INTO Utilisateurs (Login, Salt, HashedPassword) VALUES (?, ?, ?)");
        ) {
            st.setString(1, u.getLogin());
            st.setBytes(2, u.getSalt());
            st.setBytes(3, u.getHashedPassword());
            if (st.executeUpdate() != 1) {
                throw new SQLException("Could not insert user");
            }
            return true;
        } catch (SQLException e) {
            throw new DAOException("Error when creating user " + u.getLogin(), e);
        }
    }
}
