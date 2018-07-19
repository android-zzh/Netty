import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

@ChannelHandler.Sharable//@Sharable标记这个类的实例可以在 channel 里共享
public class EchoClientHandler extends SimpleChannelInboundHandler{

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(Unpooled.copiedBuffer("Netty rocks!", CharsetUtil.UTF_8));//2.当被通知该 channel 是活动的时候就发送信息
    }
    @Override
    public void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
        System.out.println("Client received: " +((ByteBuf)o).toString(CharsetUtil.UTF_8));//3.记录接收到的消息
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();//4.记录日志错误并关闭 channel
    }
}
