package com.pyzy.server808.first

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.annotation.JSONField
import com.pyzy.server808.model.Person

data class DataClassSimple(val a : Int, val b : Int)


fun main(args: Array<String>) {


  val dts = DataClassSimple(1,2)
    val jsons = JSON.toJSONString(dts)
    println(jsons)
    val clzs = DataClassSimple::class
    println(clzs.javaObjectType)
//    val dt2 = JSON.parseObject(jsons,clzs.javaObjectType)

  var dt2 = JSON.parseObject(jsons,Class.forName("com.pyzy.server808.first.DataClassSimple"))

  println(dt2)



//    var personStr = JSON.toJSONString(person)
//
//    var handlerClazz = Class.forName("com.pyzy.server808.service.PersonHandler")
//
//    var instance = handlerClazz.newInstance()
//
////            var data = JSON.parseObject(personStr,Class.forName("com.pyzy.server808.model.Person"))
//
//    var data = JSON.parseObject(personStr, Person::class.javaObjectType)


}