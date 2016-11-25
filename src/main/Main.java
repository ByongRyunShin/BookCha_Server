import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.CharsetUtil;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		EventLoopGroup bossGroup = new NioEventLoopGroup(1);
		EventLoopGroup workerGroup=new NioEventLoopGroup();
		try {
			ServerBootstrap b=new ServerBootstrap();
			b.group(bossGroup, workerGroup)
			.channel(NioServerSocketChannel.class).handler(new LoggingHandler(LogLevel.INFO))
			.childHandler(new ChannelInitializer<SocketChannel>() {

				@Override
				protected void initChannel(SocketChannel sc) throws Exception {
					StringDecoder DECODER = new StringDecoder(CharsetUtil.UTF_8);
					StringEncoder ENCODER = new StringEncoder(CharsetUtil.UTF_8);
					
					ChannelPipeline cp = sc.pipeline();
					cp.addLast(new DelimiterBasedFrameDecoder(2048, Delimiters.lineDelimiter()));

				    cp.addLast(DECODER);
				    cp.addLast(ENCODER);
					cp.addLast(new ServerHandler());
				}
			});
			
			ChannelFuture f;
			f = b.bind(8888).sync();
			f.channel().closeFuture().sync();
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
	}

}
