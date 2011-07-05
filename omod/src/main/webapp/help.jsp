<%@ include file="/WEB-INF/template/include.jsp"%>

<%@ include file="/WEB-INF/template/header.jsp"%>

<%@ include file="localHeader.jsp"%>

<h2><spring:message code="webservices.rest.help.title" /></h2>

Main documentation page for the module is on the wiki: 
<a href="https://wiki.openmrs.org/x/xoAaAQ">https://wiki.openmrs.org/display/projects/Webservices.rest+Module</a>

<br/><br/>

<table>
  <tr>
   <th>Resource</th>
   <th>Url</th>
  </tr>
  <c:forEach var="resource" items="${data}">
      <tr>
        <td>${resource.name}</td>
        <td>${resource.url}</td>
      </tr>		
  </c:forEach>
</table>

<br/><br/>
TODO: Iterate over annotations on resources and list off object representations

<%@ include file="/WEB-INF/template/footer.jsp"%>