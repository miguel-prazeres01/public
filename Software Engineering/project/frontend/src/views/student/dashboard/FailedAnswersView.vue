<template>
  <v-container fluid>
    <v-card class="table">
      
      <v-data-table
        :headers="headers"
        :items="failedAnswers"
        :sort-by="['collected']"
        sort-desc
        :mobile-breakpoint="0"
        :items-per-page="10"
        :footer-props="{ itemsPerPageOptions: [10, 30, 50, 100] }"
      >
        <template v-slot:top>
          <v-card-title>
            
            <h1>Failed Answers</h1>
            <v-col class="text-right">
              <v-btn
                    color="primary"
                    dark
                    data-cy="refreshFailedAnswersMenuButton"
                    @click="refresh"
              >REFRESH
              </v-btn>
            </v-col>
          </v-card-title>
        </template>
        <template v-slot:[`item.answered`]="{ item }">
          <div v-if="item.answered === true">
            yes
          </div>
          <div v-else>
            no
          </div>
          
        </template>
                
        <template v-slot:[`item.action`]="{ item }">
          
          <v-tooltip bottom>
            <template v-slot:activator="{ on }">
              <v-icon
                class="mr-2 action-button"
                v-on="on"
                data-cy="showStudentViewDialog"
                @click="showStudentViewDialog(item.questionAnswerDto.question)"
                >school</v-icon
              >
            </template>
            <span>Student View</span>
          </v-tooltip>
                              
          <v-tooltip bottom >
            <template v-slot:activator="{ on }">
              <v-icon
                class="mr-2 action-button"
                v-on="on"
                data-cy="deleteFailedAnswerButton"
                @click="removeQuestion(item)"
                color="red"
                >delete</v-icon
              >
            </template>
            <span>Delete Question</span>
          </v-tooltip>
        </template>
      </v-data-table>
      
           
      <student-view-dialog
        v-if="statementQuestion && studentViewDialog"
        v-model="studentViewDialog"
        :statementQuestion="statementQuestion"
        v-on:close-show-question-dialog="onCloseStudentViewDialog"
      />
      
    </v-card>
  </v-container>
</template>

<script lang="ts">
import { Component, Prop, Vue} from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import StatementQuestion from '@/models/statement/StatementQuestion';
import StudentViewDialog from '@/views/teacher/questions/StudentViewDialog.vue';
import FailedAnswer from '@/models/dashboard/FailedAnswer';
import Question from '@/models/management/Question';




@Component({
  components: {
    
       
    'student-view-dialog': StudentViewDialog,
    
  },
})
export default class FailedAnswersView extends Vue {
  
  failedAnswers: FailedAnswer[] = [];
  @Prop({ type:Number,required: true}) readonly dashboardId!:number;
  
  statementQuestion: StatementQuestion | null = null;
  
  
  studentViewDialog: boolean = false;
  
  
  search: string = '';
  
  
  headers: object = [
    {
      text: 'Actions',
      value: 'action',
      align: 'left',
      width: '5px',
      sortable: false,
    },
    { text: 'Question', value: 'questionAnswerDto.question.content', width: '50%', align: 'left' },
    {
      text: 'Answered',
      value: 'answered',
      width: '5px',
      align: 'center',
    },
    {
      text: 'Collected',
      value: 'collected',
      width: '150px',
      align: 'center',
    },
  ];

  

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.failedAnswers=await RemoteServices.getFailedAnswers(this.dashboardId);
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }  

  async showStudentViewDialog(question: Question) {
    
    if (question.id) {
      try {
        this.statementQuestion = await RemoteServices.getStatementQuestion(
          question.id
        );
        this.studentViewDialog = true;
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }

  

  onCloseStudentViewDialog() {
    this.statementQuestion = null;
    this.studentViewDialog = false;
  }

  async refresh(){
    try {
        await RemoteServices.updateFailedAnswers(this.dashboardId);
        this.failedAnswers=await RemoteServices.getFailedAnswers(this.dashboardId);
        let dashboard= await RemoteServices.getUserDashboard();
        this.$emit('refresh',dashboard.lastCheckFailedAnswers);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
  }
  async removeQuestion(toDeletequestion: FailedAnswer) {
    if (
      toDeletequestion.id &&
      confirm('Are you sure you want to delete this question?')
    ) {
      try {
        await RemoteServices.removeFailedAnswer(toDeletequestion.id);
        this.failedAnswers = this.failedAnswers.filter(
          (failedAnswer) => failedAnswer.id != toDeletequestion.id
        );
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }

}
</script>

<style lang="scss" scoped>
.question-textarea {
  text-align: left;

  .CodeMirror,
  .CodeMirror-scroll {
    min-height: 200px !important;
  }
}
.option-textarea {
  text-align: left;

  .CodeMirror,
  .CodeMirror-scroll {
    min-height: 100px !important;
  }
}
</style>