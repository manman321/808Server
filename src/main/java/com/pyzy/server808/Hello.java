package com.pyzy.server808;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;

public class Hello {

    public static void main(String[] args) {
        ServerBootstrap serverBootstrap  = new ServerBootstrap();

        serverBootstrap.option(ChannelOption.SO_REUSEADDR,true);

        int a = 100;

        a = ~a;

    }


}
