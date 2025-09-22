package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler;


import java.time.LocalDateTime;

import javax.persistence.*;
import javax.security.sasl.SaslServer;
import javax.validation.constraints.Null;

@Entity
public class DifficultQuestion implements DomainEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private int percentage;

    private boolean removed = false;

    private LocalDateTime removedDate;

    @ManyToOne
    private Question question;

    @ManyToOne
    private Dashboard dashboard;


    public DifficultQuestion(){
    }

    public DifficultQuestion(Dashboard dashboard, Question question, int percentage){
        if (percentage < 0 || percentage > 24)
            throw new TutorException(ErrorMessage.CANNOT_CREATE_DIFFICULT_QUESTION);

        if (question.getCourse() != dashboard.getCourseExecution().getCourse()) {
            throw new TutorException(ErrorMessage.CANNOT_CREATE_DIFFICULT_QUESTION);
        }

        setPercentage(percentage);
        setRemovedDate(null);
        setRemoved(false);
        setQuestion(question);
        setDashboard(dashboard);

        for(DifficultQuestion dQuestion: dashboard.getDifficultQuestions()){
            if(dQuestion == this){
                continue;
            }
            if (dQuestion.getPercentage() == percentage && dQuestion.isRemoved() == false){
            }
        }
    }
    public void remove1(){
        if (removed) {
            throw new TutorException(ErrorMessage.CANNOT_REMOVE_DIFFICULT_QUESTION);
        }
        setRemoved(true);
        setRemovedDate(LocalDateTime.now());
    }
    public void remove() {

        for(DifficultQuestion dQuestion: dashboard.getDifficultQuestions()){
            if(dQuestion==this)
                continue;
            if (dQuestion.getPercentage() == this.percentage){
            }
        }

        dashboard.getDifficultQuestions().remove(this);
        dashboard = null;
    }

    public Dashboard getDashboard() {
        return dashboard;
    }

    public void setDashboard(Dashboard dashboard) {
        this.dashboard = dashboard;
        this.dashboard.addDifficultQuestion(this);
    }



    public Integer getId() {
        return id;
    }

    public int getPercentage() {
        return percentage;
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
    }

    public boolean isRemoved() {
        return removed;
    }

    public void setRemoved(boolean removed) {
        this.removed = removed;
    }

    public LocalDateTime getRemovedDate() {
        return removedDate;
    }

    public void setRemovedDate(LocalDateTime collected) {
        this.removedDate = collected;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    @Override
    public void accept(Visitor visitor) {
        // TODO Auto-generated method stub
    }

    @Override
    public String toString() {
        return "DifficultQuestion{" +
                "id=" + id +
                ", percentage=" + percentage +
                ", removed=" + removed +
                ", question=" + question +
                "}";
    }

}