package furhatos.app.sleepagent.utilities

import furhatos.app.sleepagent.nlu.GetTirednessAndIrritability
//import furhatos.app.sleepagent.setting.Age
//import furhatos.app.sleepagent.setting.Gender
//import furhatos.app.sleepagent.setting.Session
//import furhatos.app.sleepagent.setting.jsonMap

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString

var file = "src/main/database/database.json"

val fullPath = Path("src/main/database/database.json").absolutePathString()


var databaseObject = DatabaseObject(true,null,null,null, ArrayList<Memory>(), ArrayList<FutureSchedule>())


var physicalData =  Physical(0,"")

var goalsData = Goals(0,0.0)

var memoriesToAdd = ArrayList<Memory>()

var futureScheduleToAdd = ArrayList<FutureSchedule>()

@Serializable
data class DatabaseObject(
    var is_advanced_version: Boolean?,
    var session: Int?,
    var physical: Physical?,
    var goals: Goals?,
    var memories: ArrayList<Memory>?,
    var future_schedule: ArrayList<FutureSchedule>?
)
@Serializable
data class Physical(
    var age: Int,
    var gender: String
)
@Serializable
data class Goals(
    var tiredness_goal: Int,
    var hours_needed_to_get_ready: Double
)
@Serializable
data class Memory(
    var date: String,
    var tiredness: Int,
    var irritability: Int,
    var stressful_day_rating: Int,
    var bedtime: String,
    var time_of_waking_up: String,
    var end_time_of_last_appointment: String,
    var time_of_first_appointment: String
)
@Serializable
data class FutureSchedule(
    var date: String,
    var end_time_of_last_appointment: String,
    var time_of_first_appointment: String,
    var stressful_day_rating: Int
)

fun initializeDataBase() {
    val isValid = File(fullPath).exists()
    if (isValid){
        val jsonContent = File(fullPath).readText()
        databaseObject = Json.decodeFromString<DatabaseObject>(jsonContent)
        goalsData = databaseObject.goals!!
        physicalData = databaseObject.physical!!
        memoriesToAdd = databaseObject.memories!!
        futureScheduleToAdd = databaseObject.future_schedule!!
    }
}
fun saveSession(session: Int) {
    databaseObject.session = session
}

fun saveGender(gender: String){
    physicalData.gender = gender
}

fun saveAge(age: Int){
    physicalData.age = age
}

fun saveSleepGoal(sleepGoal : Int){
    goalsData.tiredness_goal = sleepGoal
}

fun saveHoursGetReady(hoursGetReady: Double){
    goalsData.hours_needed_to_get_ready = hoursGetReady
}

fun createMemory(date : LocalDate, startTime: LocalTime, endTime: LocalTime){
    var mem = Memory(date.toString(),0,0,0,"","",endTime.toString(),startTime.toString())
    memoriesToAdd.add(mem)
}

fun createFutureSchedule(date : LocalDate, startTime: LocalTime, endTime: LocalTime){
    var schedule = FutureSchedule(date.toString(),endTime.toString(),startTime.toString(),0)
    futureScheduleToAdd.add(schedule)
}

fun isMemoryCreated(date : LocalDate) : Boolean{
    for (mem in memoriesToAdd){
        if (mem.date == date.toString())
            return true
    }
    return false
}

fun isScheduleCreated(date : LocalDate) : Boolean{
    for (schedule in futureScheduleToAdd){
        if (schedule.date == date.toString())
            return true
    }
    return false
}

fun updateMemory(date: LocalDate, startTime: LocalTime, endTime: LocalTime){
    for (mem in memoriesToAdd){
        if (mem.date == date.toString())
            if (LocalTime.parse(mem.time_of_first_appointment).isAfter(startTime) ){
                mem.time_of_first_appointment = startTime.toString()
            }
            if(LocalTime.parse(mem.end_time_of_last_appointment).isBefore(endTime)){
                mem.end_time_of_last_appointment = endTime.toString()
            }
    }
}

fun updateSchedule(date: LocalDate, startTime: LocalTime, endTime: LocalTime){
    for (schedule in futureScheduleToAdd){
        if (schedule.date == date.toString())
            if (LocalTime.parse(schedule.time_of_first_appointment).isAfter(startTime) ){
                schedule.time_of_first_appointment = startTime.toString()
            }
        if(LocalTime.parse(schedule.end_time_of_last_appointment).isBefore(endTime)){
            schedule.end_time_of_last_appointment = endTime.toString()
        }
    }
}

fun saveTirednessAndIrritability(tiredness : Int, irritability: Int){
    var lastMemory = memoriesToAdd.last()
    lastMemory.tiredness = tiredness
    lastMemory.irritability = irritability
}

fun saveStressDayRatingMemory(stress : Int){
    var lastMemory = memoriesToAdd.last()
    lastMemory.stressful_day_rating = stress
}

fun saveStressDayRatingMemoryFuture(stress : Int){
    var schedule = futureScheduleToAdd.last()
    schedule.stressful_day_rating = stress
}

fun saveBedTimeAndWakeUpTime(wakeUpTime: LocalTime,bedTime: LocalTime){
    var lastMemory = memoriesToAdd.last()
    lastMemory.time_of_waking_up = wakeUpTime.toString()
    lastMemory.bedtime = bedTime.toString()
}

fun saveFinalObject(){
    databaseObject.goals = goalsData
    databaseObject.physical = physicalData

    databaseObject.memories = memoriesToAdd
    databaseObject.future_schedule = futureScheduleToAdd

    val json = Json.encodeToString(databaseObject)

    File(file).writeText(json)
}
