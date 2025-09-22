<template>
  <v-container fluid>
    <v-card class="table">

      <v-data-table
        :headers="headers"
        :items="difficultQuestions"
        :sort-by="['percentage']"
        sort-desc
        :mobile-breakpoint="0"
        :items-per-page="10"
        :footer-props="{ itemsPerPageOptions: [10, 30, 50, 100] }"
      >
        <template v-slot:top>
          <v-card-title>

            <h1>Difficult Questions</h1>
            <v-col class="text-right">
              <v-btn color="primary"
                     data-cy="refreshDifficultQuestionsButton"
                     dark @click="refresh()"
              >REFRESH
              </v-btn>
            </v-col>
          </v-card-title>
        </template>

        <template v-slot:[`item.question`]="{ item }">
            {{item.questionDto.title}}
        </template>


        <template v-slot:[`item.percentage`]="{ item }">
            {{item.percentage}}
        </template>

        <template v-slot:[`item.action`]="{ item }">

          <v-tooltip bottom>
            <template v-slot:activator="{ on }">
              <v-icon
                class="mr-2 action-button"
                v-on="on"
                @click="showStudentViewDialog(item.questionDto)"
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
                data-cy="deleteDifficultQuestionButton"
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
import DifficultQuestion from '@/models/dashboard/DifficultQuestion';
import Question from '@/models/management/Question';




@Component({
  components: {


    'student-view-dialog': StudentViewDialog,

  },
})
export default class DifficultQuestionsView extends Vue {

  @Prop({ type:Number,required: true}) readonly dashboardId!:number;
  difficultQuestions: DifficultQuestion[] = [];

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
    { text: 'Question', value: 'question', width: '50%', align: 'left' },
    {
      text: 'Percentage',
      value: 'percentage',
      width: '150px',
      align: 'center',
    },
  ];



  async created() {
    await this.$store.dispatch('loading');
    try {
      this.difficultQuestions=await RemoteServices.getDifficultQuestion(this.dashboardId);
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
      await RemoteServices.updateDifficultQuestions(this.dashboardId);

      this.difficultQuestions=await RemoteServices.getDifficultQuestion(this.dashboardId);
      let dashboard= await RemoteServices.getUserDashboard();
      this.$emit('refresh',dashboard.lastCheckDifficultQuestions);
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
  }
  async removeQuestion(toDeletequestion: DifficultQuestion) {
    if (
      toDeletequestion.id &&
      confirm('Are you sure you want to delete this question?')
    ) {
      try {
        await RemoteServices.removeDifficultQuestion(toDeletequestion.id);
        this.difficultQuestions = this.difficultQuestions.filter(
          (difficultQuestion) => difficultQuestion.id != toDeletequestion.id
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