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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileUploadService {

    @Value("${file.dir}")
    private String uploadPath;


    private final VideoRepository videoRepository;


    public boolean uploadFile(MultipartFile uploadFile, User user) throws IOException {


            try {
                String fileName = generateFileName(uploadFile);
                String fullFilePath = uploadPath + File.separator + fileName;

//                File tmp = new File(uploadPath + fileName);
                log.info("[ video 경로 확인 ] : {}",fullFilePath);
                var video = Video.builder()
                        .name(fileName)
                        .size(uploadFile.getSize())
                        .user(user)
                        .uploadDate(LocalDateTime.now())
                        .build();
                log.info("[ video 이름 확인 ] : {}",video.getName());
                //경로에 이동


                File destdir = new File(uploadPath); //디렉토리 가져오기

                if(!destdir.exists()){
                    destdir.mkdir(); //디렉토리가 존재하지 않는다면 생성
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

    private String generateFileName(MultipartFile multipartFile) {
        Calendar cal = Calendar.getInstance();
        Date date = cal.getTime();
        return new SimpleDateFormat("yyyyMMdd").format(date) + "_" + multipartFile.getOriginalFilename();
    }
}