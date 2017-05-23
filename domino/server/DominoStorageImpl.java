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

        /*
        assert load("asd").size() == 0;
        save("asd", Arrays.asList("1 1", "2 2", "3 3", "4 4", "5 5"));
        List<String> dominos = load("asd");
        assert dominos.size() == 5;
        assert dominos.get(0).equals("1 1");
        assert dominos.get(1).equals("2 2");
        assert dominos.get(2).equals("3 3");
        assert dominos.get(3).equals("4 4");
        assert dominos.get(4).equals("5 5");

        save("fgh", Arrays.asList("a a", "b b"));
        assert load("asd").size() == 5;
        assert load("fgh").size() == 2;

        save("fgh", Arrays.asList("x x", "y y", "z z"));
        dominos = load("fgh");
        assert dominos.size() == 3;
        assert dominos.get(0).equals("x x");
        assert dominos.get(1).equals("y y");
        assert dominos.get(2).equals("z z");

        resetDominosTable();
        assert load("asd").size() == 0;
        assert load("fgh").size() == 0;

        System.out.println("DB tests are OK! :)");
        */
    }

    @Override
    public void save(String userName, List<String> dominos) throws RemoteException {
        try {
            deleteDominosForUser(userName);
            try (Statement statement = this.databaseConnection.createStatement()) {
                for (int i = 0; i < dominos.size(); i++) {
                    statement.addBatch(getInsertDominoStatement(userName, dominos.get(i), i + 1));
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
        try {
            try (Statement statement = this.databaseConnection.createStatement();
                 ResultSet results = statement.executeQuery("select * from dominos where user = '" + userName + "';")) {
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
        try (Statement statement = this.databaseConnection.createStatement()) {
            statement.executeUpdate("delete from dominos where user = '" + userName + "';");
        }
    }

    private String getInsertDominoStatement(String userName, String domino, int index) throws SQLException {
        return "insert into dominos values ('" + userName + "','" + domino + "'," + index + ");";
    }
}
