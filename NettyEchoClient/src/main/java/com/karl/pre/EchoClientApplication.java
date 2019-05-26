package com.karl.pre;

import java.net.InetSocketAddress;

import com.karl.pre.handler.EchoClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;


public class EchoClientApplication {
    private final int port;
    private final String host;

    public EchoClientApplication(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public static void main(String[] args) throws Exception {
//        if (args.length != 2) {
//            System.err.println("参数不对 ：" + EchoClientApplication.class.getSimpleName() + "");
//        }
//        // 设置端口，如果端口参数不正确，则抛出一个NumberFormatException的异常

        String host = "localhost";//args[0];
        int port = 8183; //Integer.parseInt(args[1]);

        new EchoClientApplication(host,port).start();

        //System.err.println("Start " + EchoClient.class.getSimpleName() + "");
    }

    public void start() throws Exception {
        //final EchoServerHandler serverHandler = new EchoServerHandler();
        EventLoopGroup group = new NioEventLoopGroup();     // 创建EventLooproup
        //EventLoopGroup bossgroup = new NioEventLoopGroup(); //创建EventLooproup
        try {
            Bootstrap b = new Bootstrap();
            b.group(group);// 指定EventLoopGropu以处理客户端事件，需要适用于NIO的实现
            b.channel(NioSocketChannel.class);
            b.remoteAddress(new InetSocketAddress(host, port));
            //  b.option(ChannelOption.SO_BACKLOG, 1024);

            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new EchoClientHandler());
                }

            });

            // 绑定端口，待待同步
            ChannelFuture f = b.connect().sync(); // 异步绑定服务器，调用
            // sync()方法阻塞等待直到绑定完成
            // 等待服务器监听，端口关闭
            f.channel().closeFuture().sync(); // sync会直到绑定操作结束为止。

        } finally {
            group.shutdownGracefully().sync();// 关闭EventLoopGroup，释放所有的资源
        }

    }
}