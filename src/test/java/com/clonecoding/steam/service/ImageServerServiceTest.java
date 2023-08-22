package com.clonecoding.steam.service;

import com.clonecoding.steam.dto.fileserver.MultipleImageUploadResult;
import com.clonecoding.steam.dto.fileserver.SingleImageUploadResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.*;


class ImageServerServiceTest {



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



}