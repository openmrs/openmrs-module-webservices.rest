<%@ include file="/WEB-INF/template/include.jsp"%>

<%@ include file="/WEB-INF/template/header.jsp"%>

<%@ include file="localHeader.jsp"%>

<openmrs:require anyPrivilege="View RESTWS, Manage RESTWS" otherwise="/login.htm" redirect="/module/webservices/rest/help.form" />

<h2><spring:message code="webservices.rest.help.title" /></h2>

Main documentation page for the module is on the wiki: 
<a href="https://wiki.openmrs.org/x/xoAaAQ">https://wiki.openmrs.org/display/projects/Webservices.rest+Module</a>

<br/><br/>

<style type="text/css">
	table.resourceData, table.resourceData th, table.resourceData td
	{
		border: 1px solid black;
		border-collapse: collapse;
	}
	
	table.resourceData tr.d0 td {
	background-color: #FCF6CF;
	}
	
	table.resourceData tr.d1 td {
		background-color: #FEFEF2;
	}
	
	.versionsFieldSet{
	width:50%;
	}
	
</style>

<form:form method="post" modelAttribute="documentationConfiguration">
<fieldset class="versionsFieldSet">
<legend> Displayed Versions </legend>
 <!--  <c:forEach var="openMRSVersion" items="${distinctVersions}">
          <span> <input type="checkbox" name="${openMRSVersion}" value="${openMRSVersion}"> ${openMRSVersion} <span>
    </c:forEach>-->
    <form:checkboxes path="selectedVersions" items="${distinctVersions}" />
    <input type="submit" value="Display"/> 
</fieldset>
</form:form>
<div style="height:20px"></div>
<table class="resourceData">
  <tr>
   <th>Resource</th>
   <th>Url</th>
   <th>Version</th>
   <th>Representations</th>
  </tr>
  <jsp:include page="resources.jsp" />
</table>
<h2> Search Handlers </h2>

  <jsp:include page="searchResources.jsp" />
</table>

<%@ include file="/WEB-INF/template/footer.jsp"%>