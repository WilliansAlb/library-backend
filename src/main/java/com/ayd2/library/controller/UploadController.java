package com.ayd2.library.controller;

import com.ayd2.library.dto.CreatedResponse;
import com.ayd2.library.exception.LibraryException;
import com.ayd2.library.service.UploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

@RestController
@RequestMapping("upload")
@RequiredArgsConstructor
public class UploadController {

    private final UploadService uploadService;

    @PostMapping(path = "/file", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<CreatedResponse> saveEmployee(@RequestPart MultipartFile document,
                                                        @RequestParam("todayDate") LocalDate todayDate) throws LibraryException {
        try {
            BufferedReader fileContent = new BufferedReader(new InputStreamReader(document.getInputStream(), StandardCharsets.UTF_8));
            return new ResponseEntity<>(uploadService.readFile(fileContent, todayDate), HttpStatus.OK);
        } catch (Exception e) {
            throw new LibraryException("Error at read the file");
        }
    }
}
