package com.clonecoding.steam.service;

import com.clonecoding.steam.dto.fileserver.ImageRemoveResult;
import com.clonecoding.steam.dto.fileserver.MultipleImageUploadResult;
import com.clonecoding.steam.dto.fileserver.SingleImageUploadResult;
import com.clonecoding.steam.dto.fileserver.UploadedImageInfo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImageServerService {


    UploadedImageInfo upload(MultipartFile image);
    List<UploadedImageInfo> upload(MultipartFile[] images);
    ImageRemoveResult remove(String fileId);
}
