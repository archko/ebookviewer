#!/bin/bash

TARGET_DIR=../../res/drawable
XXXHDPI_DIR=../../res/drawable-xxxhdpi

echo "Remove old resources..."

rm -f $TARGET_DIR/*.png $TARGET_DIR/*.xml

echo "Copy XML resources..."

for i in `find . -depth -name "*.xml" -type f`
do
	NAME=`basename $i`
	#cp $i "$TARGET_DIR/$NAME"
done

echo "Copy PNG resources..."

for i in `find . -maxdepth 1 -name "*.png" -type f`
do
	NAME=`basename $i`
	#cp $i "$TARGET_DIR/$NAME"
done

for i in `find activities -depth -name "*.png" -type f`
do
	NAME=`basename $i`
	#cp "$i" "$TARGET_DIR/$NAME"
done

for i in `find components -depth -name "*.png" -type f`
do
    NAME=`basename $i`
	#cp "$i" "$TARGET_DIR/$NAME"
done

echo "Convert SVG actionbar and menu resources to PNG..."

for i in `find activities -depth -name "*.svg" -type f`
do
	NAME=`basename $i .svg`
	TYPE=`echo $NAME | awk '/.*_actionbar_.*/ { print "ACTIONBAR"; } /.*_menu_.*/ { print "MENU"; }'`

	echo "========== $TYPE $i"

	if [ "$TYPE" == "ACTIONBAR" ];
	then
		if [ "$XXXHDPI_DIR/$NAME.png" -ot "$i" ];
		then
			/Users/archko/SOFT/Inkscape.app/Contents/MacOS/inkscape -w 144 -h 144 --export-type=png "$XXXHDPI_DIR/$NAME.png" $i
		fi

		cp "$XXXHDPI_DIR/$NAME.png" "$TARGET_DIR/$NAME.png"

	elif [ "$TYPE" == "MENU" ];
	then
		if [ "$XXXHDPI_DIR/$NAME.png" -ot "$i" ];
		then
			/Users/archko/SOFT/Inkscape.app/Contents/MacOS/inkscape -w 144 -h 144 --export-type=png "$XXXHDPI_DIR/$NAME.png" $i
		fi

		cp "$XXXHDPI_DIR/$NAME.png" "$TARGET_DIR/$NAME.png"

	else
		if [ "$XXXHDPI_DIR/$NAME.png" -ot "$i" ];
		then
			/Users/archko/SOFT/Inkscape.app/Contents/MacOS/inkscape -w 144 -h 144 --export-type=png "$XXXHDPI_DIR/$NAME.png" $i
		fi

		cp "$XXXHDPI_DIR/$NAME.png" "$TARGET_DIR/$NAME.png"
	fi

done

for i in `find sources -depth -name "*.svg" -type f`
do
	NAME=`basename $i .svg`
	TYPE=`echo $NAME | awk '/.*_actionbar_.*/ { print "ACTIONBAR"; } /.*_menu_.*/ { print "MENU"; }'`

	echo "========== $TYPE $i"

		if [ "$XXXHDPI_DIR/$NAME.png" -ot "$i" ];
		then
			/Users/archko/SOFT/Inkscape.app/Contents/MacOS/inkscape -w 144 -h 144 --export-type=png "$XXXHDPI_DIR/$NAME.png" $i
		fi

		cp "$XXXHDPI_DIR/$NAME.png" "$TARGET_DIR/$NAME.png"

done
