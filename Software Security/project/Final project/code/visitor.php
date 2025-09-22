<?php

include '../PHP-Parser/vendor/autoload.php';
include 'state.php';

use PhpParser\Node;
use PhpParser\NodeVisitorAbstract;
use PhpParser\NodeFinder;
use PhpParser\NodeTraverser;

class MyVisitorIdeia extends NodeVisitorAbstract
{

    private $states = array();
    private $all_states = array();
    private $final = array();
    private $state_zero;
    private $traverser_zero;
    private $ast_zero;
    private $last_ast_while;
    private $last_traverser_while;

    private $patterns = array();
    private $i = 0;
    
    function __construct(array $patterns_aux, int $i_aux){
        $this->patterns = $patterns_aux;
        $this->i = $i_aux;
        $this->traverser_zero = new NodeTraverser;
           
        $tainted = array();
        $initialized = array();
        $sanitized = array();
        $this->state_zero = new MyState($tainted,$initialized,$sanitized, $this->patterns, $this->i);
        $this->traverser_zero->addVisitor($this->state_zero);

    }


    public function leaveNode(Node $node){
        if($node instanceof Node\Stmt\If_ || $node instanceof Node\Stmt\While_  ||  ($node instanceof Node\Stmt\ElseIf_ && count($node->stmts) != 0) 
        || ($node instanceof Node\Stmt\Else_ && $node->stmts != NULL)){
            if($node instanceof Node\Stmt\While_){
                $idx = 0;
                $ast = $this->last_ast_while;
                $array_aux = array();
                for ($j = 0 ; $j < count($this->all_states) ; $j++){
                    if($this->all_states[$j] == end($this->states)){
                        $idx = $j;

                    } 

                    //Visit all states inside
                    if($j >= $idx){
                        $state = $this->all_states[$j];
                        if(count($state->getTainted()) == 0 && count($state->getInitialized()) == 0 && count($state->getSanitized()) == 0 ){
                            continue;
                        }

                        $new_state = new MyState($state->getTainted(), $state->getInitialized(),$state->getSanitized(), $this->patterns, $state->getI());
                        $traverser = new NodeTraverser;
                        $traverser->addVisitor($new_state);

                        $ast = $traverser->traverse($ast);
                        array_push($this->final,$new_state);
                        array_push($array_aux, $new_state);
                    }
                }

                foreach($array_aux as $array){
                    array_push($this->all_states, $array);
                }

                
                $this->last_ast_while = $ast;
            }
        
            if($node instanceof Node\Stmt\If_){
                
                //added to help logic of initialized variables
                if ($node->else == NULL){
                    $tainted = array();
                    $initialized = array();
                    $sanitized = array();
                    $new_state = new MyState($tainted,$initialized,$sanitized, $this->patterns, $this->i);
                    array_push($this->all_states,$new_state);
                }
            }



            if(count($this->states) >= 1){
                array_push($this->final,array_pop($this->states));
            }
        }
    }

    public function enterNode(Node $node){
        $nodeFinder = new NodeFinder;

        if($node instanceof Node\Stmt\While_){
            

            if(count($this->states) == 0){
                $tainted = $this->state_zero->getTainted();
                $initialized = $this->state_zero->getInitialized();
                $sanitized = $this->state_zero->getSanitized();

                if($this->patterns[$this->i]['implicit'] == "yes"){
                    $guard = array();

                    //Adding tainted vars of guard
                    $tainted_variables = $nodeFinder->find($node->cond, function(Node $node) use ($tainted, $initialized)  {
                        if($node instanceof Node\Expr\Variable){
                            if (key_exists("$".$node->name, $tainted)){
                                return $node->name;
                            } else if (!key_exists("$". $node->name, $initialized)){ 
                                $node->setAttribute("tainted", "$". $node->name);
                                return $node->name;
                            }
                        } else if ($node instanceof Node\Expr\FuncCall){
                            if (key_exists($node->name->parts[0], $tainted)){
                                return $node->name->parts[0];
                            }
                        }
                    });
    
                    
                    foreach($tainted_variables as $tainted_var){
                        if ($tainted_var->hasAttribute("tainted")){
                            array_push($guard,$tainted_var->name);
                            $tainted[$tainted_var->getAttribute("tainted")] = $tainted_var->getAttribute("tainted"); 
                        }
                    }
                    if(count($tainted_variables) > 0){
                        $new_state = new MyState($tainted,$initialized,$sanitized, $this->patterns,$this->i,1, $guard);
                    } else {
                        $new_state = new MyState($tainted,$initialized,$sanitized, $this->patterns,$this->i);
                    }

                } else {
                    $new_state = new MyState($tainted,$initialized,$sanitized, $this->patterns,$this->i);
                }
                $traverser = new NodeTraverser;
                $traverser->addVisitor($new_state);
                $this->last_ast_while = $traverser->traverse($node->stmts);
                $this->last_traverser_while = $traverser;

        
    
                array_push($this->states,$new_state);
                array_push($this->all_states,$new_state);

            } else {

                $last_state = end($this->states);
                $tainted = $last_state->getTainted();
                $initialized = $last_state->getInitialized();
                $sanitized = $last_state->getSanitized();


                if($this->patterns[$this->i]['implicit'] == "yes"){
                    $guard = array();

                    //Adding tainted vars of guard
                    $tainted_variables = $nodeFinder->find($node->cond, function(Node $node) use ($tainted, $initialized)  {
                        if($node instanceof Node\Expr\Variable){
                            if (key_exists("$".$node->name, $tainted)){
                                return $node->name;
                            } else if (!key_exists("$". $node->name, $initialized)){ 
                                $node->setAttribute("tainted", "$". $node->name);
                                return $node->name;
                            }
                        } else if ($node instanceof Node\Expr\FuncCall){
                            if (key_exists($node->name->parts[0], $tainted)){
                                return $node->name->parts[0];
                            }
                        }
                    });
    
                    
                    foreach($tainted_variables as $tainted_var){
                        if ($tainted_var->hasAttribute("tainted")){
                            array_push($guard,$tainted_var->name);
                            $tainted[$tainted_var->getAttribute("tainted")] = $tainted_var->getAttribute("tainted"); 
                        }
                    }
                    if(count($tainted_variables) > 0){
                        $new_state = new MyState($tainted,$initialized,$sanitized, $this->patterns,$this->i,1, $guard);
                    } else {
                        $new_state = new MyState($tainted,$initialized,$sanitized, $this->patterns,$this->i);
                    }

                } else {
                    $new_state = new MyState($tainted,$initialized,$sanitized, $this->patterns,$this->i);
                }

                $traverser = new NodeTraverser;
                $traverser->addVisitor($new_state);
                $this->last_ast_while = $traverser->traverse($node->stmts);
                $this->last_traverser_while = $traverser;

        
                array_push($this->states, $new_state);
                array_push($this->all_states,$new_state);
            }

        }


        if($node instanceof Node\Stmt\If_){
            if(count($this->states) == 0){
                $tainted = $this->state_zero->getTainted();
                $initialized = $this->state_zero->getInitialized();
                $sanitized = $this->state_zero->getSanitized();

                if($this->patterns[$this->i]['implicit'] == "yes"){
                    $tainted_variables = $nodeFinder->find($node->cond, function(Node $node) use ($tainted, $initialized)  {
                        if($node instanceof Node\Expr\Variable){
                            if (key_exists("$".$node->name, $tainted)){
                                return $node->name;
                            } else if (!key_exists("$". $node->name, $initialized)){ 
                                $node->setAttribute("tainted", "$". $node->name);
                                return $node->name;
                            }
                        } else if ($node instanceof Node\Expr\FuncCall){
                            if (key_exists($node->name->parts[0], $tainted)){
                                return $node->name->parts[0];
                            }
                        }
                    });
    
                    
                    foreach($tainted_variables as $tainted_var){
                        if ($tainted_var->hasAttribute("tainted")){
                            $tainted[$tainted_var->getAttribute("tainted")] = $tainted_var->getAttribute("tainted"); 
                        }
                    }
    
    
    
                    if(count($tainted_variables) > 0){
                        $new_state = new MyState($tainted,$initialized,$sanitized, $this->patterns, $this->i,1);
                    } else {
                        $new_state = new MyState($tainted,$initialized,$sanitized, $this->patterns, $this->i);
                    }

                } else {
                    $new_state = new MyState($tainted,$initialized,$sanitized, $this->patterns, $this->i);
                }

                

                
                $traverser = new NodeTraverser;
                $traverser->addVisitor($new_state);
                $ast = $traverser->traverse($node->stmts);

    
                array_push($this->states,$new_state);
                array_push($this->all_states,$new_state);
            } else {
                $last_state = end($this->states);
                $tainted = $last_state->getTainted();
                $initialized = $last_state->getInitialized();


                if($this->patterns[$this->i]['implicit'] == "yes"){
                    $tainted_variables = $nodeFinder->find($node->cond, function(Node $node) use ($tainted, $initialized)  {
                        if($node instanceof Node\Expr\Variable){
                            #var_dump($node);
                            if (key_exists("$".$node->name, $tainted)){
                                return $node->name;
                            } else if (!key_exists("$". $node->name, $initialized)){ 
                                $node->setAttribute("tainted", "$". $node->name);
                                return $node->name;
                            }
                        } else if ($node instanceof Node\Expr\FuncCall){
                            if (key_exists($node->name->parts[0], $tainted)){
                                return $node->name->parts[0];
                            }
                        }
                    });
    
                    
                    foreach($tainted_variables as $tainted_var){
                        if ($tainted_var->hasAttribute("tainted")){
                            $tainted[$tainted_var->getAttribute("tainted")] = $tainted_var->getAttribute("tainted"); 
                        }
                    }
    
    
    
                    if(count($tainted_variables) > 0){
                        $new_state = new MyState($last_state->getTainted(),$last_state->getInitialized(),$last_state->getSanitized(), $this->patterns, $last_state->getI(),1);
                    } else {
                        $new_state = new MyState($last_state->getTainted(),$last_state->getInitialized(),$last_state->getSanitized(), $this->patterns, $last_state->getI());
                    }

                } else {
                    $new_state = new MyState($last_state->getTainted(),$last_state->getInitialized(),$last_state->getSanitized(), $this->patterns, $last_state->getI());
                }

                $traverser = new NodeTraverser;
                $traverser->addVisitor($new_state);
                $ast = $traverser->traverse($node->stmts);
        
                array_push($this->states, $new_state);
                array_push($this->all_states,$new_state);
            }

            
        } else if (($node instanceof Node\Stmt\ElseIf_ && count($node->stmts) != 0) 
        || ($node instanceof Node\Stmt\Else_ )){
            if(count($this->states) == 0){
                $tainted = array();
                $initialized = array();
                $sanitized = array();
                $new_state = new MyState($tainted,$initialized,$sanitized, $this->patterns, $this->i);
                $traverser = new NodeTraverser;
                $traverser->addVisitor($new_state);
                $ast = $traverser->traverse($node->stmts);
    
                array_push($this->states,$new_state);
                array_push($this->all_states,$new_state);

            } else {

                array_push($this->final,array_pop($this->states));
                
                if(count($this->states) == 0 ){
                    $last_state = $this->state_zero;
                } else {
                    $last_state = end($this->states);
                }
                
                $new_state = new MyState($last_state->getTainted(), $last_state->getInitialized(), $last_state->getSanitized(),$this->patterns, $last_state->getI());
                $traverser = new NodeTraverser;
                $traverser->addVisitor($new_state);
                $ast = $traverser->traverse($node->stmts);
                
                array_push($this->states, $new_state);
                array_push($this->all_states,$new_state);

            } 

            

        } else if (($node instanceof Node\Stmt\Expression || $node instanceof Node\Stmt\Echo_) && count($this->states) == 0) {

         
            $states_array = array();
            $always_init = array();

            
            foreach ($this->all_states as $state){
                if(count($state->getTainted()) == 0 && count($state->getInitialized()) == 0 && count($state->getSanitized()) == 0 ){
                    array_push($states_array, $state->getInitialized());
                    continue;
                }
                $new_state = new MyState($state->getTainted(), $state->getInitialized(),$state->getSanitized(), $this->patterns, $state->getI());
                $traverser = new NodeTraverser;
                $traverser->addVisitor($new_state);

                $array = array();
                array_push($array, $node);

                $ast = $traverser->traverse($array);


                array_push($this->final,$new_state);
                array_push($states_array, $state->getInitialized());

            }

            if(count($states_array)> 0){
                $first_state_init = $states_array[array_key_first($states_array)];

                foreach($states_array as $init_states){

                    foreach(array_keys($first_state_init) as $init_var){
                        
                        if(!in_array($init_var, array_keys($init_states)) ){
                            
                            unset($first_state_init[$init_var]);
                        }
                    }

                }

                $this->state_zero->addInitialized($first_state_init, $this->state_zero->getInitialized());

            }

            $array = array();
            array_push($array, $node);
            
            $this->ast_zero = $this->traverser_zero->traverse($array);            
            
       }

    }

    public function afterTraverse(array $nodes){
        array_push($this->final,$this->state_zero);
    }

    public function getInitialized(){
        return $this->initialized;
    }

    public function getTainted(){
        return $this->tainted;
    }

    public function getFinal(){
        return $this->final;
    }


}