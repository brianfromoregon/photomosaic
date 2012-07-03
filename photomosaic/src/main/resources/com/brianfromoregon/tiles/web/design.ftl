<html>

<head>
    <#include "jquery.ftl"/>
    <style type="text/css">
        table { border-collapse: collapse; }
        td, tr, img { padding: 0px; margin: 0px; border: none; }
        body { font-size: 16px; }
    </style>
    <script>
        $(function() {
        $( "input[type=submit]" ).button();
        });
    </script>
</head>
<body>

<#include "nav.ftl"/>

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

<form method="post" action="/design" enctype="multipart/form-data">
    <label for="target">Change target image:</label> <input type="file" accept="image/*" id="target" name="target"
                                                            size="50"/> <font color="red">${model.errors.target!}</font>
    <br/>
    <label for="numWide">Mosaic width:</label> <input type="text" id="numWide" name="numWide" value="${model.numWide}"/>
    <font color="red">${model.errors.numWide!}</font>
    <br/>
    <label for="allowReuse">Allow reuse:</label> <input type="checkbox" id="allowReuse" name="allowReuse" value="true"
    <#if model.allowReuse>checked</#if>
    />
    <br/>
    <label for="colorSpace">Color space:</label> <input type="radio" id="colorSpace" name="colorSpace" value="sRGB"
    <#if model.isSRGB()>checked</#if>
    /> <a href="http://en.wikipedia.org/wiki/SRGB">sRGB</a> <input type="radio" name="colorSpace" value="CIELAB"
    <#if model.isCIELAB()>checked</#if>
    /> <a href="http://en.wikipedia.org/wiki/Lab_color_space#CIELAB">CIELAB</a>
    <br/>
    <label for="drillDown">Drill down:</label> <input type="text" id="drillDown" name="drillDown"
                                                      value="${model.drillDown}"/> <font color="red">${model.errors.drillDown!}</font>
    <br/>
    <input type="submit" value="Apply Settings"/>
</form>
</body>
</html>
