
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />

	
	<script type="text/javascript" src="scripts/org/jquery-1.9.1.js"></script>
	<script type="text/javascript" src="scripts/org/jquery-ui-1.10.2.custom.js"></script>
	<link rel="stylesheet" href="css/org/jquery-ui-1.10.2.custom.css" />

	<!-- jQuery UI Layout -->
	<script type="text/javascript" src="scripts/org/jquery.layout-latest.min.js"></script>
	<link rel="stylesheet" type="text/css" href="css/org/layout-default-latest.css" />

	<!-- CSS Drop Down Menu -->
	<link href="css/org/dropdown.css" media="screen" rel="stylesheet" type="text/css" />
	<link href="css/org/default.ultimate.css" media="screen" rel="stylesheet" type="text/css" />




	<script type="text/javascript">
		jQuery(document).ready(function () {
			jQuery('body').layout(
			{
				center__paneSelector: "#contentpanel"

				, north__resizable: false
				, north__closable: false
				, north__spacing_open: 0
				, north__size: 400
			});
		});
	</script>

	<!-- header -->


	<link href="css/org/primitives.latest.css?1033" media="screen" rel="stylesheet" type="text/css" />
	<link href="css/org/bporgeditor.latest.css?1033" media="screen" rel="stylesheet" type="text/css" />

	<!-- # include file="src/src.primitives.html"-->
	<!-- # include file="orgeditor/src.bporgeditor.html"-->


	<script type="text/javascript" src="scripts/org/primitives.min.js?1033"></script>
	<script type="text/javascript" src="scripts/org/bporgeditor.min.js?1033"></script>


<script>

var colorIndex = -1;
var colors = [
	primitives.common.Colors.Indigo,
	"#C57F7F",
	primitives.common.Colors.Limegreen,
	primitives.common.Colors.Orange,
	"#E64848",
	primitives.common.Colors.Olive,
	primitives.common.Colors.DarkCyan,
	"#B800E6"

];


function getMatrixedLeaves() {
 
    /* 	var rootItem = new primitives.orgdiagram.ItemConfig();
    rootItem.image = "http://www.basicprimitives.com/demo/images/photos/a.png";
		rootItem.templateName = "photoTemplate";
		
		
	var adviser1 = new primitives.orgdiagram.ItemConfig("Adviser 1", "Adviser Description", "http://www.basicprimitives.com/demo/images/photos/z.png");
	adviser1.itemType = primitives.orgdiagram.ItemType.Adviser;
	adviser1.adviserPlacementType = primitives.orgdiagram.AdviserPlacementType.Right;
	adviser1.phone = "00000000000";
	adviser1.email = "abc@abc.com";
	adviser1.groupTitle = "Audit";
	adviser1.itemTitleColor = primitives.common.Colors.LightSteelBlue ;
	rootItem.items.push(adviser1);

	
	var Assistant11 = new primitives.orgdiagram.ItemConfig("Assistant 1", "Assistant 1 Description", "http://www.basicprimitives.com/demo/images/photos/y.png");
	Assistant11.itemType = primitives.orgdiagram.ItemType.Assistant;
	Assistant11.adviserPlacementType = primitives.orgdiagram.AdviserPlacementType.Right;
	Assistant11.phone = "00000000000";
	Assistant11.email = "abc@abc.com";
	Assistant11.groupTitle = "Audit";
	Assistant11.itemTitleColor = primitives.common.Colors.LightSteelBlue ;
	adviser1.items.push(Assistant11);

	var Assistant12 = new primitives.orgdiagram.ItemConfig("Regular", "Regular Description", "http://www.basicprimitives.com/demo/images/photos/y.png");
	Assistant12.itemType = primitives.orgdiagram.ItemType.Regular;
	Assistant12.adviserPlacementType = primitives.orgdiagram.AdviserPlacementType.Right;
	Assistant12.phone = "00000000000";
	Assistant12.email = "abc@abc.com";
	Assistant12.groupTitle = "Audit";
	adviser1.items.push(Assistant12);

	var adviser2 = new primitives.orgdiagram.ItemConfig("Adviser 2", "Adviser Description", "http://www.basicprimitives.com/demo/images/photos/z.png");
	adviser2.itemType = primitives.orgdiagram.ItemType.Adviser;
	adviser2.adviserPlacementType = primitives.orgdiagram.AdviserPlacementType.Left;
	adviser2.phone = "00000000000";
	adviser2.email = "abc@abc.com";
	adviser2.groupTitle = "Contract";
	rootItem.items.push(adviser2);

	var Assistant1 = new primitives.orgdiagram.ItemConfig("Assistant 1", "Assitant Description", "http://www.basicprimitives.com/demo/images/photos/y.png");
	Assistant1.itemType = primitives.orgdiagram.ItemType.Assistant;
	Assistant1.adviserPlacementType = primitives.orgdiagram.AdviserPlacementType.Right;
	Assistant1.phone = "00000000000";
	Assistant1.email = "abc@abc.com";
	Assistant1.groupTitle = "Administration";
	rootItem.items.push(Assistant1);

	var Assistant21 = new primitives.orgdiagram.ItemConfig("Assistant 1", "Assistant 1 Description", "http://www.basicprimitives.com/demo/images/photos/y.png");
	Assistant21.itemType = primitives.orgdiagram.ItemType.Assistant;
	Assistant21.adviserPlacementType = primitives.orgdiagram.AdviserPlacementType.Right;
	Assistant21.phone = "00000000000";
	Assistant21.email = "abc@abc.com";
	Assistant21.groupTitle = "Administration";
	Assistant1.items.push(Assistant21);

	var Assistant22 = new primitives.orgdiagram.ItemConfig("Assistant 2", "Assistant 2 Description", "http://www.basicprimitives.com/demo/images/photos/y.png");
	Assistant22.itemType = primitives.orgdiagram.ItemType.Regular;
	Assistant22.adviserPlacementType = primitives.orgdiagram.AdviserPlacementType.Right;
	Assistant22.phone = "00000000000";
	Assistant22.email = "abc@abc.com";
	Assistant22.groupTitle = "Administration";
	Assistant1.items.push(Assistant22);



	var Assistant23 = new primitives.orgdiagram.ItemConfig("Assistant 3", "Assistant 3 Description", "http://www.basicprimitives.com/demo/images/photos/x.png");
	Assistant23.itemType = primitives.orgdiagram.ItemType.Regular;
	Assistant23.adviserPlacementType = primitives.orgdiagram.AdviserPlacementType.Right;
	Assistant23.phone = "00000000000";
	Assistant23.email = "abc@abc.com";
	Assistant23.groupTitle = "Administration";
	Assistant1.items.push(Assistant23);  
	
	
	
	
/* 	var rootItem = new primitives.orgdiagram.ItemConfig();
	rootItem.title = "Helper";
	rootItem.isVisible = false;
	  
	var rootItem1 = new primitives.orgdiagram.ItemConfig();
	rootItem1.title = "Scott Aasrud";
	rootItem1.description = "VP, Public Sector";
	rootItem1.image = "http://www.basicprimitives.com/demo/images/photos/a.png";
	rootItem.items.push(rootItem1);
	  
	var itemB = new primitives.orgdiagram.ItemConfig();
	itemB.title = "Ted Lucas";
	itemB.description = "VP, Human Resources";
	itemB.image = "http://www.basicprimitives.com/demo/images/photos/b.png";
	rootItem1.items.push(itemB);

	var itemC = new primitives.orgdiagram.ItemConfig();
	itemC.title = "Joao Stuger";
	itemC.description = "Business Solutions, US";
	itemC.image = "http://www.basicprimitives.com/demo/images/photos/c.png";
	rootItem1.items.push(itemC);


	var rootItem2 = new primitives.orgdiagram.ItemConfig();
	rootItem2.title = "Scott Aasrud 2";
	rootItem2.description = "VP, Public Sector";
	rootItem2.image = "http://www.basicprimitives.com/demo/images/photos/a.png";
	rootItem.items.push(rootItem2);
	  
	var itemB2 = new primitives.orgdiagram.ItemConfig();
	itemB2.title = "Ted Lucas 2";
	itemB2.description = "VP, Human Resources";
	itemB2.image = "http://www.basicprimitives.com/demo/images/photos/b.png";
	rootItem2.items.push(itemB2);

	var itemC2 = new primitives.orgdiagram.ItemConfig();
	itemC2.title = "Joao Stuger 2";
	itemC2.description = "Business Solutions, US";
	itemC2.image = "http://www.basicprimitives.com/demo/images/photos/c.png";
	rootItem2.items.push(itemC2); 
	
	*/


	var rootItem = new primitives.orgdiagram.ItemConfig();
	rootItem.title = "KONNECT CONSULTING";
	rootItem.description = "Kondhwa, PUNE";
	rootItem.isVisible = false;
	
	
	var rootItem10 = new primitives.orgdiagram.ItemConfig();
	rootItem10.title = "Konnect";
	rootItem10.description = "VP, Public Sector";
	rootItem10.image = "http://www.basicprimitives.com/demo/images/photos/a.png";
	rootItem.items.push(rootItem10);
	
	<%=request.getAttribute("sbrootItemData").toString()%>;
	 <%=request.getAttribute("sbCharData").toString()%>   
	

		
	return rootItem;
	

}
</script>




	<script type="text/javascript">
		var bpOrgEditor = null;
		jQuery(document).ready(function () {
			jQuery.ajaxSetup({
				cache: false
			});

			jQuery('#contentpanel').layout(
			{
				  center__paneSelector: "#centerpanel"
				, west__size: 200
				, west__paneSelector: "#westpanel"
				, west__resizable: true
				, center__onresize: function () {
					if (bpOrgEditor != null) {
						jQuery("#centerpanel").bpOrgEditor("update");
					}
				}
			});

			var orgEditorConfig = new primitives.orgeditor.Config();
			orgEditorConfig.editMode = true;
			orgEditorConfig.rootItem = getMatrixedLeaves();
			orgEditorConfig.onSave = function () {
				var config = jQuery("#centerpanel").bpOrgEditor("option");
				/*Read config option and store chart changes */
			};
			bpOrgEditor = jQuery("#centerpanel").bpOrgEditor(orgEditorConfig);
		});
	</script>
	<!-- /header -->

<jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Organisation Chart" name="title"/>
</jsp:include>


<div class="leftbox reportWidth " style="position:relative;overflow:hidden;" >

	<div id="contentpanel" class="ui-layout-content" style="padding: 0px;"> 
		<!--bpcontent-->

		<div id="centerpanel" style="overflow: hidden; padding: 0px; margin: 0px; border: 0px;">
		</div>
		<!--/bpcontent-->
 	</div>
 
</div>

<script>
//$("img.lazy").lazyload({ threshold : 200,effect : "fadeIn",failure_limit : 10});
$("img.lazy").lazyload({event : "sporty", threshold : 200,effect : "fadeIn",failure_limit : 10});

$(window).bind("load", function() {
    var timeout = setTimeout(function() { $("img.lazy").trigger("sporty") }, 1000);
}); 
</script>