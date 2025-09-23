package furhatos.app.sleepagent.flow.main

import furhatos.app.sleepagent.flow.Options
import furhatos.app.sleepagent.nlu.*
import furhatos.flow.kotlin.*
import furhatos.nlu.common.No
import furhatos.nlu.common.Time
import furhatos.nlu.common.Yes
import furhatos.util.Language
import furhatos.app.sleepagent.utilities.*
val GettingInformation = state {
    onEntry {
        random(
        furhat.ask("Can you tell me your gender?"),
            furhat.ask("What is your preferred gender?")
        )
    }

    onResponse<GetGender> {
        val gender = it.intent.gender.toString()
        saveGender(gender)
        goto(GettingAge)
    }

    onResponse {
        furhat.ask("Can you repeat please?")
    }

}

val GettingAge = state {
    onEntry {
        random(
            furhat.ask("What's your age?"),
            furhat.ask("How old are you?")
        )

    }
    onResponse<GetAge> {
        val age = it.intent.age?.value
        if(age != null){
            saveAge(age)
            goto(GettingSleepGoal)
        } else {
            furhat.ask("Can you please repeat your age?")
        }
    }
    onResponse {
        furhat.ask("Can you repeat please?")
    }


}

val GettingSleepGoal = state {
    onEntry {
        random (
            furhat.ask("In order to give you advice for your sleep, I need to know what would be an acceptable level of tiredness for you? Rate it on a scale of 0 (not tired at all) to 10 (very tired)" ),
        )

    }

    onResponse<GetSleepGoal>{
        when(val sleepGoal = it.intent.num?.value){
            null -> furhat.ask("Can you please repeat that?")
            else -> {
                saveSleepGoal(sleepGoal)
                goto(GettingReadyTime)
            }
        }
    }
    onResponse {
        furhat.ask("Can you repeat please?")
    }


}

val GettingReadyTime = state {
    onEntry {
        random(
            furhat.ask("How much time do you need, on average, every morning to get ready?"),
        )

    }
    onResponse<GetReadyTime>{
        val time = it.intent.time
        val hours = time?.asLocalTime()?.hour?.toDouble()
        val minutes = time?.asLocalTime()?.minute?.toDouble()

        if(hours != null && minutes != null) {
            val final:Double = hours + (minutes / 60)
            saveHoursGetReady(final)
        }
        goto(AdviceOrInformation)
    }

    onResponse<GetReadyHours>{
        val time = it.intent.hours?.value?.toDouble()

        if(time != null) {
            val final:Double = time
            saveHoursGetReady(final)
        }
        goto(AdviceOrInformation)
    }

    onResponse<GetReadyMinutes>{
        val time = it.intent.minutes?.value?.toDouble()

        if(time != null) {
            val final:Double = time/60
            saveHoursGetReady(final)
        }

        goto(AdviceOrInformation)
    }

    onResponse {
        furhat.ask("Can you repeat please?")
    }
}
