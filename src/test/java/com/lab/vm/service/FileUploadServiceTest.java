package com.lab.vm.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * packageName : com.lab.vm.service
 * fileName : FileUploadServiceTest
 * author : isbn8
 * date : 2022-01-20
 * description :
 * ===========================================================
 * DATE                  AUTHOR                  NOTE
 * -----------------------------------------------------------
 * 2022-01-20              isbn8             최초 생성
 */

@SpringBootTest
class FileUploadServiceTest {

    @Autowired
    FileUploadService fileUploadService;

    @Test
    @DisplayName("특정 경로에서 파일 잘 가져오는지 확인")
    void uploadFile() throws IOException {
        //given
        String fileName = "DrAiki";
        String contentType = "mp4";
        String filePath = "src/test/resources/file/DrAiki.mp4";
        MockMultipartFile mockMultipartFile = getMockMultipartFile(fileName, contentType, filePath);

        //when
        String getFileName = mockMultipartFile.getOriginalFilename().toLowerCase();

        //then
        assertEquals(getFileName, (fileName.toLowerCase() + "." + contentType));
    }


    @Test
    @DisplayName("파일명 포맷팅 정상 확인")
    void generateFileName() throws IOException {
        //given
        String fileName = "DrAiki";
        String contentType = "mp4";
        String filePath = "src/test/resources/file/DrAiki.mp4";
        MockMultipartFile mockMultipartFile = getMockMultipartFile(fileName, contentType, filePath);
        String userName = "yelee";
        Calendar cal = Calendar.getInstance();
        Date date = cal.getTime();
        String delimiter = "_";
       String resultName = ReflectionTestUtils.invokeMethod(fileUploadService, "generateFileName", mockMultipartFile, userName);


       assertTrue(Objects.requireNonNull(resultName).contains(new SimpleDateFormat("yyyyMMdd").format(date)));
       assertEquals(3, resultName.split(delimiter).length);
    }

    private MockMultipartFile getMockMultipartFile(String fileName, String contentType, String path) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(new File(path));
        return new MockMultipartFile(fileName, fileName + "." + contentType, contentType, fileInputStream);
    }

}