package opinnaytetyo.arviointikirja.domain;

import java.util.ArrayList;
import java.util.List;

public class PerformancesDto {

    private List<Performance> performances;
    private Lesson lesson;
    private User user;

    public PerformancesDto() {
        this.performances = new ArrayList<>();
    }

    public void addPerformance(Performance performance) {
        this.performances.add(performance);
    }

    public List<Performance> getPerformances() {
        return performances;
    }

    public void setPerformances(List<Performance> performances) {
        this.performances = performances;
    }

    public Lesson getLesson() {
        return lesson;
    }

    public void setLesson(Lesson lesson) {
        this.lesson = lesson;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
