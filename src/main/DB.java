import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import io.netty.channel.ChannelHandlerContext;

public class DB {
	static Connection con = null;
	static Statement stmt;
	
	public DB(){
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://localhost", "root", "1234");
			stmt=con.createStatement();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	static String getDate() {
		long time = System.currentTimeMillis(); 
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        return f.format(new Date(time));
	}
	
	public synchronized String getBookSummary(String Msg[], ChannelHandlerContext sc){
		String query=null;
		try {
			ResultSet rs=stmt.executeQuery("SELECT summary_title, summary from bookcha.book where book.ISBN="+Msg[1]+";");
			while(rs.next()){
				query=rs.getString("summary_title")+rs.getString("summary");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return query;
	}
	public synchronized ArrayList<String> selectbookrankingall(String Msg[], ChannelHandlerContext sc){
		ArrayList<String> query = new ArrayList<String>();
		ResultSet rs;
		try {
			rs=stmt.executeQuery("select book.name, ISBN, book.author, genre.name, country.name, (select count(*) from bookcha.readinglist where bookcha.book.ISBN = bookcha.readinglist.book_isbn) as cnt from bookcha.book, bookcha.genre, bookcha.country where genre_id = genre.id and country_id = country.id order by cnt desc;");
			while(rs.next()){
				query.add(rs.getString("book.name")+"\b"+rs.getString("ISBN")+"\b"+rs.getString("book.author")+"\b"+rs.getString("genre.name")+"\b"+rs.getString("country.name")+"\b"+rs.getString("cnt"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return query;
	}         
	
	public synchronized ArrayList<String> selectReadListById(String Msg[], ChannelHandlerContext sc){
		ArrayList<String> query = new ArrayList<String>();
		ResultSet rs;
		try {
			rs=stmt.executeQuery("select book.name, ISBN, book.author, genre.name, country.name, (select count(*) from bookcha.readinglist where bookcha.book.ISBN = bookcha.readinglist.book_isbn) as cnt from bookcha.readinglist, bookcha.book, bookcha.member, bookcha.genre, bookcha.country where bookcha.readinglist.book_isbn=bookcha.book.ISBN and bookcha.readinglist.mem_id='"+Msg[1]+"' and bookcha.readinglist.mem_id = bookcha.member.clientid and bookcha.book.genre_id=bookcha.genre.id and bookcha.book.country_id=bookcha.country.id order by cnt desc;");
			while(rs.next()){
				query.add(rs.getString("book.name")+"\b"+rs.getString("ISBN")+"\b"+rs.getString("book.author")+"\b"+rs.getString("genre.name")+"\b"+rs.getString("country.name")+"\b"+rs.getString("cnt"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return query;
	}
	
	public synchronized ArrayList<String> selectRecommendList(String Msg[], ChannelHandlerContext sc){
		
		ArrayList<String> query = new ArrayList<String>();
		ResultSet rs;
		try {
			rs=stmt.executeQuery("select bookcha.book.name, ISBN, book.author, genre.name, country.name, count(*) from bookcha.readinglist, bookcha.book, bookcha.genre, bookcha.country where bookcha.book.ISBN = bookcha.readinglist.book_isbn and bookcha.book.genre_id = bookcha.genre.id and bookcha.book.country_id = bookcha.country.id and bookcha.readinglist.book_isbn not in (select book_isbn from bookcha.readinglist where bookcha.readinglist.mem_id='"+Msg[1]+"') and bookcha.readinglist.mem_id in (select distinct mem_id from bookcha.readinglist where bookcha.readinglist.book_isbn in (select book_isbn from bookcha.readinglist where bookcha.readinglist.mem_id='"+Msg[1]+"')and NOT bookcha.readinglist.mem_id='"+Msg[1]+"') group by book_isbn having count(*)>2 order by count(*) desc;");
			while(rs.next()){
				query.add(rs.getString("book.name")+"\b"+rs.getString("ISBN")+"\b"+rs.getString("book.author")+"\b"+rs.getString("genre.name")+"\b"+rs.getString("country.name")+"\b"+rs.getString("count(*)"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return query;
	}
	
	public synchronized String LogIn(String Msg[], ChannelHandlerContext sc){
		int id=0, pw=0;
		try {
			ResultSet rs=stmt.executeQuery("SELECT * FROM bookcha.member where clientid = '"+Msg[1]+"';");
			while(rs.next()){
				id++;
			}
			rs.close();
			AES aes = new AES();
			Msg[2]=aes.Encrypt(Msg[2]);
			rs=stmt.executeQuery("SELECT * FROM bookcha.member where password = '"+Msg[2]+"' and clientid = '"+Msg[1]+"';");
			while(rs.next()){
				pw++;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String ans="LOGIN ";
		if(id!=0) ans+="O ";
		else ans+="X ";
		if(pw!=0) ans+="O ";
		else ans+="X";
		return ans;
	}
	
	public synchronized String Register(String Msg[], ChannelHandlerContext sc){
		String id=Msg[1], pw=Msg[2], name=Msg[3], gender=Msg[5], birthDate=Msg[4];
		String ans="REGISTER ";
		int c=0;
		try {
			AES aes = new AES();
			ResultSet rs=stmt.executeQuery("SELECT * FROM bookcha.member where clientid = '" + Msg[1]+"'");
			while(rs.next()){
				c++;
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(c!=0) ans+="X";
		else{
			boolean result=false;
			try {
				AES aes=new AES();
				pw=aes.Encrypt(pw);
				result=stmt.execute("INSERT INTO `bookcha`.`member` (`clientid`, `name`, `password`, `gender`, `birthDate`) VALUES ('"+ id +"', '"+ name +"', '"+ pw +"', '"+ gender +"', '"+ birthDate +"');");
				ans+="O";
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				ans+="E";
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return ans;
	}
	public synchronized ArrayList<String> CheckAllBook(String Msg[], ChannelHandlerContext sc){
		ArrayList<String> query=new ArrayList<String>();
		ResultSet rs;
		try {
			rs=stmt.executeQuery("SELECT book.name, ISBN, genre.name, country.name, (select count(*) from bookcha.readinglist where bookcha.book.ISBN = bookcha.readinglist.book_isbn) as cnt FROM bookcha.book, bookcha.genre, bookcha.country where bookcha.book.ISBN not in (SELECT book_isbn FROM bookcha.readinglist where mem_id='"+Msg[1]+"') and bookcha.book.genre_id=bookcha.genre.id and bookcha.book.country_id=bookcha.country.id order by cnt desc;");
			while(rs.next()){
				query.add(rs.getString("book.name")+"\b"+rs.getString("ISBN")+"\b"+rs.getString("genre.name")+"\b"+rs.getString("country.name")+"\b"+rs.getString("cnt"));
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return query;
	}
	public synchronized String CountMember(String Msg[], ChannelHandlerContext sc){
		String ans=null;
		try {
			ResultSet rs=stmt.executeQuery("SELECT count(*) FROM bookcha.member;");
			while(rs.next()){
				ans=rs.getString("count(*)");
			}
	
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ans;
	}
	public synchronized String readCheck(String Msg[], ChannelHandlerContext sc){
		String ans=null;
		ResultSet rs;
		int cnt=0;
		try {
			rs=stmt.executeQuery("SELECT count(*) FROM bookcha.readinglist;");
			while(rs.next()){
				cnt=rs.getInt("count(*)");
			}
			stmt.execute("INSERT INTO `bookcha`.`readinglist` (`id`, `mem_id`, `book_isbn`, `readDate`) VALUES ('"+(cnt+1)+"', '"+Msg[1]+"', '"+Msg[2]+"', '"+getDate()+"');");
			ans="Complete";
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			ans="Fail";
		}
		return ans;
	}
}
