package furhatos.app.sleepagent.flow.main

import furhatos.app.sleepagent.nlu.FruitList
import furhatos.records.User

class FruitData {
    var fruits : FruitList = FruitList()
}

val User.order : FruitData
    get() = data.getOrPut(FruitData::class.qualifiedName, FruitData())