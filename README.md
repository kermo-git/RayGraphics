# Graphics3D

This project implements ray tracing and ray marching algorithms to render simple geometric shapes like spheres, cylinders, boxes and cones. To use it, you first need to create a Scene object and add objects and light sources first (see Scenes.scala). Then you need to render it via Scene.render method, which takes to functions as arguments: 

* initImage(Int, Int) receives image dimensions as arguments and is executed before rendering. You can write your custom logic for creating an image file or initializing an array.
* setPixelColor(Int, Int, Color) receives pixel coordinates and a Color object. There you can paint a pixel color in a 2D array or an image file. Color.toHex gives the color HEX code as Int value.

An example of displaying the image in a new window is given in Display.scala, which contains the main function.
