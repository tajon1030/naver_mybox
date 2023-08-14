package com.numble.mybox.folder;

import com.numble.mybox.exception.CustomException;
import com.numble.mybox.exception.ErrorCode;
import com.numble.mybox.folder.dto.FolderResponse;
import com.numble.mybox.folder.dto.FolderSaveDto;
import com.numble.mybox.user.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/folders")
public class FolderController {

    private final FolderService folderService;
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

        Folder savedFolder = folderService.addFolder(folderMapper.toFolder(dto, loginUser));
        return ResponseEntity.ok(folderMapper.toFolderResponse(savedFolder));
    }
}
