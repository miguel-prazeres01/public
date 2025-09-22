package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser;
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.DifficultQuestionService;
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.dto.DifficultQuestionDto;

import java.security.Principal;
import java.util.List;

@RestController
public class DifficultQuestionController {
    private static final Logger logger = LoggerFactory.getLogger(DifficultQuestionController.class);

    @Autowired
    private DifficultQuestionService difficultQuestionService;

    DifficultQuestionController(DifficultQuestionService difficultQuestionService){ this.difficultQuestionService = difficultQuestionService;}

    @GetMapping("/students/dashboards/{dashboardId}/difficultQuestions/Get")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#dashboardId, 'DASHBOARD.ACCESS')")
    public List<DifficultQuestionDto> getDifficultQuestions(@PathVariable int dashboardId) {
        return this.difficultQuestionService.getDifficultQuestions(dashboardId);
    }

    @PutMapping("/students/dashboards/{dashboardId}/difficultQuestions/Update")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#dashboardId, 'DASHBOARD.ACCESS')")
    public void updateDifficultQuestions(@PathVariable int dashboardId) {
        this.difficultQuestionService.updateDifficultQuestions(dashboardId);
    }

    @DeleteMapping("/students/dashboards/difficultQuestions/Remove/{difficultQuestionId}")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#difficultQuestionId, 'DIFFICULT_QUESTION.ACCESS')")
    public void removeDifficultQuestions(@PathVariable int difficultQuestionId) {
        this.difficultQuestionService.removeDifficultQuestion(difficultQuestionId);
    }


}