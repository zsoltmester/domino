package domino.server;

import domino.Config;
import domino.DominoStorageIface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class DominoStorageImpl extends UnicastRemoteObject implements DominoStorageIface {

    private static final long serialVersionUID = 1L;

    private Connection databaseConnection;

    DominoStorageImpl(String databaseFilePath) throws RemoteException, SQLException, ClassNotFoundException {
        this.databaseConnection = connectToDatabase(databaseFilePath);
        this.databaseConnection.setAutoCommit(true);
        resetDominosTable();
    }

    @Override
    public void save(String userName, List<String> dominos) throws RemoteException {
        try {
            deleteDominosForUser(userName);
            try (PreparedStatement statement =
                         this.databaseConnection.prepareStatement("insert into dominos values (?,?,?);")) {
                for (int i = 0; i < dominos.size(); i++) {
                    statement.setString(1, userName);
                    statement.setString(2, dominos.get(i));
                    statement.setInt(3, i + 1);
                    statement.addBatch();
                }
                this.databaseConnection.setAutoCommit(false);
                statement.executeBatch();
                this.databaseConnection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RemoteException();
        }
    }

    @Override
    public List<String> load(String userName) throws RemoteException {
        Map<Integer, String> dominosWithIndex = new HashMap<>();
        try (PreparedStatement statement =
                     this.databaseConnection.prepareStatement("select * from dominos where dominos.user = ?;");) {
            statement.setString(1, userName);
            try (ResultSet results = statement.executeQuery()) {
                while (results.next()) {
                    String domino = results.getString("domino");
                    int index = results.getInt("idx");
                    dominosWithIndex.put(index, domino);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RemoteException();
        }
        List<String> dominos = new ArrayList<>(dominosWithIndex.size());
        for (int i = 0; i < dominosWithIndex.size(); i++) {
            dominos.add(dominosWithIndex.get(i + 1));
        }
        return dominos;
    }

    private static Connection connectToDatabase(String databaseFilePath) throws ClassNotFoundException, SQLException {
        String url = Config.DATABASE_URL_PREFIX + databaseFilePath;
        String user = "sa";
        String password = "";
        Class.forName(Config.DATABASE_DRIVER_CLASS);
        Connection connection = DriverManager.getConnection(url, user, password);
        return connection;
    }

    private void resetDominosTable() throws SQLException {
        try (Statement statement = this.databaseConnection.createStatement()) {
            statement.executeUpdate("drop table if exists dominos;");
            statement.executeUpdate("create table dominos (user varchar(32), domino varchar(16), idx int);");
        }
    }

    private void deleteDominosForUser(String userName) throws SQLException {
        try (PreparedStatement statement =
                     this.databaseConnection.prepareStatement("delete from dominos where user = ?;")) {
            statement.setString(1, userName);
            statement.executeUpdate();
        }
    }
}
