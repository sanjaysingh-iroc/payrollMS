function confirmMsg(html){
	
	document.getElementById('frmClockEntries_strClock').value = html;
	var id;
	if(html.indexOf("Clock On")>0){
		id = confirm('Are you sure you want to clock on?');	
	}if(html.indexOf("Clocked On")>0){
		id=false;
	}else if(html.indexOf("Clock Off")>0){
		id = confirm('Are you sure you want to clock off?');
	}
	 
	if(id){
		showClockMessage();		
		showClockLabel();
	}
}

function removeLoadingDiv(theDiv){
	if(document.getElementById(theDiv)){
		var element = document.getElementById(theDiv);
		element.parentNode.removeChild(element);
	}
}



function printData(divId) 
{
	
	var data = document.getElementById(divId);
    var mywindow = window.open('', 'my div', 'height=400,width=100%');
    mywindow.document.write('<html><head><title>Print</title>');
    mywindow.document.write('<link rel="stylesheet" href="css/style1.css" type="text/css" />');
    mywindow.document.write('<link rel="stylesheet" href="css/style_IE_N.css" type="text/css" />');
    mywindow.document.write('</head><body >');
    mywindow.document.write('<div style="float:right;"><img src="images1/powerdby.png"></div>');
    mywindow.document.write(data.innerHTML);
    mywindow.document.write('</body></html>');
    mywindow.document.close();
    mywindow.print();
    mywindow.close();
    return true;
}