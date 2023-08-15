package com.numble.mybox.folder;

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
