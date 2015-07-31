package database;

import singleton.DatabaseManager;
import util.Utilities;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @class InsertUserQuery
 * @desc Class used to insert a user into the database
 */

public class InsertUserQuery{
    private String mUsername;
    private String mHashedPW;

    public InsertUserQuery(String username, char[] textPW){
        this.mUsername = username;
        this.mHashedPW = Utilities.sha256(textPW);
    }

    public boolean execute(){
        Statement stmt = null;
        PreparedStatement prep = null;
        ResultSet rs = null;

        try {
            stmt = DatabaseManager.getInstance().createStatement();
            stmt.execute("SELECT * FROM LoginInfo");
            rs = stmt.getResultSet();
            if(rs.getRow() != 0){
                return false;
            }

            prep = DatabaseManager.getInstance().prepareStatement("INSERT INTO LoginInfo(username,password)" +
                    "VALUES(?,?)");
            prep.setString(1,this.mUsername);
            prep.setString(2, this.mHashedPW);
            prep.execute();
            return true;
        }
        catch (SQLException ex){
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
            return false;
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                }catch (SQLException sqlEx) {}
                rs = null;
            }

            if (stmt != null) {
                try {
                    stmt.close();
                }catch (SQLException sqlEx) {}
                stmt = null;
            }
            if(prep != null){
                try{
                    prep.close();
                }catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
