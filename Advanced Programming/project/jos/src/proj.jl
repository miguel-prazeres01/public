
## Base Structures ## 
mutable struct GenericClass 
    slots::Dict{Any,Any} 
end

mutable struct Instance                                                                 
    slots::Dict{Any,Any}                                                        
end

## Function only use to bootstrap the initial classes ##
function make_class_initial(name,supers,direct_slots) 
    final_slots = Dict()
    slots = []

    for x in direct_slots
        final_slots[x]=nothing
    end

    for x in supers
        if hasproperty(x,:slots)
            for y in x.slots
                push!(slots,y) 
            end
        end                                               
    end
    
    for x in direct_slots
        if !(x in slots)
        push!(slots,x)
        end
    end

    final_slots[:name] = name
    final_slots[:direct_superclasses] = supers
    final_slots[:direct_slots] = direct_slots
    final_slots[:slots] = slots
    final_slots[:class_of] = nothing
    final_slots[:initforms] = Dict()
    final_slots[:metaclass] = nothing

    class = GenericClass(final_slots)

    return class
end


######### Slot access protocols for Classes and Objects ##########
function get_slot(obj, slot)
    if !(slot in keys(getfield(obj,:slots)))
        error(string("Slot ",slot," is missing"))
    end
    sl = getfield(obj,:slots)[slot]
    if sl===nothing
        error(string("Slot ",slot," is unbound"))
    end
    sl
end

function Base.getproperty(obj::Instance, sym::Symbol)    
    return get_slot(obj, sym)
end

function Base.getproperty(obj::GenericClass, sym::Symbol)
    return get_slot(obj, sym)
end

function set_slot!(obj, slot, val)
    sl = nothing
    if !(slot in keys(getfield(obj,:slots)))
        error(string("Slot ",slot," is missing"))
    end
    getfield(obj,:slots)[slot]=val
    sl = getfield(obj,:slots)[slot]
    if sl===nothing
        error(string("Slot ",slot," is unbound"))
    end
    sl
end

function Base.setproperty!(value::Instance, name::Symbol, x)
    return set_slot!(value,name,x)
end

function Base.setproperty!(value::GenericClass, name::Symbol, x)
    return set_slot!(value,name,x)
end


################ BOOTSTRAP OF INITIAL METACLASSES #####################
global Top = make_class_initial(:Top, [], [])

global Object = make_class_initial(:Object, [Top], [])

global Class = make_class_initial(:Class, [Object], [:name, :direct_superclasses, :direct_slots, :class_of, :initforms])

Top.class_of = Class
Top.metaclass = Class

Object.class_of = Class
Object.metaclass = Class

Class.class_of = Class
Class.metaclass = Class


## Function used to create new classes ##
function make_class(name,supers,direct_slots,metaclass,initforms=[])  
    final_slots = Dict()
    meta_slots = []
    slots = []

    for x in direct_slots
        final_slots[x]=nothing
    end

    if isempty(supers)
        supers = [Object]
    end

    for x in direct_slots
        if !(x in slots)
            push!(slots,x)
        end
    end

    if metaclass != [] && hasproperty(metaclass,:slots)
        for y in metaclass.slots
            if !(y in meta_slots)
                push!(meta_slots,y) 
            end
            if !(y in slots)
                if metaclass != [] && y in keys(metaclass.initforms)
                    final_slots[y] = metaclass.initforms[y]
                end
            end
        end
    else
        for y in Class.slots
            if !(y in meta_slots)
                push!(meta_slots,y) 
            end
        end
    end  


    final_slots[:name] = name
    final_slots[:direct_superclasses] = supers
    final_slots[:direct_slots] = direct_slots
    final_slots[:slots] = slots
    final_slots[:initforms] = Dict()
    final_slots[:class_of] = nothing
    final_slots[:meta_slots] = meta_slots

    class = GenericClass(final_slots)

    if !isempty(initforms)
        for form in initforms
            class.initforms[form.first] = form.second
        end
    end

    if !(metaclass == [])
        class.class_of = metaclass                                             

    else
        class.class_of = Class
    end

    return class
end


#################### CLASSES ########################

## Macro used to create new classes ##
macro defclass(classname,super_classes,args,metaclass=[])
    arg_names = []
    readers_names = []
    writers_names = []
    initform = Dict()
    arrays = []
    symbols = []
    initforms = []

    if length(args.args) > 0
  
        arrays = map(x -> if hasproperty(x, :head) &&  x.head == :vect x end, args.args)
        symbols = map(x -> if typeof(x) == Symbol x end, args.args)
        initforms = map(x -> if x isa Expr x end, args.args)
    
        
        filter!(x -> x !== nothing, arrays)
        filter!(x -> x !== nothing, symbols)
        filter!(x -> x !== nothing, initforms)

        if !isempty(arrays)
            for vect in arrays
                for el in vect.args
                    if el isa Symbol
                        push!(arg_names, el)
                    end
                    if el isa Expr
                        if el.args[1] == :reader
                            push!(readers_names, el.args[2])
                        elseif el.args[1]  == :writer
                            push!(writers_names, el.args[2])
                        elseif el.args[1] == :initform
                            initform[arg_names[end]] = el.args[2]
                        else 
                            push!(arg_names, el.args[1])
                            initform[el.args[1]] = el.args[2]
                        end
                    end
                end
            end
        elseif !isempty(symbols)
            arg_names = symbols
        elseif !isempty(initforms)
            for arg in initforms
                push!(arg_names, arg.args[1])
                initform[arg.args[1]] = arg.args[2]
            end

        end
    else
        return esc( :(global $(classname) = make_class($(QuoteNode(classname)), :($$(super_classes)), $arg_names, $metaclass)))
    end

    accessors = []

    for i in 1:length(arg_names)
        if isempty(readers_names)
            break
        end
        if readers_names[i] !== nothing 
            push!(accessors,create_acessor(arg_names[i], readers_names[i], writers_names[i], classname))
        end
    end

    return quote
        $(accessors...)
      
        global $(classname) = make_class($(QuoteNode(classname)), $super_classes, $arg_names, $metaclass, $initform)

        return $classname
    end
end

## Function to create new accessors to a new class ##
function create_acessor(name, getter, setter, classname)
    return quote
        @defgeneric $(getter)(o) 
        @defgeneric $(setter)(o,v)
        @defmethod $(getter)(o::$(classname)) = o.$(name)
        @defmethod $(setter)(o::$(classname),v) = o.$(name) = v
    end
end


####################### INTROSPECTION #########################


# Class Precedence List
# TODO: implement class precedence list protocol 

function compute_cpl_init(class::GenericClass)
    visited = []
    classes = [class]

    while !isempty(classes)
        curr_class = popfirst!(classes)
        direct_superclasses = curr_class.direct_superclasses
        if !(curr_class in visited)
            push!(visited,curr_class)
        end
        append!(classes,direct_superclasses)
        unique!(classes)
    end
    return visited
end


    
function class_of(instance)
    if typeof(instance) === Int64
        return _Int64
    elseif typeof(instance) === Int32
        return _Int32
    elseif typeof(instance) === Bool
        return _Bool 
    elseif typeof(instance) === Float64
        return _Float64
    elseif typeof(instance) === Symbol
        return _Symbol
    elseif typeof(instance) === Char
        return _Char
    elseif typeof(instance) === String
        return _String
    elseif typeof(instance) === MultiMethod
        return MultiMethod
    elseif typeof(instance) === GenericFunction
        return GenericFunction
    elseif typeof(instance) === Instance
        return instance.class_of
    elseif typeof(instance) === GenericClass
        return instance.class_of
    else
        return _IO
    end
end


function class_name(class)
    return class.name
end

function class_direct_slots(class)
    return class.direct_slots
end

function class_direct_superclasses(class)
    return class.direct_superclasses
end

function generic_methods(generic_function)
    return reverse(generic_function.methods)
end

function class_initforms(class)
    return class.initforms
end

function method_specializers(method)
    args_types = []
        for type in method.specializers
            push!(args_types, eval(type))
        end
    return args_types
end

######################## OBJECTS #############################

## Julia functions to create objects ##

function allocate_instance_init(class)
    slots = vcat(map(class_direct_slots, compute_cpl_init(class))...)
    initforms = merge(map(class_initforms, compute_cpl_init(class))...)

    dict = Dict()
    for x in slots
        dict[x]=missing
        if x in keys(initforms)
            dict[x] = initforms[x]
        end
    end
    dict[:class_of] = class
    dict[:direct_superclasses] = class.direct_superclasses 
    return Instance(dict)
end

function initialize_init(instance, initargs)
    for x in initargs
        slot = x.first
        value = x.second
        setproperty!(instance, :($slot), value)                                            
    end
end

function new_init(class; initargs...)
    let instance = allocate_instance_init(class)
        initialize_init(instance,initargs)
        instance
    end
end


###################### METHODS ###################################


## Classes that represent Generic Functions and Methods ##
@defclass(MultiMethod, [], [specializers, procedure, generic_function])
## Befores, afters and arounds to support method combination ##
@defclass(GenericFunction, [], [name,methods,args,befores,afters,arounds])



function make_method(functionname, args, lambda, identifier)
    if length(args) != length(functionname.args)
        error(string("Incorrect Parameter Number, ",functionname.name," takes ",length(functionname.args), " parameters"))
    end

    ## Check if a primary method is created before creating a secondary method (Method combination) ##
    primary=false
    for method in functionname.methods
        if method.specializers == args
            primary=true
            if identifier ==:primary
                error(string("There is already a method defined for specializers ",args," "))
            end
        end
    end


    if !primary && identifier !=:primary
        error(string("Unable to create auxiliary methods before defining primary methods. Use @defmethod"))
    end

    specmethod= new_init(MultiMethod, specializers=args, procedure=lambda, generic_function=functionname)

    ## Used to support method combination ##
    if identifier==:before
        push!(functionname.befores,specmethod)
    elseif identifier==:after
        push!(functionname.afters,specmethod)
    elseif identifier==:around
        push!(functionname.arounds,specmethod)
    else
        push!(functionname.methods,specmethod)
    end

    return specmethod
end



################### GENERIC FUNCTIONS ########################

function make_generic(name, args)
    return new_init(GenericFunction, name=name,methods =[],befores=[],afters=[],arounds=[], args=args)
end

macro defgeneric(expr)
    name = expr.args[1]
    parameters = []
    for parameter in expr.args[2:end]
        push!(parameters, parameter)
    end
    nameQuoted = QuoteNode(name)
    esc( 
        :( global $name = make_generic($nameQuoted,$parameters)))
end


macro defmethod(expr)
    name = expr.args[1].args[1]
    parameters =  expr.args[1].args[2:end]
    body = expr.args[2]
    parameters_and_types = []
    parameters_names = ()

    for x in parameters
        if x isa Symbol
            push!(parameters_and_types ,Object)
            parameters_names = (parameters_names..., x)
        else
            push!(parameters_and_types, x.args[2])
            parameters_names = (parameters_names..., x.args[1])
        end
    end

    return quote
        try 
            make_method($(name), $parameters_and_types, ($(parameters_names...),) -> ($body), :primary)
        catch e
            if isa(e, UndefVarError)
               @defgeneric $(name)($(parameters_names...),)
               make_method($(name), $parameters_and_types, ($(parameters_names...),) -> ($body), :primary)
            else
                throw(e)
            end
        end
    end
end


@defmethod compute_cpl(class::Top) = 
begin   
    visited = []
    classes = [class]

    while !isempty(classes)
        curr_class = popfirst!(classes)
        direct_superclasses = curr_class.direct_superclasses
        if !(curr_class in visited)
            push!(visited,curr_class)
        end
        append!(classes,direct_superclasses)
        unique!(classes)
    end
    visited
end

function class_cpl(class)
    return compute_cpl_init(class)
end


@defmethod compute_slots(class::Class) = vcat(map(class_direct_slots, class_cpl(class))...)

function class_slots(class)
    return compute_slots(class)
end


@defmethod allocate_instance(class::Class) = 
begin
    slots = compute_slots(class)
    initforms = merge(map(class_initforms, class_cpl(class))...)

    dict = Dict()
    for x in slots
        dict[x]=missing
        if x in keys(initforms)
            dict[x] = initforms[x]
        end
    end
    dict[:class_of] = class
    dict[:direct_superclasses] = class.direct_superclasses 
    Instance(dict)
end

@defmethod initialize(object::Object, initargs) =
begin
    for x in initargs
        slot = x.first
        value = x.second
        setproperty!(object, :($slot), value)                                            
    end
end

@defmethod initialize(class::Class, initargs) =
begin
    for x in initargs
        slot = x.first
        value = x.second
        setproperty!(class, :($slot), value)                                            
    end
end

@defmethod initialize(generic::GenericFunction, initargs) =
begin
    for x in initargs
        slot = x.first
        value = x.second
        setproperty!(generic, :($slot), value)                                            
    end
end

@defmethod initialize(method::MultiMethod, initargs) =
begin
    for x in initargs
        slot = x.first
        value = x.second
        setproperty!(method, :($slot), value)                                            
    end
end


## Function used in the MOPs ##
function new(class; initargs...)
    let instance = allocate_instance(class)
        initialize(instance,initargs)
        instance
    end
end



##################### BUILTINCLASSES #########################


@defclass(BuiltInClass, [Class], [type])
@defclass(_Int64, [], [], metaclass = BuiltInClass)
@defclass(_String, [],[], metaclass = BuiltInClass)
@defclass(_Bool, [],[], metaclass = BuiltInClass)
@defclass(_Float64, [],[], metaclass = BuiltInClass)
@defclass(_Symbol, [],[], metaclass = BuiltInClass)
@defclass(_Char, [],[], metaclass = BuiltInClass)
@defclass(_IO, [],[], metaclass = BuiltInClass)



###################### MULTIPLE DISPATCH #######################

function find_applicable_methods(methods::Array, args)
	let list = []
		for method in methods
            		specializers = method.specializers
			applicable = true
			index = 1
			while index <= length(args)
                		precedence = compute_cpl_init(class_of(args[index]))
                		names = map(x -> x.name, precedence)

                        if !((specializers[index] in names) || (Object == specializers[index]))
                    			applicable = false
                    			break
                		end
				index += 1
			end
			if applicable
				push!(list, method)
			end
		end
		sort(list, lt=(x,y)->is_more_specific(x, y, args))
        
	end
end

function is_more_specific(m1::Instance, m2::Instance, args)
	let index = 1
        n1 = m1.specializers
        n2 = m2.specializers

		while index <= length(args)
			if n1[index] != n2[index]
				precedence_list = map(x -> x.name, compute_cpl_init(class_of(args[index])))
				return findfirst(x -> x == n1[index], precedence_list) < findfirst(x -> x == n2[index], precedence_list)
			end
			index += 1
		end
	end
	true
end


function (g::Instance)(args...)
	if length(g.methods) == 0
		error("No defined methods for generic function ", g.name)
	elseif length(args) != length(g.args)
		error("Generic function needs ", length(g.args), " argument(s)")
	else
        applicables = find_applicable_methods(g.methods, args)        #if no around returns value, checks primary methods

        aroundmethods = find_applicable_methods(g.arounds, args)      #start by computing applicable around methods
        
        if length(aroundmethods) != 0
            func=aroundmethods[1]#.procedure(args...)
            aroundmethods = aroundmethods[2:end]
            prepend!(aroundmethods, applicables)
            push!(current_function, aroundmethods => args)         #pushing all other applicable around methods
            return func.procedure(args...)
        end
        
		if length(applicables) == 0
			no_applicable_method(g,args)
		end

        if !isempty(applicables[2:end])
            push!(current_function, applicables[2:end] => args) 
        end
        beforereturn=nothing
        beforemethods = find_applicable_methods(g.befores, args)
        if length(beforemethods) != 0
            beforereturn=beforemethods[1].procedure(args...)
        end
        
        primaryreturn=applicables[1].procedure(args...)

        afterreturn=nothing
        aftermethods = find_applicable_methods(g.afters, args)
        if length(aftermethods) != 0
            afterreturn=aftermethods[1].procedure(args...)
        end

        return primaryreturn
	end
end


############### AUXILIARY FUNCTIONS TO MULTIPLE DISPATCH ##############


function no_applicable_method(gf::Instance, args)
    error("No applicable method for function $(class_name(gf)) with arguments $(string(args))")
end

global current_function = []

function call_next_method()
    if current_function != []

        func = (current_function[end].first[1]).procedure((current_function[end].second)...)
        new_current_function = current_function[end].first[2:end] => current_function[end].second 

        global current_function[end] = new_current_function
        if func !== nothing
            func
        end
    end
end



################### CLASSES AND OBJECTS DISPLAY ##################

@defgeneric print_object(obj, io)


@defmethod print_object(obj::Object, io) = print(io, "<$(class_name(class_of(obj))) $(string(objectid(obj), base=62))>")
@defmethod print_object(class::Class, io) =  print(io, "<$(class_name(class_of(class))) $(class_name(class))>")
@defmethod print_object(obj::MultiMethod, io) =  
begin
    args_types = []
        for type in obj.specializers
            push!(args_types, class_name(eval(type)))
            push!(args_types, ", ")
        end
        pop!(args_types)
        print(io, "<$(class_name(class_of(obj))) $(class_name(obj.generic_function))($(args_types...))>")
end
@defmethod print_object(c::ComplexNumber, io) = print(io, "$(c.real)$(c.imag < 0 ? "-" : "+")$(abs(c.imag))i")
@defmethod print_object(obj::GenericFunction, io) = print(io, "<$(class_name(class_of(obj))) $(class_name(obj)) with $(length(obj.methods)) methods>")



function Base.show(io::IO,obj::GenericClass) 
    print_object(obj, io)
end

function Base.show(io::IO,obj::Instance) 
    print_object(obj, io)
end


## MOPs of slot access ##


@defmethod get_slot_func(obj::Class, slot) = 
begin
    x = obj
    func = (x) -> getfield(x,:slots)[slot]
    func
end

@defmethod get_slot_func(obj::Object, slot) = 
begin
    x = obj
    func = (x) -> getfield(x,:slots)[slot]
    func
end


@defmethod set_slot!_func(obj::Class, slot) = 
begin
    x = obj
    func = (x,v) -> getfield(x,:slots)[slot] = v
    func
end

@defmethod set_slot!_func(obj::Object, slot) = 
begin
    x = obj
    func = (x,v) -> getfield(x,:slots)[slot] = v
    func
end


@defmethod compute_getter_and_setter(class::Class, slot, idx) = 
begin
    return (get_slot_func(class,slot), set_slot!_func(class,slot))
end

@defmethod compute_getter_and_setter(class::Object, slot, idx) = 
begin
    return (get_slot1_func(class,slot), set_slot!1_func(class,slot))
end


