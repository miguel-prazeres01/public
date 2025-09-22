import Reply from '@/models/management/Reply';
import { ISOtoString } from '@/services/ConvertDateService';
import Difficult from '@/models/management/Question';

export default class DifficultQuestion {
    id!: number;
    percentage!: number;
    removedDate!: string;
    removed!: boolean ;
    questionDto!: Difficult;

    constructor(jsonObj?: DifficultQuestion) {
        if (jsonObj) {
            this.id = jsonObj.id;
            if(jsonObj.removedDate)
                this.removedDate =ISOtoString(jsonObj.removedDate)
            this.percentage = jsonObj.percentage;
            this.removedDate = ISOtoString(jsonObj.removedDate);
            if(jsonObj.removed)
                this.removed = jsonObj.removed;
                this.questionDto = new Difficult(jsonObj.questionDto);
        }
    }
}
