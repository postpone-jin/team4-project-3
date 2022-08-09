package controller;

import domain.BoardVO;
import domain.Criteria;
import domain.PageMaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import service.BoardService;

import javax.inject.Inject;
import java.util.List;

/**
 * Handles requests for the application home page.
 */
@Controller
@RequestMapping(value = "/board")
public class BoardController {

	private static final Logger logger = LoggerFactory.getLogger(BoardController.class);

	@Inject
	private BoardService boardService;


	@RequestMapping(value = "/register", method = RequestMethod.GET)
	public void registerGET(BoardVO board, Model model) throws Exception {
		logger.info("writeBoard get ...........");
	}

	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public String registPOST(BoardVO board,  RedirectAttributes rttr) throws Exception {

		logger.info("regist post ...........");
		logger.info(board.toString());

		boardService.regist(board);
		rttr.addFlashAttribute("msg","success");

		return "redirect:/board/list";
	}

	@RequestMapping(value = "/list")
	public void boardList(Model model) throws Exception {
		logger.info("// /board/list");

		List<BoardVO> list = boardService.listAll();

		logger.info("// list.toString()=" + list.toString());

		model.addAttribute("list", list);
	}
	@RequestMapping(value = "/read", method = RequestMethod.GET)
	public void read(@RequestParam("boardNo") int boardNo, Model model) throws Exception {

		model.addAttribute(boardService.read(boardNo));
	}


	@RequestMapping(value = "/remove", method = RequestMethod.POST)
	public String remove(@RequestParam("boardNo") int boardNo, RedirectAttributes rttr) throws Exception {

		boardService.remove(boardNo);

		rttr.addFlashAttribute("msg", "SUCCESS");

		return "redirect:/board/list";
	}

	@RequestMapping(value = "/modify", method = RequestMethod.GET)
	public void modifyGET(int boardNo, Model model) throws Exception {

		model.addAttribute(boardService.read(boardNo));
	}

	@RequestMapping(value = "/modify", method = RequestMethod.POST)
	public String modifyPOST(BoardVO board, RedirectAttributes rttr) throws Exception {

		logger.info("mod post............");

		boardService.modify(board);
		rttr.addFlashAttribute("msg", "SUCCESS");

		return "redirect:/board/list";
	}

	@RequestMapping(value = "/listCri", method = RequestMethod.GET)
	public void listAll(Criteria cri, Model model) throws Exception {

		logger.info("show list Page with Criteria......................");

		model.addAttribute("list", boardService.listCriteria(cri));
	}

	@RequestMapping(value = "/listPage", method = RequestMethod.GET)
	public void listPage(@ModelAttribute("cri") Criteria cri, Model model) throws Exception {

		logger.info(cri.toString());

		model.addAttribute("list", boardService.listCriteria(cri));
		PageMaker pageMaker = new PageMaker();
		pageMaker.setCri(cri);
		// pageMaker.setTotalCount(131);

		pageMaker.setTotalCount(boardService.listCountCriteria(cri));

		model.addAttribute("pageMaker", pageMaker);
	}


	@RequestMapping(value = "/readPage", method = RequestMethod.GET)
	public void read(@RequestParam("bno") int bno, @ModelAttribute("cri") Criteria cri, Model model) throws Exception {

		model.addAttribute(boardService.read(bno));
	}

	@RequestMapping(value = "/removePage", method = RequestMethod.POST)
	public String remove(@RequestParam("bno") int bno, Criteria cri, RedirectAttributes rttr) throws Exception {

		boardService.remove(bno);

		rttr.addAttribute("page", cri.getPage());
		rttr.addAttribute("perPageNum", cri.getPerPageNum());
		rttr.addFlashAttribute("msg", "SUCCESS");

		return "redirect:/board/listPage";
	}

	@RequestMapping(value = "/modifyPage", method = RequestMethod.GET)
	public void modifyPagingGET(@RequestParam("bno") int bno, @ModelAttribute("cri") Criteria cri, Model model)
			throws Exception {

		model.addAttribute(boardService.read(bno));
	}

}
