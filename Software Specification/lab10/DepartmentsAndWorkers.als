// C1
sig Worker {
	manager:  lone Worker, 
	dep: Department 
}

// C2
sig Department {
	parent: lone Department
}

// C3
one sig CEO in Worker {
}

// C4
fact ceo_no_manager {
	one { w:Worker | no w.manager }
	&& 
	no CEO.manager
}

// C5
fact no_self_management {
	all w:Worker | w !in w.^manager
}

// C6
fact no_parenting_loops {
	all d: Department | 
		d !in d.^parent
}

// C7
fact worker_manager_department {
	all w:Worker |
		w.manager.dep in w.dep.^parent + w.dep
	
}


// C8
fact all_departments_must_have_workers {
	all d:Department |
		some w: Worker |
			w.dep = d
}

assert a1 {
	all d:Department | 
		no d.parent implies CEO.dep = d 
}

//check a1 for 5 Worker, 2 Department

run {} for exactly 5 Worker, exactly 2 Department
