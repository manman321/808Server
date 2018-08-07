package com.pyzy.server808.utils

import com.pyzy.server808.ext.isChildClass
import com.pyzy.server808.message.JTTMessage
import java.io.File
import java.lang.reflect.Modifier
import java.net.URL
import kotlin.reflect.KClass

class ClassHelper{

    companion object {

        fun getAllChildren(clazz: Class<*>):List<Class<*>>{
//            fun getAllChildren(clazz: Class<*>,searchPath:String):List<Class<*>>{
//            var path = "/" + clazz.`package`.name.replace(".","/")

            var url = clazz.getResource("/")

            var rootFile = File(url.file)

            var children = ArrayList<Class<*>>()

            setSubList(rootFile,clazz.getResource("/").file,clazz,children)

//            children.forEach{x-> println(x.simpleName)}

            return children
        }



        @Throws(Exception::class)
         fun setSubList(rootFile:File,parentDirectory:String,parentClass:Class<*>,out:MutableList<Class<*>>){

            if(rootFile.isDirectory){

                rootFile.listFiles().forEach { file-> setSubList(file,parentDirectory, parentClass,out) }
            }else{

                var clazzName = ""

                if(rootFile.path.indexOf(".class") != -1){

                    clazzName = rootFile.path.replace(parentDirectory,"").replace(".class","").replace("/",".")

                    var childClazz = Class.forName(clazzName)

                    if(parentClass.isChildClass(childClazz) && !clazzName.equals(parentClass.canonicalName)){
                        out.add(childClazz)
                    }
                }
            }
        }

    }


}






fun main(args: Array<String>) {

    var clazz = JTTMessage::class.java

//    var path = "/" + clazz.`package`.name.replace(".","/")

    var path = "/"


    var url = clazz.getResource(path)

    var rootFile = File(url.file)

    var children = ArrayList<Class<*>>()

    ClassHelper.setSubList(rootFile,clazz.getResource("/").file,clazz,children)



    children.forEach{x->

        var className = x.simpleName

        var index = className.indexOf("0x",0,true)


        var number = x.simpleName.substring(index  + 2)

        println(Integer.valueOf(number, 16))


//        println(x.simpleName.substring(3))

    }


   var map = children.map { child-> child.simpleName.substring(5) to child }.toMap()

    println()
    println(map)

}