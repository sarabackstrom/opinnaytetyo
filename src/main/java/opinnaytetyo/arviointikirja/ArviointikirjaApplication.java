package opinnaytetyo.arviointikirja;


import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import opinnaytetyo.arviointikirja.domain.Admin;
import opinnaytetyo.arviointikirja.domain.EducationalGoal;
import opinnaytetyo.arviointikirja.domain.EducationalGoalRepository;
import opinnaytetyo.arviointikirja.domain.Lesson;
import opinnaytetyo.arviointikirja.domain.LessonGoal;
import opinnaytetyo.arviointikirja.domain.LessonGoalRepository;
import opinnaytetyo.arviointikirja.domain.LessonRepository;
import opinnaytetyo.arviointikirja.domain.Performance;
import opinnaytetyo.arviointikirja.domain.PerformanceRepository;
import opinnaytetyo.arviointikirja.domain.Sport;
import opinnaytetyo.arviointikirja.domain.SportRepository;
import opinnaytetyo.arviointikirja.domain.Student;
import opinnaytetyo.arviointikirja.domain.Teacher;
import opinnaytetyo.arviointikirja.domain.TeachingGroup;
import opinnaytetyo.arviointikirja.domain.TeachingGroupRepository;
import opinnaytetyo.arviointikirja.domain.UserRepository;
import opinnaytetyo.arviointikirja.domain.EducationalGoal.Category;

@SpringBootApplication
public class ArviointikirjaApplication {

    private final UserRepository userRepository;

	private static final Logger log = LoggerFactory.getLogger(ArviointikirjaApplication.class);

    ArviointikirjaApplication(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

	public static void main(String[] args) {
		SpringApplication.run(ArviointikirjaApplication.class, args);
	}

	@Bean
	public CommandLineRunner arviointikirjaDemo(EducationalGoalRepository eGoalRepository, LessonRepository lRepository, LessonGoalRepository lGoalRepository, PerformanceRepository pRepository, SportRepository sRepository, TeachingGroupRepository tGroupRepository, UserRepository uRepository){
		return (args) -> {
			log.info("Save a couple of EducationalGoals, Lessons, LessonGoals, Performances, Sports, Students, Teachers, TeachingGroups");

			Sport sport1 = new Sport("koripallo");
			if(sRepository.count()==0){
				sRepository.save(sport1);
			}

			Teacher teacher1 = new Teacher("teacher1", "$2a$10$zA8JiOPsP5cEcd/5gNF3Fu8fX4LcHtMkooyDHCgbJAzUs/a26W1TG", "Sara", "Bäckström");

			Admin admin1 = new Admin("admin1", "$2a$10$cHiUj.wxb2MFCfuPp4QbwOZwn1X84DIxeMTayAOfj2g2RVfUYSE5O", "Paavo", "Pesusieni");
			if(uRepository.count()== 0) {
				uRepository.save(teacher1);
				uRepository.save(admin1);
			}

			TeachingGroup teachingGroup1 = new TeachingGroup(2025, "A", "7A", teacher1);
			if(tGroupRepository.count() == 0){
				tGroupRepository.save(teachingGroup1);
			}

			Student student1 = new Student("student1", "$2a$10$msk99ISatocqvZjtUV.WNuJd2Z0Gam5AVGYFDH4TEsa.Cb3A3uXFO", "Nalle", "Puh", teachingGroup1);
			if(uRepository.count()< 3) {
				uRepository.save(student1);
			}
			Lesson lesson1 = new Lesson(LocalDate.of(2025, 6, 23), "Pienpelit", teachingGroup1, sport1);
			if(lRepository.count()== 0){
				lRepository.save(lesson1);
			}

			EducationalGoal educationalGoal1 = new EducationalGoal("Fyysinen aktiivisuus ja yrittäminen", Category.effort);
			if(eGoalRepository.count()== 0){
				eGoalRepository.save(educationalGoal1);
			}

			LessonGoal lessonGoal1 = new LessonGoal(lesson1, educationalGoal1);
			if(lGoalRepository.count()==0){
				lGoalRepository.save(lessonGoal1);
			}

			Performance performance1 = new Performance(10, 10, "Harrastaja",false,false,false,false,student1, teacher1, lesson1);
			if(pRepository.count()==0){
				pRepository.save(performance1);
			}
};
	}}