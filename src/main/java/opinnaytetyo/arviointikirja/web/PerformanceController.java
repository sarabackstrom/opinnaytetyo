package opinnaytetyo.arviointikirja.web;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import opinnaytetyo.arviointikirja.domain.Lesson;
import opinnaytetyo.arviointikirja.domain.LessonRepository;
import opinnaytetyo.arviointikirja.domain.Performance;
import opinnaytetyo.arviointikirja.domain.PerformanceRepository;
import opinnaytetyo.arviointikirja.domain.PerformancesDto;
import opinnaytetyo.arviointikirja.domain.Student;
import opinnaytetyo.arviointikirja.domain.StudentRepository;
import opinnaytetyo.arviointikirja.domain.User;
import opinnaytetyo.arviointikirja.domain.UserRepository;

@Controller
public class PerformanceController {

    private final LessonRepository lRepository;
    private final PerformanceRepository pRepository;
    private final StudentRepository stRepository;
    private final UserRepository uRepository;

    public PerformanceController (LessonRepository lRepository, PerformanceRepository pRepository, StudentRepository stRepository, UserRepository uRepository){
        this.lRepository = lRepository;
        this.pRepository = pRepository;
        this.stRepository = stRepository;
        this.uRepository = uRepository;
    }

    @GetMapping("/teacherperformanceform/{id}")
public String showPerformanceForm(@PathVariable("id") Long id, Model model) {
    Optional<Lesson> lesson = lRepository.findById(id);
    if (lesson.isPresent()) {

        //Käyttäjän tunnistaminen
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String user = auth.getName();
        User currentUser = uRepository.findByUsername(user);
        
        Lesson selectedLesson = lesson.get();

        List<Student> students = stRepository.findByTeachingGroup(selectedLesson.getTeachingGroup());

        PerformancesDto performancesDto = new PerformancesDto();

        for (Student student : students) {
            Optional<Performance> existingPerformance = pRepository.findByStudentAndLesson(student, selectedLesson);
            Performance performance;

            if (existingPerformance.isPresent()) {
                performance = existingPerformance.get();
            } else {
                performance = new Performance();
                performance.setStudent(student);
                performance.setLesson(selectedLesson);
                performance.setUser(currentUser);
            }

            performancesDto.addPerformance(performance);
        }

        model.addAttribute("form", performancesDto);
        model.addAttribute("lesson", selectedLesson);
    } else {
        return "redirect:/lessonlist";
    }

    return "teacherperformanceform";
}

@PostMapping("/saveperformances")
public String savePerformances(@ModelAttribute PerformancesDto form) {
    for (Performance performance : form.getPerformances()) {
        // Tarkistetaan, löytyykö jo olemassa oleva suoritus
        Optional<Performance> existing = pRepository.findByStudentAndLesson(
            performance.getStudent(), performance.getLesson()
        );

        if (existing.isPresent()) {
            Performance existingPerf = existing.get();
            existingPerf.setEffort(performance.getEffort());
            existingPerf.setSkills(performance.getSkills());
            existingPerf.setShortDescription(performance.getShortDescription());
            existingPerf.setAbsence(performance.isAbsence());
            existingPerf.setSportsEquipment(performance.isSportsEquipment());
            existingPerf.setParticipation(performance.isParticipation());
            existingPerf.setInjured(performance.isInjured());

            pRepository.save(existingPerf);
        } else {
            pRepository.save(performance);
        }
    }

    return "redirect:/lessonlist";
}

}