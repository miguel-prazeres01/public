package furhatos.app.sleepagent

import furhatos.app.sleepagent.flow.*
import furhatos.skills.Skill
import furhatos.flow.kotlin.*

class SleepagentSkill : Skill() {
    override fun start() {
            Flow().run(Init)
    }
}

fun main(args: Array<String>) {
    Skill.main(args)
}
