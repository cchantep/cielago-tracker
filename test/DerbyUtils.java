package cielago;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;

/**
 * Derby utility.
 *
 * @author Cedric Chantepie
 */
public final class DerbyUtils {
    static {
        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        } // end of catch
    } // end of <cinit>

    /**
     * Returns a derby connection.
     *
     * @param jdbcUrl 
     */
    public static Connection getConnection(String jdbcUrl) 
        throws SQLException {

        return DriverManager.getConnection(jdbcUrl);
    } // end of getConnection

    /**
     * Shutdowns embedded derby engine.
     *
     * @return true if derby has been successfully shut down, or false it not
     */
    public static boolean shutdown() {
        try {
            DriverManager.getConnection("jdbc:derby:;shutdown=true");

            return false;
        } catch (SQLException e) {
            System.err.println("DERBY: " + e.getMessage());

            return true;
        } // end of catch
    } // end of shutdown
} // end of class DerbyUtils