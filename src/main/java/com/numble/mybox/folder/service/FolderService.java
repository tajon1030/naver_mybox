package com.numble.mybox.folder.service;

import com.numble.mybox.folder.entity.Folder;
import com.numble.mybox.folder.repository.FolderPathRepository;
import com.numble.mybox.folder.repository.FolderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Transactional
@Service
public class FolderService {
    private final FolderRepository folderRepository;
    private final FolderPathRepository folderPathRepository;

    public Folder addFolder(Folder folder, Long parentFolderId) {
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
}
