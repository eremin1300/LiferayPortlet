package test.demo.services;

import org.json.JSONArray;

import org.json.JSONObject;
import test.demo.model.Vacancy;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

public class DownloadServise {
    Logger logger = new Logger();
    DBService dbService = new DBService();
    ArrayList<Vacancy> list = new ArrayList<Vacancy>();

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

    public void downloadVacancy() throws IOException {
        BufferedReader in = null;
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
            logger.logThisShit(e.getMessage());
        }
      //  for (int i = 0; i < 20; i++) {
            int i = 0;
            String url = "https://api.hh.ru/vacancies?&specialization=1&area=4&page="+i+"&per_page=100";
            URL obj = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setConnectTimeout(15 * 1000);
            connection.setReadTimeout(50 * 1000);
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
                JSONObject json = new JSONObject(response.toString());
                JSONArray getItemsArrArr = (JSONArray) json.get("items");
                Iterator phonesItr = getItemsArrArr.iterator();
// Выводим в цикле данные массива
                while (phonesItr.hasNext()) {
                    JSONObject test = (JSONObject) phonesItr.next();
                    JSONObject employer = (JSONObject) test.get("employer");
                    JSONObject salary;
                    StringBuilder stringBuilder = new StringBuilder();
                    Date date = new Date();
                    String published_at = test.get("published_at").toString().substring(0, 9)
                            +" "+ test.get("published_at").toString().substring(11, 18);
                    SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                    try {
                        date = formater.parse(published_at);
                    } catch (ParseException e) {
                        logger.logThisShit(e.getMessage());
                    }
                    try {
                        salary = (JSONObject) test.get("salary");
                        if (!salary.get("from").toString().equals("null"))
                            stringBuilder.append(" от " + salary.get("from"));
                        if (!salary.get("to").toString().equals("null"))
                            stringBuilder.append(" до " + salary.get("to"));
                    } catch (ClassCastException e) {
                        stringBuilder.append(" не указана");
                    }

                    list.add(new Vacancy(test.get("id").toString(), test.get("name").toString(),
                            date, employer.get("name").toString(), stringBuilder.toString()));
         //    Vacancy(String id, String name, String dateOfPublic, String organization, String salary)
                }
            }
       // }
        try {
            dbService.checkOldVacancy();
            dbService.addToDb(list);
        } catch (SQLException e) {
            logger.logThisShit(e.getMessage());
        }
    }
}

