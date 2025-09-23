package furhatos.app.sleepagent.nlu

//import com.sun.tools.javac.jvm.Gen
import furhatos.nlu.ComplexEnumEntity
import furhatos.nlu.EnumEntity
import furhatos.nlu.Intent
import furhatos.nlu.ListEntity
import furhatos.nlu.common.Number
import furhatos.nlu.common.Time
import furhatos.util.Language

class Fruit : EnumEntity(stemming = true, speechRecPhrases = true) {
    override fun getEnum(lang: Language): List<String> {
        return listOf("banana", "orange", "apple", "cherimoya")
    }
}

class Gender : EnumEntity(stemming = true, speechRecPhrases = true) {
    override fun getEnum(lang: Language): List<String> {
        return listOf("male", "female", "other")
    }
}

class GetGender (var gender : Gender ? = null): Intent() {
    override fun getExamples(lang: Language): List<String> {
        return listOf("I am @gender", "@gender", "Prefer not to answer")
    }
}

class Weekday : EnumEntity(stemming = true, speechRecPhrases = true){
    override fun getEnum(lang: Language): List<String> {
        return listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday","Saturday","Sunday")
    }
}
class GetAdvice(var weekday : Weekday ? = null) : Intent() {
    override fun getExamples(lang: Language): List<String> {
        return listOf("@weekday","When should I go to sleep on @weekday", "Can I get sleeping advice for @weekday")
    }
}

class GetCurrentAdvice() : Intent() {
    override fun getExamples(lang: Language): List<String> {
        return listOf("I would like some advice","Advice", "Advice, please", "get advice")
    }
}

class GetAge(var age : Number ? = null) : Intent() {
    override fun getExamples(lang: Language): List<String> {
        return listOf("@age", "I am @age years old", "I am @age")
    }
}

class GetSleepGoal(var num : Number ? = null) : Intent() {
    override fun getExamples(lang: Language): List<String> {
        return listOf("@num","I would like to feel like a @num", "@num would be great")
    }
}

class GetReadyTime(var time : Time  ? = null) : Intent() {
    override fun getExamples(lang: Language): List<String> {
        return listOf("@time","I take @time", "I need @time to get ready", "I need @time to get ready in the morning")
    }
}

class GetReadyHours(var hours : Number ? = null) : Intent() {
    override fun getExamples(lang: Language): List<String> {
        return listOf("I take @hours hour", "I usually take @hours hour", "@hours hours", "I take @hours hours", "I usually take @hours hours")
    }
}

class GetReadyMinutes(var minutes : Number ? = null) : Intent() {
    override fun getExamples(lang: Language): List<String> {
        return listOf("I take @minutes minutes", "I usually take @minutes minutes",
            "@minutes minutes")
    }
}

class ProvideInformation : Intent() {
    override fun getExamples(lang: Language): List<String> {
        return listOf("I would like to talk about myself", "myself", "about myself, please", "talk about me", "about me")
    }
}

class Past : Intent() {
    override fun getExamples(lang: Language): List<String> {
        return listOf("Sleep in the past", "the past", "past", "Talk about the past", "past days", "experiences", "experiences past days" )
    }
}

class Future : Intent() {
    override fun getExamples(lang: Language): List<String> {
        return listOf("Upcoming plans", "upcoming plans", "plans", "Talk about upcoming plans", "upcoming plans, please")
    }
}

class Activity: EnumEntity(stemming = true, speechRecPhrases = true) {
    override fun getEnum(lang: Language): List<String> {
        return listOf("Work", "Sports", "met with friends", "Meeting", "Friends", "Family", "Family meeting", "have family time",
            "had dinner with friends", "Swimming", "Play sports", "went to work",
        "Went to university", "University", "School", "Went to school", "Went to work", "Went to the gym", "Went to the park", "Went to the cinema", "Went to the theater", "Went to the opera", "Went to the museum", "Went to the concert", "Went to the party", "Went to the club", "Went to the bar", "Went to the restaurant", "Went to the cafe", "Went to the beach", "Went to the pool", "Went to the zoo", "Went to the aquarium", "Went to the library", "Went to the mall", "Went to the store", "Went to the supermarket")
    }
}

class GetActivity(var activity: Activity ? = null, var day: Weekday ? = null, var timeBegin: Time ? = null,
                  var timeEnd: Time ? = null) : Intent() {
    override fun getExamples(lang: Language): List<String> {
        return listOf("I @activity on @day from @timeBegin to @timeEnd", "On @ day I @activity from @timeBegin to @timeEnd", "I did @activity on @day from @timeBegin to @timeEnd",
            "On @day I went to @activity from @timeBegin to @timeEnd","@activity on @day from @timeBegin to @timeEnd", "I did @activity on @day from @timeBegin to @timeEnd",
            "I went @activity on @day from @timeBegin until @timeEnd", "I am planning to do @activity on @day from @timeBegin to @timeEnd",
            "I am planning to go @activity on @day from @timeBegin to @timeEnd", "I am planning to go @activity on @day from @timeBegin until @timeEnd",
            "On @day I have @activity from @timeBegin to @timeEnd", "On @weekday I am going to @activity from @timeBegin until @timeEnd",
            "I am going to @activity on @day from @timeBegin to @timeEnd"
        )
    }
}

class GetTirednessAndIrritability(var tiredness: Number ? = null, var irritability: Number ? = null) : Intent() {
    override fun getExamples(lang: Language): List<String> {
        return listOf("@tiredness and @irritability", "@tiredness for tiredness and @irritability for irritability", "@tiredness + @irritability", "@tiredness out of 10 and @irritability out of 10", "@tiredness and @irritability out of 10", "@tiredness tired and @irritability irritable")
    }
}

class GetBedTimeAndWakeUpTime(var bedtime: Time ? = null, var wakeUpTime: Time ? = null) : Intent() {
    override fun getExamples(lang: Language): List<String> {
        return listOf("I went to bed at @bedtime and woke up at @wakeUpTime", "I slept from @bedtime to @wakeUpTime", "I fell asleep at @bedtime and woke up at @wakeUpTime")
    }
}

class GetStressDayRating(var stress: Number ? = null) : Intent() {
    override fun getExamples(lang: Language): List<String> {
        return listOf("The day was a @stress on stress level", "I felt like a @stress", "I was stressed on a number @stress level", "It was a @stress",
            "I'm planning to have a @stress", "It is a @stress", "@stress", "I would rate it a @stress out of 10", "I would rate it a @stress", "It was a @stress out of 10", "It will be @stress out of 10")
    }
}

class BuyFruit(var fruits : FruitList? = null) : Intent() {
    override fun getExamples(lang: Language): List<String> {
        return listOf("@fruits", "I want @fruits", "I would like @fruits", "I want to buy @fruits")
    }
}

class RequestOptions: Intent() {
    override fun getExamples(lang: Language): List<String> {
        return listOf("What options do you have?",
            "What fruits do you have?",
            "What are the alternatives?",
            "What do you have?")
    }
}

class FruitList : ListEntity<QuantifiedFruit>()

class QuantifiedFruit(
    var count : Number? = Number(1),
    var fruit : Fruit? = null) : ComplexEnumEntity() {
    override fun getEnum(lang: Language): List<String> {
        return listOf("@count @fruit", "@fruit")
    }

    override fun toText(): String {
        return generate("$count " + if (count?.value == 1) fruit?.value else "${fruit?.value}" + "s")
    }
}