package database;


import Singleton.DatabaseManager;
import client.User;

import java.security.MessageDigest;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SelectUserQuery {

    private String mUsername;
    private String mHashedPW;
    private User   mUser;

    public SelectUserQuery(String user, char[] textPW){
        this.mUsername = user;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(new String(textPW).getBytes());
            byte[] digest = md.digest();
            StringBuffer sb = new StringBuffer();
            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xff));
            }
            mHashedPW = sb.toString();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public User execute(){
        Statement stmt = null;
        PreparedStatement prep = null;
        ResultSet rs = null;

        try {
            prep = DatabaseManager.getInstance().prepareStatement("SELECT * FROM LoginInfo " +
                    "WHERE username = ?" +
                    "AND password = ?");
            prep.setString(1,this.mUsername);
            prep.setString(2,this.mHashedPW);
            prep.execute();
            prep.getResultSet().last();
            rs = prep.getResultSet();
            if(rs.getRow() == 1){
                mUser = new User(Integer.parseInt(rs.getString(1)),this.mUsername, this.mHashedPW);
            }
        }
        catch (SQLException ex){
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
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
        return mUser;
    }
}
