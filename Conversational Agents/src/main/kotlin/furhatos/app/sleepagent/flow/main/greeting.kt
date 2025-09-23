package furhatos.app.sleepagent.flow.main

import furhatos.app.sleepagent.flow.Parent
import furhatos.app.sleepagent.utilities.*
import furhatos.flow.kotlin.*
import kotlinx.serialization.json.Json
import java.io.File
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString
import kotlin.math.absoluteValue


val Greeting : State = state(Parent) {
    onEntry {
        val fullPath = Path("src/main/database/database.json").absolutePathString()
        val isValid = File(fullPath).exists()
        var session: Int = 1
        if (isValid){
            val jsonContent = File(fullPath).readText()
            val data = Json.decodeFromString<DatabaseObject>(jsonContent)
            initializeDataBase()
            if (data.session != null){
                session = data.session!!
                val saveSession = session + 1
                saveSession(saveSession)
            }

        }

        if(session == 1){
            random(
                {   furhat.say("Hi there, I'm Sleepy Rob and I can help you with your sleep schedule, In order to give you good advice, I need to know a few things about you.") },
                {   furhat.say("Oh, hello there, I'm Sleepy Rob and I can help you with your sleep schedule, but first I need to know a few things about you.") }
            )
            goto(GettingInformation)
        } else {
            furhat.say("Hello again!")
            goto(AdviceOrInformation)
        }
    }
}