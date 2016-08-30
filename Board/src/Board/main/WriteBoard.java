package Board.main;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import Board.CommandAction;
import Board.vo.BoardsVo;

public class WriteBoard implements CommandAction{//글목록 처리
	
	public String requestPro(HttpServletRequest request,
			HttpServletResponse response) throws Throwable{
		System.out.println("WriteBoard 액션 실행됌");
		request.setCharacterEncoding("UTF-8");//한글 인코딩
		
		String res = "config.xml";
		SqlSession session = null;
		try {
			InputStream is;
			is = Resources.getResourceAsStream(res);
			SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(is);
			session = factory.openSession();
		
			BoardsVo mv = new BoardsVo();
			
			
			int x = session.insert("member.addMem", mv);
			if(x==0){
				System.out.println("insert 실패!");
			}else{
				System.out.println("성공");
			}
			session.commit();
		}catch(IOException e){
			//TODO Auto
		}finally{
			session.close();
		}
		}
	}
}
