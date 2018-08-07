package com.pyzy.server808.ext

fun String.bcd():Int{
    var result = 0
    var reverse = reversed()

    for(x in 0 until length){
//        println(" x : ${reverse[x]}  parse:  ${reverse[x].toString().toInt()}" )
        result += (reverse[x].toString().toInt() shl (4 * x))//只适用于2位字符串转bcd  更多为的请使用移位操作来完成
    }
    return result
}