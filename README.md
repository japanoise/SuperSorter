**This program is not under active development; it has been superceded by a new program, [HyperSorter](https://github.com/japanoise/hypersorter)**

# SuperSorter

![The program running](screenshot.png)

SuperSorter is a program that sorts images visually, one at a time.
You should be able to figure out what it does from the screenshot.

When it runs, it will ask you for a root directory. This directory
should have subdirectories into which you want to sort your images.
Once this is chosen it will ask for a directory of unsorted images.
Once this has been chosen the main screen, shown in the screenshot,
will appear. Images will be moved into the directory you select by
pressing the corresponding button.

The images can, optionally, be renamed. Use the "Prefs" menu to select
one. Currently, there are two options:

1. The image is just moved, retaining its original name (with a suffix
   in the case of another file of the same name already being there).
   This is the default behaviour.
2. The image will be renamed to a name matching the format of the
   [waifuname](https://git.sr.ht/~japanoise/waifuname) program, i.e.
   `waifu-num-name-hash.ext`. This will allow images to have a consistent 
   naming scheme. You will need to supply a "waifu name" i.e. an arbitrary
   string of non-space non-dash characters.
   
Optionally, you can pass the directory of your image files as arguments to
the program. Please use absolute paths. Example:

    java -jar supersorter.jar C:\Users\chameleon\Pictures\Yuugi C:\Users\chameleon\Pictures\Yuugi\sortme

## Installation

### From source

Import the project into IntelliJ or Eclipse. Create jars in the usual way.
You will need to create a fat jar or otherwise include the shit in lib,
because ORACLE/QUALITY took out javax.xml. The main class is, unsurprisingly,
`club.seekrit.SuperSorter.Main`.

### JAR

Download the Jar for the latest version from the releases section of this
repository.

Install a JDK (if you want to develop) or a JRE (if you just want the program)
from Oracle (or, on Linux, your distro's repositories):

* [JDK](https://www.oracle.com/java/technologies/javase-downloads.html)
* [JRE](https://www.oracle.com/java/technologies/javase-jre8-downloads.html)

On Windows, you should now be able to double-click the Jar you downloaded, and
it should Just Work. On Linux, you should be able to execute it with

    java -jar supersorter.jar

## Copying

Licensed MIT.
