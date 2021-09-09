# AndGlideTalk
Android Glide


1. Glide手写实现之资源封装

    资源封装
    Key   -- 对Value的唯一性进行描述
    Value -- Bitmap的封装(+1, -1, 释放)

---
2. Glide手写实现之活动缓存
2.1回收机制：GC扫描的时候回收,移除容器（GC被动移除）（弱引用）
2.2容器管理方式：资源的封装 Key  ----- （弱引用<Value>)
2.3手动移除的区分
2.4关闭线程
2.5Value监听加入

---
3. Glide手写实现之内存缓存（LRU算法）
LRU算法：最少使用算法，最近没有使用的元素，会自动被移除掉

职责：
活动缓存：给正在使用的资源存储的，弱引用,正在使用的缓存。使用计数算法
内存缓存：为了减少磁盘读取，为第二次缓存服务，LRU算法，最近没有使用的元素，会自动被移除掉


* **内存缓存LruCache 类：**
利用LinkedHashMap<K, V>，

![LruCache源码](./images/README-1631113389282.png)

LinkedHashMap: true  ==拥有访问排序的功能 (最少使用元素算法-LRU算法)

put：
    1.如果是重复的key，会被移除掉一个
    key=15151511551
    previous = key=15151511551
    entryRemoved
    2.trimToSize 移除哪些最近没有使用的元素 ---》 entryRemoved

---
4. Glide手写实现之磁盘缓存

以文件形式保存在磁盘，保存时间比较长
也是LRU算法， Android官方没有提供相关Api,  
三方提供了DiskLruCache:[https://github.com/JakeWharton/DiskLruCache](https://github.com/JakeWharton/DiskLruCache)
仍然是LRU算法，LinkHashMap访问排序

----
5. Glide手写实现之生命周期

生命周期的管理：Application不能去管理，FragmentActivity可以去管理，Activity也可以去管理
管理的方式：在Activity组件上 附件Fragment，通过Fragment监听组件的生命周期

为什么发送一次Handler？
Android基于Handler消息的，LAUNCH_ACTIVITY，为了使fragment不要再在排队中

6.Glide手写实现之加载图片
