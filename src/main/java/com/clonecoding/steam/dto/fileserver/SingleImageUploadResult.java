package com.clonecoding.steam.dto.fileserver;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;


@Getter
public class SingleImageUploadResult {

    private Integer code;
    private String message;


    @JsonProperty("data")
    private UploadedImageInfo uploadedImageInfo;

    private String serverUrl;



    public UploadedImageInfo getUploadedImageInfo(){
        if(serverUrl == null || serverUrl.isBlank()){
            throw new IllegalStateException("server URL이 설정되지 않아서 FULL PATH를 생성할 수 없습니다.");
        }

        uploadedImageInfo.setFullPath(String.format("%s/images/%s", serverUrl, uploadedImageInfo.getFileId()));

        return uploadedImageInfo;
    }


    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }
}
