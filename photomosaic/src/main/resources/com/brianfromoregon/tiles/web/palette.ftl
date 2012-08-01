<#ftl encoding="UTF-8">
<!DOCTYPE html>
<html lang="en">

<head>
    <link href="//current.bootstrapcdn.com/bootstrap-v204/css/bootstrap-combined.min.css" type="text/css"
          rel="Stylesheet"/>
    <script src="//ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js"></script>
    <script src="//current.bootstrapcdn.com/bootstrap-v204/js/bootstrap.min.js"></script>
    <style type="text/css">
        .tc { text-align: center }
    </style>
    <script type="text/javascript">
        $(".alert").alert()
    </script>
</head>

<body>

<div class="navbar">
    <div class="navbar-inner">
        <div class="container">
            <ul class="nav">
                <li><a href="design">Design</a></li>
                <li class="active"><a href="palette">Palette</a></li>
                <li><a href="settings">Settings</a></li>
            </ul>
        </div>
    </div>
</div>

<div class="container">
    <#if model.shortTermMemory>
        <div class="row span4 alert fade in">
            <button type="button" class="close" data-dismiss="alert">&times;</button>
            <strong>Short term memory!</strong> <br/>Any palette updates will be forgotten on restart.
        </div>
    </#if>

    <div class="row">
        <form method="post" action="/palette" class="well form-horizontal span11">
            <fieldset>
                <div class="control-group <#if model.errors.roots??>error</#if>">
                    <label class="control-label" for="roots">Search roots</label>
                    <div class="controls">
                        <textarea id="roots" class="input-xlarge" name="roots" cols="80" rows="${model.rootsList?size+1}"
                                  placeholder="C:\Users\Brian\Pictures">${model.roots}</textarea>
                        <div class="help-block">
                            ${model.errors.roots!}
                            <p>These directories will be scanned for images to include in your palette, one per line.</p>
                        </div>
                    </div>
                </div>
                <div class="control-group <#if model.errors.excludes??>error</#if>">
                    <label class="control-label" for="excludes">Excludes</label>
                    <div class="controls">
                        <textarea id="excludes" class="input-xlarge" name="excludes" cols="80" rows="${model.excludesList?size+1}"
                                  placeholder="C:\Users\Brian\Pictures\honeymoon">${model.excludes}</textarea>
                        <div class="help-block">
                            ${model.errors.excludes!}
                            <p class="help-block">Directories or files to exclude from your palette, one per line.</p>
                        </div>
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
                        <label class="control-label">Width x Height</label>
                        <div class="controls">
                            <input class="span1 tc" type="text" id="width" name="width" value="${model.width}"/>
                            x
                            <input class="span1 tc" type="text" id="height" name="height" value="${model.height}"/>
                            <p class="help-block">This will be the size of each tile in your final mosaic.</p>
                            <p class="help-block">Also, this width/height ratio should match most of your palette images for minimal cropping.</p>
                        </div>
                    </div>
                </div>
                <div class="control-group">
                    <div class="controls">
                        <button type="submit" class="btn btn-primary btn-large"><i class="icon-refresh icon-white"></i> Update Palette</button>
                    </div>
                </div>
            </fieldset>
        </form>
    </div>

    <div class="row span12">
        <#list model.images as img>
            <img src="palette/${img.get(0)?c}" title="${img.get(1)}" height="${model.height}" width="${model.width}" />
        </#list>
    </div>
</div>
</body>
</html>
