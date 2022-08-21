package controller;

import domain.MemberVO;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.IOUtils;
import org.apache.ibatis.annotations.Param;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import service.MemberService;
import util.MediaUtils;
import util.UploadFileUtils;

import javax.annotation.Resource;
import javax.inject.Inject;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

@Controller
@RequestMapping(value = "/users/*")
public class MemberController {

    private static final Logger logger = LoggerFactory.getLogger(MemberController.class);

    @Inject
    private MemberService service;

    @Resource(name = "uploadPath")
    private String uploadPath;


    private UploadFileUtils uploadFileUtils;

    @Setter
    @Getter
    private String fullname ;


    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public void registerGET(MemberVO member, Model model) throws Exception {
        logger.info("register get ...........");
    }
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String registerPOST(MemberVO member, @RequestParam("email2")String email2, @RequestParam(value = "email3",defaultValue = "" ,required = false)String email3
            ,RedirectAttributes rttr, @RequestParam("yy")String yy, @RequestParam("mm")String mm, @RequestParam("dd")String dd) throws Exception {
        String cryptPW = BCrypt.hashpw(member.getPw(), BCrypt.gensalt());
        member.setPw(cryptPW);
        member.setEmail(member.getEmail()+"@"+email2+email3);
        member.setFilename(getFullname());
        member.setBirth(yy+"."+mm+"."+dd);
    logger.info("regist post..........");
    logger.info(member.toString());

    try {
        service.regist(member);
        rttr.addFlashAttribute("msg", "SUCCESS");
        return "redirect:/";
    } catch (Exception e){
        e.printStackTrace();
        rttr.addFlashAttribute("msg", "FAIL");
        return "redirect:/users/register";
    }
}

//    }

    @RequestMapping(value = "/remove", method = RequestMethod.POST)
    public String remove(@RequestParam("memno") Integer memno, RedirectAttributes rttr) throws Exception{
        service.remove(memno);
        rttr.addFlashAttribute("msg","SUCCESS");
        return "redirect:/" ;
    }

    @RequestMapping(value = "/modify", method = RequestMethod.GET)
    public void modifyGET(MemberVO member, Model model)throws Exception{

        model.addAttribute(service);
    }

    @RequestMapping(value = "/modify", method = RequestMethod.POST)
    public String modifyPOST(MemberVO member, RedirectAttributes rttr)throws Exception{
        logger.info("mod post..........");

        service.modify(member);
        rttr.addFlashAttribute("msg", "SUCCESS");
        return "redirect:/";
    }
    @RequestMapping(value = "/idCheck", method = RequestMethod.POST)
    @ResponseBody
    public int idCheck(@RequestParam("id") String id) throws Exception {
        int result = service.idCheck(id);

        return result;
    }

    @RequestMapping(value = "/nicknameCheck", method = RequestMethod.POST)
    @ResponseBody
    public int nicknameCheck(@RequestParam("nickname") String nickname) throws Exception {
        int result = service.nicknameCheck(nickname);

        return result;
    }

    @RequestMapping(value="confirmEmail", method = RequestMethod.GET)
    public String emailConfirm(@Param("email") String email, @Param("authKey")String authKey, Model model) throws Exception{

        service.memberAuth(email,authKey);
        model.addAttribute("memberEmail", email);

        return "redirect:/";
    }

    @ResponseBody
    @RequestMapping(value="/register/uploadAjax", method=RequestMethod.POST, produces="text/plain;charset=utf-8")
    public ResponseEntity<String> uploadAjax(MultipartFile file) throws Exception {
        logger.info("originalName : "+file.getOriginalFilename());
        logger.info("size : "+file.getSize());
        logger.info("contentType : "+file.getContentType());

        String uploadFile = uploadFileUtils.uploadFile(uploadPath, file.getOriginalFilename(), file.getBytes());
        setFullname(uploadFile);
        if(uploadFile != null) {
            ResponseEntity<String> result = new ResponseEntity<String>(uploadFile, HttpStatus.OK);
            return result;
        }else{
            ResponseEntity<String> result = new ResponseEntity<String>(uploadFile, HttpStatus.BAD_REQUEST);
            return result;
        }
    }

    @ResponseBody
    @RequestMapping("/register/displayFile")
    public ResponseEntity<byte[]>  displayFile(String fileName, MemberVO vo)throws Exception{

        InputStream in = null;
        ResponseEntity<byte[]> entity = null;

        logger.info("FILE NAME: " + fileName);

        try{

            String formatName = fileName.substring(fileName.lastIndexOf(".")+1);

            MediaType mType = MediaUtils.getMediaType(formatName);

            HttpHeaders headers = new HttpHeaders();

            in = new FileInputStream(uploadPath+fileName);

            if(mType != null){
                headers.setContentType(mType);
            }else{

                fileName = fileName.substring(fileName.indexOf("_")+1);
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                headers.add("Content-Disposition", "attachment; filename=\""+
                        new String(fileName.getBytes("UTF-8"), "ISO-8859-1")+"\"");
            }

            entity = new ResponseEntity<byte[]>(IOUtils.toByteArray(in),
                    headers,
                    HttpStatus.CREATED);
        }catch(Exception e){
            e.printStackTrace();
            entity = new ResponseEntity<byte[]>(HttpStatus.BAD_REQUEST);
        }finally{
            in.close();
        }
        return entity;
    }

    @ResponseBody
    @RequestMapping(value="/register/deleteFile", method=RequestMethod.POST)
    public ResponseEntity<String> deleteFile(String fileName){

        logger.info("delete file: "+ fileName);
        //  /2022/07/20/s_ejklsjkle_파일명.jpg
        String formatName = fileName.substring(fileName.lastIndexOf(".")+1);

        MediaType mType = MediaUtils.getMediaType(formatName);

        if(mType != null){

            String front = fileName.substring(0,12);  // /2022/07/20/
            String end = fileName.substring(14);      // ejklsjkle_파일명.jpg
            new File(uploadPath + (front+end).replace('/', File.separatorChar)).delete();  //서버에 올라간 파일을 지운다
        }

        new File(uploadPath + fileName.replace('/', File.separatorChar)).delete();


        return new ResponseEntity<String>("deleted", HttpStatus.OK);
    }

    @ResponseBody
    @RequestMapping(value="/register/deleteAllFiles", method=RequestMethod.POST)
    public ResponseEntity<String> deleteFile(@RequestParam("files[]") String[] files){

        logger.info("delete all files: "+ files);

        if(files == null || files.length == 0) {
            return new ResponseEntity<String>("deleted", HttpStatus.OK);
        }

        for (String fileName : files) {
            String formatName = fileName.substring(fileName.lastIndexOf(".")+1);

            MediaType mType = MediaUtils.getMediaType(formatName);

            if(mType != null){

                String front = fileName.substring(0,12);
                String end = fileName.substring(14);
                new File(uploadPath + (front+end).replace('/', File.separatorChar)).delete();
            }

            new File(uploadPath + fileName.replace('/', File.separatorChar)).delete();

        }
        return new ResponseEntity<String>("deleted", HttpStatus.OK);
    }

}
