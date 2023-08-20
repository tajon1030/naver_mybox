package com.numble.mybox.file;

import com.numble.mybox.exception.CustomException;
import com.numble.mybox.exception.ErrorCode;
import com.numble.mybox.user.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileController {
    private final FileService fileService;

    /**
     * 파일 업로드
     *
     * @param request
     * @param multipartFile
     * @return
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> uploadFile(HttpServletRequest request,
                                           @RequestPart MultipartFile multipartFile,
                                           @RequestParam Long folderId) {
        User loginUser = (User) request.getSession().getAttribute("loginUser");
        // 로그인 검증
        if (loginUser == null) {
            throw new CustomException(ErrorCode.INVALID_PERMISSION);
        }

        // 파일 업로드
        fileService.upload(multipartFile, loginUser.getId(), folderId);

        return ResponseEntity.ok().build();
    }


    /**
     * 파일 삭제
     *
     * @param request
     * @param fileId
     * @return
     */
    @DeleteMapping
    public ResponseEntity<Void> deleteFile(HttpServletRequest request,
                                           @RequestParam Long fileId) {
        User loginUser = (User) request.getSession().getAttribute("loginUser");
        // 로그인 검증
        if (loginUser == null) {
            throw new CustomException(ErrorCode.INVALID_PERMISSION);
        }

        fileService.deleteFile(fileId, loginUser.getId());

        return ResponseEntity.ok().build();
    }

    /**
     * 파일 다운로드
     *
     * @param request
     * @param fileId
     * @return
     */
    @GetMapping
    public ResponseEntity<Resource> downloadFile(HttpServletRequest request,
                                                 @RequestParam Long fileId) {
        User loginUser = (User) request.getSession().getAttribute("loginUser");
        // 로그인 검증
        if (loginUser == null) {
            throw new CustomException(ErrorCode.INVALID_PERMISSION);
        }

        File file = fileService.downloadFile(fileId, loginUser.getId());
        Resource resource = null;
        try {
            resource = new UrlResource("file:" + file.getOriName());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "application/octet-stream;charset=UTF-8")
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getOriName())
                .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(file.getSize()))
                .body(resource);
    }
}
