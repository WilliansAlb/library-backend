package com.ayd2.library.controller;

import com.ayd2.library.exception.LibraryException;
import com.ayd2.library.model.Book;
import com.ayd2.library.model.Career;
import com.ayd2.library.service.CareerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("careers")
@RequiredArgsConstructor
public class CareerController {
    private final CareerService careerService;

    @GetMapping
    public ResponseEntity<List<Career>> getAllCareers(){
        List<Career> careerList = careerService.findAll();
        return new ResponseEntity<>(careerList, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Career> createCareer(@RequestBody Career toCreate) throws LibraryException {
        Career career = careerService.saveCareer(toCreate);
        return new ResponseEntity<>(career, HttpStatus.CREATED);
    }

    @PutMapping("/update/{careerId}")
    public ResponseEntity<Career> updateCareer(@PathVariable("careerId") Long careerId, @RequestBody Career toUpdate) throws LibraryException {
        Career career = careerService.updateCareer(careerId, toUpdate);
        return new ResponseEntity<>(career, HttpStatus.OK);
    }

    @GetMapping("/search/{search}")
    public ResponseEntity<List<Career>> searchCareer(@PathVariable("search") String search){
        List<Career> careerList = careerService.searchCareerByName(search);
        return new ResponseEntity<>(careerList, HttpStatus.OK);
    }
}
