package com.pyzy.server808.ext

import io.netty.buffer.ByteBuf
import java.nio.ByteBuffer
import java.nio.CharBuffer
import java.nio.charset.Charset

fun ByteBuffer.printHexString(){
    for (x in 0 until this.limit()){
        print(kotlin.String.format("0x%02x,",this[x]))
    }
    println()
}

fun ByteBuffer.string():String{
    return String(Charset.forName("GBK").decode(this).array()).trimEnd(' ')
}

fun ByteBuffer.asciiString():String{
    return String(Charset.forName("ASCII").decode(this).array()).trimEnd(' ')
}

//fun ByteBuffer.asArray():ByteArray{
//
//
//    if(limit() == capacity())return array()
//
//    var array = ByteArray(this.limit())
//
//    get(array)
//
//    return array
//
//}
//
//fun CharBuffer.asArray():CharArray{
//
//    if(limit() == capacity())return array()
//
//    var array = CharArray(this.limit())
//
//    get(array)
//
//    return array

//}