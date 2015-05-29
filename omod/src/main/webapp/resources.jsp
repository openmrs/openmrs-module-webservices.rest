<%@ include file="/WEB-INF/template/include.jsp"%>

<style type="text/css">
	table.innerTable, table.innerTable td
	{
	    width:100%;
		border: 0px gray;
		 border-collapse: collapse;
	}
	.formVersion{
	  width: 50%;
	}
	.formField{
	  float:left;
	  overflow:hidden;
	}
</style>



<c:forEach var="resource" items="${data}" varStatus="status">
	<tr
		class="<c:choose><c:when test="${status.index % 2 == 0}">d0</c:when><c:otherwise>d1</c:otherwise></c:choose>">

		<td>
            <c:if test="${resource.subResource}">Subresource:</c:if>
		    ${resource.name}
            <c:if test="${!empty resource.subtypeHandlerForResourceName}">extends ${resource.subtypeHandlerForResourceName}</c:if>
        </td>
		<td>${resource.url}</td>
		<td>
		   <c:forEach var="ver" items="${resource.supportedOpenMRSVersion}">
		     ${ver} 
		   </c:forEach>
		</td>

		<td>
			<table class="innerTable">
				<c:forEach var="representation" items="${resource.representations}">
					<tr>
						<td>${representation.name}: ${representation.properties}</td>
					</tr>
				</c:forEach>
			</table>
		</td>

	</tr>
	<c:if test="${!empty resource.subResources}">
		<c:set var="data" value="${resource.subResources}" scope="request"/>
		<jsp:include page="resources.jsp"/>
	</c:if>
    <c:if test="${!empty resource.subtypeHandlers}">
        <c:set var="data" value="${resource.subtypeHandlers}" scope="request"/>
        <jsp:include page="resources.jsp"/>
    </c:if>
</c:forEach>