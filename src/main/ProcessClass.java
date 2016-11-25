import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;

public class ProcessClass {
	ProcessThread pt=new ProcessThread();
	
	public ChannelFuture DoProcess(ChannelHandlerContext ctx, String request)  {
		// TODO Auto-generated method stub
		String Msg[] = request.split(" ");
		
		switch(Msg[0]){
		case "BOOKRANKALL":
			try {
				pt.selectBookRankingAll(Msg, ctx);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			break;
		case "MYREADLIST":
			try {
				pt.selectReadListById(Msg, ctx);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			break;
		case "RECOMMENDLIST":
			try {
				pt.selectRecommendListById(Msg, ctx);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			break;
		case "LOGIN":
			pt.LogIn(Msg, ctx);
			break;
		case "REGISTER":
			pt.Register(Msg, ctx);
			break;
		default:
			break;
				
		}
		return null;
	}

}
