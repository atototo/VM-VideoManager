package com.lab.vm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.Optional;

/**
 * packageName : com.lab.vm.controller
 * fileName : VideoRestController
 * author : isbn8
 * date : 2022-01-18
 * description :
 * ===========================================================
 * DATE                  AUTHOR                  NOTE
 * -----------------------------------------------------------
 * 2022-01-18              isbn8             최초 생성
 */

@RequiredArgsConstructor
@Controller
@Slf4j
public class VideoRestController {

    @Value("${file.dir}")
    private String fileDir;

    @GetMapping(value = "/video/{name}")
    public ResponseEntity<ResourceRegion> getVideo(@RequestHeader HttpHeaders headers, @PathVariable String name) throws IOException {
        log.info("VideoController.getVideo");
        UrlResource video = new UrlResource(fileDir+ name );
        ResourceRegion resourceRegion;
        final long chunkSize = 1000000L;
        long contentLength = video.contentLength();
        Optional<HttpRange> optional = headers.getRange().stream().findFirst();
        HttpRange httpRange;
        if (optional.isPresent()) {
            httpRange = optional.get();
            long start = httpRange.getRangeStart(contentLength);
            long end = httpRange.getRangeEnd(contentLength);
            long rangeLength = Long.min(chunkSize, end - start + 1);
            resourceRegion = new ResourceRegion(video, start, rangeLength);
        } else {
            long rangeLength = Long.min(chunkSize, contentLength);
            resourceRegion = new ResourceRegion(video, 0, rangeLength);
        }
        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).contentType(MediaTypeFactory.getMediaType(video).orElse(MediaType.APPLICATION_OCTET_STREAM)).body(resourceRegion);
    }

    @GetMapping("/video-stream/{fileName}")
    public StreamingResponseBody stream(HttpServletRequest req,@PathVariable String fileName) throws IOException {
//    public StreamingResponseBody stream(HttpServletRequest req, @RequestParam("fileName") String fileName) throws IOException {
        File file = new File(fileDir + fileName);
        final InputStream is = new FileInputStream(file);
        return os -> {
            readAndWrite(is, os);
        };
    }

    private void readAndWrite(final InputStream is, OutputStream os) throws IOException {
        byte[] data = new byte[2048];
        int read = 0;
        while ((read = is.read(data)) > 0) {
            os.write(data, 0, read);
        }
        os.flush();
    }
}
