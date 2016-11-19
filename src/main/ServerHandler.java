import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ServerHandler extends SimpleChannelInboundHandler<String>{

	ProcessClass process = new ProcessClass();
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
	}

	@Override
	public void channelRead0(ChannelHandlerContext ctx, String request)
			throws Exception {
		System.out.println("\t\t받은 메시지: "+request);
		
		String msg = "";
		SendMsg(ctx, msg);
	
		//메시지 처리부분
		ChannelFuture future = process.DoProcess(ctx, request);

	}
	private synchronized void SendMsg(ChannelHandlerContext ctx, String msg) {
		System.out.println("보내는 메시지: " + msg);
		ctx.write(msg+"\n");
	}
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {
		ctx.flush();
	}

	// 예외
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
	}

}
