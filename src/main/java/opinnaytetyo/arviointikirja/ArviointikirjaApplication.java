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

@SpringBootApplication
public class ArviointikirjaApplication {

	private static final Logger log = LoggerFactory.getLogger(ArviointikirjaApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(ArviointikirjaApplication.class, args);
	}

	@Bean
	public CommandLineRunner arviointikirjaDemo(EducationalGoalRepository eGoalRepository, LessonRepository lRepository, LessonGoalRepository lGoalRepository, PerformanceRepository pRepository, SportRepository sRepository, TeachingGroupRepository tGroupRepository, UserRepository uRepository){
		return (args) -> {
			log.info("Save a couple of EducationalGoals, Lessons, LessonGoals, Performances, Sports, Students, Teachers, TeachingGroups");

			Sport sport1 = new Sport("koripallo");
			Sport sport2 = new Sport("pesäpallo");
			Sport sport3 = new Sport("lentopallo");
			Sport sport4 = new Sport("jalkapallo");
			if(sRepository.count()==0){
				sRepository.save(sport1);
				sRepository.save(sport2);
				sRepository.save(sport3);
				sRepository.save(sport4);
			}

			Teacher teacher1 = new Teacher("teacher1", "$2a$10$zA8JiOPsP5cEcd/5gNF3Fu8fX4LcHtMkooyDHCgbJAzUs/a26W1TG", "Sara", "Bäckström");
			Teacher teacher2 = new Teacher("teacher2", "$2a$10$EZez2c2QTpAL1pF05/xY5u9NKZorWipVy6v4SDG9c/qA96T99HUsq", "Maija", "Mehiläinen");
			Admin admin1 = new Admin("admin1", "$2a$10$cHiUj.wxb2MFCfuPp4QbwOZwn1X84DIxeMTayAOfj2g2RVfUYSE5O", "Paavo", "Pesusieni");
			if(uRepository.count()== 0) {
				uRepository.save(teacher1);
				uRepository.save(teacher2);
				uRepository.save(admin1);
			}

			TeachingGroup teachingGroup1 = new TeachingGroup(2025, "A", "7A", teacher1);
			TeachingGroup teachingGroup2 = new TeachingGroup(2025, "B", "7B", teacher1);
			TeachingGroup teachingGroup3 = new TeachingGroup(2025, "C", "7C", teacher2);
			if(tGroupRepository.count() == 0){
				tGroupRepository.save(teachingGroup1);
				tGroupRepository.save(teachingGroup2);
				tGroupRepository.save(teachingGroup3);
			}

			Student student1 = new Student("student1", "$2a$10$msk99ISatocqvZjtUV.WNuJd2Z0Gam5AVGYFDH4TEsa.Cb3A3uXFO", "Nalle", "Puh", teachingGroup1);
			Student student2 = new Student("student2", "$2a$10$8hf..Nl9U0utapOlOr7tX.nTnc4GVQ9W0jiuRh91H2X.MY4EsefQC", "Kaisa", "Laakso", teachingGroup1);
			Student student3 = new Student("student3", "$2a$10$mrWdY1Z1XZbCARjDvj6sfu1f01XhXImVYvWFKqeNJ1o2HfLt3ueU.", "Pekka", "Alanko", teachingGroup1);
			Student student4 = new Student("student4", "$2a$10$yValZyVVanKrJm2cY23RmO1P4ixSphL3dqw9VS7JYeTNGz.9LXS7a", "Juuso", "Kekkonen", teachingGroup2);
			Student student5 = new Student("student5", "$2a$10$zHNAY4atZquYy0bCji60R.OaETdzVXGvS2jeXjBNFUXktC3nsL1Xu", "Kalle", "Luoto", teachingGroup2);
			Student student6 = new Student("student6", "$2a$10$z3DaINl26SjzRErY2gbaHuk6DpJx/Xutf2lAO5BFVj2I1BWhsFDgi", "Oona", "Kuoppa", teachingGroup3);
			if(uRepository.count()< 4) {
				uRepository.save(student1);
				uRepository.save(student2);
				uRepository.save(student3);
				uRepository.save(student4);
				uRepository.save(student5);
				uRepository.save(student6);

			}
			Lesson lesson1 = new Lesson(LocalDate.of(2025, 6, 23), "Pienpelit", teachingGroup1, sport1);
			if(lRepository.count()== 0){
				lRepository.save(lesson1);
			}

			EducationalGoal educationalGoal1 = new EducationalGoal("T1 Työskentely ja parhaansa yrittäminen", "effort");
			EducationalGoal educationalGoal2 = new EducationalGoal("T2 Havaintomotoriset taidot", "skills");
			EducationalGoal educationalGoal3 = new EducationalGoal("T3 Tasapaino- ja liikkumistaidot", "skills");
			EducationalGoal educationalGoal4 = new EducationalGoal("T4 Välineenkäsittelytaidot", "skills");
			EducationalGoal educationalGoal5 = new EducationalGoal("T5 Fyysisten ominaisuuksien arviointi, ylläpitäminen ja kehittäminen", "skills");
			EducationalGoal educationalGoal6 = new EducationalGoal("T6 Uima- ja vesipelastustaidot ", "skills");
			EducationalGoal educationalGoal7 = new EducationalGoal("T7 Turvallinen ja asiallinen toiminta liikuntatunneilla", "effort");
			EducationalGoal educationalGoal8 = new EducationalGoal("T8 Vuorovaikutus- ja työskentelytaidot", "effort");
			EducationalGoal educationalGoal9 = new EducationalGoal("T9 Toimiminen reilun pelin periaatteella ja vastuunotto yhteisistä oppimistilanteista", "effort");
			EducationalGoal educationalGoal10 = new EducationalGoal("T10 Vastuullinen itsenäinen työskentely liikuntatunneilla", "effort");
			if(eGoalRepository.count()== 0){
				eGoalRepository.save(educationalGoal1);
				eGoalRepository.save(educationalGoal2);
				eGoalRepository.save(educationalGoal3);
				eGoalRepository.save(educationalGoal4);
				eGoalRepository.save(educationalGoal5);
				eGoalRepository.save(educationalGoal6);
				eGoalRepository.save(educationalGoal7);
				eGoalRepository.save(educationalGoal8);
				eGoalRepository.save(educationalGoal9);
				eGoalRepository.save(educationalGoal10);
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