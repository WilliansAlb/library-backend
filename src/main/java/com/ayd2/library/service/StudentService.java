package com.ayd2.library.service;

import com.ayd2.library.exception.LibraryException;
import com.ayd2.library.model.Career;
import com.ayd2.library.model.Student;
import com.ayd2.library.model.UserLibrary;
import com.ayd2.library.repository.StudentRepository;
import com.ayd2.library.util.LibraryConstant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentService {
    private final StudentRepository studentRepository;
    private final CareerService careerService;

    public List<Student> findAllStudents(){
        return studentRepository.findAll();
    }

    public Optional<Student> findByLicenseAndNameAndBirthday(String license, String name, LocalDate birthday){
        return studentRepository.findByLicenseAndNameAndBirthdayAndUserLibraryIsNull(license,name,birthday);
    }

    public boolean hasPendingInvitation(String license) throws LibraryException {
        Optional<Student> student = findByLicense(license);
        if (student.isEmpty()) throw new LibraryException("student_doesnt_exists");
        return student.get().getUserLibrary()==null;
    }

    public Optional<Student> findByLicense(String license) {
        return studentRepository.findById(license);
    }

    public Student saveStudent(Student student){
        return studentRepository.save(student);
    }

    public Student createStudent(Student toCreate) throws LibraryException {
        if (!LibraryConstant.isNumber(toCreate.getLicense())) throw new LibraryException("Not valid form for license");
        if (findByLicense(toCreate.getLicense()).isPresent()) throw new LibraryException("The student exists");
        if (toCreate.getCareer()==null) throw new LibraryException("Missing the career");
        Optional<Career> careerSaved = careerService.findById(toCreate.getCareer().getCareerId());
        if (careerSaved.isEmpty()) throw new LibraryException("The career doesnt exists");
        toCreate.setUserLibrary(null);
        return studentRepository.save(toCreate);
    }

    public List<Student> filterStudent(String search, Long careerId) {
        List<Student> found;
        if (search == null || search.isEmpty()){
            found = studentRepository.findAll();
        } else {
            found = studentRepository.findByLicenseOrNameContainingIgnoreCase(search, search);
        }
        if (careerId > 0 ){
            found = found
                    .stream()
                    .filter(student -> student.getCareer().getCareerId().equals(careerId))
                    .toList();
        }
        return found;
    }

    public Optional<Student> findByUserLibrary(UserLibrary userLibrary){
        return studentRepository.findByUserLibrary(userLibrary);
    }
}
