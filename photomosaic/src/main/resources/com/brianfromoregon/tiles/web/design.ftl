<html>

<head>
    <style media="screen" type="text/css">
        table { border-collapse: collapse; }
        td, tr, img { padding: 0px; margin: 0px; border: none; }

    </style>
</head>

<form method="post" action="/design" accept="image/*" enctype="multipart/form-data">
    Change target image: <input type="file" accept="image/*" name="target" size="50"/> <font color="red">${model.errors.target!}</font>
    <br/>
    Number of tiles wide: <input type="text" name="numWide" value="${model.numWide!}"/> <font color="red">${model.errors.numWide!}</font>
    <br/>
    <input type="submit" value="Refresh"/>
</form>

<#if model.success()>
    <table>
        <#list 0..model.numTall()-1 as row>
            <tr>
                <#list model.positions[model.numWide*row..model.numWide*(row+1)-1] as idx>
                    <td><img src="palette/${idx}"/></td>
                </#list>
            </tr>
        </#list>
    </table>
</#if>

</html>
