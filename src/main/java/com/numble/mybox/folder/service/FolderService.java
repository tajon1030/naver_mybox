package com.numble.mybox.folder.service;

import com.numble.mybox.exception.CustomException;
import com.numble.mybox.exception.ErrorCode;
import com.numble.mybox.file.FileService;
import com.numble.mybox.folder.entity.Folder;
import com.numble.mybox.folder.repository.FolderPathRepository;
import com.numble.mybox.folder.repository.FolderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Transactional
@Service
@Slf4j
public class FolderService {
    private final FolderRepository folderRepository;
    private final FolderPathRepository folderPathRepository;
    private final FileService fileService;

    public Folder addFolder(Folder folder, Long parentFolderId, Long userId) {
        // 부모폴더소유자가 loginUser 와 일치하는지 확인
        if (parentFolderId != null) {
            Folder parentFolder = folderRepository.findById(parentFolderId)
                    .orElseThrow(() -> new CustomException(ErrorCode.FOLDER_NOT_FOUND));
            if (!parentFolder.getUser().getId().equals(userId)) {
                throw new CustomException(ErrorCode.INVALID_PERMISSION);
            }
        }

        Folder savedFolder = folderRepository.save(folder);
        folderPathRepository.saveFolderPath(savedFolder.getId(), parentFolderId);
        return savedFolder;
    }

    /**
     * 자식 폴더 조회
     *
     * @param folderId
     * @return
     */
    @Transactional(readOnly = true)
    public List<Folder> getChildFolderList(Long folderId, Long userId) {
        // 폴더소유자가 loginUser 와 일치하는지 확인
        folderRepository.findByIdAndUserId(folderId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.FOLDER_NOT_FOUND));

        return folderPathRepository.findByAncestorAndDepth(folderId, 1L);
    }

    /**
     * 전체 하위 폴더 조회
     *
     * @param folderId
     * @return
     */
    @Transactional(readOnly = true)
    public List<Folder> getSubFolderList(Long folderId, Long userId) {
        // 폴더소유자가 loginUser 와 일치하는지 확인
        folderRepository.findByIdAndUserId(folderId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.FOLDER_NOT_FOUND));

        return folderPathRepository.findByAncestorAndDepth(folderId, null);
    }

    /**
     * 폴더 삭제
     *
     * @param folderId
     * @param userId
     */
    public void deleteFolder(Long folderId, Long userId) {
        List<Long> subFolderIdList = getSubFolderList(folderId, userId).stream()
                .map(Folder::getId)
                .toList();
        // 파일 정보 삭제
        for (Long subFolderId : subFolderIdList) {
            fileService.deleteFileWithFolderId(subFolderId, userId);
        }
        // 폴더 정보 삭제
        folderRepository.deleteAllById(subFolderIdList);
        folderPathRepository.deleteByFolderPathIdDescendant(folderId);
    }
}
