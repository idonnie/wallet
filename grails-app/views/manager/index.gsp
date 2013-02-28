<!doctype html>
<html><head>
  <meta name="layout" content="manager">
		
  <link rel="stylesheet" type="text/css" href="../css/index.css">
				
  <title>Wallet Management Center</title>
  
</head><body>

<script>
		
//----------------------------------------
//             INITIALIZATION
//----------------------------------------------

$(function() { 
  $("#button-client-start").button().click(function(e){
    $.post(prevUri() + "/client/start" + "?" + $.param({
      "username" : $("#username").val()
    }))
      .success(function() {
	    alert("Success")
	  })
	  .error(function() {
        alert("Error")
	  })
  })
  $("#button-client-stop").button().click(function(e){
	$.post(prevUri() + "/client/stop" + "?" + $.param({
      "username" : $("#username").val()
    }))
	  .success(function() {
	    alert("Success")
      })
      .error(function() {
	    alert("Error")
      })	  
  })
  $("#button-server-start").button().click(function(e){
    $.post(prevUri() + "/server/start" + "?" + $.param({
      "username" : $("#username").val()
    }))
	  .success(function() {
	    alert("Success")
	  })
	  .error(function() {
	    alert("Error")
      })
	})
  $("#button-server-stop").button().click(function(e){
    $.post(prevUri() + "/server/stop" + "?" + $.param({
      "username" : $("#username").val()
    }))
	  .success(function() {
	    alert("Success")
	  })
	  .error(function() {
		alert("Error")
	  })	  
	})    
})

/**
 * Returns window current location minus last URI part, with trailing slash removed.  
 * Used when commands are issued from the same hierarchy depth.
 */
function prevUri(depth, val) {
	  
  if (typeof depth === "undefined") {
	depth = 1
  }
  if (typeof val === "undefined") {
    val = window.location.href
  }
  if (depth <= 0) {
	return val
  } else {
		
	//  Get window location
	var path = val 

	    
	// Strange things happen  
	if ((typeof path === "undefined") || (path.length <= 0)) {
	  return ""
	}

	// Remove trailing slash if exists
	var slashPos = path.lastIndexOf("/")
	if (slashPos === path.length - 1) {	  
	  path = path.substring(0, slashPos)
    }

	// Remove last URI part if exists
	slashPos = path.lastIndexOf("/")
	var result = (slashPos && slashPos >= 0 ? path.substring(0, slashPos) : path) 
	return prevUri(depth - 1, result)	  
  }
	  
}

</script>

  <g:form id="form-client" action="save" controller="smtp" name="smtp_form" >
    <div>
      <label for="username">User Name</label> <input type="text" id="username" name="username" value="user-one"/>
    </div>
  </g:form>
  <div id="button-client-start">[C] Start</div>
  <div id="button-client-stop">[C] Stop</div>
  <div id="button-server-start">[S] Start</div>
  <div id="button-server-stop">[S] Stop</div>
</body></html>
