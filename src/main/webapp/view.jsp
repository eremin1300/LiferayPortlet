<%@ page import="com.liferay.portal.kernel.search.SearchContext" %>
<%@ page import="com.liferay.portal.kernel.search.SearchContextFactory" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="test.demo.model.Vacancy" %>
<%@ page import="test.demo.HelloWorld" %>
<%--<%@ page import="java.util.Date" %>
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

<portlet:actionURL name="updateVacancies" var="updateVacanciesURL"></portlet:actionURL>
<aui:form action="<%= updateVacanciesURL %>" name="<portlet:namespace />fm">
    <div>
    <h5>Внимание! Вакансии будут обновляться значительное время!</h5>
    <aui:button type="submit" value="Update" />
    </div>
</aui:form>

<jsp:useBean id="vacancies" class="java.util.ArrayList" scope="request"/>
<liferay-ui:search-container
        emptyResultsMessage="there-are-no-vacancies-for-the-selected-resource">

    <liferay-ui:search-container-results
            results="<%= HelloWorld.getResults(searchContainer.getStart(), searchContainer.getEnd()) %>"
            total="<%= HelloWorld.getVacanciesSize() %>"
    />
    <% searchContainer.setResults(results);%>
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
