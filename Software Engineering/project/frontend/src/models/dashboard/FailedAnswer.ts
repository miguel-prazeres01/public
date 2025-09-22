import { ISOtoString } from '@/services/ConvertDateService';
import { QuestionAnswer } from '@/models/management/QuestionAnswer';


export default class FailedAnswer {
  id!: number;
  collected!: string;
  answered!: boolean;
  questionAnswerDto!: QuestionAnswer;
  constructor(jsonObj?: FailedAnswer) {
    if (jsonObj) {
      
      
      this.id = jsonObj.id;
      if (jsonObj.collected)
        this.collected =ISOtoString(jsonObj.collected);
      if (jsonObj.answered)
        this.answered =jsonObj.answered;

        this.questionAnswerDto = new QuestionAnswer(jsonObj.questionAnswerDto);
      
      
    }
  }
}