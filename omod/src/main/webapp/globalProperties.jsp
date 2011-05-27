<%@ include file="/WEB-INF/template/include.jsp"%>

<openmrs:require privilege="Manage Global Properties" otherwise="/login.htm" redirect="/module/webservices/rest/globalProperties.form" />

<%@ include file="/WEB-INF/template/header.jsp"%>

<style type="text/css">
.propHeader{
	text-align: center;
}
</style>

<a href="<openmrs:contextPath />/admin"><spring:message code="general.back"/></a>
<h2><spring:message code="webservices.rest.manage.globalProperties" /></h2>

<form:form method="post" modelAttribute="globalPropertiesModel">
<div class="box">
<table cellspacing="0" cellpadding="3" width="100%">
	<tr class="darkGreyBackground">
		<th class="propHeader"><spring:message code="general.name" /></th>
		<th class="propHeader"><spring:message code="general.value" /></th>
		<th class="propHeader"><spring:message code="general.description" /></th>
	</tr>
	<c:forEach var="prop" items="${globalPropertiesModel.properties}" varStatus="varStatus">
		<spring:nestedPath path="properties[${varStatus.index}]">
		<tr <c:if test="${varStatus.index % 2 == 0}">class='evenRow'</c:if>>
			<td valign="top">${prop.property}</td>
			<td valign="top">
			<spring:bind path="propertyValue">
				<input type="text" name="${status.expression}" value="${status.value}" size="45">
			</spring:bind>
			</td>
			<td width="100%">
			<spring:bind path="description">
				<textarea name="${status.expression}" rows="2" style="width:100%">${status.value}</textarea>
			</spring:bind>
			</td>
		</tr>
		</spring:nestedPath>
	</c:forEach>
	<tr>
		<td colspan="3">
			<input type="submit" value='<spring:message code="general.save"/>' /> &nbsp;&nbsp; 
			<input type="button" value='<spring:message code="general.cancel"/>' onclick="javascript:window.location='<openmrs:contextPath />/admin'" />
		</td>
	</tr>
</table>
</div>
</form:form>

<%@ include file="/WEB-INF/template/footer.jsp"%>