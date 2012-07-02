<html>

<head>
    <style media="screen" type="text/css">
        table { border-collapse: collapse; }
        td, tr, img { padding: 0px; margin: 0px; border: none; }

    </style>
</head>

<#include "header.ftl"/>

<#if model.success()>
    <table>
        <#list 0..model.numTall()-1 as row>
            <tr>
                <#list model.positions[model.numWide*row..model.numWide*(row+1)-1] as idx>
                    <td><img src="palette/${idx}" height="${model.height()}" width="${model.width()}"/></td>
                </#list>
            </tr>
        </#list>
    </table>
    <p>Used ${model.distinctTiles()} distinct images from palette of size ${model.paletteSize()}</p>
</#if>

<form method="post" action="/design" accept="image/*" enctype="multipart/form-data">
    Change target image: <input type="file" accept="image/*" name="target" size="50"/> <font color="red">${model.errors.target!}</font>
    <br/>
    Mosaic width: <input type="text" name="numWide" value="${model.numWide}"/> <font color="red">${model.errors.numWide!}</font>
    <br/>
    Allow reuse: <input type="checkbox" name="allowReuse" value="true" <#if model.allowReuse>checked</#if> />
    <br/>
    Color space: <input type="radio" name="colorSpace" value="sRGB" <#if model.isSRGB()>checked</#if> /> <a href="http://en.wikipedia.org/wiki/SRGB">sRGB</a> <input type="radio" name="colorSpace" value="CIELAB" <#if model.isCIELAB()>checked</#if>/> <a href="http://en.wikipedia.org/wiki/Lab_color_space#CIELAB">CIELAB</a>
    <br/>
    Drill down: <input type="text" name="drillDown" value="${model.drillDown}" />  <font color="red">${model.errors.drillDown!}</font>
    <br/>
    <input type="submit" value="Apply"/>
</form>

</html>
