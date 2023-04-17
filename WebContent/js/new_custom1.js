$(".selectedL").parent().addClass("active");
	        $(".selectedL").next('.treeview-menu').css('display','block');
	        $(".selectedL").next('.treeview-menu').addClass("menu-open");

	        $("img.lazy").lazyload({event : "sporty", threshold : 200,effect : "fadeIn",failure_limit : 10});

	        $(window).bind("load", function() {
	            var timeout = setTimeout(function() { $("img.lazy").trigger("sporty"); }, 1000);
	        }); 

	        $(document).on('mouseover', 'input, select, textarea',function(){
	        	$(this).next('.hint').css("visibility", "visible");
	        });
	        $(document).on('mouseout', 'input, select, textarea',function(){
	        	$(this).next('.hint').css("visibility", "hidden");
	        });

	        $(document).on('click', '.products-list a',function(){
	        	$('a').removeClass("activelink");
	        	$(this).addClass("activelink");
	        });

	        $(window).on('load',function(){
	        	$("input[type='number']").prop('step','any');
	        });

	        $('body').on('onkeypress','input[type="number"]',function(evt){
	        	var charCode = (evt.which) ? evt.which : event.keyCode;
	            if (charCode > 31 && (charCode < 48 || charCode > 57) && charCode != 46){
	               return false;
	            }
	            return true;
	        });


	        var nowTemp = new Date();
	        var now = new Date(nowTemp.getFullYear(), nowTemp.getMonth(), nowTemp.getDate(), 0, 0, 0, 0);

	        jQuery.browser = {};
	        (function () {
	            jQuery.browser.msie = false;
	            jQuery.browser.version = 0;
	            if (navigator.userAgent.match(/MSIE ([0-9]+)\./)) {
	                jQuery.browser.msie = true;
	                jQuery.browser.version = RegExp.$1;
	            }
	        })();

	        $(function(){
	        	xhrPool = [];
	        	
	        	$("body").on('click','#closeButton',function(){
	        		$(".modal-dialog").removeAttr('style');
	        		$(".modal-bodyCP").height(400);
	        		$("#modalInfoCP").hide();
	        	});
	        	$("body").on('click','.close',function(){
	        		$(".modal-dialog").removeAttr('style');
	        		$(".modal-bodyCP").height(400);
	        		$("#modalInfoCP").hide();
	        	});
	        	
	        	var active = $(".active-workrig-user").html();
	        	$(".workrig-users-button>.btn-sm[for='"+active+"']").addClass("active");
	        	
	        	if($('body').hasClass('sidebar-collapse')){
	        		$(".treeview").removeClass("arrow_box");
	        		$(".treeview.active").addClass("arrow_box");
	        	}
	        	
	        	$('body').on('click','.sidebar-toggle',function(){
	        		if($('body').hasClass('sidebar-collapse')){
	        			$(".treeview").removeClass("arrow_box");
	        			
	        		}else{
	        			$(".treeview").removeClass("arrow_box");
	        			$(".treeview.active").addClass("arrow_box");
	        			//$(".treeview.active").addClass("arrow_box");
	        		}
	        	});

	        	$(document).find(".box-tools").parent().attr("data-widget","collapse-full");
	        
	        	$( document ).on('ajaxComplete',function() {
	        		$(document).find(".box-tools").parent().attr("data-widget","collapse-full");
	        		$.ajaxSetup({async:false});
	        	});
	        	
	        	$('body').on('click','.box-header', function(e) { 
	        	  var e_target = e.target;
	        	  if (e_target !== this){
	        		  if($(e_target).hasClass('box-title') || $(e_target).parent().hasClass('box-title') || $(e_target).attr('data-widget') === "collapse" || 
	        				  $(e_target).hasClass('fa-minus') || $(e_target).hasClass('fa-plus')){
	        			  
	        		  }else{
	        			  e.stopPropagation();
	        		  }
	        	  }
	        	});
	        });
	        
	        function fadeForm(form_id){
	        	if($('#'+form_id).find('.there').length === 0){
	        		$('#'+form_id).prepend('<div class="there"><div id="ajaxLoadImage"></div></div>');
	        	}
	        } 

	        function unfadeForm(form_id){
	        	$("#"+form_id).find('.there').remove();
	        }

	        function isNumberKey(evt){
	            var charCode = (evt.which) ? evt.which : event.keyCode;
	            if (charCode > 31 && (charCode < 48 || charCode > 57) && charCode != 46){
	               return false;
	            }
	            return true;
	         } 


	        function isOnlyNumberKey(evt) {
	            var charCode = (evt.which) ? evt.which : event.keyCode;
	            if (charCode <= 31 || (charCode >= 48 && charCode <= 57)) {
	         		return true;
	            }
	            return false;
	         }

	        function clearField(elementId){
	        	document.getElementById(elementId).value = '';
	        }