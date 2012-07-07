<!DOCTYPE html>
<html lang="en">

<head>
    <link href="//current.bootstrapcdn.com/bootstrap-v204/css/bootstrap-combined.min.css" type="text/css"
          rel="Stylesheet"/>
    <script src="//current.bootstrapcdn.com/bootstrap-v204/js/bootstrap.min.js"></script>
</head>

<body>

<div class="navbar">
    <div class="navbar-inner">
        <div class="container">
            <ul class="nav">
                <li><a href="design">Design</a></li>
                <li class="active"><a href="palette">Palette</a></li>
            </ul>
        </div>
    </div>
</div>

<form method="post" action="/palette" class="well form-horizontal row span12">
    <fieldset>
        <div class="control-group">
            <label class="control-label" for="roots">Search roots</label>
            <div class="controls">
                <textarea id="roots" class="input-xlarge" name="roots" cols="80" rows="${model.numRoots()+1}" placeholder="TODO">${model.roots}</textarea>
                <p class="help-block">IF ERROR then ERROR TEXT else Describe search roots</p>
            </div>
        </div>
    </fieldset>
    <fieldset>
        <div class="control-group">
            <label class="control-label" for="excludes">Excludes</label>
            <div class="controls">
                <textarea id="excludes" class="input-xlarge" name="excludes" cols="80" rows="${model.numExcludes()+1}" placeholder="TODO">${model.excludes}</textarea>
                <p class="help-block">IF ERROR then ERROR TEXT else Describe excludes</p>
            </div>
        </div>
    </fieldset>
    <fieldset>
        <div class="control-group">
            <div class="controls">
                <button type="submit" class="btn btn-primary btn-large"><i class="icon-refresh icon-white"></i> Update Palette</button>
            </div>
        </div>
    </fieldset>
</form>

<div class="row span12">
<#list model.images() as img>
    <img src="palette/${img.get(0)?c}" title="${img.get(1)}" height="${model.height()}" width="${model.width()}" />
</#list>
</div>
</body>
</html>
