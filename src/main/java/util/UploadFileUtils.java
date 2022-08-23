package util;

import org.imgscalr.Scalr;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class UploadFileUtils {
    public static String uploadFile(String uploadPath, String originalName, byte[] fileData) throws Exception{
        // UUID 발급
        UUID uuid = UUID.randomUUID();
        // 저장할 파일명 = UUID + 원본이름
        String savedName = uuid.toString() + "_" + originalName;
        // 업로드할 디렉토리(날짜별 폴더) 생성
        String savedPath = calcPath(uploadPath);
        // 파일 경로(기존의 업로드경로+날짜별경로), 파일명을 받아 파일 객체 생성
        File target = new File(uploadPath + savedPath, savedName);
        // 썸네일을 생성하기 위한 파일의 확장자 검사
        // 파일명이 aaa.bbb.ccc.jpg일 경우 마지막 마침표를 찾기 위해
        String formatName = originalName.substring(originalName.lastIndexOf(".") + 1);
        String uploadedFileName = null;

        try {
            // 임시 디렉토리에 업로드된 파일을 지정된 디렉토리로 복사
            FileCopyUtils.copy(fileData, target);

            // 이미지 파일은 썸네일 사용
            if (MediaUtils.getMediaType(formatName) != null) {
                // 썸네일 생성
                uploadedFileName = makeThumbnail(uploadPath, savedPath, savedName);
                // 나머지는 아이콘
            } else {
                // 아이콘 생성
                return null;
            }

        }catch (Exception e){
            e.printStackTrace();
        }
            return uploadedFileName;

    }


    private static String calcPath(String uploadPath){
        Calendar cal = Calendar.getInstance();
        // File.separator : 디렉토리 구분자(\\)
        // 연도, ex) \\2017
        String yearPath = File.separator + cal.get(Calendar.YEAR);
        System.out.println(yearPath);
        // 월, ex) \\2017\\03
        String monthPath = yearPath + File.separator + new DecimalFormat("00").format(cal.get(Calendar.MONTH) + 1);
        System.out.println(monthPath);
        // 날짜, ex) \\2017\\03\\01
        String datePath = monthPath + File.separator + new DecimalFormat("00").format(cal.get(Calendar.DATE));
        System.out.println(datePath);
        // 디렉토리 생성 메서드 호출
        makeDir(uploadPath, yearPath, monthPath, datePath);
        return datePath;
    }

    private static void makeDir(String uploadPath, String... paths) {
        // 디렉토리가 존재하면
        if (new File(paths[paths.length - 1]).exists()){
            return;
        }
        // 디렉토리가 존재하지 않으면
        for (String path : paths) {
            //
            File dirPath = new File(uploadPath + path);
            // 디렉토리가 존재하지 않으면
            if (!dirPath.exists()) {
                dirPath.mkdirs(); //디렉토리 생성
            }
        }
    }

    // 썸네일 생성
    private static String makeThumbnail(String uploadPath, String path, String fileName) throws Exception {
        // 이미지를 읽기 위한 버퍼
        BufferedImage sourceImg = ImageIO.read(new File(uploadPath + path, fileName));

        // 썸네일의 너비와 높이 입니다.
        int dw = 300, dh = 300;

        // 원본 이미지의 너비와 높이 입니다.
        int ow = sourceImg.getWidth();
        int oh = sourceImg.getHeight();

        // 원본 너비를 기준으로 하여 썸네일의 비율로 높이를 계산합니다.
        int nw = ow; int nh = (ow * dh) / dw;

        // 계산된 높이가 원본보다 높다면 crop이 안되므로
        // 원본 높이를 기준으로 썸네일의 비율로 너비를 계산합니다.
        if(nh > oh) {
            nw = (oh * dw) / dh;
            nh = oh;
        }

        BufferedImage cropImg = Scalr.crop(sourceImg, (ow-nw)/2, (oh-nh)/2, nw, nh);


        BufferedImage destImg =
                Scalr.resize(sourceImg, dw, dh);


        // 썸네일의 이름을 생성(원본파일명에 's_'를 붙임)
        String thumbnailName = uploadPath + path + File.separator + "s_" + fileName;
        File newFile = new File(thumbnailName);
        String formatName = fileName.substring(fileName.lastIndexOf(".") + 1);
        // 썸네일 생성
        ImageIO.write(destImg, formatName.toUpperCase(), newFile);
        // 썸네일의 이름을 리턴함
        return thumbnailName.substring(uploadPath.length()).replace(File.separatorChar, '/');
    }

    // 아이콘 생성
    private static String makeIcon(String uploadPath, String path, String fileName) throws Exception {
        // 아이콘의 이름
        String iconName = uploadPath + path + File.separator + fileName;
        // 아이콘 이름을 리턴
        // File.separatorChar : 디렉토리 구분자
        // 윈도우 \ , 유닉스(리눅스) /
        return iconName.substring(uploadPath.length()).replace(File.separatorChar, '/');
    }
}




//    private static final String filepath = "c:\\zzz\\upload\\";

//        public String updateImg(MultipartHttpServletRequest mpRequest) throws Exception{
//        Iterator<String> iterator = mpRequest.getFileNames();
//        MultipartFile multipartFile = null;
//        String originalFileName = null;
//        String originalFileExtension = null;
//        String storedFileName = null;
//        UUID uuid = UUID.randomUUID();
//        String memberImg = "";
//
//        File file = new File(filepath);
//        if(file.exists() == false) {
//            file.mkdirs();
//        }
//
//        while(iterator.hasNext()) {
//            multipartFile = mpRequest.getFile(iterator.next());
//            if(multipartFile.isEmpty() == false) {
//                originalFileName = multipartFile.getOriginalFilename();
//                originalFileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
//                storedFileName = uuid.toString() + "_" + originalFileName + originalFileExtension;
//                file = new File(filepath + storedFileName);
//                multipartFile.transferTo(file);
//                memberImg = storedFileName;
//            }
//        }
//        return memberImg;
//    }


