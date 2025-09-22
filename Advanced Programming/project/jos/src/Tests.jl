include("proj.jl")

##2.1##
@defclass(ComplexNumber, [], [real , imag])

##2.2##
c1 = new(ComplexNumber, real = 1, imag = 2)

c1.real

##2.3##

getproperty(c1,:real)
c1.real
setproperty!(c1,:imag,-1)
c1.imag += 3

##2.4##

@defgeneric add(a, b)
@defmethod add(a::ComplexNumber, b::ComplexNumber) = new(ComplexNumber, real=(a.real + b.real), imag=(a.imag + b.imag))

##2.5## 

c2 = new(ComplexNumber, real=3, imag=4)   #<ComplexNumber Azn2z9XrGHH>
c1
c2
add(c1, c2)

##2.6##

class_of(c1) === ComplexNumber
ComplexNumber.direct_slots
class_of(class_of(c1)) === Class 
class_of(class_of(class_of(c1))) === Class

Class.slots
ComplexNumber.name
ComplexNumber.direct_superclasses == [Object]
add                #<GenericFunction add with 1 methods>
class_of(add) === GenericFunction

class_of(add.methods[1]) == MultiMethod

add.methods[1]
add.methods[1].generic_function === add

##2.7##

@defclass(UndoableClass, [Class], [])
@defclass(Person, [],[[name, reader=get_name, writer=set_name!],[age, reader=get_age, writer=set_age!, initform=0],[friend, reader=get_friend, writer=set_friend!]],metaclass=UndoableClass)
Person           #<UndoableClass Person>
@defmethod print_object(class::Class, io) = print(io, "<$(class_name(class_of(class))) $(class_name(class))>")
class_of(Person)   #<Class UndoableClass>
class_of(class_of(Person))   #<Class Class>
Person.initforms

##2.8##
get_age(new(Person))
get_name(new(Person))

##2.9##
add(123, 456)   #ERROR: No applicable method for function add with arguments (123, 456)


##2.10##

@defclass(Shape, [], [])
@defclass(Device, [], [])
@defgeneric draw(shape, device)
@defclass(Line, [Shape], [from, to])
@defclass(Circle, [Shape], [center, radius])
@defclass(Screen, [Device], [])
@defclass(Printer, [Device], [])
@defmethod draw(shape::Line, device::Screen) = println("Drawing a Line on Screen")
@defmethod draw(shape::Circle, device::Screen) = println("Drawing a Circle on Screen")
@defmethod draw(shape::Line, device::Printer) = println("Drawing a Line on Printer")
@defmethod draw(shape::Circle, device::Printer) = println("Drawing a Circle on Printer")

let devices = [new(Screen), new(Printer)],
    shapes = [new(Line), new(Circle)]
    for device in devices
        for shape in shapes
            draw(shape,device)
        end
    end
end



##2.11##
@defclass(ColorMixin, [],[[color, reader=get_color, writer=set_color!]])
@defmethod draw(s::ColorMixin, d::Device) =
let previous_color = get_device_color(d)
    set_device_color!(d, get_color(s))
    call_next_method()
    set_device_color!(d, previous_color)
end
@defclass(ColoredLine, [ColorMixin, Line], [])
@defclass(ColoredCircle, [ColorMixin, Circle], [])

@defclass(ColoredPrinter, [Printer],[[ink=:black, reader=get_device_color, writer=_set_device_color!]])
@defmethod set_device_color!(d::ColoredPrinter, color) = 
    begin
        println("Changing printer ink color to $color")
        _set_device_color!(d, color)
    end

let shapes = [new(Line), new(ColoredCircle, color=:red), new(ColoredLine, color=:blue)],printer = new(ColoredPrinter, ink=:black)
    for shape in shapes
        draw(shape, printer)
    end
end




##2.12##
ColoredCircle.direct_superclasses  #[<Class ColorMixin>, <Class Circle>]
ans[1].direct_superclasses   #[<Class Object>]
ans[1].direct_superclasses  #[<Class Top>]
ans[1].direct_superclasses  #[]

##2.13##
@defclass(A, [], [])
@defclass(B, [], [])
@defclass(C, [], [])
@defclass(D, [A, B], [])
@defclass(E, [A, C], [])
@defclass(F, [D, E], [])

compute_cpl(F)   #[<Class F>, <Class D>, <Class E>,<Class A>, <Class B>, <Class C>,<Class Object>, <Class Top>]

##2.14##
class_of(1)   #<BuiltInClass _Int64>
class_of("Foo")   #<BuiltInClass _String>

@defmethod add(a::_Int64, b::_Int64) = a + b
@defmethod add(a::_String, b::_String) = a * b

add(1, 3)
add("Foo", "Bar")

##2.15##
class_name(Circle)  #:Circle
class_direct_slots(Circle)  #[:center, :radius]
class_direct_slots(ColoredCircle)  #[]
class_slots(ColoredCircle)  #[:color, :center, :radius]
class_direct_superclasses(ColoredCircle) #[<Class ColorMixin>, <Class Circle>]
class_cpl(ColoredCircle)  #[<Class ColoredCircle>, <Class ColorMixin>, <Class Circle>,<Class Object>, <Class Shape>, <Class Top>]
generic_methods(draw) #[<MultiMethod draw(ColorMixin, Device)>, <MultiMethod draw(Circle, Printer)>,<MultiMethod draw(Line, Printer)>, <MultiMethod draw(Circle, Screen)>,<MultiMethod draw(Line, Screen)>]
method_specializers(generic_methods(draw)[1]) #[<Class ColorMixin>, <Class Device>]


##2.16.1##
new(class; initargs...) =
    let instance = allocate_instance(class)
    initialize(instance, initargs)
    instance
end

@defclass(CountingClass, [Class],[counter=0])

@defmethod allocate_instance(class::CountingClass) = begin
    class.counter += 1
    call_next_method()
    end

@defclass(Foo, [], [], metaclass=CountingClass)
@defclass(Bar, [], [], metaclass=CountingClass)

new(Foo)
new(Foo)
new(Bar)

Foo.counter
Bar.counter

##2.16.2##
@defclass(Foo, [], [a=1, b=2])

@defclass(Bar, [], [b=3, c=4])

@defclass(FooBar, [Foo, Bar], [a=5, d=6])

compute_slots(FooBar)

foobar1 = new(FooBar)

foobar1.a

foobar1.b

foobar1.c

foobar1.d

@defclass(AvoidCollisionsClass, [Class], [])

@defmethod compute_slots(class::AvoidCollisionsClass) =
let slots = call_next_method(),
        duplicates = symdiff(slots, unique(slots))
    isempty(duplicates) ?
        slots :
        error("Multiple occurrences of slots: $(join(map(string, duplicates), ", "))")
end

@defclass(FooBar2, [Foo, Bar], [a=5, d=6], metaclass=AvoidCollisionsClass)

## compute_slots is not called when a class is defined, but it works ##
compute_slots(FooBar2)



##2.16.3##

undo_trail = []

store_previous(object, slot, value) = push!(undo_trail, (object, slot, value))

current_state() = length(undo_trail)

restore_state(state) =
    while length(undo_trail) != state
        restore(pop!(undo_trail)...)
    end
save_previous_value = true

restore(object, slot, value) =
let previous_save_previous_value = save_previous_value
    global save_previous_value = false
    try
        setproperty!(object, slot, value)
    finally
        global save_previous_value = previous_save_previous_value
    end
end

@defclass(UndoableClass, [Class], [])

@defmethod compute_getter_and_setter(class::UndoableClass, slot, idx) =
let (getter, setter) = call_next_method()
    (getter,
        (o, v)->begin
            if save_previous_value
                store_previous(o, slot, getter(o))
            end
            setter(o, v)
            end)
end



@defclass(Person, [],
    [name, age, friend],
    metaclass=UndoableClass)

    
## Only works if these getters/setters are used ##
(getname, setname) = compute_getter_and_setter(Person,:name,0)
(getage, setage) = compute_getter_and_setter(Person,:age,0)
(getfriend, setfriend) = compute_getter_and_setter(Person,:friend,0)

@defmethod print_object(p::Person, io) =
    print(io, "[$(p.name), $(p.age)$(ismissing(p.friend) ? "" : " with friend $(p.friend)")]")

#get_slot1(Person, :age)

p0 = new(Person, name="John", age=21)
p1 = new(Person, name="Paul", age=23)
#Paul has a friend named John

## Used these functions and not the "dot" ones ##
getage(p0)
setfriend(p1, p0)

p1.friend = p0

println(p1) #[Paul,23 with friend [John,21]]
state0 = current_state()
#32 years later, John changed his name to 'Louis' and got a friend
setage(p0, 53)
p0.age = 53

setage(p1, 55)
p1.age = 55

setname(p0, "Louis")
p0.name = "Louis"

setfriend(p0, new(Person, name="Mary", age=19))

p0.friend = new(Person, name="Mary", age=19)
println(p1) #[Paul,55 with friend [Louis,53 with friend [Mary,19]]]
state1 = current_state()
#15 years later, John (hum, I mean 'Louis') died

setage(p1, 70)
p1.age = 70

setfriend(p1, missing)
p1.friend = missing
println(p1) #[Paul,70]
#Let's go back in time
restore_state(state1)
println(p1) #[Paul,55 with friend [Louis,53 with friend [Mary,19]]]
#and even earlier
restore_state(state0)
println(p1) #[Paul,23 with friend [John,21]]



##2.16.4##

@defclass(FlavorsClass, [Class], [])

@defmethod compute_cpl(class::FlavorsClass) =
let depth_first_cpl(class) =
    [class, foldl(vcat, map(depth_first_cpl, class_direct_superclasses(class)), init=[])...],
    base_cpl = [Object, Top]
    vcat(unique(filter(!in(base_cpl), depth_first_cpl(class))), base_cpl)
end


@defclass(A, [], [], metaclass=FlavorsClass)
@defclass(B, [], [], metaclass=FlavorsClass)
@defclass(C, [], [], metaclass=FlavorsClass)
@defclass(D, [A, B], [], metaclass=FlavorsClass)
@defclass(E, [A, C], [], metaclass=FlavorsClass)
@defclass(F, [D, E], [], metaclass=FlavorsClass)

compute_cpl(F)


## 2.17 ##

@defclass(UndoableCollisionAvoidingCountingClass,
[UndoableClass, AvoidCollisionsClass, CountingClass],
[])


@defclass(NamedThing, [], [name])

@defclass(Person, [NamedThing],
[name, age, friend],
metaclass=UndoableCollisionAvoidingCountingClass)

compute_slots(Person)

@defclass(Person, [NamedThing],
[age, friend],
metaclass=UndoableCollisionAvoidingCountingClass)

@defmethod print_object(p::Person, io) =
print(io, "[$(p.name), $(p.age)$(ismissing(p.friend) ? "" : " with friend $(p.friend)")]")




########################### EXTRA TESTS #######################


## Used to test the :around, :before and :around features ##
@defauxmethod print_object(:before,class::ComplexNumber, io) = print(io, "Complex number ")
@defauxmethod print_object(:before,class::Class, io) = print(io, "This is Generic ")
@defauxmethod print_object(:after,class::ComplexNumber, io) = print(io, " This is After Method for complex number")

@defgeneric add(a, b)  #remove , done before
@defmethod add(a::ComplexNumber, b::ComplexNumber) = new(ComplexNumber, real=(a.real + b.real), imag=(a.imag + b.imag))  #remove , done before
c1 = new(ComplexNumber, real = 1, imag = 2) #remove , done before
c2 = new(ComplexNumber, real=3, imag=4) #remove , done before
@defmethod add(a::ComplexNumber, b::ComplexNumber) = println(" This is a duplicate")    #shouldn't allow
@defauxmethod add(:before,a::ComplexNumber, b::ComplexNumber) = println(" The result of the add is: ")
#@defauxmethod add(:before,a::ComplexNumber, b::ComplexNumber) = 1+1
@defauxmethod add(:after,a::ComplexNumber, b::ComplexNumber) =     #check if it's made after calculating c3
begin
    a.real+=1
end
@defauxmethod add(:around,a::ComplexNumber, b::ComplexNumber) = 
begin
    println("Entered the around")
    call_next_method()
end

add(c1, c2)
c1
c2
