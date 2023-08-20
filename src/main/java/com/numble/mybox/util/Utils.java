package com.numble.mybox.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

public class Utils {

    public static File multipartToFile(MultipartFile multipartFile) throws IOException {
        // 프로젝트 폴더에 저장하기 위해 절대경로를 설정 (Window 의 Tomcat 은 Temp 파일을 이용한다)
        String absolutePath = new File("").getAbsolutePath() + "\\";
        File file = new File(absolutePath + "/" + multipartFile.getOriginalFilename());
        multipartFile.transferTo(file);
        return file;
    }

}
