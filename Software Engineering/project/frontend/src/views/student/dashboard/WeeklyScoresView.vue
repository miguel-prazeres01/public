<template>
  <v-card class="table">
    <h2>WeeklyScores</h2>
    <v-row>
      <v-col class="text-right">
        <v-btn
            right
            color="primary"
            dark
            data-cy="refreshWeeklyScoresMenuButton"
            v-on:click="refresh"
        >
          refresh
        </v-btn>
      </v-col>
    </v-row>
      <v-data-table
          :headers="headers"
          :custom-filter="customFilter"
          :items="weeklyScores"
          :search="search"
          :sort-by="['week']"
          sort-desc
          :mobile-breakpoint="0"
          :items-per-page="15"
          :footer-props="{ itemsPerPageOptions: [15, 30, 50, 100] }"
          >
        <template v-slot:[`item.action`]="{ item }">
          <v-tooltip>
            <template v-slot:activator="{ on }">
              <v-icon
                  class="mr-2 action-button"
                  v-on="on"
                  data-cy="deleteWeeklyScoreButton"
                  @click="deleteWeeklyScore(item)"
                  color="red"
              >delete</v-icon
              >
            </template>
            <span>Delete WeeklyScore</span>
          </v-tooltip>
        </template>
      </v-data-table>
    </v-card>
</template>

<script lang="ts">
import {Component, Prop, Vue, Watch} from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import WeeklyScore from '@/models/dashboard/WeeklyScore';
import Dashboard from '@/models/dashboard/Dashboard';

@Component
export default class WeeklyScoresView extends Vue{
  @Prop({ type: Number, required: true }) readonly dashboardId!: number;

  weeklyScores: WeeklyScore[] = [];
  search: string = '';
  caption: string = 'WeeklyScores';

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.weeklyScores = await RemoteServices.getWeeklyScores(this.dashboardId);
    }catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  headers: object = [
    {
      text: 'Actions',
      value: 'action',
      align: 'left',
      width: '5px',
      sortable: false,
    },
    {text: 'Week', value: 'week', align: 'left', width: '5px', sortable: true,},
    {
      text: 'Number Answered',
      value: 'numberAnswered',
      align: 'left',
      width: '5px',
      sortable: true,
    },
    {text: 'Uniquely Answered', value: 'uniquelyAnswered', align: 'left', width: '5px', sortable: true,
    },
    {text: 'Percentage Answered',
      value: 'percentageCorrect',
      align: 'left',
      width: '5px',
      sortable: false,
    },
  ];


  customFilter(value: string, search: string, weeklyScore: WeeklyScore) {
    // noinspection SuspiciousTypeOfGuard,SuspiciousTypeOfGuard
    return (
        search != null &&
        JSON.stringify(weeklyScore).toLowerCase().indexOf(search.toLowerCase()) !==
        -1
    );
  }

  async refresh() {
    try {
      await RemoteServices.updateWeeklyScores(this.dashboardId);
      this.weeklyScores = await RemoteServices.getWeeklyScores(this.dashboardId);
      let dashboard = await RemoteServices.getUserDashboard();
      this.$emit('refresh', dashboard.lastCheckWeeklyScores);
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
  }

  async deleteWeeklyScore(toDeleteWeeklyScore: WeeklyScore) {
    if (
        toDeleteWeeklyScore.id &&
        confirm('Are you sure you want to delete this question?')
    ) {
      try {
        await RemoteServices.deleteWeeklyScore(toDeleteWeeklyScore.id);
        this.weeklyScores = this.weeklyScores.filter(
            (question) => question.id != toDeleteWeeklyScore.id
        );
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }
}
</script>

<style lang="scss" scoped>
h2{
  display: block;
  font-size: 2.5em;
  margin-top: 0.67em;
  margin-bottom: 0.67em;
  margin-left: 4em;
  font-weight: bold;
  text-align: left;

}
</style>