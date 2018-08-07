package com.pyzy.server808.ext

import java.lang.reflect.Modifier

//判断一个类是否继承了某个父类或者实现了某个接口
@Throws(Exception::class)
fun Class<*>.isChildClass(clazzName:String):Boolean{

    if(clazzName.isEmpty())return false

    var clazz = Class.forName(clazzName)

    return isChildClass(clazz);
}

@Throws(Exception::class)
fun Class<*>.isChildClass(childClazz:Class<*>):Boolean{
    //忽略抽象类
    if(Modifier.isAbstract(childClazz.modifiers))return false

    if(Modifier.isInterface(childClazz.modifiers))return false

    return  this.isAssignableFrom(childClazz)
}

