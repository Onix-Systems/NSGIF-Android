# NsGifLib

**NsGifLib** is a library for working with GIF files in Android.

## Installation
**for gradle.kts**:
- add this inside *repositories* block:
  - maven("https://jitpack.io")
- implementation("com.github.Onix-Systems:NSGIF-Android:0.0.2")

## Core module
- Load, process, utilize gifs
- Work with native code using JNI
- Without *any Android dependencies

## Usage
### Initialization
- val gifLib = NsGifLib.getInstance()

### Loading Gifs
- Load GIF from file: 
  val gifId = gifLib.setGif("path/to/your/file.gif")
- Load GIF from byte array
val gifId = gifLib.setGif(byteArray)
- Load GIF from input stream
val gifId = gifLib.setGif(inputStream)

### Retrieve Gif info
- val gifInfo = gifLib.getGifInfo(gifId)

### Copy Gif Data(frame)
- val dest = IntArray(gifInfo.width * gifInfo.height) //create pixel array of gif size
- val isSuccess = gifLib.copyPixels(dest, gifId)
**or**
- gifLib.setGifFrame(frame, gifId) // **when using, please be aware that non-zero frame might be dependent on previous one**
  - For example: frame 2 is dependent on frames 0 and 1, so to get frame 2 of the gif animation, you need to call:
    - gifLib.setGifFrame(0, gifId) // not needed, frame 0 is set by default
    - gifLib.setGifFrame(1, gifId)
    - gifLib.setGifFrame(2, gifId)
    - And then copy pixels as of your need
- val isSuccess = gifLib.copyPixels(dest, gifId)

### Cleaning up
- gifLib.destroyGif(gifId) // free allocated memory. please don't skip this step, it will cause memory leaks

## License
 MIT License. For more information see [LICENSE](LICENSE)