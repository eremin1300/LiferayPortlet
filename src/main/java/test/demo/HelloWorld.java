package test.demo;

import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.service.ServiceContext;
import com.liferay.util.bridges.mvc.MVCPortlet;
import test.demo.model.Vacancy;
import test.demo.services.DBService;
import test.demo.services.DownloadServise;
import test.demo.services.Logger;

import javax.portlet.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;


public class HelloWorld<processAction> extends MVCPortlet {
    private int page;
    Logger logger = new Logger();
    DownloadServise downloadServise = new DownloadServise();
    DBService dbService = new DBService();

     ArrayList<Vacancy> vacancies = new ArrayList<Vacancy>();
    ArrayList<Vacancy> vacanciesView = new ArrayList<Vacancy>();

    public void init(PortletConfig portletConfig) throws PortletException {
        super.init(portletConfig);
        page = 1;
        try {
            dbService.initDbTables();
            downloadServise.downloadVacancy();
            vacancies = dbService.getFromDb("");

        } catch (IOException e) {
            logger.logThisShit(e.getMessage());
        } catch (SQLException throwables) {
            logger.logThisShit(throwables.getMessage());
        }
    }

    @Override
    public void render(RenderRequest renderRequest, RenderResponse renderResponse)
            throws PortletException, IOException {

        if (vacancies != null) {
                vacanciesView.clear();
                for (int i = (page - 1)*10 ; i < vacancies.size() && i< page * 10; i++) {
                    vacanciesView.add(vacancies.get(i));
            }
            renderRequest.setAttribute("vacancies", vacanciesView);
        }
        super.render(renderRequest, renderResponse);
    }

    public  void findByKeyword(ActionRequest actionRequest, ActionResponse actionResponse)
            throws PortletModeException, SQLException {
        page = 1;
        PortletPreferences prefs = actionRequest.getPreferences();
        String keyword = ParamUtil.getString(actionRequest, "keyword");
        logger.logThisShit("KEYWORD = " + keyword);
        if (keyword != null && keyword != "") {
            vacancies = dbService.getFromDb(keyword);
            actionResponse.setPortletMode(PortletMode.VIEW);
        } else
            vacancies = dbService.getFromDb("");
            actionResponse.setPortletMode(PortletMode.VIEW);
    }

    public  void updateVacancies(ActionRequest actionRequest, ActionResponse actionResponse)
            throws PortletModeException, SQLException, IOException {
                downloadServise.downloadVacancy();
        actionResponse.setPortletMode(PortletMode.VIEW);
    }

    public  void nextPage (ActionRequest actionRequest, ActionResponse actionResponse)
            throws PortletModeException {
        if (vacancies.size() > page*10)
        page ++;
        actionResponse.setPortletMode(PortletMode.VIEW);

    }    public  void previousPage(ActionRequest actionRequest, ActionResponse actionResponse)
            throws PortletModeException {
        if(page >1)
        page --;
        actionResponse.setPortletMode(PortletMode.VIEW);
    }

}


