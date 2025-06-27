package opinnaytetyo.arviointikirja.web;

import java.util.List;
import java.util.Optional;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import opinnaytetyo.arviointikirja.domain.LessonRepository;
import opinnaytetyo.arviointikirja.domain.PerformanceRepository;
import opinnaytetyo.arviointikirja.domain.Student;
import opinnaytetyo.arviointikirja.domain.StudentRepository;
import opinnaytetyo.arviointikirja.domain.TeachingGroup;
import opinnaytetyo.arviointikirja.domain.TeachingGroupRepository;
import opinnaytetyo.arviointikirja.domain.User;
import opinnaytetyo.arviointikirja.domain.UserRepository;

@Controller
public class UserController {

    private final StudentRepository stRepository;
    private final TeachingGroupRepository tGroupRepository;
    private final UserRepository uRepository;

    public UserController(StudentRepository stRepository, TeachingGroupRepository tGroupRepository, UserRepository uRepository) {
        this.stRepository = stRepository;
        this.tGroupRepository = tGroupRepository;
        this.uRepository = uRepository;
    }

    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    @GetMapping("/teachinggroups")
    public String teachingGroupsList(Model model) {

        // Käyttäjän tunnistaminen
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String user = auth.getName();
        User currentUser = uRepository.findByUsername(user);

        List<TeachingGroup> teachingGroups = tGroupRepository.findByTeacherId(currentUser.getId());

        model.addAttribute("teachingroups", teachingGroups);

        return "teachinggroups";
    }

    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    @GetMapping("/studentlist/{id}")
    public String showStudentsByTeachingGroup (@PathVariable("id") Long id, Model model){
    Optional<TeachingGroup> tGrouOptional = tGroupRepository.findById(id);
    if(tGrouOptional.isPresent()){
        TeachingGroup teachinggroup = tGrouOptional.get();
        List<Student>students = stRepository.findByTeachingGroup(teachinggroup);
        model.addAttribute("students", students);
        return "studentlist";
    } else {
        return "redirect:teachinggroups";
    }
}}