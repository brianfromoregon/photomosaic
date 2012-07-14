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
                <li><a href="palette">Palette</a></li>
                <li class="active"><a href="settings">Settings</a></li>
            </ul>
        </div>
    </div>
</div>
<div class="container">
    <div class="row">
        <div class="span12">
            <h2>ImageMagick required</h2>
            <p>ImageMagick does all the heavy lifting of mosaic generation and image indexing, you need it!</p>
        </div>
    </div>
    <div class="row">
        <div class="span5 well">
            <h3>Step 1: Install</h3>
            <p>It's available for many platforms. The link for Windows <a href="http://www.imagemagick.org/script/binary-releases.php#windows">is right here</a>.
            The latest version should be fine and you probably want the dynamic 16-bit version (looks like ImageMagick-X.Y.Z-Q16-windows-dll.exe).</p>
        </div>
        <div class="span5 well">
            <h3>Step 2: Configure</h3>
            <form method="post" action="/settings" class="form-inline">
                <fieldset class="control-group <#if !model.valid>error<#else>success</#if>">
                    <span class="help-block">Where did you install it?</span>
                    <input type="text" name="imagemagick" class="span3" placeholder="C:\ImageMagick" value="${model.imagemagick!}">
                    <button type="submit" class="btn btn-primary"><i class="icon-ok icon-white"></i> Submit</button>
                </fieldset>
            </form>
        </div>
    </div>
</div>
</body>
</html>