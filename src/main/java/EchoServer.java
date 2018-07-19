import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

public class EchoServer {
    private final int port;

    public EchoServer(int port) {
        this.port = port;
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1){
            System.err.println( "Usage: " + EchoServer.class.getSimpleName() +
                    " <port>");
            return;
        }
        int port = Integer.parseInt(args[0]);//1.设置端口值（抛出一个 NumberFormatException 如果该端口参数的格式不正确）
        new EchoServer(port).start();//2.呼叫服务器的 start() 方法
    }

    public void start() throws Exception{
            NioEventLoopGroup group = new NioEventLoopGroup();//3.创建 EventLoopGroup
        try {
            ServerBootstrap b =  new ServerBootstrap();//4.创建 ServerBootstrap
            b.group(group)
                    .channel(NioServerSocketChannel.class)//5.指定使用 NIO 的传输 Channel
                    .localAddress(new InetSocketAddress(port))//6.设置 socket 地址使用所选的端口
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) {
                            socketChannel.pipeline().addLast(new EchoServerHandler());//7.添加 EchoServerHandler 到 Channel 的 ChannelPipeline
                        }
                    });
            ChannelFuture f = b.bind().sync();//8.绑定的服务器;sync 等待服务器关闭
            System.out.println(EchoServer.class.getName() + " started and listen on " + f.channel().localAddress());
            f.channel().closeFuture().sync();//9.关闭 channel 和 块，直到它被关闭
        } finally {
            group.shutdownGracefully().sync();//10.关机的 EventLoopGroup，释放所有资源。
        }
    }
}
