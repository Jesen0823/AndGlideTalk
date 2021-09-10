
### 大长图的加载

主要原理：分块加载 + 内存复用池

* **图片压缩方式：**

  * 设置图片格式
     Android目前常用的图片格式有png，jpeg和webp
     `Bitmap.CompressFormat.JPEG`
  * 质量压缩
    根据width * height * 一个像素的所占用的字节数计算,宽高不变
    `bitmap.compress(format, quality, baos);`
    由于png是无损压缩，所以设置quality无效(不适合作为缩略图)
  * 采样率压缩
    缩小图片分辨率，减少所占用磁盘空间和内存大小
    `BitmapFactory.Options.inSampleSize`
  * 缩放压缩
    减少图片的像素,降低所占用磁盘空间大小和内存大小
    `canvas.drawBitmap(bitmap, null, rectF, null);`
     可以用于缓存缩略图
  * JNI调用JPEG库
    Android的图片引擎使用的是阉割版的skia引擎，去掉了图片压缩中的哈夫曼算法
    `libjpeg`


* **缩放压缩**
  可以通过自定义控件绘制Bitmap, 等比缩放 + 内存复用

* **图片压缩开源库**
  [https://github.com/Curzibn/Luban](https://github.com/Curzibn/Luban)

  >缺点：
  > 1、当没有设定压缩路径时，抛异常无闪退
  > 2、源码中,压缩比率固定值60，无法修改
  > 3、压缩配置、参数不太适应真实项目需求
  > 4、不能指定压缩大小，比如100KB以内

* **内存泄漏与ANR**

  内存泄漏会导致内存不足，如果GC频繁触发，线程会被暂时停止，影响到UI线程就会触发ANR













  [https://github.com/zetbaitsu/Compressor](https://github.com/zetbaitsu/Compressor)


