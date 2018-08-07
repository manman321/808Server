package com.pyzy.server808.ext

fun List<Byte>.printHexString(){
    for (x in 0 until size){
        print(String.format("0x%02x ",get(x)))
    }
    println()
}