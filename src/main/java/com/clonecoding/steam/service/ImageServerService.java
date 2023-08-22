package com.clonecoding.steam.service;


import com.clonecoding.steam.dto.fileserver.MultipleImageUploadResult;
import com.clonecoding.steam.dto.fileserver.SingleImageUploadResult;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageServerService {

    private final String imageServerUrl;

    private final String singleFileUploadUri;

    private final String multiFileUploadUri;

    private final String deleteFileUri;

    private final CloseableHttpClient httpClient;


    public ImageServerService(Environment environment, CloseableHttpClient httpClient) {
        this.httpClient = httpClient;

        this.imageServerUrl = environment.getProperty("static.image-server-uri", String.class);
        this.singleFileUploadUri = environment.getProperty("static.single-file-upload-uri", String.class);
        this.multiFileUploadUri = environment.getProperty("static.multi-file-upload-uri", String.class);
        this.deleteFileUri = environment.getProperty("static.delete-uri", String.class);

    }


    public SingleImageUploadResult upload(MultipartFile image){
        return null;
    }


    public MultipleImageUploadResult upload(MultipartFile[] image){
        return null;
    }

}
