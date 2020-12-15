package test.demo;

import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;
import test.demo.model.Vacancy;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.portlet.*;
import javax.servlet.RequestDispatcher;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;


public class HelloWorld<processAction> extends MVCPortlet {

    static {
        javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(
                new javax.net.ssl.HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname,
                                          javax.net.ssl.SSLSession sslSession) {
                        if (hostname.equals("api.hh.ru")) {
                            return true;
                        }
                        return false;
                    }
                });
    }

    static ArrayList<Vacancy> vacancies = new ArrayList<Vacancy>();

    public void init(PortletConfig portletConfig) throws PortletException {
        super.init(portletConfig);
        try {
            addTableDb();
            addToDb(downloadVacancy());
            vacancies = getFromDb();
        } catch (SQLException e) {
            logThisShit(e.getMessage() + "SQL IN INIT");
        } catch (IOException e) {
            logThisShit(e.getMessage() + "IOEX IN INIT");
        }
    }

    public static int index = 0;

    public ArrayList<Vacancy> downloadVacancy() throws IOException {
        String[] subStr;
        ArrayList<String> neededString = new ArrayList<String>();
        String delimeter = ","; // Разделитель

        // Create a trust manager that does not validate certificate chains
        //Это сделано чтобы побороть ошибку верфикации сертификата SSH
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        }};
        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {

        }

        String url = "https://api.hh.ru/vacancies?&specialization=1&area=4&page=0&per_page=100";
        URL obj = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setConnectTimeout(15 * 1000);
        connection.setReadTimeout(50 * 1000);
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
            subStr = response.toString().split(delimeter);
            neededString = getNeededString(subStr);
        }
        in.close();
        return formatVacancyFromArray(neededString);
    }

    //алгоритм преобразования полученной информации в приемлимый вид вакансий
    private ArrayList formatVacancyFromArray(ArrayList<String> list) {

        String vacId = "";
        String vacName = "";
        String vacSalary = "";
        String vacOrganization = "";
        String pubDateVac = "";


        for (int i = 0; i < list.size(); i++) {

            if (list.get(i).contains("id") && list.get(i + 1).contains("name")) {
                String result = list.get(i);
                if (result.contains("items"))
                    result = result.substring(result.indexOf(':') + 2);
                result = result.substring((result.indexOf(':') + 2), result.length() - 1);
                vacId = result;
            } else if (list.get(i).contains("name") && list.get(i + 1).contains("salary")) {
                String result = list.get(i);
                result = result.substring((result.indexOf(':') + 2), result.length() - 1);
                vacName = result;
            } else if (list.get(i).contains("name") && list.get(i + 1).contains("name")) {
                String result = list.get(i);
                result = result.substring((result.indexOf(':') + 2), result.length() - 1);
                vacName = result;
                i++;
            } else if (list.get(i).contains("salary") && list.get(i + 1).contains("name")) {
                String result = list.get(i);
                if (list.get(i).contains("null") && !list.get(i).contains("from"))
                    result = "Не указана";
                else result = result.substring((result.indexOf(':') + 2));
                vacSalary = result;
            } else if (list.get(i).contains("name") && list.get(i + 1).contains("published")) {
                String result = list.get(i);
                result = result.substring((result.indexOf(':') + 2), result.length() - 1);
                vacOrganization = result;
            } else if (list.get(i).contains("published")) {
                String result = list.get(i);
                result = result.substring((result.indexOf(':') + 2), result.length() - 15);
                pubDateVac = result;
            }


            if (!vacId.equals("") && !vacName.equals("") && !vacOrganization.equals("")
                    && !vacSalary.equals("") && !pubDateVac.equals("")) {
                Vacancy vac = new Vacancy(vacId, vacName, pubDateVac, vacOrganization, vacSalary);
                if (!vacancies.contains(vac)) {
                    vacancies.add(vac);
                    vacId = "";
                    vacName = "";
                    vacSalary = "";
                    vacOrganization = "";
                    pubDateVac = "";
                } else {
                    vacId = "";
                    vacName = "";
                    vacSalary = "";
                    vacOrganization = "";
                    pubDateVac = "";
                }
            }
        }
        return vacancies;
    }

    private static ArrayList<String> getNeededString(String[] subStr) {
        ArrayList<String> neededString = new ArrayList<String>();
        String nameStr = "\"name\"";
        String ID = "{\"id\"";
        String salaryStr = "\"salary\"";
        String vacDate = "\"published_at\"";
        for (int i = 0; i < subStr.length; i++) {

            if (subStr[i].contains(nameStr) || subStr[i].contains(ID) || subStr[i].contains(salaryStr)
                    || subStr[i].contains(vacDate)) {
                if (!subStr[i].contains("area") && !subStr[i].contains("type")
                        && !subStr[i].contains("schedule") && !subStr[i].contains("Новосибирск")
                        && !subStr[i].contains("график") && !subStr[i].contains("день") && !subStr[i].contains("Открытая")
                        && !subStr[i].contains("employer") && !subStr[i].contains("department")
                        && !subStr[i].contains("\"name\":\"Удаленная работа\"")) {
                    neededString.add(subStr[i]);

                }
            }
        }
        return neededString;
    }

    public static void addToDb(ArrayList<Vacancy> list) throws SQLException {
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
                logThisShit(e.getMessage());
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

    public static ArrayList<Vacancy> getFromDb() throws SQLException {
        vacancies.clear();
        Connection connection = null;
        Statement statement = null;
        String selectTableSQL;
        selectTableSQL = "SELECT * from vacancy";
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

                vacancies.add(vac);
            }
        } catch (SQLException e) {
            logThisShit(e.getMessage());
        } finally {
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
        return vacancies;
    }

    public static void addTableDb() throws SQLException {
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
            logThisShit(e.getMessage());
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

    public static void logThisShit(String log) {
        Date time = new Date();
        DateFormat df = new SimpleDateFormat("ddMMMyyy", Locale.ENGLISH);
        String currentDate = df.format(time);
        File file = new File("C:\\liferay\\log " + currentDate + ".txt");
        String output = "\n" + time + " : " + log;
        try {
            FileWriter writer = new FileWriter(file, true);
            writer.write(output);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void render(RenderRequest renderRequest, RenderResponse renderResponse)
            throws PortletException, IOException {
        if (vacancies != null) {

            renderRequest.setAttribute("vacancies", vacancies);
        }
        super.render(renderRequest, renderResponse);
    }

    public static ArrayList<Vacancy> findByKeyword(String keyword) {
        ArrayList<Vacancy> vac = new ArrayList<Vacancy>();
        for (int i = 0; i < vacancies.size(); i++) {
            if (vacancies.get(i).toString().toLowerCase().contains(keyword.toLowerCase()))
                vac.add(vacancies.get(i));
        }
        return vac;
    }

    public static void findByKeyword(ActionRequest actionRequest, ActionResponse actionResponse)
            throws PortletModeException, SQLException {
        PortletPreferences prefs = actionRequest.getPreferences();
        String keyword = ParamUtil.getString(actionRequest, "keyword");
        logThisShit("KEYWORD = " + keyword);
        if (keyword != null && keyword != "") {
            getFromDb();
            vacancies = findByKeyword(keyword);
            actionResponse.setPortletMode(PortletMode.VIEW);
        } else
            getFromDb();
            actionResponse.setPortletMode(PortletMode.VIEW);
    }
}


