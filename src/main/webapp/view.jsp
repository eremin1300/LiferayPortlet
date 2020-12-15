<%@ page import="com.liferay.portal.kernel.search.SearchContext" %>
<%@ page import="com.liferay.portal.kernel.search.SearchContextFactory" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="test.demo.model.Vacancy" %>
<%@ page import="test.demo.HelloWorld" %><%--<%@ page import="java.util.Date" %>
<%@ page import="test.demo.HelloWorld" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="test.demo.model.Vacancy" %>
<%@ page import="java.io.IOException" %>
<%@ page import="java.util.HashMap" %>--%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://alloy.liferay.com/tld/aui" prefix="aui" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>
<%@ taglib prefix="liferay-portlet" uri="http://liferay.com/tld/portlet" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--
/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
--%>

<%--
<% out.println("<p>Текущая дата " + new Date() + " </p>");%>
<% ArrayList<Vacancy> list  = HelloWorld.getFromDb("", "");
    int countVacancy = list.size();
%>
<% out.println("<p>Количество загруженных вакансий: " + countVacancy + "</p>");%>

<%  String countOutput = (String) request.getAttribute("countOutput");

int countOnPage;
try {
    countOnPage = Integer.parseInt(countOutput);
}catch (Exception e){
    HelloWorld.logThisShit(e.getMessage());
    countOnPage = 10;
}


    try {
        for (int k = HelloWorld.index; k < HelloWorld.index + countOnPage && k < countVacancy; k++) {
            out.println("<p>");
            out.println("Вакансия: " + list.get(k).getName());
            out.println("Организация: " + list.get(k).getOrganization());
            if (list.get(k).getSalary().equals("null"))
                out.println("Зарплата: не указана");
            else out.println("Зарплата: " + list.get(k).getSalary());
            out.println("Дата публикации: " + list.get(k).getDateOfPublic());
            out.println("</p>");
        }
    }
    catch (IOException e)
    {
        HelloWorld.logThisShit(e.getMessage());
    }
 %>
--%>
<portlet:defineObjects/>
<%
    String keyword = com.liferay.portal.kernel.util.ParamUtil.getString(request, "keywords");
%>
<portlet:actionURL name="findByKeyword" var="findbykeywordURL"></portlet:actionURL>
<aui:form action="<%= findbykeywordURL %>" name="<portlet:namespace />fm">
    <div class="search-form">
        <span class="aui-search-bar">
            <aui:input inlineField="<%= true %>" label="" name="keyword" size="30" title="search-vacancies" type="text" />

            <aui:button type="submit" value="search" />
        </span>
    </div>
</aui:form>

<jsp:useBean id="vacancies" class="java.util.ArrayList" scope="request"/>
<liferay-ui:search-container delta="10"
        emptyResultsMessage="there-are-no-vacancies-for-the-selected-resource">

    <liferay-ui:search-container-results
            results="<%= vacancies %>"
            total="<%= vacancies.size() %>"
    />

    <liferay-ui:search-container-row
            className="test.demo.model.Vacancy"
            modelVar="vacancy" escapedModel="<%=true%>"
    >
        <liferay-ui:search-container-column-text property="name" />
        <liferay-ui:search-container-column-text property="dateOfPublic" />
        <liferay-ui:search-container-column-text property="organization" />
        <liferay-ui:search-container-column-text property="salary" />

    </liferay-ui:search-container-row>

    <liferay-ui:search-iterator />
</liferay-ui:search-container>