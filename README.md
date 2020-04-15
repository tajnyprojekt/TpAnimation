# TpAnimation
TpAnimation is a library for <a href="https://processing.org/">Processing</a>,
that aims to simplfy the process of creating animations.

## Overview
It takes care of value transitions, easing, interpolation, timing and playback.
It can also render your animation to frames.<br><br>
All you have to do is to define the start and end values for your sketch's variables or arrays,
set the animation duration (and eventually a couple of other parameters) and start playback.<br>
For more read the reference included with the library or see the examples.

## Use with PDE
To use the library with the Processing IDE, clone or download the repo and copy `tpanimation`
directory into Processing's library folder
<a href="https://github.com/processing/processing/wiki/How-to-Install-a-Contributed-Library">more on installing libraries here.</a>
When you copied the folder, launch some examples from `tpanimation/examples`.

## Build
The project is configured with Gradle. To build the library use:
`gradle releaseLibrary`, this will also generate and prepare the reference and library.properties in `tpanimation` directory.
Then you can use your build with Processing.

## License
This project is licensed under the terms of the MIT license.<br>
See [LICENSE.txt](LICENSE.txt) for more details.

