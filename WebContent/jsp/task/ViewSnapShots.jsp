<%-- <%@page import="java.util.List"%>

<script type="text/javascript">
</script>
<%
String docRetriveLocation = (String) request.getAttribute("DOC_RETRIVE_LOCATION")+"Tracker/101/1074/3243/448/images/2016-01-04_12:21:27_3243_448_.jpg";
List <String>filenames= (List)request.getAttribute("filenames");
System.out.println("jsp file name--"+docRetriveLocation);
%>
<img height="275" width="774" class="lazy" id="screenshots" style="width: 1px solid #AAAAAA; border: 1px lightgray solid;" src="<%=docRetriveLocation %>" />
<br>
<img height="20" width="20" class="lazy" id="screenshots" style="width: 1px solid #AAAAAA; border: 1px lightgray solid;" src="userImages/company_avatar_photo.png" />

 --%>
 <%
 String docRetriveLocation = (String) request.getAttribute("DOC_RETRIVE_LOCATION")+"Tracker/101/1074/3243/448/images/2016-01-04_12:21:27_3243_448_.jpg";

 %>

 <link rel="stylesheet" href="styles/simpleStyle.css">     
        <div class="gallery"> 
            <div class="slider">
                <ul>
                <%for(int i=0;i<10;i++){ %>
                    <li><img height="520" width="600" src="<%=docRetriveLocation %>" class="slides" alt="Image"+"<%=i%>"></li>
                    <%-- <li><img height="520" width="600" src="<%=docRetriveLocation %>" class="slides" alt="Image2"></li>
                    <li><img height="520" width="600" src="<%=docRetriveLocation %>" class="slides" alt="Image3"></li>
                    <li><img height="520" width="600" src="<%=docRetriveLocation %>" class="slides" alt="Image4"></li> --%>
                    <%} %>
                </ul>
            </div>
            <!-- Navigation Button Controls -->
            <div id="slider-nav">
                <button data-dir="prev">Previous</button>
                <button data-dir="next" style="float:right;">Next</button>
            </div> 
         </div>
        <!-- Loading JavaScript Codes. -->
        <script src="js/jquery-2.1.0.js"></script>
        <script src="js/simpleSliderScript.js"></script>
        <script>
            //
            jQuery(document).ready(function ($) {
                // creating a container variable to hold the 'UL' elements. It uses method chaining.
                var container=$('div.slider')
                                            .css('overflow','hidden')
                                            .children('ul');
                 
                /*
                On the event of mouse-hover,
                    i) Change the visibility of Button Controls.
                    ii) SET/RESET the "intv" variable to switch between AutoSlider and Stop mode.
                */
                $('.gallery').hover(function( e ){
                    $('#slider-nav').toggle();
                    return e.type=='mouseenter'?clearInterval(intv):autoSlider();
                });
                 
                // Creating the 'slider' instance which will set initial parameters for the Slider.
                var sliderobj= new slider(container,$('#slider-nav'));
                /*
                This will trigger the 'setCurrentPos' and 'transition' methods on click of any button
                 "data-dir" attribute associated with the button will determine the direction of sliding.
                */
                sliderobj.nav.find('button').on('click', function(){
                    sliderobj.setCurrentPos($(this).data('dir'));
                    sliderobj.transition();
                });
                 
                autoSlider(); // Calling autoSlider() method on Page Load.
                 
                /*
                This function will initialize the interval variable which will cause execution of the inner function after every 2 seconds automatically.
                */
                function autoSlider()
                {
                    return intv = setInterval(function(){
                        sliderobj.setCurrentPos('next');
                        sliderobj.transition();
                    }, 2000);
                }
                 
            });
        </script>
