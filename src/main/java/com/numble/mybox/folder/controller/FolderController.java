package com.numble.mybox.folder.controller;

import com.numble.mybox.exception.CustomException;
import com.numble.mybox.exception.ErrorCode;
import com.numble.mybox.file.File;
import com.numble.mybox.file.FileService;
import com.numble.mybox.folder.dto.FolderFileListResponse;
import com.numble.mybox.folder.dto.FolderResponse;
import com.numble.mybox.folder.dto.FolderSaveDto;
import com.numble.mybox.folder.entity.Folder;
import com.numble.mybox.folder.mapper.FolderMapper;
import com.numble.mybox.folder.service.FolderService;
import com.numble.mybox.user.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/folders")
public class FolderController {

    private final FolderService folderService;
    private final FileService fileService;
    private final FolderMapper folderMapper;

    /**
     * 폴더 생성
     *
     * @param dto
     * @return
     */
    @PostMapping
    public ResponseEntity<FolderResponse> makeFolder(HttpServletRequest request, @RequestBody FolderSaveDto dto) {
        User loginUser = (User) request.getSession().getAttribute("loginUser");
        // 로그인 검증
        if (loginUser == null) {
            throw new CustomException(ErrorCode.INVALID_PERMISSION);
        }

        Folder savedFolder = folderService.addFolder(folderMapper.toFolder(dto, loginUser), dto.parentFolderId());
        return ResponseEntity.ok(folderMapper.toFolderResponse(savedFolder));
    }

    /**
     * 폴더 & 파일 조회
     *
     * @param request
     * @param folderId
     * @return
     */
    @GetMapping("/{folderId}")
    public ResponseEntity<FolderFileListResponse> getFolderAndFile(HttpServletRequest request, @PathVariable Long folderId) {
        User loginUser = (User) request.getSession().getAttribute("loginUser");
        // 로그인 검증
        if (loginUser == null) {
            throw new CustomException(ErrorCode.INVALID_PERMISSION);
        }

        // TODO 조회하려는 폴더의 소유자가 loginUser와 일치하는지 확인 필요

        // 자식 폴더 조회
        List<Folder> childFolderList = folderService.getChildFolderList(folderId);

        // 자식 파일 조회
        List<File> fileList = fileService.getFileList(folderId, loginUser.getId());

        return ResponseEntity.ok(folderMapper.toFolderFileResponse(childFolderList, fileList));
    }

    @DeleteMapping("/{folderId}")
    public ResponseEntity<Void> deleteFolder(HttpServletRequest request,
                                             @PathVariable Long folderId){
        User loginUser = (User) request.getSession().getAttribute("loginUser");
        // 로그인 검증
        if (loginUser == null) {
            throw new CustomException(ErrorCode.INVALID_PERMISSION);
        }

        folderService.deleteFolder(folderId, loginUser.getId());

        return ResponseEntity.ok().build();
    }
}
