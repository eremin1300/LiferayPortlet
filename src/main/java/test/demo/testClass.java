package test.demo;

import test.demo.model.Vacancy;
import test.demo.services.DBService;
import test.demo.services.DownloadServise;
import test.demo.services.Logger;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

public class testClass {
    public static void main(String[] args) throws SQLException {
        Logger logger = new Logger();
        DownloadServise downloadServise = new DownloadServise();
        DBService dbService = new DBService();

        ArrayList<Vacancy> vacancies = new ArrayList<Vacancy>();

        try {
           // dbService.initDbTables();
           // downloadServise.downloadVacancy();
            vacancies = dbService.getFromDb("");
        } catch (SQLException throwables) {
            logger.logThisShit(throwables.getMessage());
        }
        vacancies = dbService.getFromDb("JetBrains");
    }

    }

