<% request.setAttribute("DO_NOT_INCLUDE_JQUERY", true); %>
<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp"%>

<link rel="icon" type="image/png" href="<openmrs:contextPath/>/moduleResources/webservices/rest/js/swagger-ui/dist/images/favicon-32x32.png" sizes="32x32" />
<link rel="icon" type="image/png" href="<openmrs:contextPath/>/moduleResources/webservices/rest/js/swagger-ui/dist/images/favicon-16x16.png" sizes="16x16" />
<link href="<openmrs:contextPath/>/moduleResources/webservices/rest/js/swagger-ui/dist/css/typography.css" media="screen" rel="stylesheet" type="text/css"/>
<link href="<openmrs:contextPath/>/moduleResources/webservices/rest/js/swagger-ui/dist/css/reset.css" media="screen" rel="stylesheet" type="text/css"/>
<link href="<openmrs:contextPath/>/moduleResources/webservices/rest/js/swagger-ui/dist/css/screen.css" media="screen" rel="stylesheet" type="text/css"/>
<link href="<openmrs:contextPath/>/moduleResources/webservices/rest/js/swagger-ui/dist/css/reset.css" media="print" rel="stylesheet" type="text/css"/>
<link href="<openmrs:contextPath/>/moduleResources/webservices/rest/js/swagger-ui/dist/css/print.css" media="print" rel="stylesheet" type="text/css"/>

<openmrs:htmlInclude file="/moduleResources/webservices/rest/js/swagger-ui/dist/lib/jquery-1.8.0.min.js"  />
<openmrs:htmlInclude file="/moduleResources/webservices/rest/js/swagger-ui/dist/lib/jquery.slideto.min.js"  />
<openmrs:htmlInclude file="/moduleResources/webservices/rest/js/swagger-ui/dist/lib/jquery.wiggle.min.js"  />
<openmrs:htmlInclude file="/moduleResources/webservices/rest/js/swagger-ui/dist/lib/jquery.ba-bbq.min.js"  />
<openmrs:htmlInclude file="/moduleResources/webservices/rest/js/swagger-ui/dist/lib/handlebars-2.0.0.js"  />
<openmrs:htmlInclude file="/moduleResources/webservices/rest/js/swagger-ui/dist/lib/underscore-min.js"  />
<openmrs:htmlInclude file="/moduleResources/webservices/rest/js/swagger-ui/dist/lib/backbone-min.js"  />
<openmrs:htmlInclude file="/moduleResources/webservices/rest/js/swagger-ui/dist/swagger-ui.js"  />
<openmrs:htmlInclude file="/moduleResources/webservices/rest/js/swagger-ui/dist/lib/highlight.7.3.pack.js"  />
<openmrs:htmlInclude file="/moduleResources/webservices/rest/js/swagger-ui/dist/lib/marked.js"  />
<openmrs:htmlInclude file="/moduleResources/webservices/rest/js/swagger-ui/dist/lib/swagger-oauth.js" />

<openmrs:require privilege="Manage RESTWS" otherwise="/login.htm" redirect="/module/webservices/rest/settings.form" />
  <script type="text/javascript">
			jQuery(document).ready(function() {
				jQuery("#content").addClass("swagger-section ");
				var swaggerUi = new SwaggerUi({
					  url:"${pageContext.request.contextPath}/module/webservices/rest/swaggerSpec.json",
					  dom_id:"swaggerDocumentation",
					  docExpansion: "none",
                      apisSorter: "alpha",
                      onFailure: function(data) {
                          console.log("Unable to Load SwaggerUI");
                          jQuery("#swaggerError").innerHTML = "Error Loading Swagger UI";
                        }
					});
					swaggerUi.load();

				});

</script>
<div id="message-bar" class="swagger-ui-wrap">&nbsp;</div>
<div id="swaggerDocumentation" class="swagger-ui-wrap">
<div id="swaggerError" ></div>
</div>


</html>