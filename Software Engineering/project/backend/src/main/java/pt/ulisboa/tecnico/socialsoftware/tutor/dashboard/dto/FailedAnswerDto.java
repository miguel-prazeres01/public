package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.QuestionAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain.FailedAnswer;

public class FailedAnswerDto implements Serializable {

    private Integer id;

    private boolean answered;

    private QuestionAnswerDto questionAnswerDto;

    private LocalDateTime collected;

    public FailedAnswerDto(){
    }

    public FailedAnswerDto(FailedAnswer failedAnswer){
        setAnswered(failedAnswer.getAnswered());
        this.id=failedAnswer.getId();
        setQuestionAnswerDto(new QuestionAnswerDto(failedAnswer.getQuestionAnswer()));
        setCollected(failedAnswer.getCollected());

    }

    public Integer getId() {
        return id;
    }

    public boolean getAnswered() {
        return answered;
    }

    public void setAnswered(boolean answered) {
        this.answered = answered;
    }

    public QuestionAnswerDto getQuestionAnswerDto() {
        return questionAnswerDto;
    }

    public void setQuestionAnswerDto(QuestionAnswerDto questionAnswerDto) {
        this.questionAnswerDto = questionAnswerDto;
    }

    public void setCollected(LocalDateTime collected){
        this.collected=collected;
    }

    public String getCollected(){
        return this.collected.toString();
    }



    @Override
    public String toString() {
        return "FailedAnswerDto{" +
            "id=" + id +
            ", answered=" + answered +
            "}";
    }

}