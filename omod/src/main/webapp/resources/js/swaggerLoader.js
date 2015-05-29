var $j = jQuery.noConflict();
jQuery(document).ready(function() {


	var swaggerUi = new SwaggerUi({
		  url:"${pageContext.request.contextPath}/module/webservices/rest/swaggerSpec.json",
		  dom_id:"swaggerDocumentation"
		});

		swaggerUi.load();
	    
	});