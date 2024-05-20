package com.ayd2.library.service;

import com.ayd2.library.exception.LibraryException;
import com.ayd2.library.model.Career;
import com.ayd2.library.repository.CareerRepository;
import com.ayd2.library.util.LibraryConstant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class CareerServiceTest
{
    public static final String SEARCH_WORD = "search";
    public static final String CAREER_NAME = "Profesorado de Enseñanza Media";
    @Mock
    private CareerRepository careerRepository;

    @InjectMocks
    private CareerService careerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindAll(){
        when(careerRepository.findAll()).thenReturn(new ArrayList<>());

        List<Career> careerList = careerService.findAll();

        assertNotNull(careerList);
    }

    @Test
    void testSearchCareerByName(){
        when(careerRepository.findByNameContainingIgnoreCase(SEARCH_WORD)).thenReturn(new ArrayList<>());

        List<Career> careerList = careerService.searchCareerByName(SEARCH_WORD);

        assertNotNull(careerList);
    }

    @Test
    void testSaveChangesCareerSuccessfully() throws LibraryException {
        Career toCreate = new Career();
        toCreate.setName(CAREER_NAME);
        Career created = new Career();
        String cap = LibraryConstant.usingCharacterToUpperCaseMethod(CAREER_NAME);
        created.setName(cap);
        created.setCareerId(1L);
        when(careerRepository.findByName(LibraryConstant.usingCharacterToUpperCaseMethod(CAREER_NAME))).thenReturn(Optional.empty());
        when(careerRepository.save(toCreate)).thenReturn(created);

        Career tested = careerService.saveChangesCareer(toCreate);

        assertEquals(cap, tested.getName());
        assertEquals(1L, tested.getCareerId());
    }

    @Test
    void testSaveChangesCareerNotGiveName() {
        Career toCreate = new Career();
        toCreate.setName(null);

        LibraryException libraryException = assertThrows(LibraryException.class,() -> {
            careerService.saveChangesCareer(toCreate);
        });

        assertEquals("bad_name_career", libraryException.getMessage());
    }

    @Test
    void testSaveChangesCareerGiveEmptyName() {
        Career toCreate = new Career();
        toCreate.setName("");

        LibraryException libraryException = assertThrows(LibraryException.class,() -> {
            careerService.saveChangesCareer(toCreate);
        });

        assertEquals("bad_name_career", libraryException.getMessage());
    }

    @Test
    void testSaveChangesCareerGiveCareerNameExists() throws LibraryException {
        Career toCreate = new Career();
        toCreate.setName(CAREER_NAME);
        when(careerRepository.findByName(LibraryConstant.usingCharacterToUpperCaseMethod(CAREER_NAME))).thenReturn(Optional.of(toCreate));

        LibraryException libraryException = assertThrows(LibraryException.class,() -> {
            careerService.saveChangesCareer(toCreate);
        });

        assertEquals("career_name_exists", libraryException.getMessage());
    }

    @Test
    void testUpdateCareerSuccessfully() throws LibraryException {
        Career toUpdate = new Career();
        toUpdate.setCareerId(1L);
        toUpdate.setName(CAREER_NAME);
        Career created = new Career();
        String cap = LibraryConstant.usingCharacterToUpperCaseMethod(CAREER_NAME);
        created.setName(cap);
        created.setCareerId(1L);
        when(careerRepository.findById(1L)).thenReturn(Optional.of(toUpdate));
        when(careerService.saveChangesCareer(toUpdate)).thenReturn(created);

        Career tested = careerService.updateCareer(1L, toUpdate);

        assertEquals(cap, tested.getName());
        assertEquals(1L, tested.getCareerId());
    }

    @Test
    void testUpdateCareerExistsException() throws LibraryException {
        Career toUpdate = new Career();
        toUpdate.setCareerId(1L);
        toUpdate.setName(CAREER_NAME);
        when(careerRepository.findById(1L)).thenReturn(Optional.empty());

        LibraryException libraryException = assertThrows(LibraryException.class, () -> {
           careerService.updateCareer(1L, toUpdate);
        });

        assertEquals("career_not_exists", libraryException.getMessage());
    }

    @Test
    void testUpdateCareerIdsNotMatchException() throws LibraryException {
        Career toUpdate = new Career();
        toUpdate.setCareerId(2L);
        toUpdate.setName(CAREER_NAME);
        when(careerRepository.findById(1L)).thenReturn(Optional.of(toUpdate));

        LibraryException libraryException = assertThrows(LibraryException.class, () -> {
            careerService.updateCareer(1L, toUpdate);
        });

        assertEquals("career_not_match", libraryException.getMessage());
    }

    @Test
    void testCreateCareerSuccessfully() throws LibraryException {
        Career toUpdate = new Career();
        toUpdate.setCareerId(1L);
        toUpdate.setName(CAREER_NAME);
        Career created = new Career();
        String cap = LibraryConstant.usingCharacterToUpperCaseMethod(CAREER_NAME);
        created.setName(cap);
        created.setCareerId(1L);
        when(careerRepository.findById(1L)).thenReturn(Optional.empty());
        when(careerService.saveChangesCareer(toUpdate)).thenReturn(created);

        Career tested = careerService.saveCareer(toUpdate);

        assertEquals(cap, tested.getName());
        assertEquals(1L, tested.getCareerId());
    }

    @Test
    void testCreateCareerIdExistsException() {
        Career toUpdate = new Career();
        toUpdate.setCareerId(1L);
        toUpdate.setName(CAREER_NAME);
        when(careerRepository.findById(1L)).thenReturn(Optional.of(toUpdate));

        LibraryException libraryException = assertThrows(LibraryException.class, () -> {
            careerService.saveCareer(toUpdate);
        });

        assertEquals("career_id_exists", libraryException.getMessage());
    }
}
