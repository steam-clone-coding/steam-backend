package com.clonecoding.steam.service;

import com.clonecoding.steam.dto.fileserver.ImageRemoveResult;
import com.clonecoding.steam.dto.fileserver.MultipleImageUploadResult;
import com.clonecoding.steam.dto.fileserver.SingleImageUploadResult;
import com.clonecoding.steam.dto.fileserver.UploadedImageInfo;
import com.clonecoding.steam.exceptions.InternalServerException;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.BeforeEach;
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
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;


@SpringBootTest
@ActiveProfiles("test")
class ImageServerServiceTest {

    private static ClientAndServer mockServer;


    @Autowired
    private ImageServerService imageServerService;


    @BeforeAll
    static void beforeAll() {
        mockServer = startClientAndServer(1080);
    }

    @BeforeEach
    public void setUp() {
        mockServer.reset();
    }


    @AfterAll
    static void afterAll() {
        mockServer.close();
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
        final UploadedImageInfo actual = imageServerService.upload(multipartFile);


        //then
        assertThat(actual.getFileName()).isEqualTo("bts_jk.gif");
        assertThat(actual.getFileId()).isEqualTo("b7436194d5034bb69767688807393e48");
        assertThat(actual.getFullPath()).isEqualTo("http://localhost:1080/images/b7436194d5034bb69767688807393e48");


    }

    @Test
    @DisplayName("이미지 서버의 Access Token이 잘못되었을 때, InternalServerException을 throw 하며 403 오류코드를 보여준다.")
    public void t6() throws Exception{
        //given
        mockServer.when(
                HttpRequest.request()
                        .withMethod("POST")
                        .withPath("/images/upload")
                        // 잘못된 토큰이라고 가정
                        .withHeader("X-Access-Token", "c95a5024651547fa82e1eebc0daa52a2")
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA_VALUE)
        ).respond(
                HttpResponse.response()
                        .withStatusCode(HttpStatus.FORBIDDEN.value())
        );

        FileInputStream fileInputStream = new FileInputStream(new File("testImage.png"));
        MultipartFile multipartFile = new MockMultipartFile("testImage.png", fileInputStream);

        //when & then
        assertThatThrownBy(()->imageServerService.upload(multipartFile))
                .isInstanceOf(InternalServerException.class)
                .hasMessage("이미지 서버가 200이 아닌 상태 코드를 남겼습니다: 403");

    }

    @Test
    @DisplayName("이미지 업로드시, Body가 비어있는 경우 IllegalArgumentException를 throw 한다.")
    public void t9() throws Exception{
        //given
        mockServer.when(
                HttpRequest.request()
                        .withMethod("POST")
                        .withPath("/images/upload")
                        .withHeader("X-Access-Token", "c95a5024651547fa82e1eebc0daa52a2")
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA_VALUE)
        ).respond(
                HttpResponse.response()
                        .withStatusCode(HttpStatus.BAD_REQUEST.value())
        );
        // 빈 이미지 전달
        MultipartFile multipartFile = new MockMultipartFile("testImage.png", new byte[]{});

        //when & then
        assertThatThrownBy(()->imageServerService.upload(multipartFile))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미지(Request Body)가 비어있습니다.");


    }


    @Test
    @DisplayName("이미지 업로드시, 파일이 너무 큰 경우 IllegalArgumentException을 throw 한다.")
    public void t10() throws Exception{
        //given
        mockServer.when(
                HttpRequest.request()
                        .withMethod("POST")
                        .withPath("/images/upload")
                        .withHeader("X-Access-Token", "c95a5024651547fa82e1eebc0daa52a2")
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA_VALUE)
        ).respond(
                //이미지가 너무 크다고 가정
                HttpResponse.response()
                        .withStatusCode(413)
        );

        FileInputStream fileInputStream = new FileInputStream(new File("testImage.png"));
        MultipartFile multipartFile = new MockMultipartFile("testImage.png", fileInputStream);

        //when & then
        assertThatThrownBy(()->imageServerService.upload(multipartFile))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미지가 너무 커서 업로드할 수 없습니다.");


    }



    @Test
    @DisplayName("multi Image upload API를 호출해 정상적인 업로드 성공 결과를 받아올 수 있다.")
    public void t7() throws Exception{
        //given
        String testResponseBody = "{\n" +
                "    \"code\": 200,\n" +
                "    \"message\": \"File(s) successfully uploaded.\",\n" +
                "    \"data\": [\n" +
                "        {\n" +
                "            \"file_name\": \"testImage.png\",\n" +
                "            \"file_id\": \"b3b2bc5b075f434692f71657afbae2c9\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"file_name\": \"testImage2.png\",\n" +
                "            \"file_id\": \"20995dfcf94a49e7b6d34ccce744609c\"\n" +
                "        }" +
                "    ]\n" +
                "}";

        mockServer.when(
                HttpRequest.request()
                        .withMethod("POST")
                        .withPath("/images/upload_many")
                        .withHeader("X-Access-Token", "c95a5024651547fa82e1eebc0daa52a2")
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA_VALUE)
        ).respond(
                HttpResponse.response()
                        .withStatusCode(HttpStatus.OK.value())
                        .withBody(testResponseBody)
        );

        FileInputStream fileInputStream = new FileInputStream(new File("testImage.png"));
        MultipartFile multipartFile = new MockMultipartFile("testImage.png", fileInputStream);

        FileInputStream fileInputStream2 = new FileInputStream(new File("testImage2.png"));
        MultipartFile multipartFile2 = new MockMultipartFile("testImage2.png", fileInputStream2);
        //when
        final List<UploadedImageInfo> actual = imageServerService.upload(new MultipartFile[]{multipartFile, multipartFile2});
        //then
        assertThat(actual).extracting( "fileName", "fileId")
                .contains(
                        tuple("testImage.png", "b3b2bc5b075f434692f71657afbae2c9"),
                        tuple("testImage2.png", "20995dfcf94a49e7b6d34ccce744609c")
                );


    }


    @Test
    @DisplayName("multi Image upload시 업로드된 이미지들 중 한 건의 이미지라도 허용되지 않는 확장자인 경우, 이미지 모두의 저장이 실패하며 IllegalArgumentException을 throw한다.")
    public void t11() throws Exception{
        String testResponseBody = "{\n" +
                "    \"code\": 400,\n" +
                "    \"message\": \"File extension of file webp_jk.webp not allowed.\"\n" +
                "}";

        mockServer.when(
                HttpRequest.request()
                        .withMethod("POST")
                        .withPath("/images/upload_many")
                        .withHeader("X-Access-Token", "c95a5024651547fa82e1eebc0daa52a2")
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA_VALUE)
        ).respond(
                HttpResponse.response()
                        .withStatusCode(HttpStatus.BAD_REQUEST.value())
                        .withBody(testResponseBody)
        );

        FileInputStream fileInputStream = new FileInputStream(new File("testImage.png"));
        MultipartFile multipartFile = new MockMultipartFile("testImage.png", fileInputStream);

        FileInputStream fileInputStream2 = new FileInputStream(new File("testImage2.png"));
        MultipartFile multipartFile2 = new MockMultipartFile("testImage2.png", fileInputStream2);

        //when & then
        assertThatThrownBy(()->imageServerService.upload(new MultipartFile[]{multipartFile, multipartFile2}))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미지의 확장자가 허용되지 않습니다.");

    }

    @Test
    @DisplayName("멀티 이미지 업로드시, 파일이 너무 큰 경우 IllegalArgumentException과 함께 이미지 서버가 리턴하는 413코드를 보여준다.")
    public void t12() throws Exception{
        //given
        String testResponseBody = "<html>\n" +
                "\n" +
                "<head>\n" +
                "  <title>413 Request Entity Too Large</title>\n" +
                "</head>\n" +
                "\n" +
                "<body>\n" +
                "  <center>\n" +
                "    <h1>413 Request Entity Too Large</h1>\n" +
                "  </center>\n" +
                "  <hr>\n" +
                "  <center>nginx/1.20.2</center>\n" +
                "</body>\n" +
                "\n" +
                "</html>";

        mockServer.when(
                HttpRequest.request()
                        .withMethod("POST")
                        .withPath("/images/upload_many")
                        .withHeader("X-Access-Token", "c95a5024651547fa82e1eebc0daa52a2")
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA_VALUE)
        ).respond(
                HttpResponse.response()
                        .withStatusCode(413)
                        .withBody(testResponseBody)
        );

        FileInputStream fileInputStream = new FileInputStream(new File("testImage.png"));
        MultipartFile multipartFile = new MockMultipartFile("testImage.png", fileInputStream);

        FileInputStream fileInputStream2 = new FileInputStream(new File("testImage2.png"));
        MultipartFile multipartFile2 = new MockMultipartFile("testImage2.png", fileInputStream2);


        //when & then
        assertThatThrownBy(()->imageServerService.upload(new MultipartFile[]{multipartFile, multipartFile2}))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미지가 너무 커서 업로드할 수 없습니다.");
    
    }


    @Test
    @DisplayName("image remove API를 호출해 정상적으로 삭제되었을 시 아무 오류를 throw하지 않는다.")
    public void t8() throws Exception{
        //given
        String testResponseBody = "{\n" +
                "    \"code\": 200,\n" +
                "    \"message\": \"File b3b2bc5b075f434692f71657afbae2c9 successfully deleted.\"\n" +
                "}";

        mockServer.when(
                HttpRequest.request()
                        .withMethod("POST")
                        .withPath("/images/delete/b3b2bc5b075f434692f71657afbae2c9")
                        .withHeader("X-Access-Token", "c95a5024651547fa82e1eebc0daa52a2")
        ).respond(
                HttpResponse.response()
                        .withStatusCode(HttpStatus.OK.value())
                        .withBody(testResponseBody)
        );
        //when & then
        assertThatCode(()->imageServerService.remove("b3b2bc5b075f434692f71657afbae2c9"))
                .doesNotThrowAnyException();


    }

    @Test
    @Disabled
    @DisplayName("이미지 삭제 API 호출시, 삭제하고자 하는 이미지 파일이 없을 경우 400 코드와 함께 IllegalArgumentException을 리턴한다.")
    public void t13() throws Exception{
        //given
        String testResponseBody = "{\n" +
                "    \"code\": 400,\n" +
                "    \"message\": \"File 20995dfcf94a49e7b6d34ccce744609c does not exists.\"\n" +
                "}";

        mockServer.when(
                HttpRequest.request()
                        .withMethod("POST")
                        .withPath("/images/delete/b3b2bc5b075f434692f71657afbae2c9")
                        .withHeader("X-Access-Token", "c95a5024651547fa82e1eebc0daa52a2")
        ).respond(
                HttpResponse.response()
                        .withStatusCode(HttpStatus.BAD_REQUEST.value())
                        .withBody(testResponseBody)
        );
        //when & then
        assertThatThrownBy(()->imageServerService.remove("b3b2bc5b075f434692f71657afbae2c9"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("삭제하고자 하는 이미지 파일이 존재하지 않습니다.");

    }

}