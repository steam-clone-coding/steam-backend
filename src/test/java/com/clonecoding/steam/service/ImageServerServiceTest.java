package com.clonecoding.steam.service;

import com.clonecoding.steam.dto.fileserver.MultipleImageUploadResult;
import com.clonecoding.steam.dto.fileserver.SingleImageUploadResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.*;


class ImageServerServiceTest {



    @Test
    @DisplayName("JSON을 사용해서 DTO로 객체를 변환할 수 있다.")
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
}