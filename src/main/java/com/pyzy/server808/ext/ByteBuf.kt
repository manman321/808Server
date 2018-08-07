package com.pyzy.server808.ext

import io.netty.buffer.ByteBuf
import java.nio.ByteBuffer
import java.nio.charset.Charset

fun ByteBuf.printHexString(){
    for (x in 0 until readableBytes()){
        print(kotlin.String.format("0x%02x ",getByte(x)))
    }
    println()
}

fun ByteBuf.readInt16():Int{
    return readShort().toInt()
}

fun ByteBuf.readInt8():Int{
    return readByte().toInt()
}

fun ByteBuf.readString(length:Int):String{
    var bytes = ByteBuffer.allocate(length)
    readBytes(bytes)
    return String(Charset.defaultCharset().decode(bytes).array())
}

fun ByteBuf.readLastString():String{
    var length = readableBytes() - readerIndex()
    return readString(length)
}

fun ByteBuf.writeString(value:String){

    var buffer = Charset.defaultCharset().encode(value)

    writeBytes(buffer)

}