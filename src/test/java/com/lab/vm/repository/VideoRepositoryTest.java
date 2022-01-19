package com.lab.vm.repository;

import com.lab.vm.model.domain.Authority;
import com.lab.vm.model.domain.Video;
import com.lab.vm.model.dto.RegisterDto;
import com.lab.vm.service.UserService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Transactional
class VideoRepositoryTest {

    @Autowired
    VideoRepository videoRepository;

    @Autowired
    UserService userService;

    @Autowired
    AuthorityRepository authorityRepository;

    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("비디오 정보 저장 확인")
    void saveVideo() {
        // given
        var registerDto = userService.findUserInfoByName("user");
        var user = userRepository.findById(registerDto.getId());
        var saveVideo = Video.builder()
                .id(1L)
                .name("video.mp4")
                .size(1234L)
                .user(user.get())
                .uploadDate(LocalDateTime.now())
                .build();
        //when
        saveVideo = videoRepository.save(saveVideo);
        var findVideo = videoRepository.findById(saveVideo.getId());

        //then
        assertTrue(findVideo.isPresent());
        assertEquals(saveVideo.getId(), findVideo.get().getId());
        assertEquals(saveVideo.getName(), findVideo.get().getName());

    }


    @Test
    @DisplayName("비디오_모든_목록을_조회한다")
    void listAllVideos() throws Exception {
        // given 비디오 정보 2개 저장
        setUpVideo();
        //when
        List<Video> result = videoRepository.findAll();
        //then

        assertEquals(2, result.size());
    }


    @BeforeEach
    @DisplayName("유저 생성")
    void makeUsers(){


        Authority authority = new Authority();
        authority.setName("ROLE_ADMIN");

        Authority authority2 = new Authority();
        authority2.setName("ROLE_USER");

        authorityRepository.save(authority);
        authorityRepository.save(authority2);

        var admin = RegisterDto.builder()
//            .activated(true)
                .username("admin")
                .password("admin")
                .passwordConfirm("admin")
                .phone("01011112222")
                .email("admin@admin.com")
                .build();

        userService.registerUser(admin);

        var user  = RegisterDto.builder()
//            .activated(true)
                .username("user")
                .password("user")
                .passwordConfirm("user")
                .phone("01011113333")
                .email("user@user.com")
                .build();

        userService.registerUser(user);


    }


    /**
     * 테스트 용도 비디오 정보 저장
     */
    void setUpVideo() {

        var registerDto = userService.findUserInfoByName("user");
        var user1 = userRepository.findById(registerDto.getId());


        var saveVideo1 = Video.builder()
                .id(1L)
                .name("video.mp4")
                .size(1234L)
                .user(user1.get())
                .uploadDate(LocalDateTime.now())
                .build();

        videoRepository.save(saveVideo1);
    }



}