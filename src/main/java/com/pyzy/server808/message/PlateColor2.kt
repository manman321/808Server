package com.pyzy.server808.message

enum class PlateColor2 private constructor(val value: Int, val colorName: String) {

    BLUE(1, "蓝色"),
    YELLO(2, "黄色");

    override fun toString(): String {
        return colorName
    }
}
