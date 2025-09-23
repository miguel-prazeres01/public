package furhatos.app.sleepagent.flow.main

//import com.sun.org.apache.xpath.internal.operations.Bool
import furhatos.app.sleepagent.nlu.GetAdvice
import furhatos.app.sleepagent.nlu.Weekday
import furhatos.app.sleepagent.utilities.DatabaseObject
import furhatos.app.sleepagent.utilities.Memory
import furhatos.flow.kotlin.furhat
import furhatos.flow.kotlin.onResponse
import furhatos.flow.kotlin.state
import kotlinx.serialization.json.*
import java.io.File
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.time.Duration
import java.util.*
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString

val Advice = state() {
    onEntry {
        random(
            { furhat.ask("I would love to give you some advice. What day should I tell you more about?") },
            { furhat.ask("What day do you want advice for?") },
            { furhat.ask("Which day would you like some bedtime advice for?")}
        )
    }
    onResponse<GetAdvice> {
        val weekday = it.intent.weekday;
        // Read the JSON content from the file
        val fullPath = Path("src/main/database/database.json").absolutePathString()
        val jsonContent = File(fullPath).readText()
            val data = Json.decodeFromString<DatabaseObject>(jsonContent)
        println(data)
        val advice = calculateNeededSleep(weekday, data);
        furhat.say(advice)
        goto(ContinueOrEnd)
    }
}

fun calculateNeededSleep(weekday: Weekday? = null, data: DatabaseObject): String {
    val age = data.physical?.age;
    var sleepHours: Long;
    val dateAsked = nextOccurenceOfDay(weekday)
    val nextDay = dateAsked.plusDays(1)
    println("nextDay is $nextDay and dateAsked is $dateAsked")
    val weekDayAsked = dateAsked.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.ENGLISH)
    val nextWeekday = nextDay.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.ENGLISH)
    val normalTime = calculateAverageWakingUp(data)
    val firstAppointment = data.future_schedule?.find { it.date == nextDay.toString() }?.time_of_first_appointment
    println("first appointment is $firstAppointment, because nextDay.toString() is ${nextDay.toString()} ")
    val lastAppointment = data.future_schedule?.find { it.date == dateAsked.toString() }?.end_time_of_last_appointment
    val hoursReady = data.goals?.hours_needed_to_get_ready;
    val alarmTime: LocalTime;
    var advisedHours:String = "the right amount of sleep"

    if((age != null) && (age <= 17)){
        sleepHours = 9
    }else if ((age != null) && (age <= 65)){
        sleepHours = 8
    }else {
        sleepHours = 7.5.toLong()
    }
    var minutesReady = 45
    if (hoursReady!=null){
        minutesReady = (hoursReady*60).toInt()
    }

//    If there are no appointments for the next day, the user can go to sleep whenever he/she wants
    if (firstAppointment == null && data.is_advanced_version == true){
        return "You have not told me about any appointments for $nextWeekday. In that case, you can go to bed on $weekDayAsked whenever you want. " +
                "However, if you want to stay in your rhythm I advise you to get up at $normalTime "
    }
    else if (firstAppointment == null){
        return "You do not have an appointment on $nextWeekday. You can go to sleep on $weekDayAsked whenever you feel like it."
    }

//    Else, if the first appointment is early, the user needs to get up as much time as he needs to get ready before that appointment.
    val appointmentTime = LocalTime.parse(firstAppointment)
    var memoryInformation = ""
    var schedule = false
    if (data.is_advanced_version == true) {
        //    Find memory that most closely matches situation
        val closestMemory = findMemory(data, appointmentTime, minutesReady)

        var experience =
            "were a bit tired and irritable. I therefore suggest you sleep a little longer than you did then. "
        if (closestMemory != null) {
            val (hoursInMemory, positiveNegative) = useMemory(closestMemory)
            if (positiveNegative) {
                experience = "felt good and were not so tired."
                advisedHours = "an equal amount of sleep"
                sleepHours = hoursInMemory
            } else {
                advisedHours = "more sleep then last time"
                if (hoursInMemory >= sleepHours){
                    sleepHours = hoursInMemory+1
                }
            }
            memoryInformation =
                "The last time you had a similar appointment you slept $hoursInMemory hours and you $experience. "
        }
    }
    else{
        advisedHours = "the advised amount of sleep for your age"
    }

    val getUp = appointmentTime.minus(minutesReady.toLong(), ChronoUnit.MINUTES)
    var lastAppointmentTime = LocalTime.of(12,0)
    if (lastAppointment != null){
        lastAppointmentTime = LocalTime.parse(lastAppointment)
    }
    if (getUp.isBefore(normalTime)){
        alarmTime = getUp
    }
    else if (calculateBedtime(lastAppointmentTime, normalTime)>= sleepHours && !getUp.isBefore(normalTime.plusMinutes(60))){
        alarmTime = normalTime.plusMinutes(60)
        schedule = true
    }
    else if(calculateBedtime(lastAppointmentTime, getUp)<= sleepHours){
        alarmTime = lastAppointmentTime.plusMinutes(sleepHours*60)
    }
    else{
        alarmTime = getUp
    }

//    If the alarm time minus hours of sleep is later than the last appointment, bedtime is this time. Else, bedtime is directly after last appointment.
    var hoursOfSleep = ""
    var bedTime = alarmTime.minus((sleepHours*60 ).toLong(), ChronoUnit.MINUTES)
    if (bedTime.isBefore(lastAppointmentTime) && bedTime.isAfter(LocalTime.of(12,0))){
        hoursOfSleep = "Since your latest appointment on $weekDayAsked is at $lastAppointmentTime you should go to bed right after to get as much sleep as you can. "
    }
    else {
        hoursOfSleep = "To get $advisedHours you should go to bed at $bedTime"
    }
    var scheduleinfo = "Since you need $hoursReady hours to get ready, you should set an alarm on $nextWeekday for $alarmTime. "
    if (schedule){
        scheduleinfo = "To stay in your normal rhythm you are advised to get up at $nextWeekday at $alarmTime. "
    }
    return "Your first appointment is at $appointmentTime. " + scheduleinfo + memoryInformation +
            hoursOfSleep;
}
//"Based on your age you are recommended to have $sleepHours hours of sleep. " + "Since it always takes about 15 minutes to fall asleep,
fun findMemory(data: DatabaseObject, appointment: LocalTime, minutesReady:Int): Memory? {
    var closest:Int = 60
    var closestMemory:Memory? = null
    var memoryFound = false
    for(memory in data.memories!!){
        val timeInMemory = memory.time_of_waking_up
        val memoryTime = LocalTime.parse(timeInMemory)
        val getup = appointment.minusMinutes(minutesReady.toLong())
        println("memoryTime = $memoryTime")
        val difference = Duration.between(memoryTime, getup).abs().toMinutes().toInt()
        println("Difference is $difference, day is ${memory.date}")
        if (difference <= closest) {
            println("update for $memory.date")
            closest = difference
            closestMemory = memory
        }
    }
    println("Closest memory returning: $closestMemory.date")
    return closestMemory
}

fun useMemory(closestMemory: Memory): Pair<Long, Boolean>{
    val noon = LocalTime.of(12,0)
    val midNight = 60*24
    var minutesToAdd:Long = 0
    val dayRating = closestMemory.irritability + closestMemory.tiredness
    println("closest memory is ${closestMemory.date} closestMemory.irritability is ${closestMemory.irritability} and closestMemory.tiredness is ${closestMemory.tiredness}")
    val positiveNegative = dayRating <=10
    if (LocalTime.parse(closestMemory.bedtime).isAfter(noon)){
        val bedTimeMinutes = LocalTime.parse(closestMemory.bedtime).toSecondOfDay()/60.toLong()
        minutesToAdd = midNight - bedTimeMinutes
        println("Minutes to add are $minutesToAdd")
    }
    var hoursOfSleep = Duration.between(LocalTime.of(0,0), LocalTime.parse(closestMemory.time_of_waking_up))
    hoursOfSleep = hoursOfSleep.plusMinutes(minutesToAdd)
    println(hoursOfSleep)
    return Pair(hoursOfSleep.toHours(), positiveNegative)
}

fun calculateBedtime(timeSleep: LocalTime, timeUp: LocalTime) :Long{
    val midNight = 60*24
    var minutesToAdd:Long = 0
    val bedTimeMinutes = timeSleep.toSecondOfDay()/60.toLong()
    if(bedTimeMinutes >= 720) {
        minutesToAdd = midNight - bedTimeMinutes
    }
    val hoursOfSleep = Duration.between(LocalTime.of(0,0), timeUp).plusMinutes(minutesToAdd)
    return hoursOfSleep.toHours()
}

fun nextOccurenceOfDay(dayOfWeek: Weekday?): LocalDate { //returns next occurence of day of week as a LocalDate
    val dayOfWeekStr = dayOfWeek.toString()
    val dayOfWeek = DayOfWeek.valueOf(dayOfWeekStr.toUpperCase())
    val today = LocalDate.now()
    var daysToAdd = dayOfWeek.value - today.dayOfWeek.value

    if (daysToAdd < 0) {
        // If it's earlier in the week, move to next week
        daysToAdd += 7
    }

    return today.plusDays(daysToAdd.toLong())
}

//Calculate at what time the user usually gets up
fun calculateAverageWakingUp(data: DatabaseObject):LocalTime{
    var totalWakeUpTime = 0
    var totalMemories = 0
    var averageWakeUpTime : LocalTime
    for (memory in data.memories!!){
        val wakeUp = LocalTime.parse(memory.time_of_waking_up).toSecondOfDay()/60
        totalWakeUpTime += wakeUp
        totalMemories ++
    }
    if (totalMemories > 0){
        val averageMinutes = (totalWakeUpTime/totalMemories).toLong()
        averageWakeUpTime = LocalTime.MIN.plus(averageMinutes, ChronoUnit.MINUTES);
    }
    else {
        averageWakeUpTime = LocalTime.of(8,0)
    }
    return averageWakeUpTime
}
