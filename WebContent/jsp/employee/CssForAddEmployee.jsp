<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<style>
.hide-tr{display: none;}  
.row_language {		width: 100%;	border-bottom: solid 1px #efefef;}
.row_hobby{	width: 100%;	border-bottom: solid 1px #efefef;}
.row_education{	width: 100%;}
#div_language {	height: 300px;	border: solid 2px #F5F5F5;	overflow: auto;}
#div_education {	height: 300px;	border: solid 2px #F5F5F5;	overflow: auto;}
.table>thead>tr>th, .table>tbody>tr>th, .table>tfoot>tr>th, .table>thead>tr>td, .table>tbody>tr>td, .table>tfoot>tr>td {border-top: 1px solid #FFFFFF;}
.table {margin-bottom: 0px;}
.wizard {margin: 30px auto;    background: #fff;}
.wizard .nav-tabs {position: relative; margin-bottom: 0;border-bottom-color: #e0e0e0;    }
.wizard > div.wizard-inner { position: relative;}
.connecting-line {    height: 2px;background: #e0e0e0;position: absolute;width: 83%;margin-left: 40px;left: 0;right: 0;top: 52%;z-index: 1;}
.wizard .nav-tabs > li.active > a, .wizard .nav-tabs > li.active > a:hover, .wizard .nav-tabs > li.active > a:focus {color: #555555;cursor: default;border: 0;border-bottom-color: transparent;}
span.round-tab {width: 50px;height: 50px;line-height: 50px;display: inline-block;border-radius: 100px;background: #fff;border: 2px solid #e0e0e0;z-index: 2;position: absolute;left: 0;	text-align: center;font-size: 20px;}
span.round-tab i{color:#555555;}
.wizard li.active span.round-tab {background: #fff;border: 2px solid #5bc0de; }
.wizard li.active span.round-tab i{color: #5bc0de;}
span.round-tab:hover {color: #333;border: 2px solid #333;}
.wizard .nav-tabs > li {width: 10%;}
.wizard li:after {content: "";position: absolute;left: 46%;opacity: 0;margin: 0 auto;bottom: 0px;border: 5px solid transparent; border-bottom-color: #5bc0de;transition: 0.1s ease-in-out;}
.wizard li.active:after {content: "";position: absolute;left: 42%;opacity: 1;margin: 0 auto;bottom: 0px;border: 10px solid transparent;border-bottom-color: #5bc0de;}
.wizard .nav-tabs > li a {width: 50px; height: 50px;margin: 20px auto;border-radius: 100%;padding: 0;}
.wizard .nav-tabs > li a:hover {background: transparent;}
.wizard .tab-pane {position: relative;padding-top: 50px;}
.wizard h3 {margin-top: 0;}
@media( max-width : 585px ) {
    .wizard { width: 90%; height: auto !important;  }
    span.round-tab {font-size: 16px;  width: 50px; height: 50px; line-height: 50px;  }
    .wizard .nav-tabs > li a { width: 50px; height: 50px;line-height: 50px;}
    .wizard li.active:after { content: " "; position: absolute; left: 35%; }
}
.tooltip{top: 75px !important;}
.tooltip-arrow{display: none;}
.wizard .tab-pane {padding-top: 20px;}
.wizard .nav-tabs {margin: 0px;}
 .wizard {margin: 0px;}
.ui-multiselect {min-width: 200px !important;}
#pi table{width: 100% !important;}
#pi table tr td{width: 50% !important;}
</style> 