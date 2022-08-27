package controller;

import domain.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import service.NoticeService;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping(value = "/notice/*")
public class NoticeController {

    private static final Logger logger = LoggerFactory.getLogger(NoticeController.class);

    @Inject
    private NoticeService service;

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public void registerGET(NoticeVO notice, Model model) throws Exception {
        logger.info("register get ...........");


    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String registerPOST(NoticeVO notice, RedirectAttributes rttr, HttpServletRequest request) throws Exception {

        HttpSession session = request.getSession();
        MemberVO vo = (MemberVO) session.getAttribute("login");
        if (vo.getRank() != 3) {
            return "redirect:/users/notAdmin";
        } else {
            logger.info("regist post..........");
            logger.info(notice.toString());

            service.create(notice);
            rttr.addFlashAttribute("msg", "SUCCESS");
            return "redirect:/notice/list";
        }

    }


       /*logger.info("regist post..........");
        logger.info(notice.toString());

        service.create(notice);
        rttr.addFlashAttribute("msg", "SUCCESS");
        return "redirect:/notice/list";
*/


    @RequestMapping(value = "/listCri", method = RequestMethod.GET)
    public void listAll(Criteria cri, Model model) throws Exception {
        logger.info("show list Page with Criteria......................");
        model.addAttribute("list", service.listCriteria(cri));
    }


    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String list(Criteria cri, Model model) throws Exception {

        model.addAttribute("list", service.listNotice(cri));

        PageMaker pageMaker = new PageMaker();
        pageMaker.setCri(cri);
        pageMaker.setTotalCount(service.listCount());

        model.addAttribute("pageMaker", pageMaker);

        return "notice/list";
    }


    @RequestMapping(value = "/read", method = RequestMethod.GET)
    public void read(NoticeVO notice, int noticeNo, Model model) throws Exception {
        model.addAttribute("notice", service.read(noticeNo));
    }


    @RequestMapping(value = "/remove", method = RequestMethod.GET)
    public String remove(@RequestParam("noticeNo") Integer noticeNo, MemberVO member, RedirectAttributes rttr, HttpServletRequest request) throws Exception {

        HttpSession session = request.getSession();
        MemberVO vo = (MemberVO) session.getAttribute("login");
        if (vo.getRank() != 3) {
            return "redirect:/users/notAdmin";
        } else {


            service.delete(noticeNo);
            rttr.addFlashAttribute("msg", "SUCCESS");
            return "redirect:/notice/list";
        }

    }


   /* @RequestMapping(value = "/remove", method = RequestMethod.GET)
    public String remove(@RequestParam("noticeNo") Integer noticeNo, RedirectAttributes rttr) throws Exception {
        service.delete(noticeNo);
        rttr.addFlashAttribute("msg", "SUCCESS");
        return "redirect:/notice/list";

       *//*  if(memberVO.rank == 3){
             service.delete(noticeNo);
              rttr.addFlashAttribute("msg", "SUCCESS");
             return "redirect:/notice/list";
        } else {
            return "redirect:/users/errorNotAdmin";
        }
        *//*

    }*/


    @RequestMapping(value = "/modify", method = RequestMethod.GET)
    public void modifyGET(@RequestParam("noticeNo") int noticeNo, NoticeVO notice, MemberVO member, Model model) throws Exception {

        model.addAttribute("notice", service.read(noticeNo));

    }

    // return string으로 안바꾸고 이프문만 추가함 get방식에는 이렇게 하는게 옳은것 같은데 ?
    
    
/*
    @RequestMapping(value = "/modify", method = RequestMethod.GET)
    public void modifyGET(NoticeVO notice, Model model) throws Exception {

        model.addAttribute(service);

         *//*  if(memberVO.rank == 3){
              model.addAttribute(service);
        } else {
            return "redirect:/users/errorNotAdmin";
        }
        *//*

    }
    */


    @RequestMapping(value = "/modify", method = RequestMethod.POST)
    public String modifyPOST(NoticeVO notice, MemberVO member, RedirectAttributes rttr, HttpServletRequest request) throws Exception {

        logger.info("mod post..........");

        HttpSession session = request.getSession();
        MemberVO vo = (MemberVO) session.getAttribute("login");
        if (vo.getRank() != 3) {
            return "redirect:/users/notAdmin";
        } else {


            service.update(notice);
            rttr.addFlashAttribute("msg", "SUCCESS");
            return "redirect:/notice/list";
        }

    }

   /* @RequestMapping(value = "/modify", method = RequestMethod.POST)
    public String modifyPOST(NoticeVO notice, RedirectAttributes rttr) throws Exception {
        logger.info("mod post..........");

        service.update(notice);
        rttr.addFlashAttribute("msg", "SUCCESS");
        return "redirect:/notice/list";

          *//*  if(memberVO.rank == 3){
             logger.info("mod post..........");

        service.update(notice);
        rttr.addFlashAttribute("msg", "SUCCESS");
        return "redirect:/notice/list";
        } else {
            return "redirect:/users/errorNotAdmin";
        }
        *//*

    }
    */


}