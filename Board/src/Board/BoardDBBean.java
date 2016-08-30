package Board;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

public class BoardDBBean {   
	
	public static BoardDBBean instance = new BoardDBBean();
	
	public static BoardDBBean getInstance(){ 
		return instance; 
	}
	
	private BoardDBBean(){}
	
	private Connection getConnection() throws Exception{
		String jdbcDriver = "jdbc:apache:commons:dbcp:/pool";
			return DriverManager.getConnection(jdbcDriver);
	}
	private SqlSession getConnection1() throws Exception{
		String res = "config.xml";
		InputStream is = Resources.getResourceAsStream(res);
		SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(is);
		SqlSession session = factory.openSession();
		return session;
	}
    //writePro.jsp
    public void insertArticle(BoardDataBean article) throws Exception {
        SqlSession conn = null;
        
        //답변글인지 일반글인지를 구분해서 입력시켜주는 로직!!!
        int num=article.getNum();
        int ref=article.getRef();
        int re_step=article.getRe_step();
        int re_level=article.getRe_level();
        int number=0;

        try {
            conn = getConnection1();
            int i = conn.selectOne("board.getNum");
            System.out.println("i:::"+i);

            if (i != 0){
            	number= i + 1;
            }else{
            	number=1;
            }
            if (num!=0){
            	Map map = new HashMap<>();
            	map.put("ref", ref);
            	map.put("re_step", re_step);
            	conn.update("board.upBoard", map);
	    	
            	re_step = re_step + 1;
            	re_level = re_level + 1;

            }else{
            	ref = number;//글번호 == 그룹번호
            	re_step = 0;
            	re_level = 0;
            }
            article.setRef(ref);
            article.setRe_step(re_step);
            article.setRe_level(re_level);
            conn.insert("board.addBoard", article);
            
	    } catch(Exception ex) {
            ex.printStackTrace();
        } finally {
	    conn.commit();
        }
    }
   
    //list.jsp : 페이징을 위해서 전체 DB에 입력된 행의수가 필요하다...!!!
    public int getArticleCount() throws Exception {
        SqlSession conn = null;
        PreparedStatement pstmt = null;
        
        int x=0;

        try {
            conn = getConnection1();
            
            x = conn.selectOne("board.getCount");
            System.out.println("DBBean list 메서드 실행::"+x);
        
        } catch(Exception ex) {
            ex.printStackTrace();
        } finally {
        	if (pstmt != null) try { pstmt.close(); } catch(SQLException ex) {}
            if (conn != null){ conn.close(); }
        }
	return x;
    }
    
    //list.jsp ==> Paging!!! DB로부터 여러행을 결과로 받는다.
    public List getArticles(int start, int end) throws Exception {
        SqlSession conn = null;

        List articleList=null;
        List articleList2=null;
        
        try {
            conn = getConnection1();
            Map map =new HashMap<>();
            map.put("start", start);
            map.put("end", end);
            
            articleList2 = new ArrayList<>();
            articleList2 = conn.selectList("board.getPage", map);
            
            if(articleList2.isEmpty()){
            	System.out.println("불러올 목록이 없음");
            }else{
            	articleList = new ArrayList(end);
            	for(int i = 0; i < articleList2.size(); i++){
            		articleList.add(articleList2.get(i));
            	}
            }
           
/*            pstmt = conn.prepareStatement(
            "select num,writer,email,subject,passwd,reg_date,ref,re_step,re_level,content,ip,readcount,r  " +
            "from (select num,writer,email,subject,passwd,reg_date,ref,re_step,re_level,content,ip,readcount,rownum r " +
            "from (select num,writer,email,subject,passwd,reg_date,ref,re_step,re_level,content,ip,readcount " +
            "from board order by ref desc, re_step asc) order by ref desc, re_step asc ) where r >= ? and r <= ? ");
            pstmt.setInt(1, start);
	    pstmt.setInt(2, end);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                articleList = new ArrayList(end);
                do{
                  BoardDataBean article= new BoardDataBean();
		  article.setNum(rs.getInt("num"));
		  article.setWriter(rs.getString("writer"));
                  article.setEmail(rs.getString("email"));
                  article.setSubject(rs.getString("subject"));
                  article.setPasswd(rs.getString("passwd"));
                  article.setReg_date(rs.getTimestamp("reg_date"));
		  article.setReadcount(rs.getInt("readcount"));
                  article.setRef(rs.getInt("ref"));
                  article.setRe_step(rs.getInt("re_step"));
		  article.setRe_level(rs.getInt("re_level"));
                  article.setContent(rs.getString("content"));
		  article.setIp(rs.getString("ip"));
 
                  articleList.add(article);
		}while(rs.next());
	    }*/
        } catch(Exception ex) {
            ex.printStackTrace();
        } finally {
           /* if (rs != null) try { rs.close(); } catch(SQLException ex) {}
            if (pstmt != null) try { pstmt.close(); } catch(SQLException ex) {}
            if (conn != null) try { conn.close(); } catch(SQLException ex) {}*/
        }
	return articleList;
    }

    //read.jsp : DB로부터 한줄의 데이터를 가져온다.
    public BoardDataBean getArticle(int num) throws Exception {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        BoardDataBean article=null;
        try {
            conn = getConnection();

            pstmt = conn.prepareStatement(
            "update board set readcount=readcount+1 where num = ?");
            pstmt.setInt(1, num);
	    pstmt.executeUpdate();

            pstmt = conn.prepareStatement(
            "select * from board where num = ?");
            pstmt.setInt(1, num);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                article = new BoardDataBean();
                article.setNum(rs.getInt("num"));
		article.setWriter(rs.getString("writer"));
                article.setEmail(rs.getString("email"));
                article.setSubject(rs.getString("subject"));
                article.setPasswd(rs.getString("passwd"));
		article.setReg_date(rs.getTimestamp("reg_date"));
		article.setReadcount(rs.getInt("readcount"));
                article.setRef(rs.getInt("ref"));
                article.setRe_step(rs.getInt("re_step"));
		article.setRe_level(rs.getInt("re_level"));
                article.setContent(rs.getString("content"));
		article.setIp(rs.getString("ip"));    
	    }
        } catch(Exception ex) {
            ex.printStackTrace();
        } finally {
            if (rs != null) try { rs.close(); } catch(SQLException ex) {}
            if (pstmt != null) try { pstmt.close(); } catch(SQLException ex) {}
            if (conn != null) try { conn.close(); } catch(SQLException ex) {}
        }
	return article;
    }

    //updateForm.jsp : 수정폼에 한줄의 데이터를 가져올때.
    public BoardDataBean updateGetArticle(int num) throws Exception {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        BoardDataBean article=null;
        try {
            conn = getConnection();

            pstmt = conn.prepareStatement(
            "select * from board where num = ?");
            pstmt.setInt(1, num);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                article = new BoardDataBean();
                article.setNum(rs.getInt("num"));
		article.setWriter(rs.getString("writer"));
                article.setEmail(rs.getString("email"));
                article.setSubject(rs.getString("subject"));
                article.setPasswd(rs.getString("passwd"));
		article.setReg_date(rs.getTimestamp("reg_date"));
		article.setReadcount(rs.getInt("readcount"));
                article.setRef(rs.getInt("ref"));
                article.setRe_step(rs.getInt("re_step"));
		article.setRe_level(rs.getInt("re_level"));
                article.setContent(rs.getString("content"));
		article.setIp(rs.getString("ip"));    
	    }
        } catch(Exception ex) {
            ex.printStackTrace();
        } finally {
            if (rs != null) try { rs.close(); } catch(SQLException ex) {}
            if (pstmt != null) try { pstmt.close(); } catch(SQLException ex) {}
            if (conn != null) try { conn.close(); } catch(SQLException ex) {}
        }
	return article;
    }

    //updatePro.jsp : 실제 데이터를 수정하는 메소드.
    public int updateArticle(BoardDataBean article) throws Exception {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs= null;

        String dbpasswd="";
        String sql="";
	int x=-1;
        try {
            conn = getConnection();
           
	    pstmt = conn.prepareStatement("select passwd from board where num = ?");
            pstmt.setInt(1, article.getNum());
            rs = pstmt.executeQuery();
           
	if(rs.next()){
	    dbpasswd= rs.getString("passwd");
	    if(dbpasswd.equals(article.getPasswd())){
		sql="update board set writer=?,email=?,subject=?,passwd=?";
		sql+=",content=? where num=?";
                pstmt = conn.prepareStatement(sql);

                pstmt.setString(1, article.getWriter());
                pstmt.setString(2, article.getEmail());
                pstmt.setString(3, article.getSubject());
                pstmt.setString(4, article.getPasswd());
                pstmt.setString(5, article.getContent());
		pstmt.setInt(6, article.getNum());
                pstmt.executeUpdate();
		x= 1;
	    }else{
		x= 0;
	    }
	  }
        } catch(Exception ex) {
            ex.printStackTrace();
        } finally {
	    if (rs != null) try { rs.close(); } catch(SQLException ex) {}
            if (pstmt != null) try { pstmt.close(); } catch(SQLException ex) {}
            if (conn != null) try { conn.close(); } catch(SQLException ex) {}
        }
	 return x;
    }
   
    //deletePro.jsp : 실제 데이터를 삭제하는 메소드...
    public int deleteArticle(int num, String passwd) throws Exception {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs= null;
        String dbpasswd="";
        int x=-1;
        try {
	    conn = getConnection();

            pstmt = conn.prepareStatement(
            "select passwd from board where num = ?");
            pstmt.setInt(1, num);
            rs = pstmt.executeQuery();
           
            if(rs.next()){
		dbpasswd= rs.getString("passwd");
		if(dbpasswd.equals(passwd)){
		    pstmt = conn.prepareStatement("delete from board where num=?");
                    pstmt.setInt(1, num);
                    pstmt.executeUpdate();
		    x= 1; //글삭제 성공
		}else
		    x= 0; //비밀번호 틀림
	    }
        } catch(Exception ex) {
            ex.printStackTrace();
        } finally {
            if (rs != null) try { rs.close(); } catch(SQLException ex) {}
            if (pstmt != null) try { pstmt.close(); } catch(SQLException ex) {}
            if (conn != null) try { conn.close(); } catch(SQLException ex) {}
        }
	return x;
    }
}