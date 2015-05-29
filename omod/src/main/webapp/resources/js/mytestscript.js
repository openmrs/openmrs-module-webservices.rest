

jQuery(document).ready(function() {
	var table = document.getElementById("resourceTable");
	for (var i = 0, row; row = table.rows[i]; i++) {
	     var col = row.cells[0];
	     if(col.innerHTML.search("Subresource:") !== -1){
	     	//console.log(col.innerHTML);
	    	  row.className = "collapsed";
	    	}

	    
	}

	jQuery(".parentResource").live("click", function(event){
                   var classes = this.className.split(" ");
                   var stateClass = classes[1];
                   var rowId = this.parentElement.parentElement.id;

                   if(stateClass === "collapse"){
	                 jQuery(this).addClass("expand");  
	                 jQuery(this).removeClass("collapse");
	                 jQuery("#"+rowId).nextUntil('.resource').addClass("collapsed").removeClass("expanded");
                }else{
                	 jQuery(this).addClass("collapse");
                    jQuery(this).removeClass("expand");
                   jQuery("#"+rowId).nextUntil('.resource').addClass("expanded").removeClass("collapsed");
                }
         });

	
});

