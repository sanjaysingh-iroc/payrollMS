

<%@page import="java.util.ArrayList"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@page import="com.konnect.jpms.util.CommonFunctions"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<style type="text/css">

.cf:after { visibility: hidden; display: block; font-size: 0; content: " "; clear: both; height: 0; }
* html .cf { zoom: 1; }
*:first-child+html .cf { zoom: 1; }

html { margin: 0; padding: 0; }
body { font-size: 100%; margin: 0; }

h1 { font-size: 1.75em; margin: 0 0 0.6em 0; }

a { color: #2996cc; }
a:hover { text-decoration: none; }

p { line-height: 1.5em; }
.small { color: #666; font-size: 0.875em; }
.large { font-size: 1.25em; }

/**
 * Nestable
 */

.dd { position: relative; display: block; margin: 0; padding: 0; max-width: 600px; list-style: none; font-size: 13px; line-height: 20px; }

.dd-list { display: block; position: relative; margin: 0; padding: 0; list-style: none; }
.dd-list .dd-list { padding-left: 30px; }
.dd-collapsed .dd-list { display: none; }

.dd-item,
.dd-empty,
.dd-placeholder { display: block; position: relative; margin: 0; padding: 0; min-height: 20px; font-size: 13px; line-height: 20px; }

.dd-handle { display: block; height: 30px; margin: 5px 0; padding: 5px 10px; color: #333; text-decoration: none; font-weight: bold; border: 1px solid #ccc;
    background: #fafafa;
    background: -webkit-linear-gradient(top, #fafafa 0%, #eee 100%);
    background:    -moz-linear-gradient(top, #fafafa 0%, #eee 100%);
    background:         linear-gradient(top, #fafafa 0%, #eee 100%);
    -webkit-border-radius: 3px;
            border-radius: 3px;
    box-sizing: border-box; -moz-box-sizing: border-box;
}
.dd-handle:hover { color: #2ea8e5; background: #fff; }

.dd-item > button { display: block; position: relative; cursor: pointer; float: left; width: 25px; height: 20px; margin: 5px 0; padding: 0; text-indent: 100%; white-space: nowrap; overflow: hidden; border: 0; background: transparent; font-size: 12px; line-height: 1; text-align: center; font-weight: bold; }
.dd-item > button:before { content: '+'; display: block; position: absolute; width: 100%; text-align: center; text-indent: 0; }
.dd-item > button[data-action="collapse"]:before { content: '-'; }

.dd-placeholder,
.dd-empty { margin: 5px 0; padding: 0; min-height: 30px; background: #f2fbff; border: 1px dashed #b6bcbf; box-sizing: border-box; -moz-box-sizing: border-box; }
.dd-empty { border: 1px dashed #bbb; min-height: 100px; background-color: #e5e5e5;
    background-image: -webkit-linear-gradient(45deg, #fff 25%, transparent 25%, transparent 75%, #fff 75%, #fff), 
                      -webkit-linear-gradient(45deg, #fff 25%, transparent 25%, transparent 75%, #fff 75%, #fff);
    background-image:    -moz-linear-gradient(45deg, #fff 25%, transparent 25%, transparent 75%, #fff 75%, #fff), 
                         -moz-linear-gradient(45deg, #fff 25%, transparent 25%, transparent 75%, #fff 75%, #fff);
    background-image:         linear-gradient(45deg, #fff 25%, transparent 25%, transparent 75%, #fff 75%, #fff), 
                              linear-gradient(45deg, #fff 25%, transparent 25%, transparent 75%, #fff 75%, #fff);
    background-size: 60px 60px;
    background-position: 0 0, 30px 30px;
}

.dd-dragel { position: absolute; pointer-events: none; z-index: 9999; }
.dd-dragel > .dd-item .dd-handle { margin-top: 0; }
.dd-dragel .dd-handle {
    -webkit-box-shadow: 2px 4px 6px 0 rgba(0,0,0,.1);
            box-shadow: 2px 4px 6px 0 rgba(0,0,0,.1);
}

/**
 * Nestable Extras
 */

.nestable-lists { display: block; clear: both; padding: 30px 0; width: 100%; border: 0; border-top: 2px solid #ddd; border-bottom: 2px solid #ddd; }

#nestable-menu { padding: 0; margin: 20px 0; }

#nestable-output,
#nestable2-output { width: 100%; height: 7em; font-size: 0.75em; line-height: 1.333333em; font-family: Consolas, monospace; padding: 5px; box-sizing: border-box; -moz-box-sizing: border-box; }

#nestable2 .dd-handle {
    color: #fff;
    border: 1px solid #999;
    background: #bbb;
    background: -webkit-linear-gradient(top, #bbb 0%, #999 100%);
    background:    -moz-linear-gradient(top, #bbb 0%, #999 100%);
    background:         linear-gradient(top, #bbb 0%, #999 100%);
}
#nestable2 .dd-handle:hover { background: #bbb; }
#nestable2 .dd-item > button:before { color: #fff; }

@media only screen and (min-width: 700px) { 

    .dd { float: left; width: 48%; }
    .dd + .dd { margin-left: 2%; }

}

.dd-hover > .dd-handle { background: #2ea8e5 !important; }

/**
 * Nestable Draggable Handles
 */

.dd3-content { display: block; height: 30px; margin: 5px 0; padding: 5px 10px 5px 40px; color: #333; text-decoration: none; font-weight: bold; border: 1px solid #ccc;
    background: #fafafa;
    background: -webkit-linear-gradient(top, #fafafa 0%, #eee 100%);
    background:    -moz-linear-gradient(top, #fafafa 0%, #eee 100%);
    background:         linear-gradient(top, #fafafa 0%, #eee 100%);
    -webkit-border-radius: 3px;
            border-radius: 3px;
    box-sizing: border-box; -moz-box-sizing: border-box;
}
.dd3-content:hover { color: #2ea8e5; background: #fff; }

.dd-dragel > .dd3-item > .dd3-content { margin: 0; }

.dd3-item > button { margin-left: 30px; }

.dd3-handle { position: absolute; margin: 0; left: 0; top: 0; cursor: pointer; width: 30px; text-indent: 100%; white-space: nowrap; overflow: hidden;
    border: 1px solid #aaa;
    background: #ddd;
    background: -webkit-linear-gradient(top, #ddd 0%, #bbb 100%);
    background:    -moz-linear-gradient(top, #ddd 0%, #bbb 100%);
    background:         linear-gradient(top, #ddd 0%, #bbb 100%);
    border-top-right-radius: 0;
    border-bottom-right-radius: 0;
}
.dd3-handle:before { content: '='; display: block; position: absolute; left: 0; top: 3px; width: 100%; text-align: center; text-indent: 0; color: #fff; font-size: 20px; font-weight: normal; }
.dd3-handle:hover { background: #ddd; }

</style>
    


<%

UtilityFunctions uF = new UtilityFunctions();
CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
if(CF==null)return;

String strSessionEmpId = (String)session.getAttribute(IConstants.EMPID);
String strUserType = (String)session.getAttribute(IConstants.USERTYPE);

Map hmHireracyLevels = (Map)request.getAttribute("hmHireracyLevels");
List alHireracyLevels = (List)request.getAttribute("alHireracyLevels");

Map hmEmpDesigMap = (Map)request.getAttribute("hmEmpDesigMap");
Map hmEmpMap = (Map)request.getAttribute("hmEmpMap");
Map hmEmpProfileImage = (Map)request.getAttribute("hmEmpProfileImage");

String strContextPath = request.getContextPath();


%>




<div class="reportWidth">

 
 
    <menu id="nestable-menu">
        <button type="button" data-action="expand-all">Expand All</button>
        <button type="button" data-action="collapse-all">Collapse All</button>
    </menu>

   <!--  <div class="cf nestable-lists">

        <div class="dd" id="nestable">
            <ol class="dd-list">
                <li class="dd-item" data-id="1">
                    <div class="dd-handle">Item 1</div>
                </li>
                <li class="dd-item" data-id="2">
                    <div class="dd-handle">Item 2</div>
                    <ol class="dd-list">
                        <li class="dd-item" data-id="3"><div class="dd-handle">Item 3</div></li>
                        <li class="dd-item" data-id="4"><div class="dd-handle">Item 4</div></li>
                        <li class="dd-item" data-id="5">
                            <div class="dd-handle">Item 5</div>
                            <ol class="dd-list">
                                <li class="dd-item" data-id="6"><div class="dd-handle">Item 6</div></li>
                                <li class="dd-item" data-id="7"><div class="dd-handle">Item 7</div></li>
                                <li class="dd-item" data-id="8"><div class="dd-handle">Item 8</div></li>
                            </ol>
                        </li>
                        <li class="dd-item" data-id="9"><div class="dd-handle">Item 9</div></li>
                        <li class="dd-item" data-id="10"><div class="dd-handle">Item 10</div></li>
                    </ol>
                </li>
                <li class="dd-item" data-id="11">
                    <div class="dd-handle">Item 11</div>
                </li>
                <li class="dd-item" data-id="12">
                    <div class="dd-handle">Item 12</div>
                </li>
            </ol>
        </div>

        

    </div> -->







 <div class="cf nestable-lists">

        <div class="dd" id="nestable">
            <ol class="dd-list">
            <%
            List alParent = (List)hmHireracyLevels.get("0");
            for(int i=0; i<alParent.size(); i++){
            	StringBuilder sb = new StringBuilder();
            	%>
            	
            	<li class="dd-item" data-id="<%=alParent.get(i)%>">
                    <div class="dd-handle"><%=alParent.get(i) %></div>

						<%-- <%
						if(hmHireracyLevels.containsKey((String)alParent.get(i))){
							List alChild = (List)(List)hmHireracyLevels.get((String)alParent.get(i));
							for(int c=0; c<alChild.size(); c++){
									if(c==0){
										out.println("<ol class=\"dd-list\">");
									}
								%>
			                        <li class="dd-item" data-id="<%=alChild.get(c)%>"><div class="dd-handle"><%=alChild.get(c)%></div></li>
								<%
								if(c==alChild.size()-1){
									out.println("</ol>");
								}
							}
						} 
						%> --%>
						
						<%=func(hmHireracyLevels, (String)alParent.get(i), sb) %>
                </li>
            	<%
            }
            %>
            
            </ol>
        </div>

        

    </div>   
    
    
    
<form action="Navigation.action" method="post">

    <textarea id="nestable-output" name="navigation"></textarea>
    <input type="submit" value="Save" name="save" />

</form>
    

</div>


<script src="http://ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js"></script>
<script type="text/javascript" src="<%= request.getContextPath()%>/scripts/jquery.nestable.js"> </script>
<script>

$(document).ready(function()
{

    var updateOutput = function(e)
    {
        var list   = e.length ? e : $(e.target),
            output = list.data('output');
        if (window.JSON) {
            output.val(window.JSON.stringify(list.nestable('serialize')));//, null, 2));
        } else {
            output.val('JSON browser support required for this demo.');
        }
    };

    // activate Nestable for list 1
    $('#nestable').nestable({
        group: 1
    })
    .on('change', updateOutput);
    
    // activate Nestable for list 2
    $('#nestable2').nestable({
        group: 1
    })
    .on('change', updateOutput);

    // output initial serialised data
    updateOutput($('#nestable').data('output', $('#nestable-output')));
    updateOutput($('#nestable2').data('output', $('#nestable2-output')));

    $('#nestable-menu').on('click', function(e)
    {
        var target = $(e.target),
            action = target.data('action');
        if (action === 'expand-all') {
            $('.dd').nestable('expandAll');
        }
        if (action === 'collapse-all') {
            $('.dd').nestable('collapseAll');
        }
    });

    $('#nestable3').nestable();

});
</script>



<%!

String func(Map hmHireracyLevels, String strId, StringBuilder sb){
	
	
	
	if(hmHireracyLevels.containsKey(strId)){
		List alChild = (List)hmHireracyLevels.get(strId);
		for(int c=0; c<alChild.size(); c++){
				if(c==0){
					sb.append("<ol class=\"dd-list\">");
				}
			
				sb.append("<li class=\"dd-item\" data-id="+alChild.get(c)+"><div class=\"dd-handle\">"+alChild.get(c)+"</div>");
				func(hmHireracyLevels, (String)alChild.get(c), sb);
				sb.append("</li>");
			
			if(c==alChild.size()-1){
				sb.append("</ol>");
			}
		}
	}
	return sb.toString();
	
}

%>