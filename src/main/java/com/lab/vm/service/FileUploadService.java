package com.lab.vm.service;

import com.lab.vm.model.domain.User;
import com.lab.vm.model.domain.Video;
import com.lab.vm.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

/**
 * packageName : com.lab.vm.service
 * fileName : FileUploadService
 * author : yelee
 * date : 2022-01-18
 * description : 파일 업로드 관련 서비스
 * ===========================================================
 * DATE                  AUTHOR                  NOTE
 * -----------------------------------------------------------
 * 2022-01-18              yelee             최초 생성
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class FileUploadService {

    @Value("${file.dir}")
    private String uploadPath;
    private final VideoRepository videoRepository;


    /**
     * methodName : uploadFile
     * author : yelee
     * description : 파일 업로드 기능
     * @param uploadFile uploadFile
     * @param user user
     * @return boolean
     * @throws IOException the io exception
     */
    public boolean uploadFile(MultipartFile uploadFile, User user) throws IOException {


            try {
                String fileName = generateFileName(uploadFile, user.getUsername());
                String fullFilePath = uploadPath + File.separator + fileName;

                log.info("[ video 경로 확인 ] : {}",fullFilePath);
                var video = Video.builder()
                        .name(fileName)
                        .size(uploadFile.getSize())
                        .user(user)
                        .uploadDate(LocalDateTime.now())
                        .build();
                log.info("[ video 이름 확인 ] : {}",video.getName());

                File fileDir = new File(uploadPath); //디렉토리 가져오기
                //디렉토리가 존재하는지 확인 후 없으면  생성
                if(!fileDir.exists()){
                    fileDir.mkdir();
                }

                Path path = Paths.get(fullFilePath).toAbsolutePath();
                uploadFile.transferTo(path.toFile());
                videoRepository.save(video);
            } catch (Exception e) {
                log.error("Error while uploading", e);
                return false;
            }

        return true;

    }

    /**
     * methodName : generateFileName
     * author : yelee
     * description : 파일명 generate
     * @param multipartFile multipartFile
     * @param userName userName
     * @return String
     */
    private String generateFileName(MultipartFile multipartFile, String userName) {
        Calendar cal = Calendar.getInstance();
        Date date = cal.getTime();

        return new StringJoiner("_")
                .add(new SimpleDateFormat("yyyyMMdd").format(date) )
                .add(userName)
                .add(multipartFile.getOriginalFilename())
                .toString();
    }

}