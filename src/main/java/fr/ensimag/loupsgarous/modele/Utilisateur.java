package fr.ensimag.loupsgarous.modele;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 *
 * @author lartigab
 */
public class Utilisateur {
    private String login;
    private byte[] salt;
    private byte[] hashedPassword;

    public Utilisateur(String login, byte[] salt, byte[] hashedPassword) {
        this.login = login;
        this.salt = salt;
        this.hashedPassword = hashedPassword;
    }
    
    public Utilisateur(String login, String password) {
        this.login = login;
        try {
            this.salt = new byte[32];
            SecureRandom.getInstance("SHA1PRNG").nextBytes(this.salt);
            PBEKeySpec k = new PBEKeySpec(password.toCharArray(), this.salt, 128, 256);
            SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            this.hashedPassword = f.generateSecret(k).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    public String getLogin() {
        return login;
    }

    public byte[] getSalt() {
        return salt;
    }

    public byte[] getHashedPassword() {
        return hashedPassword;
    }
    
    public boolean checkPassword(String password) {
        try {
            PBEKeySpec k = new PBEKeySpec(password.toCharArray(), this.salt, 128, 256);
            SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] testedHash = f.generateSecret(k).getEncoded();
            return Arrays.equals(testedHash, this.hashedPassword);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }
}
