-- C1
sig Account {
	resourcesA: set Resource,
	users: set User
}

-- C2
sig User {
	resourcesU: set Resource
}

-- C3
sig Resource {
	parent: lone Resource
}

-- C4
fact C4 {
	all u:User,a1:Account,a2:Account |
		 u in a1.users && u in a2.users implies a1 = a2	
}

-- C5
fact C5 {
	all r:Resource,a1:Account,a2:Account |
		 r in a1.resourcesA && r in a2.resourcesA implies a1 = a2	
}

-- F1
fun childResources[r:Resource] : set Resource {
	{ rx:Resource | r in rx.^parent }
}

-- C6
fact C6 {
	all r1:Resource,u:User {
		r1 in u.resourcesU implies all r2:Resource | r2 in childResources[r1] implies r2 in u.resourcesU 
	}
}

-- C7 
fact C7 {
	all r1:Resource,r2:Resource,a1:Account,a2:Account | 
		r2.parent = r1 && r2 in a1.resourcesA && r1 in a2.resourcesA implies a1 = a2
}

-- C8
fact C8 {
	all r:Resource |
		r !in r.^parent
}

-- C9
fact C9 {
	all u:User,a1:Account,r:Resource,a2:Account |
		r in u.resourcesU && u in a1.users && r in a2.resourcesA implies a1 = a2
	//all a:Account, u:User | u in a.users implies u.resourcesU in a.resourcesA
}

-- C10
fact C10 {
	all a:Account |
		some u:User | u in a.users
}

run { some User.resourcesU } for 5
