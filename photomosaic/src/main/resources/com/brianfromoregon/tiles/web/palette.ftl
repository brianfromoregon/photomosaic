<html>
<head>
    <#include "jquery.ftl"/>
    <script>
        $(function() {
        $( "input[type=submit]" ).button();
        });
    </script>
</head>
<body>
<#include "nav.ftl"/>


<form method="post" action="/palette">
    <label for="roots">Search roots:</label><br/> <textarea id="roots" name="roots" cols="80" rows="${model.numRoots()+1}">${model.roots}</textarea>
    <br/>
    <label for="excludes">Excludes:</label><br/> <textarea id="excludes" name="excludes" cols="80" rows="${model.numExcludes()+1}">${model.excludes}</textarea>
    <br/>
    <input type="submit" value="Update Palette"/>
</form>

<#list model.images() as img>
    <img src="palette/${img.get(0)}" title="${img.get(1)}" height="${model.height()}" width="${model.width()}" />
</#list>

</body>
</html>
