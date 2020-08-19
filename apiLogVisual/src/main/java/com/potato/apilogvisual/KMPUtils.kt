package com.potato.apilogvisual

/**
 * create by Potato
 * create time 2020/5/30
 * Description：匹配字符串算法
 */
class KMPUtils {
    companion object {

        /**
         * 完全匹配（匹配全部）
         * @param str  总体
         * @param dest 要搜索的内容
         * @return 匹配正确时的，开始下标
         */
        fun kmpIndexAll(str: String, dest: String): ArrayList<Int> {
            val list = ArrayList<Int>()
            if (str.isEmpty() || dest.isEmpty()) return list
            val next = kmpNext(dest)
            var i = 0
            var j = 0
            while (i < str.length) {
                while (j > 0 && str[i] != dest[j]) {
                    j = next[j - 1]
                }
                if (str[i] == dest[j]) {
                    j++
                }
                if (j == dest.length) {
                    list.add(i - j + 1)//因为i从0开始计算的，所以计算位置后+1
                    j = 0
                }
                i++
            }
            return list
        }

        /**
         * 完全匹配（只匹配第一个）
         */
        fun kmpIndexFirst(str: String, dest: String): Int {
            if (str.isEmpty() || dest.isEmpty()) return -1
            val next = kmpNext(dest)
            var i = 0
            var j = 0
            while (i < str.length) {
                while (j > 0 && str[i] != dest[j]) {
                    j = next[j - 1]
                }
                if (str[i] == dest[j]) {
                    j++
                }
                if (j == dest.length) {
                    return i - j + 1
                }
                i++
            }
            return -1
        }

        /**
         * 相似度匹配（匹配出所有）
         * @param str  总体
         * @param dest 要搜索的内容
         * @return 匹配正确时的，返回开始下标和匹配长度
         */
        fun kmpIndex(str: String, dest: String, match: Float): ArrayList<Map<String, Int>> {
            val list = ArrayList<Map<String, Int>>()
            if (str.isEmpty() || dest.isEmpty() || match.compareTo(0) < 0 || match.compareTo(1) > 0) return list
            val next = kmpNext(dest)
            var i = 0
            var j = 0
            while (i < str.length) {
                //不相同
                while (j > 0 && str[i] != dest[j]) {
                    j = if (j >= dest.length * match) {//达到了相似度
                        insert(HashMap(), i, j, dest, list)
                        0//j移动到0位置(从i的位置起，从j=0开始比较)
                    } else {
                        next[j - 1]//移动位置
                    }
                }
                //相同
                if (str[i] == dest[j]) {
                    j++//计算匹配数量
                }
                //完全匹配
                if (j == dest.length) {
                    insert(HashMap(), i, j, dest, list)
                    j = 0//继续下一个匹配
                }
                i++
            }
            return list
        }

        private fun insert(map: HashMap<String, Int>, i: Int, j: Int, dest: String, list: ArrayList<Map<String, Int>>) {
            map["index"] = i - j + 1
            map["length"] = j
            list.add(map)
        }

        /**
         * @param dest 搜索目标
         * @return 目标串儿对应的数组（next[*]代表对称数量）
         */
        private fun kmpNext(dest: String): IntArray {
            val next = IntArray(dest.length)
            var i = 1
            var j = 0
            while (i < dest.length) {
                while (j > 0 && dest[j] != dest[i]) {
                    j = next[j - 1]
                }
                if (dest[i] == dest[j]) {
                    j++
                }
                next[i] = j
                i++
            }
            return next
        }

    }
}