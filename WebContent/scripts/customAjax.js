function getContent(showDiv,urlWithParameters) {
	$.ajax({ 
		type : 'GET',
		url: urlWithParameters,
		cache: false,
		success: function(result){
			$("#"+showDiv).html(result);
   		}
	});
	/*var xmlhttp;
	
	//alert(showDiv);
	//alert(urlWithParameters);
	
	if (window.XMLHttpRequest){
		// code for IE7+, Firefox, Chrome, Opera, Safari
	  xmlhttp=new XMLHttpRequest();
	}else{// code for IE6, IE5
	  xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
	}
	
	xmlhttp.onreadystatechange=function()
	  {
	  if (xmlhttp.readyState==4 && xmlhttp.status==200)
	    {
		 
	    document.getElementById(showDiv).innerHTML=xmlhttp.responseText;
	    } 
	  }
	xmlhttp.open("GET",urlWithParameters,true); 
	
	xmlhttp.send();  */
}

//Created By Dattatray Date:18-10-21
function disabledPointerAddAndRemove(total,id,index,isAddClass){
	var strDisablePntr = "disabled-pointer";
	for (var i = 0; i < total; i++) {
		if(i != index){
			if(isAddClass == true){
				$("#"+id+i).addClass(strDisablePntr);
			}else{
				$("#"+id+i).removeClass(strDisablePntr);
			}
		}
		if(index == ''){
			if(isAddClass == true){
				$("#"+id+i).addClass(strDisablePntr);
			}else{
				$("#"+id+i).removeClass(strDisablePntr);
			}
		}
	}
}


function getContentAcs(showDiv,urlWithParameters) {
	var xmlhttp;
	//alert(showDiv);
	//alert(urlWithParameters);
	
	if (window.XMLHttpRequest){
		// code for IE7+, Firefox, Chrome, Opera, Safari
	  xmlhttp=new XMLHttpRequest();
	} else {// code for IE6, IE5
	  xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
	}
	
	xmlhttp.onreadystatechange=function() {
		if (xmlhttp.readyState==4 && xmlhttp.status==200) {
			document.getElementById(showDiv).innerHTML=xmlhttp.responseText;
	    } 
	}
	xmlhttp.open("GET",urlWithParameters,false); 
	xmlhttp.send();  
}


function showAjaxLoading(divId){
	document.getElementById(divId).innerHTML = '<img src=\"images1/icons/loading-ajax.gif\" height=25px>';
}