<%@ include file="/WEB-INF/template/include.jsp"%>

<openmrs:require privilege="Manage Global Properties" otherwise="/login.htm" redirect="/module/webservices/rest/settings.form" />

<%@ include file="/WEB-INF/template/header.jsp"%>

<style type="text/css">
.propHeader{
	text-align: left; padding-left: 20px;
}
</style>

<a href="<openmrs:contextPath />/admin"><spring:message code="general.back"/></a>
<h2><spring:message code="webservices.rest.manage.globalProperties" /></h2>

<form:form method="post" modelAttribute="globalPropertiesModel">
<div class="box">
<table cellspacing="0" cellpadding="3" width="100%">
	<c:forEach var="prop" items="${globalPropertiesModel.properties}" varStatus="varStatus">
		<spring:nestedPath path="properties[${varStatus.index}]">
		<tr <c:if test="${varStatus.index % 2 == 0}">class='evenRow'</c:if>>
			<th>
				<spring:message code="${prop.property}.label" />
			</th>
		</tr>
		<tr <c:if test="${varStatus.index % 2 == 0}">class='evenRow'</c:if>>
			<td>
			<spring:bind path="propertyValue">
				<c:set var="inputSize" value="50" scope="page" />
				<c:if test="${prop.property == 'webservices.rest.maxresults'}"><c:set var="inputSize" value="2" /></c:if>
				<input type="text" name="${status.expression}" value="${status.value}" size="${inputSize}">
			</spring:bind>
			<small>${prop.description}</small>
			</td>
		</tr>
		</spring:nestedPath>
	</c:forEach>
	<tr>
		<td>
			<br/>
			<input type="submit" value='<spring:message code="general.save"/>' /> &nbsp;&nbsp; 
			<input type="button" value='<spring:message code="general.cancel"/>' onclick="javascript:window.location='<openmrs:contextPath />/admin'" />
		</td>
	</tr>
</table>
</div>
</form:form>

<%@ include file="/WEB-INF/template/footer.jsp"%>