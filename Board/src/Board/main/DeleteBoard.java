package Board.main;

import java.io.IOException;
import java.io.InputStream;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import Board.vo.BoardsVo;

public class DeleteBoard {
	public static void main(String[] arge){
	String res = "config.xml";
	SqlSession session = null; 
	
	try {
		InputStream is;
		is = Resources.getResourceAsStream(res);
		SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(is);
		session = factory.openSession();
		BoardsVo mem = new BoardsVo();//새로운 DTO 객체생성
		mem.setId("java");//객체에 ID저장
		BoardsVo mv = session.selectOne("member.getid", mem);//저장된 아이디로 아이디 검색
		if(mv.getId()!=null){//아이디를 받아왔는지 확인
		int x = session.update("member.delMem", mv);//업데이트 메서드 실행
		if(x==0){
			System.out.println("delete 실패!");
		}else{
			System.out.println("성공");
			
		}
		}
		session.commit();
	}catch(IOException e){
		//TODO Auto
	}finally{
		session.close();
	}
	}
	
}
