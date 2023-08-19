package com.numble.mybox.folder.service;

import com.numble.mybox.file.FileService;
import com.numble.mybox.folder.entity.Folder;
import com.numble.mybox.folder.repository.FolderPathRepository;
import com.numble.mybox.folder.repository.FolderRepository;
import com.numble.mybox.util.S3Client;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional
@Service
@Slf4j
public class FolderService {
    private final FolderRepository folderRepository;
    private final FolderPathRepository folderPathRepository;
    private final FileService fileService;
    private final S3Client s3Client;

    public Folder addFolder(Folder folder, Long parentFolderId, Long userId) {
        // TODO 부모폴더소유자가 loginUser 와 일치하는지 확인 필요

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
    public List<Folder> getChildFolderList(Long folderId) {
        return folderPathRepository.findByAncestorAndDepth(folderId, 1L);
    }

    /**
     * 전체 하위 폴더 조회
     *
     * @param folderId
     * @return
     */
    @Transactional(readOnly = true)
    public List<Folder> getSubFolderList(Long folderId) {
        return folderPathRepository.findByAncestorAndDepth(folderId, null);
    }

    /**
     * 폴더 삭제
     *
     * @param folderId
     * @param userId
     */
    public void deleteFolder(Long folderId, Long userId) {
        List<Long> subFolderIdList = getSubFolderList(folderId).stream()
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
