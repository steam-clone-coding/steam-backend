package com.clonecoding.steam.dto.fileserver;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@Getter
public class MultipleImageUploadResult {

    private Integer code;

    private String message;

    @JsonProperty("data")
    private List<UploadedImageInfo> uploadedImageInfo;
    private String serverUrl;

    public List<UploadedImageInfo> getUploadedImageInfo(){
        uploadedImageInfo.forEach(f->f.setFullPath(String.format("%s/images/%s", serverUrl, f.getFileId())));

         return uploadedImageInfo;
    }


    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

}
