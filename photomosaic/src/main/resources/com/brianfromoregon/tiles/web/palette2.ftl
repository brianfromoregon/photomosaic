<html>
<head>
<style media="screen" type="text/css">
table { border-collapse: collapse; }
td, tr, img  { padding: 0px; margin: 0px; border: none; }
img {width=${model.width}; height=${model.height};}
</style>
</head>
<table>
<#list 0..model.count-10 as i>
  <tr><td><img src="palette/#{i}"/></td><td><img src="palette/#{i+1}"/></td><td><img src="palette/#{i+2}"/></td><td><img src="palette/#{i+3}"/></td><td><img src="palette/#{i+4}"/></td><td><img src="palette/#{i+5}"/></td></tr>
</#list>
</table>
</html>
