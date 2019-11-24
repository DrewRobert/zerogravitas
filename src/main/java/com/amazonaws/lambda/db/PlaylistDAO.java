package com.amazonaws.lambda.db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.amazonaws.lambda.model.Playlist;

public class PlaylistDAO {

	java.sql.Connection conn;

    public PlaylistDAO() {
    	try  {
    		conn = DatabaseUtil.connect();
    	} catch (Exception e) {
    		conn = null;
    	}
    }

    public Playlist getPlaylist(String name) throws Exception {
        
        try {
            Playlist playlist = null;
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM constants WHERE name=?;");
            ps.setString(1,  name);
            ResultSet resultSet = ps.executeQuery();
            
            while (resultSet.next()) {
                playlist = generatePlaylist(resultSet);
            }
            resultSet.close();
            ps.close();
            
            return playlist;

        } catch (Exception e) {
        	e.printStackTrace();
            throw new Exception("Failed in getting constant: " + e.getMessage());
        }
    }
    
    public boolean updatePlaylist(Playlist playlist) throws Exception {
        try {
        	String query = "UPDATE constants SET value=? WHERE name=?;";
        	PreparedStatement ps = conn.prepareStatement(query);
            //ps.setDouble(1, playlist.value);
            ps.setString(2, playlist.name);
            int numAffected = ps.executeUpdate();
            ps.close();
            
            return (numAffected == 1);
        } catch (Exception e) {
            throw new Exception("Failed to update report: " + e.getMessage());
        }
    }
    
    public boolean deletePlaylist(Playlist playlist) throws Exception {
        try {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM constants WHERE name = ?;");
            ps.setString(1, playlist.name);
            int numAffected = ps.executeUpdate();
            ps.close();
            
            return (numAffected == 1);

        } catch (Exception e) {
            throw new Exception("Failed to insert constant: " + e.getMessage());
        }
    }


    public boolean addPlaylist(Playlist playlist) throws Exception {
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM constants WHERE name = ?;");
            ps.setString(1, playlist.name);
            ResultSet resultSet = ps.executeQuery();
            
            // already present?
            while (resultSet.next()) {
                Playlist c = generatePlaylist(resultSet);
                resultSet.close();
                return false;
            }

            ps = conn.prepareStatement("INSERT INTO constants (name,value) values(?,?);");
            ps.setString(1,  playlist.name);
            //ps.setDouble(2,  playlist.value);
            ps.execute();
            return true;

        } catch (Exception e) {
            throw new Exception("Failed to insert constant: " + e.getMessage());
        }
    }

    public List<Playlist> getAllPlaylists() throws Exception {
        
        List<Playlist> allConstants = new ArrayList<>();
        try {
            Statement statement = conn.createStatement();
            String query = "SELECT * FROM constants";
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                Playlist c = generatePlaylist(resultSet);
                allConstants.add(c);
            }
            resultSet.close();
            statement.close();
            return allConstants;

        } catch (Exception e) {
            throw new Exception("Failed in getting books: " + e.getMessage());
        }
    }
    
    private Playlist generatePlaylist(ResultSet resultSet) throws Exception {
        String name  = resultSet.getString("name");
        //Double value = resultSet.getDouble("value");
        return new Playlist(name);
    }

}