
package fr.ensimag.loupsgarous.dao;

public class DAOException extends RuntimeException {

    public DAOException() {
    }

    public DAOException(String message) {
        super(message);
    }

    public DAOException(String message,Throwable cause) {
        super(message, cause);
    }

}
