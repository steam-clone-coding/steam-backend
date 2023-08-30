package com.clonecoding.steam.dto.fileserver;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
public class UploadedImageInfo {


    @JsonProperty("file_id")
    private String fileId;


    @JsonProperty("file_name")
    private String fileName;

    private String fullPath;

    protected void setFullPath(String fullPath) {
        this.fullPath = fullPath;
    }
}
