package com.jesen.native_gif_lib

class NativeLib {

    /**
     * A native method that is implemented by the 'native_gif_lib' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String

    companion object {
        // Used to load the 'native_gif_lib' library on application startup.
        init {
            System.loadLibrary("native_gif_lib")
        }
    }
}