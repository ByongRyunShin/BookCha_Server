import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;

public class ProcessClass {
	ProcessThread pt=new ProcessThread();
	
	public ChannelFuture DoProcess(ChannelHandlerContext ctx, String request)  {
		// TODO Auto-generated method stub
		String Msg[] = request.split(" ");
		
		switch(Msg[0]){
		case "GETSUMMARY":
			pt.getBookSummary(Msg, ctx);
			break;
		case "BOOKRANKALL":
			try {
				pt.selectBookRankingAll(Msg, ctx);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			break;
		case "MYREADBOOK":
			try {
				pt.selectReadListById(Msg, ctx);
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
		case "BOOKCHECKALL":
			try {
				pt.CheckAllBook(Msg, ctx);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case "EACHRANKALL":
			try {
				pt.EachRankAll(Msg, ctx);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case "COUNTMEMBER":
			pt.CountMember(Msg, ctx);
			break;
		case "READ":
			pt.readCheck(Msg, ctx);
			break;
		default:
			
			break;
				
		}
		return null;
	}

}
