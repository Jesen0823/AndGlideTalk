package com.jesen.customglide;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * LRU算法，LinkedHashMap已经实现了
 * 最新访问的元素会被排在最上面，最上面的元素最不易被回收
 * */
public class Test {

    public static void main(String[] args) {

        // true表示支持 访问排序
        LinkedHashMap<String, Integer> map = new LinkedHashMap(0, 0.75F, true);

        map.put("一", 1); // 最开始添加的，它的LRU算法移除是最高的(越容易被回收)
        map.put("二", 2);
        map.put("三", 3);
        map.put("四", 4);
        map.put("五", 5); // 最后添加的，它的LRU算法移除是最高的(越难被回收)

        // 使用了某个元素
        map.get("三"); // 使用了一次，就越不可能被回收了

        for (Map.Entry<String, Integer> l : map.entrySet()) {
            System.out.println(l.getValue());
        }

    }


}
