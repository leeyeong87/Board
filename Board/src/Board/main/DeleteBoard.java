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
		BoardsVo mem = new BoardsVo();//���ο� DTO ��ü����
		mem.setId("java");//��ü�� ID����
		BoardsVo mv = session.selectOne("member.getid", mem);//����� ���̵�� ���̵� �˻�
		if(mv.getId()!=null){//���̵� �޾ƿԴ��� Ȯ��
		int x = session.update("member.delMem", mv);//������Ʈ �޼��� ����
		if(x==0){
			System.out.println("delete ����!");
		}else{
			System.out.println("����");
			
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
