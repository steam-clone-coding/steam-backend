package com.clonecoding.steam.service;

import com.clonecoding.steam.dto.fileserver.ImageRemoveResult;
import com.clonecoding.steam.dto.fileserver.MultipleImageUploadResult;
import com.clonecoding.steam.dto.fileserver.SingleImageUploadResult;
import org.springframework.web.multipart.MultipartFile;

public interface ImageServerService {

    SingleImageUploadResult upload(MultipartFile image);

    MultipleImageUploadResult upload(MultipartFile[] images);

    ImageRemoveResult remove(String fileId);
}
