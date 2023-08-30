package com.clonecoding.steam.service;

import com.clonecoding.steam.dto.fileserver.ImageRemoveResult;
import com.clonecoding.steam.dto.fileserver.MultipleImageUploadResult;
import com.clonecoding.steam.dto.fileserver.SingleImageUploadResult;
import com.clonecoding.steam.dto.fileserver.UploadedImageInfo;
import com.clonecoding.steam.exceptions.InternalServerException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImageServerService {
    /**
     * @author minseok kim
     * @description 이미지를 정적서버에 업로드하는 메서드
     * @param image 저장하고자 하는 Image
     * @return 정적서버의 API 호출 결과. UploadedImageInfo.getFullPath()를 통해 이미지로 접근가능한 URL을 알 수 있음.
     * @exception IllegalArgumentException 이미지가 비어있거나, 이미지가 너무 커서 업로드에 실패한 경우
     * @exception InternalServerException IOException 발생시, 이미지 정적 서버에서 200이 아닌 나머지 응답코드를 남겼을 시
     */
    UploadedImageInfo upload(MultipartFile image);


    /**
     * @author minseok kim
     * @description 여러 이미지를 한번에 정적서버에 업로드하는 메서드
     * @param images 저장하고자 하는 Image list
     * @return 정적서버의 API 호출 결과. UploadedImageInfo.getFullPath()를 통해 이미지로 접근가능한 URL을 알 수 있음.
     * @exception IllegalArgumentException 하나 이상의 이미지의 확장자가 허용하지 않는 타입일 때, 하나 이상의 이미지가 너무 커서 업로드에 실패했을 때, 업로드하려는 이미지가 1개일때
     * @exception InternalServerException IOException 발생시, 이미지 정적 서버에서 200이 아닌 나머지 응답코드를 남겼을 시
     */
    List<UploadedImageInfo> upload(MultipartFile[] images);


    /**
     * @author minseok kim
     * @description 저장한 이미지를 삭제하는 메서드
     * @param fileId 삭제하고자 하는 파일의 id
     * @return 정적 서버의 호출 결과.
     * @exception IllegalArgumentException 삭제하고자 하는 이미지 파일이 존재하지 않을 때
     * @exception InternalServerException IOException 발생시, 이미지 정적 서버에서 200이 아닌 나머지 응답코드를 남겼을 시
     */
    void remove(String fileId);
}
