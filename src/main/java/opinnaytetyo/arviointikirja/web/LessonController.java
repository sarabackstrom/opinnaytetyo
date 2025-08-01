package opinnaytetyo.arviointikirja.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import opinnaytetyo.arviointikirja.domain.EducationalGoal;
import opinnaytetyo.arviointikirja.domain.EducationalGoalRepository;
import opinnaytetyo.arviointikirja.domain.Lesson;
import opinnaytetyo.arviointikirja.domain.LessonGoal;
import opinnaytetyo.arviointikirja.domain.LessonGoalRepository;
import opinnaytetyo.arviointikirja.domain.LessonRepository;
import opinnaytetyo.arviointikirja.domain.Performance;
import opinnaytetyo.arviointikirja.domain.PerformanceRepository;
import opinnaytetyo.arviointikirja.domain.SportRepository;
import opinnaytetyo.arviointikirja.domain.Student;
import opinnaytetyo.arviointikirja.domain.Teacher;
import opinnaytetyo.arviointikirja.domain.TeacherRepository;
import opinnaytetyo.arviointikirja.domain.TeachingGroup;
import opinnaytetyo.arviointikirja.domain.TeachingGroupRepository;
import opinnaytetyo.arviointikirja.domain.User;
import opinnaytetyo.arviointikirja.domain.UserRepository;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.validation.Valid;

@Controller
public class LessonController {

    private final EducationalGoalRepository eGoalRepository;
    private final LessonGoalRepository lGoalRepository;
    private final LessonRepository lRepository;
    private final PerformanceRepository pRepository;
    private final SportRepository sRepository;
    private final TeacherRepository tRepository;
    private final TeachingGroupRepository tGroupRepository;
    private final UserRepository uRepository;

    public LessonController(EducationalGoalRepository eGoalRepository, LessonRepository lRepository,
            LessonGoalRepository lGoalRepository,
            PerformanceRepository pRepository, SportRepository sRepository, TeacherRepository tRepository,
            TeachingGroupRepository tGroupRepository, UserRepository uRepository) {
        this.eGoalRepository = eGoalRepository;
        this.lRepository = lRepository;
        this.lGoalRepository = lGoalRepository;
        this.pRepository = pRepository;
        this.sRepository = sRepository;
        this.tRepository = tRepository;
        this.tGroupRepository = tGroupRepository;
        this.uRepository = uRepository;
    }

    @RequestMapping(value = "/login")
    public String login() {
        return "login";
    }

    @GetMapping("/lessonlist")
    public String showLessonList(Model model) {
        // Hae kirjautunut käyttäjä
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User currentUser = uRepository.findByUsername(username);

        List<Lesson> filteredLessons = new ArrayList<>();

        if (currentUser instanceof Teacher) {
            Teacher teacher = (Teacher) currentUser;

            // Hae kaikki tunnit, joiden ryhmän opettaja on kirjautunut opettaja tai
            // oppilaan oman opetusryhmän tunnit
            lRepository.findAll().forEach(lesson -> {
                TeachingGroup group = lesson.getTeachingGroup();
                if (group != null && group.getTeacher().equals(teacher)) {
                    filteredLessons.add(lesson);
                }
            });

        } else if (currentUser instanceof Student) {
            Student student = (Student) currentUser;
            TeachingGroup group = student.getTeachingGroup();

            if (group != null) {
                lRepository.findAll().forEach(lesson -> {
                    if (group.equals(lesson.getTeachingGroup())) {
                        filteredLessons.add(lesson);
                    }
                });
            }
        }

        // Haetaan kaikki kirjautuneen käyttäjän lisäämät suoritukset
        List<Performance> userPerformances = pRepository.findByUser(currentUser);

        // Map: Oppitunnin ID -> käyttäjän lisäämien suoritusten määrä, käytetään
        // siihen, näkyykö käyttäjälle muokkaa vai lisää suoritus lessonlist-näkymässä
        Map<Long, Integer> performanceCountByLesson = new HashMap<>();
        for (Lesson lesson : filteredLessons) {
            performanceCountByLesson.put(lesson.getId(), 0);
        }

        for (Performance p : userPerformances) {
            Long lessonId = p.getLesson().getId();
            if (performanceCountByLesson.containsKey(lessonId)) {
                performanceCountByLesson.put(
                        lessonId,
                        performanceCountByLesson.getOrDefault(lessonId, 0) + 1);
            }
        }

        model.addAttribute("lessons", filteredLessons);
        model.addAttribute("performanceCountByLesson", performanceCountByLesson);
        model.addAttribute("user", currentUser);

        //System.out.println("Käyttäjän rooli: ROLE_" + currentUser.getRole());
        //System.out.println("Authorities: " + AuthorityUtils.createAuthorityList("ROLE_" + currentUser.getRole()));
        return "lessonlist";
    }

    @PreAuthorize("hasRole('TEACHER')")
    @GetMapping("/lessonform")
    public String showLessonForm(Model model) {

        // Hae kirjautunut käyttäjä
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        // Haetaan useria vastaava teacher teacherRepositorysta
        Teacher currentTeacher = tRepository.findByUsername(username);

        // Luodaan lista, jossa on opettajan kirjautuneen opettajan opetusryhmät
        List<TeachingGroup> teachingGroups = tGroupRepository.findByTeacher(currentTeacher);

        // Lisätään lessonformille välitettävät arvot
        model.addAttribute("lesson", new Lesson());
        model.addAttribute("sports", sRepository.findAll());
        model.addAttribute("goals", eGoalRepository.findAll());
        model.addAttribute("selectedGoals", new ArrayList<Long>());
        model.addAttribute("teachingroups", teachingGroups);

        // Kerrotaan, mihin näkymään pyyntö ohjataan
        return "lessonform";
    }

    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    @GetMapping("/editlessonform/{id}")
    public String showEditLessonForm(@PathVariable("id") Long id, Model model) {
        Optional<Lesson> lessonOpt = lRepository.findById(id);
        if (lessonOpt.isPresent()) {
            Lesson lesson = lessonOpt.get();
            model.addAttribute("lesson", lesson);

            List<Long> selectedGoalIds = lesson.getLessonGoals()
                    .stream()
                    .map(lg -> lg.getEducationalGoal().getId())
                    .toList();

            model.addAttribute("selectedGoals", selectedGoalIds);

        } else {
            return "redirect:lessonlist";
        }
        model.addAttribute("sports", sRepository.findAll());
        model.addAttribute("teachingroups", tGroupRepository.findAll());
        model.addAttribute("goals", eGoalRepository.findAll());
        return "editlessonform";
    }

    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    @PostMapping("/savelesson")
    public String saveLesson(
            @Valid @ModelAttribute("lesson") Lesson lesson,
            BindingResult bindingResult,
            @RequestParam(value = "selectedGoals", required = false) List<Long> selectedGoalIds,
            Model model) {

        if (bindingResult.hasErrors()) {
            // Hae kirjautunut käyttäjä uudelleen
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();

            Teacher currentTeacher = tRepository.findByUsername(username);
            List<TeachingGroup> teachingGroups = tGroupRepository.findByTeacher(currentTeacher);

            model.addAttribute("goals", eGoalRepository.findAll());
            model.addAttribute("teachingroups", teachingGroups);
            model.addAttribute("sports", sRepository.findAll());
            model.addAttribute("selectedGoals", selectedGoalIds);
            return "lessonform";
        }

        lRepository.save(lesson);

        if (selectedGoalIds != null) {
            for (Long goalId : selectedGoalIds) {
                EducationalGoal goal = eGoalRepository.findById(goalId).orElseThrow();

                LessonGoal lg = new LessonGoal();
                lg.setLesson(lesson);
                lg.setEducationalGoal(goal);

                lGoalRepository.save(lg);
            }
        }

        return "redirect:/lessonlist";
    }

    /*
     * @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
     * 
     * @PostMapping("/savelesson")
     * public String saveLesson(@ModelAttribute Lesson lesson,
     * 
     * @RequestParam(value = "selectedGoals", required = false) List<Long>
     * selectedGoalIds) {
     * 
     * lRepository.save(lesson);
     * 
     * if (selectedGoalIds != null) {
     * for (Long goalId : selectedGoalIds) {
     * EducationalGoal goal = eGoalRepository.findById(goalId).orElseThrow();
     * 
     * LessonGoal lg = new LessonGoal();
     * lg.setLesson(lesson);
     * lg.setEducationalGoal(goal);
     * 
     * lGoalRepository.save(lg); // Tallennetaan jokainen yhdistelmä
     * }
     * }
     * return "redirect:lessonlist";
     * }
     */

    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    @PostMapping("/updatelesson")
    public String updateLesson(@ModelAttribute Lesson lesson,
            @RequestParam(value = "selectedGoals", required = false) List<Long> selectedGoalIds) {

        lRepository.save(lesson);

        // Poistetaan vanhat tavoitteet ja lisätään uudet
        lGoalRepository.deleteByLesson(lesson);

        if (selectedGoalIds != null) {
            for (Long goalId : selectedGoalIds) {
                EducationalGoal goal = eGoalRepository.findById(goalId).orElseThrow();
                LessonGoal lg = new LessonGoal();
                lg.setLesson(lesson);
                lg.setEducationalGoal(goal);
                lGoalRepository.save(lg);
            }
        }

        return "redirect:/lessonlist";
    }

    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    @GetMapping("/deletelesson/{id}")
    public String deleteLesson(@PathVariable("id") Long id, Model model) {
        lRepository.deleteById(id);
        return "redirect:/lessonlist";
    }

}