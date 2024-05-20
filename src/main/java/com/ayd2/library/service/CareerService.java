package com.ayd2.library.service;

import com.ayd2.library.exception.LibraryException;
import com.ayd2.library.model.Career;
import com.ayd2.library.repository.CareerRepository;
import com.ayd2.library.util.LibraryConstant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CareerService {
    private final CareerRepository careerRepository;

    public List<Career> findAll() {
        return careerRepository.findAll();
    }

    public List<Career> searchCareerByName(String search) {
        return careerRepository.findByNameContainingIgnoreCase(search);
    }

    public Career saveCareer(Career toCreate) throws LibraryException {
        Optional<Career> saved = careerRepository.findById(toCreate.getCareerId());
        if (saved.isPresent()) throw new LibraryException("career_id_exists");
        return saveChangesCareer(toCreate);
    }

    public Career saveChangesCareer(Career saveChanges) throws LibraryException {
        if (saveChanges.getName() == null || saveChanges.getName().isEmpty())
        {
            throw new LibraryException("bad_name_career");
        }
        saveChanges.setName(LibraryConstant.usingCharacterToUpperCaseMethod(saveChanges.getName()));
        Optional<Career> careerList = careerRepository.findByName(saveChanges.getName());
        if (careerList.isPresent()) throw new LibraryException("career_name_exists");
        return careerRepository.save(saveChanges);
    }

    public Career updateCareer(Long careerId, Career toUpdate) throws LibraryException {
        Optional<Career> career = careerRepository.findById(careerId);
        if (career.isEmpty()) throw new LibraryException("career_not_exists");
        if (!careerId.equals(toUpdate.getCareerId())) throw new LibraryException("career_not_match");
        return saveChangesCareer(toUpdate);
    }
}
