package com.numble.mybox.folder;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class FolderService {
    private final FolderRepository folderRepository;

    public Folder addFolder(Folder folder) {
        Folder savedFolder = folderRepository.save(folder);

        if (folder.getParent() != null) {
            folder.getParent().addChildFolder(folder);
        }

        return savedFolder;
    }
}
