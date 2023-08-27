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
     * @description 이미지를 정적서버에 업로드하는 메서드
     * @param image 저장하고자 하는 Image
     * @return 정적서버의 API 호출 결과. SingleImageUploadImage.getFullPath()를 통해 이미지로 접근가능한 URL을 알 수 있음.
     * @exception IllegalArgumentException 이미지가 비어있거나, 이미지가 너무 커서 업로드에 실패한 경우
     * @exception InternalServerException IOException 발생시, 이미지 정적 서버에서 200이 아닌 나머지 응답코드를 남겼을 시
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


            // TODO : 400일 때 확장자 오류, 빈 이미지 오류를 나누어 메시지를 전달해야함.
            if(responseStatusCode == 400){
                throw new IllegalArgumentException("이미지(Request Body)가 비어있습니다.");
            }
            else if(responseStatusCode == 413){
                throw new IllegalArgumentException("이미지가 너무 커서 업로드할 수 없습니다.");
            }
            else if(responseStatusCode != 200){
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

    /**
     * @author minseok kim
     * @description 여러 이미지를 한번에 정적서버에 업로드하는 메서드
     * @param images 저장하고자 하는 Image list
     * @return 정적서버의 API 호출 결과. SingleImageUploadImage.getFullPath()를 통해 이미지로 접근가능한 URL을 알 수 있음.
     * @exception IllegalArgumentException 하나 이상의 이미지의 확장자가 허용하지 않는 타입일 때, 하나 이상의 이미지가 너무 커서 업로드에 실패했을 때
     * @exception InternalServerException IOException 발생시, 이미지 정적 서버에서 200이 아닌 나머지 응답코드를 남겼을 시
    */
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


            // TODO : 400일 때 확장자 오류, 빈 이미지 오류를 나누어 메시지를 전달해야함.
            if(responseStatusCode == 400){
                throw new IllegalArgumentException("이미지의 확장자가 허용되지 않습니다.");
            }
            else if(responseStatusCode == 413){
                throw new IllegalArgumentException("이미지가 너무 커서 업로드할 수 없습니다.");
            }
            else if(responseStatusCode != 200){
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

    /**
     * @author minseok kim
     * @description 저장한 이미지를 삭제하는 메서드
     * @param fileId 삭제하고자 하는 파일의 id
     * @return 정적 서버의 호출 결과.
     * @exception IllegalArgumentException 삭제하고자 하는 이미지 파일이 존재하지 않을 때
     * @exception InternalServerException IOException 발생시, 이미지 정적 서버에서 200이 아닌 나머지 응답코드를 남겼을 시
    */
    public ImageRemoveResult remove(String fileId) {
        try{
            // 기본 url, header 설정
            HttpPost httpPost = new HttpPost(imageServerUrl + removeFileUri + "/" +fileId);
            httpPost.addHeader("X-Access-Token", accessToken);

            // API 서버로 요청 및 응답 파싱
            CloseableHttpResponse response = httpClient.execute(httpPost);

            int responseStatusCode = getResponseStatusCode(response);


            if(responseStatusCode == 400){
                throw new IllegalArgumentException("삭제하고자 하는 이미지 파일이 존재하지 않습니다.");
            }

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
