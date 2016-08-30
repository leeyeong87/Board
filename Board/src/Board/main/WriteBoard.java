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

public class WriteBoard implements CommandAction{//�۸�� ó��
	
	public String requestPro(HttpServletRequest request,
			HttpServletResponse response) throws Throwable{
		System.out.println("WriteBoard �׼� ������");
		request.setCharacterEncoding("UTF-8");//�ѱ� ���ڵ�
		
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
				System.out.println("insert ����!");
			}else{
				System.out.println("����");
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
