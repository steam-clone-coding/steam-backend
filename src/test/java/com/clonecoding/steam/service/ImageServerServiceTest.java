package com.clonecoding.steam.service;

import com.clonecoding.steam.dto.fileserver.MultipleImageUploadResult;
import com.clonecoding.steam.dto.fileserver.SingleImageUploadResult;
import com.clonecoding.steam.dto.fileserver.UploadedImageInfo;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.*;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileInputStream;

import static org.assertj.core.api.Assertions.*;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;


@SpringBootTest
@ActiveProfiles("test")
class ImageServerServiceTest {

    private ClientAndServer mockServer;


    @Autowired
    private ImageServerService imageServerService;

    @BeforeEach
    void setUp() {
        mockServer = startClientAndServer(1080);
    }

    @AfterEach
    void tearDown() {
        mockServer.stop();
    }

    @Test
    @DisplayName("JSON을 사용해서 Single Upload의 응답을 DTO객체로 변환할 수 있다.")
    public void t1() throws Exception{
        //given
        ObjectMapper objectMapper = new ObjectMapper();

        String testJsonString = "{\n" +
                "    \"code\": 200,\n" +
                "    \"message\": \"File(s) successfully uploaded.\",\n" +
                "    \"data\": {\n" +
                "        \"file_name\": \"bts_jk.gif\",\n" +
                "        \"file_id\": \"b7436194d5034bb69767688807393e48\"\n" +
                "    }\n" +
                "}";
        //when
        SingleImageUploadResult actual = objectMapper.readValue(testJsonString, SingleImageUploadResult.class);
        //then
        assertThat(actual.getCode()).isEqualTo(200);
        assertThat(actual.getMessage()).isEqualTo("File(s) successfully uploaded.");
    }

    @Test
    @DisplayName("JSON을 사용해서 Multi Upload의 응답을 DTO객체로 변환할 수 있다.")
    public void t2() throws Exception{
        //given
        ObjectMapper objectMapper = new ObjectMapper();

        String testJsonString = "{\n" +
                "    \"code\": 200,\n" +
                "    \"message\": \"File(s) successfully uploaded.\",\n" +
                "    \"data\": [\n" +
                "        {\n" +
                "            \"file_name\": \"jpeg_jk3.jpg\",\n" +
                "            \"file_id\": \"b3b2bc5b075f434692f71657afbae2c9\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"file_name\": \"png_jk.png\",\n" +
                "            \"file_id\": \"20995dfcf94a49e7b6d34ccce744609c\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"file_name\": \"png_bts.png\",\n" +
                "            \"file_id\": \"9d15ce7799dd499181bbc8cace4761b7\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"file_name\": \"jpg_cat.jpg\",\n" +
                "            \"file_id\": \"5fd0f71238ed4086b9bb58859ac3b271\"\n" +
                "        }\n" +
                "    ]\n" +
                "}";
        //when

        MultipleImageUploadResult actual = objectMapper.readValue(testJsonString, MultipleImageUploadResult.class);
        //then
        assertThat(actual.getCode()).isEqualTo(200);
        assertThat(actual.getMessage()).isEqualTo("File(s) successfully uploaded.");
    }

    @Test
    @DisplayName("JSON으로 Single Image Upload DTO 객체를 생성한다음, serverUrl을 set 해 uploadedImageInfo에서 이미지의 FULL PATH를 가져올 수 있다.")
    public void t3() throws Exception{
        //given
        ObjectMapper objectMapper = new ObjectMapper();

        String testJsonString = "{\n" +
                "    \"code\": 200,\n" +
                "    \"message\": \"File(s) successfully uploaded.\",\n" +
                "    \"data\": {\n" +
                "        \"file_name\": \"bts_jk.gif\",\n" +
                "        \"file_id\": \"b7436194d5034bb69767688807393e48\"\n" +
                "    }\n" +
                "}";
        SingleImageUploadResult testJsonObject = objectMapper.readValue(testJsonString, SingleImageUploadResult.class);
        testJsonObject.setServerUrl("http://test.com");
        //when
        UploadedImageInfo actual = testJsonObject.getUploadedImageInfo();

        //then
        assertThat(actual.getFileName()).isEqualTo("bts_jk.gif");
        assertThat(actual.getFileId()).isEqualTo("b7436194d5034bb69767688807393e48");
        assertThat(actual.getFullPath()).isEqualTo("http://test.com/images/b7436194d5034bb69767688807393e48");
    }

    @Test
    @DisplayName("이미지의 FULL PATH를 가져오고자 했지만, serverURL이 설정되어 있지 않다면 IllegalStateException을 throw 한다.")
    public void t4() throws Exception{
        //given
        ObjectMapper objectMapper = new ObjectMapper();

        String testJsonString = "{\n" +
                "    \"code\": 200,\n" +
                "    \"message\": \"File(s) successfully uploaded.\",\n" +
                "    \"data\": {\n" +
                "        \"file_name\": \"bts_jk.gif\",\n" +
                "        \"file_id\": \"b7436194d5034bb69767688807393e48\"\n" +
                "    }\n" +
                "}";
        SingleImageUploadResult testJsonObject = objectMapper.readValue(testJsonString, SingleImageUploadResult.class);
        //when & then
        assertThatThrownBy(()->testJsonObject.getUploadedImageInfo())
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("Single Image Upload를 호출해 정상적인 업로드 성공 결과를 받아올 수 있다.")
    public void t5() throws Exception{
        //given
        String testResponseBody = "{\n" +
                "    \"code\": 200,\n" +
                "    \"message\": \"File(s) successfully uploaded.\",\n" +
                "    \"data\": {\n" +
                "        \"file_name\": \"bts_jk.gif\",\n" +
                "        \"file_id\": \"b7436194d5034bb69767688807393e48\"\n" +
                "    }\n" +
                "}";

        mockServer.when(
                HttpRequest.request()
                        .withMethod("POST")
                        .withPath("/images/upload")
                        .withHeader("X-Access-Token", "c95a5024651547fa82e1eebc0daa52a2")
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA_VALUE)
        ).respond(
                HttpResponse.response()
                        .withStatusCode(HttpStatus.OK.value())
                        .withBody(testResponseBody)
        );

        FileInputStream fileInputStream = new FileInputStream(new File("testImage.png"));
        MultipartFile multipartFile = new MockMultipartFile("testImage.png", fileInputStream);


        //when
        SingleImageUploadResult actual = imageServerService.upload(multipartFile);


        //then
        assertThat(actual.getCode()).isEqualTo(200);
        assertThat(actual.getMessage()).isEqualTo("File(s) successfully uploaded.");

        assertThat(actual.getUploadedImageInfo().getFileName()).isEqualTo("bts_jk.gif");
        assertThat(actual.getUploadedImageInfo().getFileId()).isEqualTo("b7436194d5034bb69767688807393e48");
        assertThat(actual.getUploadedImageInfo().getFullPath()).isEqualTo("http://localhost:1080/images/b7436194d5034bb69767688807393e48");


    }


}