package com.clonecoding.steam.service;


import com.clonecoding.steam.dto.fileserver.MultipleImageUploadResult;
import com.clonecoding.steam.dto.fileserver.SingleImageUploadResult;
import com.clonecoding.steam.exceptions.ExceptionMessages;
import com.clonecoding.steam.exceptions.InternalServerException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Service
public class ImageServerService {

    private final String imageServerUrl;

    private final String singleFileUploadUri;

    private final String multiFileUploadUri;

    private final String deleteFileUri;

    private final String accessToken;

    private final CloseableHttpClient httpClient;

    private final ObjectMapper objectMapper;
    public ImageServerService(Environment environment, CloseableHttpClient httpClient, ObjectMapper objectMapper) {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;

        this.imageServerUrl = environment.getProperty("static.image-server-uri", String.class);
        this.singleFileUploadUri = environment.getProperty("static.single-file-upload-uri", String.class);
        this.multiFileUploadUri = environment.getProperty("static.multi-file-upload-uri", String.class);
        this.accessToken = environment.getProperty("static.access-token");
        this.deleteFileUri = environment.getProperty("static.delete-uri", String.class);

    }


    public SingleImageUploadResult upload(MultipartFile image){
        try{
            HttpPost httpPost = new HttpPost(imageServerUrl + singleFileUploadUri);

            httpPost.addHeader("X-Access-Token", accessToken);
            httpPost.addHeader("Content-Type", MediaType.MULTIPART_FORM_DATA_VALUE);
            httpPost.setEntity(new InputStreamEntity(image.getInputStream()));

            CloseableHttpResponse response = httpClient.execute(httpPost);

            int responseStatusCode = getResponseStatusCode(response);
            String responseBody = getResponseBody(response);


            SingleImageUploadResult singleImageUploadResult = objectMapper.readValue(responseBody, SingleImageUploadResult.class);

            singleImageUploadResult.setServerUrl(imageServerUrl);

            return singleImageUploadResult;
        }catch (IOException e){
            throw new InternalServerException(ExceptionMessages.IMAGE_SERVER_PROCESS_FAILED.getMessage(), e);

        }

    }


    public MultipleImageUploadResult upload(MultipartFile[] image){
        return null;
    }



    private int getResponseStatusCode(CloseableHttpResponse response){
        return response.getStatusLine().getStatusCode();
    }

    private String getResponseBody(CloseableHttpResponse response){
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(
                response.getEntity().getContent()));
        ){
            String inputLine;
            StringBuffer responseBody = new StringBuffer();

            while ((inputLine = reader.readLine()) != null) {
                responseBody.append(inputLine);
            }

            return responseBody.toString();

        }catch (IOException e){
            throw new InternalServerException(ExceptionMessages.IMAGE_SERVER_PROCESS_FAILED.getMessage(), e);
        }

    }
}
