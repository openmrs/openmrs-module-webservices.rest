<%@ include file="/WEB-INF/template/include.jsp"%>

<%@ include file="/WEB-INF/template/header.jsp"%>

type: <input id="type" type="text" value="POST" size="12"/> (GET, POST, PUT, or DELETE)
<br/>
url: <input id="url" type="text" value="/openmrs/ws/rest/person/" size="45"/>
<br/>
json content: <input type="text" id="json" value="{patient:'2'}" size="45"/>
<br/><br?>
<input type="button" name="button" value="send" onclick="sendToServer()"/>

<br/><br/>
<div id="output">
</div>
<script>
function sendToServer() {
	    var d = jQuery("#json").val();
	    var type = jQuery("#type").val();
	    jQuery.ajax({
	        type: type,
	        contentType: "application/json;",
	        url: jQuery("#url").val(),
	        data: d,
	        //dataType: "json",
	        success: function(rv) { jQuery("#output").val("good: \r\n" + rv); },
	        error: function(req, msg, err) { jQuery("#output").val("error!\r\nmsg: " + msg + "\r\nerr: " + err + "\r\rreq: " + req); }
	    });
	    	    
	    return false;
	}
</script>

<%@ include file="/WEB-INF/template/footer.jsp"%>