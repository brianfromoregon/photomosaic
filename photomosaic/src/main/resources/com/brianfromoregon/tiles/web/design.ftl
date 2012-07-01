<html>

<head>
    <style media="screen" type="text/css">
        table { border-collapse: collapse; }
        td, tr, img { padding: 0px; margin: 0px; border: none; }

    </style>
</head>

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
</#if>

<form method="post" action="/design" accept="image/*" enctype="multipart/form-data">
    Change target image: <input type="file" accept="image/*" name="target" size="50"/> <font color="red">${model.errors.target!}</font>
    <br/>
    Mosaic width: <input type="text" name="numWide" value="${model.numWide!}"/> <font color="red">${model.errors.numWide!}</font>
    <br/>
    Allow reuse: <input type="checkbox" name="reuse" <#if model.allowReuse>checked</#if> />
    <br/>
    Color space: <input type="radio" name="colorSpace" value="sRGB" <#if model.isSRGB()>checked</#if> /> sRGB <input type="radio" name="colorSpace" value="CIELAB" <#if model.isCIELAB()>checked</#if>/> CIELAB
    <br/>
    <input type="submit" value="Apply"/>
</form>

</html>
