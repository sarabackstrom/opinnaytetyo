package opinnaytetyo.arviointikirja.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import opinnaytetyo.arviointikirja.ArviointikirjaApplication;
import opinnaytetyo.arviointikirja.domain.*;

@Controller
public class PerformanceController {

    private final LessonController lessonController;

    private final EducationalGoalRepository educationalGoalRepository;


    private final ArviointikirjaApplication arviointikirjaApplication;

    private final TeachingGroupRepository teachingGroupRepository;

    private final LessonRepository lRepository;
    private final PerformanceRepository pRepository;
    private final StudentRepository stRepository;
    private final UserRepository uRepository;

    public PerformanceController(LessonRepository lRepository, PerformanceRepository pRepository,
            StudentRepository stRepository, UserRepository uRepository, TeachingGroupRepository teachingGroupRepository,
            ArviointikirjaApplication arviointikirjaApplication, CommandLineRunner arviointikirjaDemo,
            SecurityFilterChain configure, EducationalGoalRepository educationalGoalRepository,
            LessonController lessonController) {
        this.lRepository = lRepository;
        this.pRepository = pRepository;
        this.stRepository = stRepository;
        this.uRepository = uRepository;
        this.teachingGroupRepository = teachingGroupRepository;
        this.arviointikirjaApplication = arviointikirjaApplication;
        this.educationalGoalRepository = educationalGoalRepository;
        this.lessonController = lessonController;
    }

    @GetMapping("/teacherperformanceform/{id}")
    public String showPerformanceForm(@PathVariable("id") Long id, Model model) {
        Optional<Lesson> lesson = lRepository.findById(id);
        if (lesson.isPresent()) {

            // Käyttäjän tunnistaminen
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String user = auth.getName();
            User currentUser = uRepository.findByUsername(user);

            Lesson selectedLesson = lesson.get();

            List<Student> students = stRepository.findByTeachingGroup(selectedLesson.getTeachingGroup());

            PerformancesDto performancesDto = new PerformancesDto();

            for (Student student : students) {
                Optional<Performance> existingPerformance = pRepository.findByStudentAndLessonAndUser(student,
                        selectedLesson, currentUser);
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

    @GetMapping("/studentaddsperformance/{id}")
    public String ShowStudentAddsPerformanceForm(@PathVariable("id") Long id, Model model) {
        Optional<Lesson> lesson = lRepository.findById(id);
        if (lesson.isPresent()) {

            // Käyttäjän tunnistaminen
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String user = auth.getName();
            User currentUser = uRepository.findByUsername(user);

            Lesson selectedLesson = lesson.get();
            // Haetaan student-olio käyttäjän perusteella
            Student student = stRepository.findByUsername(user);

            Optional<Performance> existingPerformance = pRepository.findByStudentAndLessonAndUser(student,
                    selectedLesson, currentUser);
            Performance performance;

            if (existingPerformance.isPresent()) {
                performance = existingPerformance.get();
            } else {
                performance = new Performance();
                performance.setStudent(student);
                performance.setUser(currentUser);
                performance.setLesson(selectedLesson);
            }

            model.addAttribute("lesson", selectedLesson);
            model.addAttribute("performance", performance);
        } else {
            return "redirect:/lessonlist";
        }

        return "studentaddsperformance";
    }

    @PostMapping("/saveperformances")
    public String savePerformances(@ModelAttribute PerformancesDto form) {
        // Selvitetään kirjautunut käyttäjä
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User currentUser = uRepository.findByUsername(username);

        for (Performance performance : form.getPerformances()) {
            // Asetetaan nykyinen käyttäjä suoritus-olioon varmistuksena
            performance.setUser(currentUser);

            // Tarkistetaan, onko jo olemassa oleva suoritus kyseiselle oppilaalle,
            // oppitunnille ja lisääjälle
            Optional<Performance> existing = pRepository.findByStudentAndLessonAndUser(
                    performance.getStudent(), performance.getLesson(), currentUser);

            if (existing.isPresent()) {
                // Päivitetään olemassa oleva suoritus
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
                // Uusi suoritus
                pRepository.save(performance);
            }
        }

        return "redirect:/lessonlist";
    }

    @PostMapping("/saveStudentPerformance")
    public String savePerformance( @ModelAttribute Performance performance) {

        // Selvitetään kirjautunut käyttäjä
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User currentUser = uRepository.findByUsername(username);

        performance.setUser(currentUser);

         Optional<Performance> existing = pRepository.findByStudentAndLessonAndUser(
                    performance.getStudent(), performance.getLesson(), currentUser);

            if (existing.isPresent()) {
                // Päivitetään olemassa oleva suoritus
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
                // Uusi suoritus
                pRepository.save(performance);
            }
            return "redirect:/lessonlist";
        }

    @PostMapping("/studentsaveperformance")
    public String saveStudentPerformance(@ModelAttribute Performance performance) {
        pRepository.save(performance);
        return "redirect:/lessonlist";
    }

    @GetMapping("/showperformances/{id}")
    public String showTeacherStudentsPerformances(@PathVariable("id") Long id, Model model) {

        // Käyttäjän tunnistaminen
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String user = auth.getName();
        User currentUser = uRepository.findByUsername(user);

        Optional<Student> studentOpt = stRepository.findById(id);
        if (studentOpt.isPresent()) {
            Student student = studentOpt.get();
            List<Performance> allPerformances = pRepository.findByStudent(student);
            // Rajataan hakua kirjautuneen opettajan merkintöihin
            List<Performance> teacherAddedPerformances = allPerformances.stream()
                    .filter(p -> p.getUser().equals(currentUser))
                    .collect(Collectors.toList());
            // Rajataan hakua näytettävän oppilaan omiin merkintöihin
            List<Performance> studentAddedPerformances = allPerformances.stream()
                    .filter(p -> p.getUser().equals(student))
                    .collect(Collectors.toList());
            // opettajan arvioimia oppitunteja yhteensä
            long lessonsT = teacherAddedPerformances.stream()
                    .count();
            // oppilaan arvioimia oppitunteja yhteensä
            long lessonsS = studentAddedPerformances.stream()
                    .count();
            // osaaminen-keskiarvo open arviot
            double averageSkillsT = teacherAddedPerformances.stream()
                    .map(p -> Optional.ofNullable(p.getSkills()).orElse(0))
                    .mapToDouble(Integer::doubleValue)
                    .filter(skill -> skill > 0)
                    .average()
                    .orElse(0.0);
            // osaaminen-keskiarvo oppilaan arviot
            double averageSkillsS = studentAddedPerformances.stream()
                    .map(p -> Optional.ofNullable(p.getSkills()).orElse(0))
                    .mapToDouble(Integer::doubleValue)
                    .filter(skill -> skill > 0)
                    .average()
                    .orElse(0.0);
            // työskentely-keskiarvo open arviot
            double averageEffortT = teacherAddedPerformances.stream()
                    .map(p -> Optional.ofNullable(p.getEffort()).orElse(0))
                    .mapToDouble(Integer::doubleValue)
                    .filter(effort -> effort > 0)
                    .average()
                    .orElse(0.0);
            // työskentely-keskiarvo oppilaan arviot
            double averageEffortS = studentAddedPerformances.stream()
                    .map(p -> Optional.ofNullable(p.getEffort()).orElse(0))
                    .mapToDouble(Integer::doubleValue)
                    .filter(effort -> effort > 0)
                    .average()
                    .orElse(0.0);
            // työskentely ja taidot yhdistetty keskiarvo painoarvolla 50% työskentely ja
            // 50% taidot ope
            double averageEffortAndSkillsT = (averageEffortT + averageSkillsT) / 2;
            // työskentely ja taidot yhdistetty keskiarvo painoarvolla 50% työskentely ja
            // 50% taidot oppilas
            double averageEffortAndSkillsS = (averageEffortS + averageSkillsS) / 2;
            // poissaolot yhteensä
            long absenceCount = teacherAddedPerformances.stream()
                    .filter(Performance::isAbsence)
                    .count();
            // ei osallistu yhteensä
            long noParticipationCount = teacherAddedPerformances.stream()
                    .filter(Performance::isParticipation)
                    .count();
            // loukkaantunut yhteensä
            long injuredCount = teacherAddedPerformances.stream()
                    .filter(Performance::isInjured)
                    .count();
            // ei varusteita yhteensä
            long noEquipmentCount = teacherAddedPerformances.stream()
                    .filter(Performance::isSportsEquipment)
                    .count();

            model.addAttribute("averageSkillsT", averageSkillsT);
            model.addAttribute("averageEffortT", averageEffortT);
            model.addAttribute("averageSkillsS", averageSkillsS);
            model.addAttribute("averageEffortS", averageEffortS);
            model.addAttribute("averageEffortAndSkillsT", averageEffortAndSkillsT);
            model.addAttribute("averageEffortAndSkillsS", averageEffortAndSkillsS);
            model.addAttribute("absenceCount", absenceCount);
            model.addAttribute("noParticipationCount", noParticipationCount);
            model.addAttribute("injuredCount", injuredCount);
            model.addAttribute("noEquipmentCount", noEquipmentCount);
            model.addAttribute("lessonsT", lessonsT);
            model.addAttribute("lessonsS", lessonsS);

            // opetussuunnitelman tavoitteet -tekoäly
            List<Lesson> studentLessons = teacherAddedPerformances.stream()
                    .map(Performance::getLesson)
                    .distinct()
                    .collect(Collectors.toList());

            List<LessonGoal> allLessonGoals = new ArrayList<>();
            for (Lesson lesson : studentLessons) {
                allLessonGoals.addAll(lesson.getLessonGoals());
            }

            Map<EducationalGoal, List<Integer>> goalToScores = new HashMap<>();

            for (LessonGoal lg : allLessonGoals) {
                EducationalGoal eg = lg.getEducationalGoal();
                String category = eg.getCategory();

                for (Performance p : teacherAddedPerformances) {
                    if (p.getLesson().equals(lg.getLesson())) {
                        Integer value = null;
                        if ("effort".equalsIgnoreCase(category)) {
                            value = p.getEffort();
                        } else if ("skills".equalsIgnoreCase(category)) {
                            value = p.getSkills();
                        }
                        if (value != null && value > 0) {
                            goalToScores.computeIfAbsent(eg, k -> new ArrayList<>()).add(value);
                        }
                    }
                }
            }

            Map<EducationalGoal, Double> goalAverages = goalToScores.entrySet().stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            e -> e.getValue().stream().mapToInt(Integer::intValue).average().orElse(0.0)));

            // model.addAttribute("goalAverages", goalAverages);

            // Oppilaan painotetut keskiarvot ed-goal mukaan, tekoälyapuna
            List<Lesson> studentLessonsSelf = studentAddedPerformances.stream()
                    .map(Performance::getLesson)
                    .distinct()
                    .collect(Collectors.toList());

            List<LessonGoal> allLessonGoalsSelf = new ArrayList<>();
            for (Lesson lesson : studentLessonsSelf) {
                allLessonGoalsSelf.addAll(lesson.getLessonGoals());
            }

            Map<EducationalGoal, List<Integer>> studentGoalToScores = new HashMap<>();

            for (LessonGoal lg : allLessonGoalsSelf) {
                EducationalGoal eg = lg.getEducationalGoal();
                String category = eg.getCategory();

                for (Performance p : studentAddedPerformances) {
                    if (p.getLesson().equals(lg.getLesson())) {
                        Integer value = null;
                        if ("effort".equalsIgnoreCase(category)) {
                            value = p.getEffort();
                        } else if ("skills".equalsIgnoreCase(category)) {
                            value = p.getSkills();
                        }
                        if (value != null && value > 0) {
                            studentGoalToScores.computeIfAbsent(eg, k -> new ArrayList<>()).add(value);
                        }
                    }
                }
            }

            Map<EducationalGoal, Double> studentGoalAverages = studentGoalToScores.entrySet().stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            e -> e.getValue().stream().mapToInt(Integer::intValue).average().orElse(0.0)));

            // model.addAttribute("studentGoalAverages", studentGoalAverages);

            // Yhdistä molemmat tavoitteet yhdeksi setiksi
            Set<EducationalGoal> allGoals = new HashSet<>();
            allGoals.addAll(goalAverages.keySet());
            allGoals.addAll(studentGoalAverages.keySet());

            // Rakenna lista tietorakenteesta jossa on molemmat arvot rinnakkain
            List<Map<String, Object>> combinedGoalAverages = new ArrayList<>();

            for (EducationalGoal goal : allGoals) {
                Map<String, Object> row = new HashMap<>();
                row.put("goalName", goal.getName());
                row.put("category", goal.getCategory());
                row.put("teacherAverage", goalAverages.getOrDefault(goal, 0.0));
                row.put("studentAverage", studentGoalAverages.getOrDefault(goal, 0.0));
                combinedGoalAverages.add(row);
            }

            model.addAttribute("combinedGoalAverages", combinedGoalAverages);

            return "studentperformances";

        } else {
            return "redirect:/teachinggroups";
        }
    }

}