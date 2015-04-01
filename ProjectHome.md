Design photomosaics in your browser. Try it yourself, the download link to an executable jar which will pop open the design page is on the left.

This is how you create your palette, by entering file system paths to scan.
![http://photomosaic.googlecode.com/git/samples/release-0.5/palette.png](http://photomosaic.googlecode.com/git/samples/release-0.5/palette.png)

This is how you design the mosaic and see the effect of your tweaks.
![http://photomosaic.googlecode.com/git/samples/release-0.5/design.png](http://photomosaic.googlecode.com/git/samples/release-0.5/design.png)

### Release Notes ###
0.5 - 08/01/2012 ([announcement](https://plus.google.com/107444811490059715698/posts/gDp8k7a9xsr))
  * Changed to web UI
0.1 - 06/24/2010
  * Rewrite, the code is much improved
  * Broke up the creation process into 2 steps: indexing and designing.
  * CIELAB color space support
  * ImageMagick commands are invoked using commons-exec instead of asking the user to run a script.
  * Meant for expert use only (indexing especially is not user friendly)
0.0.2 - 03/03/2008
  * Changed grid cell priority numbers color from orange to red.
  * Removing notion of x and y splits, always just montaging 1 column at at time, then montaging them all together to the final mosaic.
  * Got rid of log4j, using java.util.logging
  * Fixed 'random' grid cell walk.
  * Eliminated a useless dialog.
  * Using separate directories for thumbnails and montage output.
0.0.1 - 01/28/2008
  * Change max same image usage to preferred max same image usage, so as not to fail when there aren't enough source images.
  * Create single executable jar as dist, using jar jar links.
  * Randomize order of processing of non-prioritized cells.
  * Serialize all prioritized cells to single thread to guarantee order.
  * Disable GUI during processing steps.
0.0 - 01/27/2008
  * Release a GUI to better streamline mosaic creation process.

## Thanks! ##
[![](http://www.cloudbees.com/sites/default/files/Button-Built-on-CB-1.png)](https://brianfromoregon.ci.cloudbees.com/job/tiles/)

Cloudbees is hosting the Jenkins instance that runs CI for this project.