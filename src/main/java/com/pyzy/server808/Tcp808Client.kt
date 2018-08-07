package com.pyzy.server808


data class Customer(val name: String, val email: String){


}


class Message{

    var type: Int = 1

    var message:String? = null;


    fun convert(convert: Boolean = false): Int{

        if(!convert)return -1;

        return if (message == null) 0 else 2;
    }


    fun getStringLength(obj: Any):Int?{
        if(obj is String)
            return obj.length;

        return null;
    }


}


class Tcp808Client{


     fun  main(args:Array<String>) {
        println("Hello world")


    }


    fun sum(a: Int, b: Int): Int{
        return  a + b
    }

}


fun main(args: Array<String>) {

    var client = Tcp808Client();

    var items = listOf<String>("a","b")

    client.main(items.toTypedArray())



    var sum = Tcp808Client().sum(1,2);

    println(sum)


    var message = Message();

    var length = message.getStringLength(1);

    println(length)





   println(message.convert(true))





    var list = listOf<Int>(1,2,3,4,5).filter { x-> x > 2 }

    list.forEach { i-> print(i) }



    mapOf<String,String>("a" to "b","c" to "d").forEach { t, u -> println("$t,$u") }




}