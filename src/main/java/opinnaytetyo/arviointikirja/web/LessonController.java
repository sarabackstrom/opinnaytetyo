package opinnaytetyo.arviointikirja.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
import opinnaytetyo.arviointikirja.domain.TeachingGroupRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LessonController {

    private final EducationalGoalRepository eGoalRepository;
    private final LessonGoalRepository lGoalRepository;
    private final LessonRepository lRepository;
    private final PerformanceRepository pRepository;
    private final SportRepository sRepository;
    private final TeachingGroupRepository tGroupRepository;

    public LessonController(EducationalGoalRepository eGoalRepository, LessonRepository lRepository,
            LessonGoalRepository lGoalRepository,
            PerformanceRepository pRepository, SportRepository sRepository,
            TeachingGroupRepository tGroupRepository) {
        this.eGoalRepository = eGoalRepository;
        this.lRepository = lRepository;
        this.lGoalRepository = lGoalRepository;
        this.pRepository = pRepository;
        this.sRepository = sRepository;
        this.tGroupRepository = tGroupRepository;
    }

    @RequestMapping(value = "/login")
    public String login() {
        return "login";
    }

    @GetMapping("/lessonlist")
    public String lessonList(Model model) {

        List<Lesson> lessons = new ArrayList<>();
        lRepository.findAll().forEach(lessons::add);

        List<Performance> performances = new ArrayList<>();
        pRepository.findAll().forEach(performances::add);

        // Luodaan Map: Lesson ID -> suoritusten määrä
        Map<Long, Integer> performanceCountByLesson = new HashMap<>();
        for (Lesson lesson : lessons) {
            performanceCountByLesson.put(lesson.getId(), 0);
        }

        for (Performance p : performances) {
            Long lessonId = p.getLesson().getId();
            performanceCountByLesson.put(lessonId, performanceCountByLesson.getOrDefault(lessonId, 0) + 1);
        }

        model.addAttribute("lessons", lessons);
        model.addAttribute("performanceCountByLesson", performanceCountByLesson);

        return "lessonlist";
    }

    @GetMapping("/lessonform")
    public String showLessonForm(Model model) {
        model.addAttribute("lesson", new Lesson());
        model.addAttribute("sports", sRepository.findAll());
        model.addAttribute("teachingroups", tGroupRepository.findAll());
        model.addAttribute("goals", eGoalRepository.findAll());
        model.addAttribute("selectedGoals", new ArrayList<Long>());
        return "lessonform";
    }

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

    @PostMapping("/savelesson")
    public String saveLesson(@ModelAttribute Lesson lesson,
            @RequestParam(value = "selectedGoals", required = false) List<Long> selectedGoalIds) {

        lRepository.save(lesson);

        if (selectedGoalIds != null) {
            for (Long goalId : selectedGoalIds) {
                EducationalGoal goal = eGoalRepository.findById(goalId).orElseThrow();

                LessonGoal lg = new LessonGoal();
                lg.setLesson(lesson);
                lg.setEducationalGoal(goal);

                lGoalRepository.save(lg); // Tallennetaan jokainen yhdistelmä
            }
        }
        return "redirect:lessonlist";
    }

    @PostMapping("/updatelesson")
    public String updateLesson(@ModelAttribute Lesson lesson,
            @RequestParam(value = "selectedGoals", required = false) List<Long> selectedGoalIds) {

        lRepository.save(lesson);

        // Poistetaan vanhat tavoitteet ja lisää uudet
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

    @GetMapping("/deletelesson/{id}")
    public String deleteLesson(@PathVariable("id") Long id, Model model) {
        lRepository.deleteById(id);
        return "redirect:/lessonlist";
    }

}