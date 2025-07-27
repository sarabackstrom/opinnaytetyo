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
import org.springframework.security.access.prepost.PreAuthorize;
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

    /*
     * private final LessonController lessonController;
     * 
     * private final EducationalGoalRepository educationalGoalRepository;
     * 
     * private final ArviointikirjaApplication arviointikirjaApplication;
     * 
     * private final TeachingGroupRepository teachingGroupRepository;
     */

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
        /*
         * this.teachingGroupRepository = teachingGroupRepository;
         * this.arviointikirjaApplication = arviointikirjaApplication;
         * this.educationalGoalRepository = educationalGoalRepository;
         * this.lessonController = lessonController;
         */
    }

    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    @GetMapping("/teacherperformanceform/{id}")
    public String showPerformanceForm(@PathVariable("id") Long id, Model model) {
        // Etsitään valittu oppitunti ja siihen liittyvät oppilaat
        Optional<Lesson> lesson = lRepository.findById(id);
        if (lesson.isPresent()) {

            // Käyttäjän tunnistaminen
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String user = auth.getName();
            User currentUser = uRepository.findByUsername(user);

            Lesson selectedLesson = lesson.get();

            // Etsitään sen luokan oppilaat, joille oppitunti on pidetty
            List<Student> students = stRepository.findByTeachingGroup(selectedLesson.getTeachingGroup());

            // Luodaan performancesDto-olio, johon voi tallentaa listan suorituksia.
            PerformancesDto performancesDto = new PerformancesDto();

            // Jokaiseen suoritukseen linkitetään luokan oppilas, oppitunti sekä kirjautunut
            // käyttäjät (=opettaja) suorituksen lisääjäksi
            for (Student student : students) {
                // Tarkistetaanko löytyykö oppitunnille jo opettajan lisäämät suoritusmerkinnät
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

    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
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

            // Tarkistetaan löytyykö oppilaan lisäämä suoritus
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

    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
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

    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    @PostMapping("/saveStudentPerformance")
    public String savePerformance(@ModelAttribute Performance performance) {

        // Selvitetään kirjautunut käyttäjä
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User currentUser = uRepository.findByUsername(username);

        performance.setUser(currentUser);

        // Tarkisteaan löytyykö jo oppilaan lisäämä suoritus, jota voidaan päivittää
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

    /*
     * @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
     * 
     * @PostMapping("/studentsaveperformance")
     * public String saveStudentPerformance(@ModelAttribute Performance performance)
     * {
     * pRepository.save(performance);
     * return "redirect:/lessonlist";
     * }
     */

    // Kirjautuneen oppilaan tunnistaminen ja ohjaaminen omalle suoritussivulle
    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/myperformances")
    public String redirectToOwnPerformances() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = uRepository.findByUsername(username);

        return "redirect:/showperformances/" + user.getId();
    }

    @GetMapping("/showperformances/{id}")
    public String showStudentsPerformances(@PathVariable("id") Long id, Model model) {

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
            // 50% taidot ope, mikäli toinen arvoista nolla, sitä ei huomioida keskiarvoossa
            double averageEffortAndSkillsT;
            if (averageEffortT > 0 && averageSkillsT > 0) {
                averageEffortAndSkillsT = (averageEffortT * 0.5) + (averageSkillsT * 0.5);
            } else if (averageEffortT > 0) {
                averageEffortAndSkillsT = averageEffortT;
            } else if (averageSkillsT > 0) {
                averageEffortAndSkillsT = averageSkillsT;
            } else {
                averageEffortAndSkillsT = 0; // molemmat nollia
            }
            // työskentely ja taidot yhdistetty keskiarvo painoarvolla 50% työskentely ja
            // 50% taidot oppilas
            double averageEffortAndSkillsS;
            if (averageEffortS > 0 && averageSkillsS > 0) {
                averageEffortAndSkillsS = (averageEffortS * 0.5) + (averageSkillsS * 0.5);
            } else if (averageEffortS > 0) {
                averageEffortAndSkillsS = averageEffortS;
            } else if (averageSkillsS > 0) {
                averageEffortAndSkillsS = averageSkillsS;
            } else {
                averageEffortAndSkillsS = 0; // molemmat nollia
            }
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
            model.addAttribute("student", student);
            model.addAttribute("allPerformances", allPerformances);
            model.addAttribute("studentAddedPerformances", studentAddedPerformances);

            // Opetussuunnitelman tavoitteet, Tekoälyä käytetty apuna loogikan
            // rakentamisessa

            // Haetaan kaikki oppitunnit, joihin opettaja on lisännyt suoritusmerkintöjä
            List<Lesson> studentLessons = teacherAddedPerformances.stream()
                    .map(Performance::getLesson)
                    .distinct()
                    .collect(Collectors.toList());

            // Haetaan kaikki oppituntien tavoitteet, jotka on liitetty oppitunteihin
            List<LessonGoal> allLessonGoals = new ArrayList<>();
            for (Lesson lesson : studentLessons) {
                allLessonGoals.addAll(lesson.getLessonGoals());
            }

            // Luodaan "Map", joka yhdistää jokaisen opetussuunnitelman tavoitteen siihen
            // liittyviin suorituksiin
            Map<EducationalGoal, List<Integer>> goalToScores = new HashMap<>();

            // Käydään kaikki oppituntitavoitteet läpi
            for (LessonGoal lg : allLessonGoals) {
                EducationalGoal eg = lg.getEducationalGoal();
                String category = eg.getCategory();

                // Katsotaan, onko opettajan suoritusmerkintä tehty kyseiseen oppituntiin.
                for (Performance p : teacherAddedPerformances) {
                    if (p.getLesson().equals(lg.getLesson())) {
                        // Selvitetään mikä arvo suoritusmerkinnästä haetaan:
                        // Jos tavoitteen kategoria on effort, käytetään effort-arvoa.
                        // Jos skills, käytetään skills-arvoa.
                        Integer value = null;
                        if ("effort".equalsIgnoreCase(category)) {
                            value = p.getEffort();
                        } else if ("skills".equalsIgnoreCase(category)) {
                            value = p.getSkills();
                        }
                        // Lisätään suorituksen arvo oikeaan tavoitteeseen liittyvään listaan.
                        // Jos tavoitteelle ei ole vielä listaa, luodaan uusi lista ensin.
                        if (value != null && value > 0) {
                            goalToScores.computeIfAbsent(eg, k -> new ArrayList<>()).add(value);
                        }
                    }
                }
            }

            // Jokaiselle tavoitteelle lasketaan suoritusnumeroiden keskiarvo.
            Map<EducationalGoal, Double> goalAverages = goalToScores.entrySet().stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            e -> e.getValue().stream().mapToInt(Integer::intValue).average().orElse(0.0)));

            // model.addAttribute("goalAverages", goalAverages);

            // Oppilaan painotetut keskiarvot ed-goal mukaan, tehty opettajan mallia
            // mukaillen
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

            // Jokaisesta tavoitteesta tehdään oma rivi, jossa on tavoitteen nimi,
            // kategoria, opettajan keskiarvo ja oppilaan keskiarvo
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