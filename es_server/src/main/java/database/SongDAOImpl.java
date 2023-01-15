package database;

import common.Song;
import common.interfaces.SongDAO;
import server.ServerApp;
import server.ServerLogger;

import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SongDAOImpl implements SongDAO {

    public SongDAOImpl(Registry registry) throws RemoteException {
        SongDAO songDAOStub = (SongDAO) UnicastRemoteObject.exportObject(this, 3939);
        registry.rebind("SongService", songDAOStub);
    }

    public int getSongEmotionRating(int userId, int songId) {
        Connection conn = ServerApp.getConnection();

        String query = "SELECT SE.rating " +
                "FROM song_emotion SE " +
                "JOIN emotions E ON SE.emotion_id = E.id " +
                "JOIN songs S ON S.id = SE.song_id " +
                "JOIN users U on U.id = SE.user_id " +
                "WHERE S.id = " + songId + ", U.id = " + userId;

        return 0;
    }

    public HashMap<Integer, Integer> getSongEmotions(int songId) {
        Connection conn = ServerApp.getConnection();

        String query = "SELECT E.id, COUNT(E.id) " +
                "FROM song_emotion SE " +
                "JOIN emotions E ON SE.emotion_id = E.id " +
                "JOIN songs S ON S.id = SE.song_id " +
                "WHERE S.id = " + songId +
                " GROUP BY E.id";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            HashMap<Integer, Integer> results = new HashMap<>();

            while (rs.next()) {
                int emoId = rs.getInt("id");
                int count = rs.getInt("count");

                results.put(emoId, count);
                System.out.println(emoId + " " + count);

            }
            return results;

        } catch (SQLException ex) {
            ServerLogger.error("Error: " + ex);
        }

        return null;
    }

    public int getSongEmotionsCount(int songId) {
        Connection conn = ServerApp.getConnection();

        String query = "SELECT COUNT(E.id) " +
                "FROM song_emotion SE " +
                "JOIN emotions E ON SE.emotion_id = E.id " +
                "JOIN songs S ON S.id = SE.song_id " +
                "WHERE S.id = " + songId;

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            int count = 0;

            while (rs.next()) {

                count = rs.getInt("count");

            }

            return count;

        } catch (SQLException ex) {
            ServerLogger.error("Error: " + ex);
        }

        return 0;
    }

    public void setSongEmotion(int userId, int songId, int emotionId, int rating) {
        Connection conn = ServerApp.getConnection();
        String query = "INSERT INTO song_emotion (user_id, song_id, emotion_id, rating) VALUES (?,?,?,?) " +
                "ON CONFLICT( user_id, song_id, emotion_id) DO UPDATE " +
                "SET rating = excluded.rating;";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, songId);
            stmt.setInt(3, emotionId);
            stmt.setInt(4, rating);
            stmt.execute();

        } catch (SQLException ex) {
            ServerLogger.error("Error: " + ex);
        }
    }

    public List<Song> searchByString(String searchString) {
        Connection conn = ServerApp.getConnection();
//        String query = "SELECT * "
//                + "FROM songs "
//                + "WHERE author LIKE '%"
//                + searchString + "%'"
//                + "OR title LIKE '%"
//                + searchString + "%'"
//                + "OR album LIKE '%"
//                + searchString + "%'";

        String query = "SELECT * "
                + "FROM songs "
                + "WHERE (author, title, album)::text "
                + "ILIKE ('%"
                + searchString + "%')";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            List<Song> results = new ArrayList<>();

            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String author = rs.getString("author");
                int year = rs.getInt("year");
                String album = rs.getString("album");
                String genre = rs.getString("genre");
                Integer duration = rs.getInt("duration") > 0 ? rs.getInt("duration") : null;

//              System.out.println(id + "\t" + year + "\t" + author + "\t" + title + "\t" + album + "\t" + genre + "\t" + duration);
                results.add(new Song(id, title, author, year, album, genre, duration));

            }
            return results;

        } catch (SQLException ex) {
            ServerLogger.error("Error: " + ex);
        }

        return null;
    }
}
