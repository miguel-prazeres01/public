sig Worker {
    manager : lone Worker,
    department : one Department
}

sig Department {
    parent : lone Department
}

one sig CEO extends Worker {}

fact C4{
    all w:Worker | w != CEO implies some w1 : Worker | w1 = w.manager
}