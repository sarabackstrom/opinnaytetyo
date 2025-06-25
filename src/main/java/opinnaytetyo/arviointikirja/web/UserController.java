package opinnaytetyo.arviointikirja.web;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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

    private final LessonRepository lRepository;
    private final PerformanceRepository pRepository;
    private final StudentRepository stRepository;
    private final TeachingGroupRepository tGroupRepository;
    private final UserRepository uRepository;

    public UserController(LessonRepository lRepository, PerformanceRepository pRepository,
            StudentRepository stRepository, TeachingGroupRepository tGroupRepository, UserRepository uRepository) {
        this.lRepository = lRepository;
        this.pRepository = pRepository;
        this.stRepository = stRepository;
        this.tGroupRepository = tGroupRepository;
        this.uRepository = uRepository;
    }

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

}