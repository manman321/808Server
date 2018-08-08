package com.pyzy.server808.ext

import java.nio.ByteBuffer
import java.nio.charset.Charset

fun String.bcd():Int{
    var result = 0
    var reverse = reversed()

    for(x in 0 until length){
//        println(" x : ${reverse[x]}  parse:  ${reverse[x].toString().toInt()}" )
        result += (reverse[x].toString().toInt() shl (4 * x))//只适用于2位字符串转bcd  更多为的请使用移位操作来完成
    }
    return result
}

fun String.classExists():Boolean{
    try{
        Thread.currentThread().contextClassLoader.loadClass(this)
        return true
    }catch (e:ClassNotFoundException){
        return false
    }
}

fun String.ascii():ByteBuffer{
    var charset = Charset.forName("ASCII")
    var buffer = charset.encode(this)
    return buffer
}