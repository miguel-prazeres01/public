package furhatos.app.sleepagent.setting

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*
import java.io.File


var file = "src/main/database/file.json"

var jsonMap = HashMap<String,Any>()

@Serializable
data class Session(val session: Int)

@Serializable
data class Gender(val gender: String)

@Serializable
data class Age(val age: Int?)

fun saveSession(session: Int) {
    jsonMap.put("session", session)
    val numSession = jsonMap["session"] as Int
    val json = Json.encodeToString(Session(numSession))
    File(file).writeText(json)
}

fun saveGender(gender: String){
    val json = Json.encodeToString(Gender(gender))
    println(json)
}

fun saveAge(age: Int? = 1){
    val json = Json.encodeToString(Age(age))
    println(age)
}