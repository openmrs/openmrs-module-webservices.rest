<%@ include file="/WEB-INF/template/include.jsp"%>

<%@ include file="/WEB-INF/template/header.jsp"%>

<%@ include file="localHeader.jsp"%>

<h2><spring:message code="webservices.rest.help.title" /></h2>

Main documentation page for the module is on the wiki: 
<a href="https://wiki.openmrs.org/x/xoAaAQ">https://wiki.openmrs.org/display/projects/Webservices.rest+Module</a>

<br/><br/>

<style type="text/css">
	table.resourceData, table.resourceData th, table.resourceData td
	{
		border: 1px solid black;
	}
</style>

<table class="resourceData">
  <tr>
   <th>Resource</th>
   <th>Url</th>
   <th>Representations</th>
  </tr>
  <c:forEach var="resource" items="${data}">
      <tr>
        <td>${resource.name}</td>
        <td>${resource.url}</td>
        
        <td>
	        <table style="width:100%">
				<c:forEach var="representation" items="${resource.representations}">
			      <tr>
			        <td>${representation.name}: ${representation.properties}</td>
			      </tr>	
			    </c:forEach>
			</table>
		</td>
		
      </tr>		
  </c:forEach>
</table>

<%@ include file="/WEB-INF/template/footer.jsp"%>