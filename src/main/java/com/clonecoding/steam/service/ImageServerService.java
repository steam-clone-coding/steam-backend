package com.clonecoding.steam.service;


import com.clonecoding.steam.dto.fileserver.ImageRemoveResult;
import com.clonecoding.steam.dto.fileserver.MultipleImageUploadResult;
import com.clonecoding.steam.dto.fileserver.SingleImageUploadResult;
import com.clonecoding.steam.exceptions.ExceptionMessages;
import com.clonecoding.steam.exceptions.InternalServerException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
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

    private final String removeFileUri;

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
        this.removeFileUri = environment.getProperty("static.delete-uri", String.class);

    }



    /**
     * @author minseok kim
     * @description 인자로 이미지를 정적서버에 업로드하는 메서드
     * @param image 저장하고자 하는 Image
     * @return 정적서버의 API 호출 결과. SingleImageUploadImage.getFullPath()를 통해 이미지로 접근가능한 URL을 알 수 있음.
     * @exception InternalServerException IOException 발생시, 이미지 정적 서버에서 200이 아닌 응답코드를 남겼을 시
    */
    public SingleImageUploadResult upload(MultipartFile image){
        try{

            // 기본 url, header 설정
            HttpPost httpPost = new HttpPost(imageServerUrl + singleFileUploadUri);

            httpPost.addHeader("X-Access-Token", accessToken);
            httpPost.addHeader("Content-Type", MediaType.MULTIPART_FORM_DATA_VALUE);

            // body(multipart/form-data) 설정
            httpPost.setEntity(new InputStreamEntity(image.getInputStream()));


            // API 서버로 요청 및 응답 파싱
            CloseableHttpResponse response = httpClient.execute(httpPost);

            int responseStatusCode = getResponseStatusCode(response);

            if(responseStatusCode != 200){
                throw new InternalServerException("이미지 서버가 200이 아닌 상태 코드를 남겼습니다: " + responseStatusCode);
            }

            String responseBody = getResponseBody(response);


            SingleImageUploadResult singleImageUploadResult = objectMapper.readValue(responseBody, SingleImageUploadResult.class);

            // 클라이언트가 Full Path를 알기 위해 server url 설정
            singleImageUploadResult.setServerUrl(imageServerUrl);

            return singleImageUploadResult;
        }catch (IOException e){
            throw new InternalServerException(ExceptionMessages.IMAGE_SERVER_PROCESS_FAILED.getMessage(), e);
        }

    }


    public MultipleImageUploadResult upload(MultipartFile[] images){
        try{
            // 기본 url, header 설정
            HttpPost httpPost = new HttpPost(imageServerUrl + multiFileUploadUri);

            httpPost.addHeader("X-Access-Token", accessToken);
            httpPost.addHeader("Content-Type", MediaType.MULTIPART_FORM_DATA_VALUE);


            // body(multipart/form-data) 설정
            MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();

            for(MultipartFile image : images){
                multipartEntityBuilder.addBinaryBody("images", image.getInputStream());
            }

            HttpEntity multipartEntity = multipartEntityBuilder.build();

            httpPost.setEntity(multipartEntity);

            // API 서버로 요청 및 응답 파싱
            CloseableHttpResponse response = httpClient.execute(httpPost);

            int responseStatusCode = getResponseStatusCode(response);

            if(responseStatusCode != 200){
                throw new InternalServerException("이미지 서버가 200이 아닌 상태 코드를 남겼습니다: " + responseStatusCode);
            }

            String responseBody = getResponseBody(response);

            MultipleImageUploadResult multipleImageUploadResult = objectMapper.readValue(responseBody, MultipleImageUploadResult.class);

            // 클라이언트가 Full Path를 알기 위해 server url 설정
            multipleImageUploadResult.setServerUrl(imageServerUrl);

            return multipleImageUploadResult;

        }catch (IOException e){
            throw new InternalServerException(ExceptionMessages.IMAGE_SERVER_PROCESS_FAILED.getMessage(), e);
        }
    }


    public ImageRemoveResult remove(String fileId) {
        try{
            // 기본 url, header 설정
            HttpPost httpPost = new HttpPost(imageServerUrl + removeFileUri + "/" +fileId);
            httpPost.addHeader("X-Access-Token", accessToken);

            // API 서버로 요청 및 응답 파싱
            CloseableHttpResponse response = httpClient.execute(httpPost);

            int responseStatusCode = getResponseStatusCode(response);

            if(responseStatusCode != 200){
                throw new InternalServerException("이미지 서버가 200이 아닌 상태 코드를 남겼습니다: " + responseStatusCode);
            }

            String responseBody = getResponseBody(response);

            return objectMapper.readValue(responseBody, ImageRemoveResult.class);


        }catch (IOException e){
            throw new InternalServerException(ExceptionMessages.IMAGE_SERVER_PROCESS_FAILED.getMessage(), e);
        }


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
