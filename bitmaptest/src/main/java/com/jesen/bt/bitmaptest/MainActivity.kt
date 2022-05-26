package com.jesen.bt.bitmaptest

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapRegionDecoder
import android.graphics.Rect
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.annotation.DrawableRes
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    // 可复用的bitmap
    private var reuseBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val op = BitmapFactory.Options()
        op.inMutable = true
        reuseBitmap = BitmapFactory.decodeResource(resources, R.drawable._advert, op)

        /**
         * 查看图片加载所需内存大小
         * */
        val bitmap8888 = BitmapFactory.decodeResource(resources, R.drawable._advert)
        val size = bitmap8888.allocationByteCount
        val size2 = bitmap8888.byteCount
        Log.d(TAG, "8888,  size: $size, size2: $size2")

        /**
         * 图片内存优化：修改Config为RGB_565，设置采样参数
         * */
        val options = BitmapFactory.Options()
        options.inPreferredConfig = Bitmap.Config.RGB_565 // 用565,一个像素两字节，内存减半
        options.inSampleSize = 2 // 宽高维度每隔2个像素一次采样，所需内存进一步减小
        val bitmap565 = BitmapFactory.decodeResource(resources, R.drawable._advert, options)
        val size3 = bitmap565.allocationByteCount
        val size4 = bitmap565.byteCount
        Log.d(TAG, "565  size: $size3, size2: $size4")
    }

    /**
     * Bitmap内存复用
     * */
    fun getBitmap(): Bitmap {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeResource(resources, R.drawable._advert, options)
        if (canUseForInBitmap(reuseBitmap, options)) {
            options.inMutable = true
            options.inBitmap = reuseBitmap
        }
        options.inJustDecodeBounds = false
        return BitmapFactory.decodeResource(resources, R.drawable._advert, options)
    }

    private fun canUseForInBitmap(
        reuseBitmap: Bitmap?,
        targetOptions: BitmapFactory.Options
    ): Boolean {
        val width = targetOptions.outWidth / Math.max(targetOptions.inSampleSize, 1)
        val height = targetOptions.outHeight / Math.max(targetOptions.inSampleSize, 1)

        val byteCount = width * height * getBytesPerPixl(reuseBitmap?.config)
        return byteCount <= reuseBitmap?.allocationByteCount!!
    }

    private fun getBytesPerPixl(config: Bitmap.Config?): Int {
        return when (config) {
            Bitmap.Config.ALPHA_8 -> 1
            Bitmap.Config.RGB_565, Bitmap.Config.ARGB_4444 -> 2
            else -> 4
        }
    }

    /**
     * 大图加载的基础：
     * BitmapRegionDecoder 图片分片显示
     * */
    private fun getReginImage():Bitmap?{
        try {
            val inputStream = assets.open("test.jpg")
            // 设置显示图片的中心区域
            val regionDecoder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                BitmapRegionDecoder.newInstance(inputStream)
            } else {
                BitmapRegionDecoder.newInstance(inputStream,false)
            }
            val options = BitmapFactory.Options()
            val bitmap = regionDecoder?.decodeRegion(Rect(0,0,200,200),options)
            return bitmap
        }catch (e:IOException){
            e.printStackTrace()
        }
        return null
    }
}