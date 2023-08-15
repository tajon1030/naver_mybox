package com.numble.mybox.folder.service;

import com.numble.mybox.folder.entity.Folder;
import com.numble.mybox.folder.repository.FolderPathRepository;
import com.numble.mybox.folder.repository.FolderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class FolderService {
    private final FolderRepository folderRepository;
    private final FolderPathRepository folderPathRepository;

    public Folder addFolder(Folder folder, Long parentFolderId) {
        Folder savedFolder = folderRepository.save(folder);
        folderPathRepository.saveFolderPath(savedFolder.getId(),parentFolderId);
        return savedFolder;
    }
}
