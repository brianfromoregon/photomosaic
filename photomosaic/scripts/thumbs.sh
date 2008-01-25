#!/bin/bash

# srcDir should contain all your images
export srcDir=in

# outDir is where to put all the thumbnails
export outDir=out

mkdir $outDir &>/dev/null

count=0

# you can add any other image file extensions here
find $srcDir -type f | egrep "\.(jpg|jpeg|gif|bmp|png|tif|tiff)$" | while read F
do
	# echo which file we're on
	echo $F

	# -auto-orient rotates the images to adjust for people turning the camera sideways
	# the multiple resize commands accomplish optimistic (for landscape) cropping.
	convert "$F" -auto-orient -resize x450 -resize "600x<" -resize 50% -gravity center -crop 300x225+0+0 +repage "$outDir/$count.png";
	
	let count=count+1;
done
