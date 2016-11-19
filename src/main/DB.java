

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
	//시연해야되는 서버라서 사용 자제 부탁드립니다.
	static String getDate() {
		long time = System.currentTimeMillis(); 
        SimpleDateFormat f = new SimpleDateFormat("yyyy/MM/dd");
        return f.format(new Date(time));
	}
	public synchronized ArrayList<String> selectbookrankingall(String Msg[], ChannelHandlerContext sc){
		ArrayList<String> query = new ArrayList<String>();
		ArrayList<String> isbnlist = new ArrayList<String>();
		int[] cnt;
		ResultSet rs;
		int c=0;
		
		try {
			rs=stmt.executeQuery("select ISBN, book.name, author, publisher, genre.name, country.name from bookcha.book, bookcha.genre, bookcha.country where genre_id = genre.id and country_id = country.id;");
			while(rs.next()){
				isbnlist.add(rs.getString(1));
				query.add(rs.getString(1)+" "+rs.getString(2)+" "+rs.getString(3)+" "+rs.getString(4)+" "+rs.getString(5)+" "+rs.getString(6));
			}
			
			cnt=new int[query.size()];
			
			rs=stmt.executeQuery("SELECT * FROM bookcha.readinglist;");
			while(rs.next()){
				String isn=rs.getString(3);
				for(int i=0; i<query.size(); i++){
					if(isbnlist.get(i).equals(isn)){
						cnt[i]++;
					}
				}
			}
			
			for(int i=0; i<query.size(); i++){
				query.get(i).concat(" "+String.valueOf(cnt[i]));
			}
			query.add("end");	
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return query;
	}         
	
	public synchronized ArrayList<String> selectReadListById(String Msg[], ChannelHandlerContext sc){
		ArrayList<String> query = new ArrayList<String>();
		
		try {
			ResultSet rs=stmt.executeQuery("select ISBN, book.name, book.author, book.publisher, genre.name, country.name from bookcha.readinglist, bookcha.book, bookcha.member, bookcha.genre, bookcha.country where bookcha.readinglist.book_isbn=bookcha.book.ISBN and bookcha.readinglist.mem_id='"+Msg[1]+"' and bookcha.readinglist.mem_id = bookcha.member.clientid and bookcha.book.genre_id=bookcha.genre.id and bookcha.book.country_id=bookcha.country.id;");
			while(rs.next()){
				query.add(rs.getString(1)+" "+rs.getString(2)+" "+rs.getString(3)+" "+rs.getString(4)+" "+rs.getString(5)+" "+rs.getString(6));
			}
			query.add("end");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return query;
	}
	
	public synchronized ArrayList<String> selectRecommendList(String Msg[], ChannelHandlerContext sc){
		ArrayList<String> query = new ArrayList<String>();
		try {
			ResultSet rs=stmt.executeQuery("select mem_id, bookcha.book.name, ISBN, bookcha.member.name, count(*) from bookcha.readinglist, bookcha.book, bookcha.member where bookcha.readinglist.mem_id in (select distinct mem_id from bookcha.readinglist, bookcha.book, bookcha.member where bookcha.readinglist.book_isbn in (select ISBN from bookcha.readinglist, bookcha.book, bookcha.member where bookcha.readinglist.mem_id='"+Msg[1]+"' and bookcha.readinglist.mem_id=bookcha.member.clientid and bookcha.readinglist.book_isbn=bookcha.book.ISBN) and bookcha.readinglist.mem_id=member.clientid and bookcha.readinglist.book_isbn=bookcha.book.ISBN) and bookcha.readinglist.book_isbn=bookcha.book.ISBN and bookcha.readinglist.mem_id=bookcha.member.clientid group by ISBN order by count(*) desc;");
			while(rs.next()){
				query.add(rs.getString(1)+" "+rs.getString(2)+" "+rs.getString(3)+" "+rs.getString(5));
			}
			query.add("end");
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
			rs=stmt.executeQuery("SELECT * FROM bookcha.member where password = '"+Msg[2]+"';");
			while(rs.next()){
				pw++;
			}
		} catch (SQLException e) {
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
			ResultSet rs=stmt.executeQuery("SELECT * FROM bookcha.member where clientid = '" + Msg[1]+"'");
			while(rs.next()){
				c++;
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(c!=0) ans+="X";
		else{
			boolean result=false;
			try {
				result=stmt.execute("INSERT INTO `bookcha`.`member` (`clientid`, `name`, `password`, `gender`, `birthDate`) VALUES ('"+ id +"', '"+ name +"', '"+ pw +"', '"+ gender +"', '"+ birthDate +"');");
				ans+="O";
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				ans+="E";
				e.printStackTrace();
			}
		}
		
		return ans;
	}
}
