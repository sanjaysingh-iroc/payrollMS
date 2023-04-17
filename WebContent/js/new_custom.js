$(".selectedL").parent().addClass("active");
$(".selectedL").next('.treeview-menu').css('display','block');
$(".selectedL").next('.treeview-menu').addClass("menu-open");

function isNumberKey(evt) {
	   var charCode = (evt.which) ? evt.which : event.keyCode;
	   if (charCode > 31 && (charCode < 48 || charCode > 57) && charCode != 46)
	      return false;
	
	   return true;
	}

$("img.lazy").lazyload({event : "sporty", threshold : 200,effect : "fadeIn",failure_limit : 10});

$(window).bind("load", function() {
    var timeout = setTimeout(function() { $("img.lazy").trigger("sporty") }, 1000);
}); 


$(document).on('mouseover', 'input, select, textarea',function(){
	$(this).next('.hint').css("visibility", "visible");
});
$(document).on('mouseout', 'input, select, textarea',function(){
	$(this).next('.hint').css("visibility", "hidden");
});

$(window).on('load',function(){
	$("input[type='number']").prop('step','any');
});

function isNumberKey(evt){
    var charCode = (evt.which) ? evt.which : event.keyCode;
    if (charCode > 31 && (charCode < 48 || charCode > 57) && charCode != 46){
       return false; 
    }
    return true;
 }

var nowTemp = new Date();
var now = new Date(nowTemp.getFullYear(), nowTemp.getMonth(), nowTemp.getDate(), 0, 0, 0, 0);

