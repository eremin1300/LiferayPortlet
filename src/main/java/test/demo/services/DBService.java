package test.demo.services;

import test.demo.model.Vacancy;

import java.sql.*;
import java.util.ArrayList;

public class DBService {

    Logger logger = new Logger();

    public void addToDb(ArrayList<Vacancy> list) throws SQLException {
        Connection connection = null;
        Statement statement = null;
        for (Vacancy vac : list) {
            String insertTableSQL = "INSERT INTO vacancy"
                    + "(id, vacname, vacorganization, vacsalary, vacpublicdate) " + "VALUES"
                    + "(" + vac.getId() + ",'" + vac.getName() + "','" + vac.getOrganization()
                    + "','" + vac.getSalary() + "','" + vac.getDateOfPublic() + "')";

            try {
                connection = getDBConnection();
                statement = connection.createStatement();
                statement.execute(insertTableSQL);
            } catch (SQLException e) {
                logger.logThisShit(e.getMessage());
            } finally {
                if (statement != null) {
                    statement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            }
        }
    }

    public ArrayList<Vacancy> getFromDb(String keyword) throws SQLException {
        ArrayList<Vacancy> vacancyList = new ArrayList<Vacancy>();
        Connection connection = null;
        Statement statement = null;
        String selectTableSQL;
        if(keyword.isEmpty() || keyword == "")
        selectTableSQL = "SELECT * from vacancy ";
        else selectTableSQL = "SELECT * FROM vacancy WHERE vacname" +
                " ILIKE '%"+ keyword +"%' OR vacorganization ILIKE '%"+ keyword +"%'" ;
        try {
            connection = getDBConnection();
            statement = connection.createStatement();

            ResultSet rs = statement.executeQuery(selectTableSQL);
            while (rs.next()) {
                String id = rs.getString("id");
                String vacname = rs.getString("vacname");
                String vacorganization = rs.getString("vacorganization");
                String vacsalary = rs.getString("vacsalary");
                String vacpublicdate = rs.getString("vacpublicdate");
                Vacancy vac = new Vacancy(id, vacname, vacpublicdate, vacorganization, vacsalary);

                vacancyList.add(vac);
            }
        } catch (SQLException e) {
            logger.logThisShit(e.getMessage());
        } finally {
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
        return vacancyList;
    }

    public void initDbTables() throws SQLException {
        Connection connection = null;
        Statement statement = null;

        String createTableSQL = "CREATE TABLE IF NOT EXISTS vacancy("
                + "id INTEGER NOT NULL, "
                + "vacname VARCHAR(100) NOT NULL, "
                + "vacorganization VARCHAR(100) NOT NULL, "
                + "vacsalary VARCHAR(20) NOT NULL, "
                + "vacpublicdate VARCHAR(20) NOT NULL, "
                + "PRIMARY KEY (id) "
                + ")";

        try {
            connection = getDBConnection();
            statement = connection.createStatement();
            statement.execute(createTableSQL);
        } catch (SQLException e) {
            logger.logThisShit(e.getMessage());
        } finally {
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }

    private static Connection getDBConnection() {
        final String DB_URL = "jdbc:postgresql://localhost/lportal";
        final String USER = "postgres";
        final String PASS = "123";
        Connection dbConnection = null;
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        try {
            dbConnection = DriverManager.getConnection(DB_URL, USER, PASS);
            return dbConnection;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return dbConnection;
    }

}
