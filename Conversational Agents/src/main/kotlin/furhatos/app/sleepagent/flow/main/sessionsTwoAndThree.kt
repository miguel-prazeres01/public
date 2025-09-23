package furhatos.app.sleepagent.flow.main

import furhatos.app.sleepagent.nlu.*
import furhatos.flow.kotlin.*
import furhatos.nlu.common.No
import furhatos.nlu.common.Yes
import furhatos.app.sleepagent.utilities.*
import java.time.DayOfWeek
import java.time.LocalDateTime

val ContinueOrEnd : State = state {
    onEntry {
        random(
            furhat.ask("Do you want to talk more?"),
            furhat.ask("Do you want to continue?")
        )

    }

    onResponse<Yes> {
        goto(AdviceOrInformation)
    }

    onResponse<No> {
        saveFinalObject()
        exit()
    }
}

val AdviceOrInformation : State = state {
    onEntry {
        random(
            furhat.ask("Would you like to tell me about yourself or get advice?"),
            furhat.ask("Do you want to talk about yourself or get advice?"),
            furhat.ask("You can tell me about yourself or get advice from me. What would you like?")
        )



    }

    onResponse<GetCurrentAdvice> {
        goto(Advice)
    }

    onResponse<ProvideInformation> {
        goto(GatherInformation)
    }
}


val GatherInformation : State = state {
    onEntry {
        random(
            furhat.ask("Do you want to talk about your experiences the past days or tell me about your upcoming plans?"),
            furhat.ask("Do you want to share some experiences from the past or tell me about your upcoming plans?")
        )

    }

    onResponse<Past> {
        goto(GatherInformationPast)
    }

    onResponse<Future> {
        goto(GatherInformationFuture)
    }
}

val GatherInformationFuture : State = state {
    onEntry {
        furhat.ask("Tell me about your plans for the next days. Pick a day and tell me what you have planned. " +
                "Also tell me when your earliest appointment is and when your last appointment ends that day. " )
    }

    onResponse<GetActivity> {
//        val day = DayOfWeek.valueOf(it.intent.day.toString().uppercase())
//        val time = LocalDateTime.now()
//        val today = time.dayOfWeek
//        val dayDiff = kotlin.math.abs(day.value - today.value).toLong()
//
//        val finalDay = time.plusDays(dayDiff+7).toLocalDate()
        val finalDay =  nextOccurenceOfDay(it.intent.day)

        val startTime = it.intent.timeBegin?.asLocalTime()
        val endTime = it.intent.timeEnd?.asLocalTime()

        if (!isScheduleCreated(finalDay) && startTime != null && endTime != null){
            // Do the math to calculate the date
            createFutureSchedule(finalDay,startTime,endTime)
            goto(GatherStressDayRatingFuture)
        } else {
            if(startTime != null && endTime != null){
                updateSchedule(finalDay,startTime,endTime)
                saveFinalObject()
                furhat.say("Thank you!")
                goto(ContinueOrEnd)
            }
        }
    }
}

val GatherStressDayRatingFuture : State = state {
    onEntry {
        random(
            furhat.ask("How stressful do you think your day will be on a scale of 0 to 10? "),
            furhat.ask("How stressful do you think your day will be? Rate it on a scale of 0 to 10")
        )


    }

    onResponse<GetStressDayRating> {
        val stress = it.intent.stress?.value
        if (stress != null){
            saveStressDayRatingMemoryFuture(stress)
            saveFinalObject()
            furhat.say("Thank you!")
            furhat.ask("Are you interested on sharing more about yourself?")
        }

    }

    onResponse<Yes> {
        furhat.say("Ok")
        goto(AdviceOrInformation)
    }

    onResponse<No> {
        furhat.say("Ok")
        goto(ContinueOrEnd)
    }
}

val GatherInformationPast : State = state {
    onEntry {
        random(
            furhat.ask("Tell me about some experience you had last week. What day was it, what did you do and when did it start and end?"),
            furhat.ask("Tell me about something you did last week. What day was it, what did you do and when did it start and end?"),
            furhat.ask("Why don't you tell me about something you did last week? What day was it, and when did it start and end?"),

        )

    }

    onResponse<GetActivity> {
        val day = DayOfWeek.valueOf(it.intent.day.toString().uppercase())
        val time = LocalDateTime.now()
        val today = time.dayOfWeek
        val dayDiff = kotlin.math.abs(day.value - today.value).toLong()

        val finalDay = time.minusDays(dayDiff).toLocalDate()
        val startTime = it.intent.timeBegin?.asLocalTime()
        val endTime = it.intent.timeEnd?.asLocalTime()

        if (!isMemoryCreated(finalDay) && startTime != null && endTime != null){
            // Do the math to calculate the date
            createMemory(finalDay,startTime,endTime)
            goto(GatherTirednessAndIrritability)
        } else {
            if(startTime != null && endTime != null){
                updateMemory(finalDay,startTime,endTime)
                saveFinalObject()
                furhat.say("Thank you!")
                goto(ContinueOrEnd)
            }
        }
    }
}

val GatherStressDayRating : State = state {
    onEntry {
        random(
        furhat.ask("How stressed did you feel that day? Rate it on a scale of 0 to 10"),
            furhat.ask("How stressed did you feel that day on a scale of 0 to 10?"),


        )
    }

    onResponse<GetStressDayRating> {
        val stress = it.intent.stress?.value
        if (stress != null){
            saveStressDayRatingMemory(stress)
            saveFinalObject()
            furhat.say("Thank you!")
            furhat.ask("Are you interested on sharing more about yourself?")
        }

    }

    onResponse<Yes> {
        furhat.say("Ok")
        goto(GatherInformationPast)
    }

    onResponse<No> {
        furhat.say("Ok")
        goto(AdviceOrInformation)
    }
}


val GatherTirednessAndIrritability : State = state {
    onEntry {
        random(
            furhat.ask("How tired were you and how easily were you irritated by others or your surroundings? rate them from 0 to 10"),
            furhat.ask("What would you say was your level of tiredness and irritability? rate them from 0 to 10"),
        )

    }
    onResponse<GetTirednessAndIrritability> {
        val tiredness = it.intent.tiredness?.value
        val irritability = it.intent.irritability?.value

        if (tiredness != null && irritability  != null){
            saveTirednessAndIrritability(tiredness, irritability)
        }
        goto(GatherBedTimeAndWakeUpTime)
    }
}

val GatherBedTimeAndWakeUpTime : State = state {
    onEntry {
        furhat.ask("What time did you go to bed and when did you wake up")
    }
    onResponse<GetBedTimeAndWakeUpTime> {
        val bedTime = it.intent.bedtime?.asLocalTime()
        val wakeUpTime = it.intent.wakeUpTime?.asLocalTime()

        if(bedTime != null && wakeUpTime != null){
            saveBedTimeAndWakeUpTime(wakeUpTime,bedTime)
            goto(GatherStressDayRating)
        } else {
            furhat.ask("Could you repeat?")
        }

    }

}
