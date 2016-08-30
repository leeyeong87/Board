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

public class ListBoard implements CommandAction{//글목록 처리
	
	public String requestPro(HttpServletRequest request,
			HttpServletResponse response) throws Throwable{
		System.out.println("list 액션 실행됌");
		String pageNum = request.getParameter("pageNum");//페이지 번호
		
		if(pageNum == null){
			pageNum = "1";
		}
		int pageSize = 10;//한 페이지의 글의 개수
		int currentPage = Integer.parseInt(pageNum);
		int startRow = (currentPage - 1) * pageSize + 1;//한 페이지의 시작글 번호
		int endRow = currentPage * pageSize;//한 페이지의 마지막 글번호 
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
				/*articleList = dbPro.getArticles(startRow, endRow);*///현재 페이지에 해당하는 글 목록
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
		
		
		number = count - (currentPage -1) * pageSize;//글목록에 표시할 글번호
		//해당 뷰에서 사용할 속성
		request.setAttribute("currentPage", new Integer(currentPage));
		request.setAttribute("startRow", new Integer(startRow));
		request.setAttribute("endRow", new Integer(endRow));
		request.setAttribute("count", new Integer(count));
		request.setAttribute("pageSize", new Integer(pageSize));
		request.setAttribute("number", new Integer(number));
		request.setAttribute("boardlist", boardlist);
		
		return "/board/list.jsp";//해당 뷰
	}
	
}
