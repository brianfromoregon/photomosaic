<#ftl encoding="UTF-8">
<!DOCTYPE html>
<html lang="en">

<head>
    <link href="//current.bootstrapcdn.com/bootstrap-v204/css/bootstrap-combined.min.css" type="text/css"
          rel="Stylesheet"/>
    <script src="//ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js"></script>
    <script src="//current.bootstrapcdn.com/bootstrap-v204/js/bootstrap.min.js"></script>
    <style type="text/css">
        .mosaic { border-collapse: collapse; padding: 0px; margin: 0px; border: none; }
    </style>
    <script type="text/javascript">
        $(".collapse").collapse()
        $(".alert").alert()
    </script>
</head>
<body>

<div class="navbar">
    <div class="navbar-inner">
        <div class="container">
            <ul class="nav">
                <li class="active"><a href="design">Design</a></li>
                <li><a href="palette">Palette</a></li>
                <li><a href="settings">Settings</a></li>
            </ul>
        </div>
    </div>
</div>

<#if model.createdFile??>
    <div class="row span12 alert alert-success fade in">
        <button type="button" class="close" data-dismiss="alert">&times;</button>
        <strong>You did it!</strong> <br/><a href="${model.createdFile}">${model.createdFile}</a>
    </div>
</#if>

<#if model.success()>
    <div class="row span12">
        <blockquote>
            <p>Mosaic with <strong>${model.positions?size}</strong> tiles (${model.numWide}x${model.numTall()}) using <strong>${model.distinctTiles()}</strong> unique images from your palette of size <strong>${model.paletteSize}</strong>.</p>
        </blockquote>
        <table class="mosaic">
            <#list 0..model.numTall()-1 as row>
                <tr class="mosaic">
                    <#list model.positions[model.numWide*row..model.numWide*(row+1)-1] as idx>
                        <td class="mosaic"><img class="mosaic" src="palette/${idx?c}" height="${model.height}" width="${model.width}"/></td>
                    </#list>
                </tr>
            </#list>
        </table>
    </div>
</#if>

<form method="post" action="/design" enctype="multipart/form-data" class="well form-horizontal row span12">
    <fieldset>
        <div class="control-group">
            <label class="control-label" for="target">Target image</label>
            <div class="controls">
                <input type="file" accept="image/*" id="target" name="target" size="50"/>
            </div>
        </div>
        <div class="control-group">
            <label class="control-label" for="numWide">Mosaic width</label>
            <div class="controls">
                <input class="span1" type="text" id="numWide" name="numWide" value="${model.numWide}"/>
                <p class="help-block">The number of tile columns in your mosaic.</p>
            </div>
        </div>
        <div class="control-group">
            <label class="control-label" for="allowReuse">Allow reuse</label>
            <div class="controls">
                <input type="checkbox" id="allowReuse" name="allowReuse" value="true" <#if model.allowReuse>checked</#if> />
                <p class="help-block">Whether the same image from your palette can be used more than once in the mosaic.</p>
            </div>
        </div>
        <div class="control-group">
            <div class="control-label">
                <button class="btn" data-toggle="collapse" data-target="#advanced" type="button">
                    <i class="icon-wrench"></i> Advanced
                </button>
            </div>
        </div>
        <div id="advanced" class="collapse">
                <div class="control-group">
                    <label class="control-label">Color space</label>
                    <div class="controls">
                        <input type="radio" name="colorSpace" value="sRGB" <#if model.isSRGB()>checked</#if> />
                        <a href="http://en.wikipedia.org/wiki/SRGB">sRGB</a>
                        <input type="radio" name="colorSpace" value="CIELAB" <#if model.isCIELAB()>checked</#if> />
                        <a href="http://en.wikipedia.org/wiki/Lab_color_space#CIELAB">CIELAB</a>
                        <p class="help-block">Explained and demonstrated in <a href="http://brianfromoregon.blogspot.com/2010/06/photomosaics.html">a blog post</a>.</p>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label" for="drillDown">Drill down</label>
                    <div class="controls">
                        <input class="span1" type="text" id="drillDown" name="drillDown" value="${model.drillDown}"/>
                        <p class="help-block">Explained and demonstrated in <a href="http://brianfromoregon.blogspot.com/2010/06/photomosaics.html">a blog post</a>.</p>
                    </div>
                </div>
        </div>
        <div class="control-group">
            <div class="controls">
                <button type="submit" class="btn btn-primary btn-large" name="action" value="apply"><i class="icon-th icon-white"></i> Apply</button>
                <button type="submit" class="btn btn-inverse btn-large" name="action" value="create"><i class="icon-ok icon-white"></i> Create</button>
            </div>
        </div>
    </fieldset>
</form>

</body>
</html>
