package Board.main;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import Board.BoardDBBean;
import Board.CommandAction;
import Board.vo.BoardsVo;

public class ListBoard implements CommandAction{//�۸�� ó��
	
	public String requestPro(HttpServletRequest request,
			HttpServletResponse response) throws Throwable{
		System.out.println("list �׼� ������");
		String pageNum = request.getParameter("pageNum");//������ ��ȣ
		
		if(pageNum == null){
			pageNum = "1";
		}
		int pageSize = 10;//�� �������� ���� ����
		int currentPage = Integer.parseInt(pageNum);
		int startRow = (currentPage - 1) * pageSize + 1;//�� �������� ���۱� ��ȣ
		int endRow = currentPage * pageSize;//�� �������� ������ �۹�ȣ 
		int count = 0;
		int number = 0;
		List<BoardsVo> boardlist = null;
		
		try {
			String res = "config.xml";
			InputStream is = Resources.getResourceAsStream(res);
			SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(is);
			SqlSession session = factory.openSession();
			count = session.selectOne("board.getCount");
			
			if(count > 0){
				/*articleList = dbPro.getArticles(startRow, endRow);*///���� �������� �ش��ϴ� �� ���
				HashMap<String, Integer> map = new HashMap<String, Integer>();
				map.put("start", startRow);
				map.put("end", endRow);
				boardlist = session.selectList("board.getPage",map);
			}else{
				boardlist = Collections.EMPTY_LIST;
			}
			session.close();
		} catch (IOException ie) {
			System.out.println(ie.getMessage());
		}
		
		
		number = count - (currentPage -1) * pageSize;//�۸�Ͽ� ǥ���� �۹�ȣ
		//�ش� �信�� ����� �Ӽ�
		request.setAttribute("currentPage", new Integer(currentPage));
		request.setAttribute("startRow", new Integer(startRow));
		request.setAttribute("endRow", new Integer(endRow));
		request.setAttribute("count", new Integer(count));
		request.setAttribute("pageSize", new Integer(pageSize));
		request.setAttribute("number", new Integer(number));
		request.setAttribute("boardlist", boardlist);
		
		return "/board/list.jsp";//�ش� ��
	}
	
}
