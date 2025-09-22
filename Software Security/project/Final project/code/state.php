<?php

include '../PHP-Parser/vendor/autoload.php';

use PhpParser\Node;
use PhpParser\NodeVisitorAbstract;
use PhpParser\NodeFinder;
use PhpParser\NodeTraverser;

class MyState extends NodeVisitorAbstract
{

    private $tainted = array();
    private $initialized = array();
    private $patterns = array();
    private $sanitized = array();
    private $guard = array();
    private $i = 0;
    private $tainted_guard = 0;
    private $init_tainted = array();
    private $break = false;

    function __construct(array $tainted, array $initialized,array $sanitized, array $patterns, int $i, int $tainted_guard=0, array $guard = array()){
        $this->tainted = $tainted;
        $this->initialized = $initialized;
        $this->patterns = $patterns;
        $this->i = $i;
        $this->sanitized = $sanitized;
        $this->tainted_guard = $tainted_guard;
        $this->guard = $guard;
    }



    public function beforeTraverse(array $nodes){
        $nodeFinder = new NodeFinder;

        if($this->tainted_guard == 1 && $this->patterns[$this->i]['implicit'] == "yes"){
            $tainted_variables = $nodeFinder->find($nodes, function(Node $node) {
                if($node instanceof Node\Expr\Variable){
                    return $node;
                } 
            });

            foreach ($tainted_variables as $new_tainted){
                foreach ($this->tainted as $old_tainted){
                    if(!key_exists("$".$new_tainted->name, $this->tainted)){
                        $this->tainted["$". $new_tainted->name] = $old_tainted;
                    } else {
                        $this->tainted["$". $new_tainted->name] = $old_tainted . ",". $this->tainted["$". $new_tainted->name];
                    }
                    
                }
            }
    
        }

    }

    public function leaveNode(Node $node){
        if($this->break){
            return;
        }

        $nodeFinder = new NodeFinder;

        if($node instanceof Node\Expr\Variable){

            for ($j = 0 ; $j < count($this->patterns[$this->i]['sources']) ; $j++){
                if("$" .$node->name == $this->patterns[$this->i]['sources'][$j] && !key_exists("$" .$node->name, $this->tainted)){
                    $this->tainted["$". $node->name] = "$". $node->name;
                    return $node->setAttribute("tainted", "$". $node->name);
                }
            }
            if (key_exists("$".$node->name, $this->tainted)){
                return $node->setAttribute("tainted", $this->tainted["$".$node->name]);
            }

        } else if ($node instanceof Node\Expr\FuncCall){

            for ($j = 0 ; $j < count($this->patterns[$this->i]['sources']) ; $j++){
                if($node->name->parts[0] == $this->patterns[$this->i]['sources'][$j]){
                    $this->tainted[$node->name->parts[0]] = $node->name->parts[0];
                    return $node->setAttribute("tainted", $node->name->parts[0]);
                }
            }

            for ($j = 0 ; $j < count($this->patterns[$this->i]['sanitizers']) ; $j++){
                if($node->name->parts[0] == $this->patterns[$this->i]['sanitizers'][$j] ){
                    $tainted_nodes = 0;
                    foreach($node->args as $sanitized_node){
                        if($sanitized_node->value->hasAttribute("tainted")){
                            $tainted_nodes = $tainted_nodes + 1;
                        }
                    }

                    $sanitized_variables = $nodeFinder->find($node->args, function(Node $node) {
                        if($node instanceof Node\Expr\Variable){
                            if($node->hasAttribute("tainted")){
                                $tainted_vars = $node->getAttribute("tainted");

                                $vars = explode(",", $tainted_vars);
                                $vars = array_values(array_unique($vars, SORT_REGULAR));

                                foreach($vars as $var){
                                    if(in_array($var,$this->patterns[$this->i]['sources'])){
                                        $node->setAttribute("sanitized", $var);
                                        return $node;
                                    } 
                                }
                                
                                if(!in_array($node->name,$this->initialized)){
                                    $node->setAttribute("sanitized","$" .$node->name);
                                    return $node;
                                }
                            } 
                            
                        } else if ($node instanceof Node\Expr\FuncCall){
                            if($node->hasAttribute("tainted") && !$node->hasAttribute("sanitized") 
                                    && !in_array($node->name->parts[0],$this->patterns[$this->i]['sanitizers'])){
                                $tainted_vars = $node->getAttribute("tainted");

                                $vars = explode(",", $tainted_vars);
                                $vars = array_values(array_unique($vars, SORT_REGULAR));

                                foreach($vars as $var){
                                    if(in_array($var,$this->patterns[$this->i]['sources'])){
                                        $node->setAttribute("sanitized", $var);
                                        return $node;
                                    } 
                                }
                            }
                        }
                    });

                    $sanitized_variables_init = $nodeFinder->find($node->args, function(Node $node) {
                        if($node instanceof Node\Expr\Variable){
                            if($node->hasAttribute("tainted")){
                                $tainted_vars = $node->getAttribute("tainted");

                                $vars = explode(",", $tainted_vars);
                                $vars = array_values(array_unique($vars, SORT_REGULAR));

                                foreach($vars as $var){
                                    if(!in_array($var,array_keys($this->initialized)) && !in_array($var,$this->patterns[$this->i]['sources']) ){
                                        $node->setAttribute("sanitized_init", $var);
                                        return $node;
                                    } 
                                }

                            } 
                            
                        } 
                    });

                    if(count($sanitized_variables) > 0){
                        
                        $count = 0;
                        if($tainted_nodes > count($sanitized_variables)){
                            $count = count($sanitized_variables);
                        } else {
                            $count = $tainted_nodes;
                        }

                        $string_aux="";
                        for ($l = 0 ; $l < count($sanitized_variables) ; $l++){
                            if($string_aux == ""){
                                $string_aux = $sanitized_variables[$l]->getAttribute("sanitized");
                            } else {
                                $string_aux = $sanitized_variables[$l]->getAttribute("sanitized") ."," . $string_aux  ;
                            }

                            if($sanitized_variables[$l]->hasAttribute("sanitized_init") && ($sanitized_variables[$l]->getAttribute("sanitized_init") !=$sanitized_variables[$l]->getAttribute("sanitized"))){
                                $string_aux = $sanitized_variables[$l]->getAttribute("sanitized_init") ."," . $string_aux  ;   
                            }
                            
                        }

                        if($string_aux != ""){
                            if(!key_exists($node->name->parts[0], $this->sanitized) || $this->sanitized[$node->name->parts[0]] == NULL){
                                $this->sanitized[$node->name->parts[0]] = array();
                            }
                            $node->setAttribute("sanitized", $node->name->parts[0]);
                            $this->sanitized[$node->name->parts[0]] = $string_aux;
                        }
                    }

                }
            }

            
            $tainted_variables = $nodeFinder->find($node->args, function(Node $node) {
                if($node instanceof Node\Expr\Variable){
                    if (key_exists("$".$node->name, $this->tainted)){
                        return $node->name;
                    } else if (!key_exists("$".$node->name, $this->initialized) ){
                        $this->init_tainted["$" . $node->name] = "$" . $node->name;
                        $node->setAttribute("tainted", "$". $node->name);
                        return $node->name;
                    }
                } else if ($node instanceof Node\Expr\FuncCall){
                    if (key_exists($node->name->parts[0], $this->tainted) ){
                        
                        return $node->name->parts[0];
                    }
                }
            });
        
            $string_aux="";
            for ($j = 0 ; $j < count($tainted_variables) ; $j++){
                $string_aux = $string_aux ."," . $tainted_variables[$j]->getAttribute("tainted");
            }
            if($string_aux != ""){
                $this->tainted[$node->name->parts[0]] = $node->name->parts[0] . $string_aux;
                return $node->setAttribute("tainted", $node->name->parts[0] . $string_aux);
            }




        } else if ($node instanceof Node\Expr\Assign || $node instanceof Node\Expr\AssignOp){
            if($node instanceof Node\Expr\AssignOp && $node->var->hasAttribute("tainted")){
                $this->tainted["$". $node->var->name] = $node->var->getAttribute("tainted"). "," . "$". $node->var->name;
                if($node->expr->hasAttribute("tainted")){
                    $this->tainted["$". $node->var->name] = $node->expr->getAttribute("tainted"). ",". $this->tainted["$". $node->var->name] .",".  "$". $node->var->name;                
                    $node->var->setAttribute("tainted", $node->expr->getAttribute("tainted"). ",". $this->tainted["$". $node->var->name] .",". "$" . $node->var->name);
                }

            }
            
            
            else if($node->expr->hasAttribute("tainted")){
                $this->tainted["$". $node->var->name] = $node->expr->getAttribute("tainted"). "," ."$". $node->var->name;           
                $node->var->setAttribute("tainted", $node->expr->getAttribute("tainted"). "," . "$" . $node->var->name);
            } 


            $tainted_variables = $nodeFinder->find($node->expr, function(Node $node) {
                if($node instanceof Node\Expr\Variable){
                    if (key_exists("$".$node->name, $this->tainted)){
                        return $node->name;
                    } else if (!key_exists("$".$node->name, $this->initialized)){
                        $this->init_tainted["$" . $node->name] = "$" . $node->name;
                        $node->setAttribute("tainted", "$". $node->name);
                        return $node->name;
                    }
                } else if ($node instanceof Node\Expr\FuncCall){
                    if (key_exists($node->name->parts[0], $this->tainted)){
                        return $node->name->parts[0];
                    }
                }
            });

            $string_aux="";
            for ($j = 0 ; $j < count($tainted_variables) ; $j++){
                $string_aux = $string_aux ."," . $tainted_variables[$j]->getAttribute("tainted");
            }
            if($string_aux != ""){
                if(key_exists("$" . $node->var->name, $this->tainted)){
                    $this->tainted["$" . $node->var->name] = "$" . $node->var->name . $string_aux . ",".$this->tainted["$" . $node->var->name];
                    return $node->var->setAttribute("tainted", "$" .$node->var->name . $string_aux. ",".$this->tainted["$" . $node->var->name]);
                } else {
                    $this->tainted["$" . $node->var->name] = "$" . $node->var->name . $string_aux;
                    return $node->var->setAttribute("tainted", "$" .$node->var->name . $string_aux);
                }
                
            }
            
        } else if ($node instanceof Node\Stmt\Echo_){
            
            $tainted_variables = $nodeFinder->find($node->exprs, function(Node $node) {
                if($node instanceof Node\Expr\Variable){
                    if (key_exists("$".$node->name, $this->tainted)){
                        return $node->name;
                    } else if (!key_exists("$".$node->name, $this->initialized)){
                        $this->init_tainted["$" . $node->name] = "$" . $node->name;
                        $node->setAttribute("tainted", "$". $node->name);
                        return $node->name;
                    }
                } else if ($node instanceof Node\Expr\FuncCall){
                    if (key_exists($node->name->parts[0], $this->tainted)){
                        return $node->name->parts[0];
                    }
                }
            });

            $string_aux="";
            for ($j = 0 ; $j < count($tainted_variables) ; $j++){
                $string_aux = $string_aux ."," . $tainted_variables[$j]->getAttribute("tainted");
            }
            if($string_aux != ""){
                $this->tainted["echo"] = "echo". $string_aux;
                return $node->setAttribute("tainted", "echo". $string_aux);
            }

        }
    
    }

    public function getTainted(){
        return $this->tainted;
    }

    public function setTainted(array $tainted){
        $this->tainted = $tainted;

    }

    public function setInitialized(array $initialized){
        $this->initialized = $initialized;
    }

    public function addInitialized(array $initialized){
        foreach (array_keys($initialized) as $init){
            $this->initialized[$init] = $initialized[$init];
        }
    }

    public function getInitialized(){
        return $this->initialized;
    }

    public function getSanitized(){
        return $this->sanitized;
    }

    public function getInitTainted(){
        return $this->init_tainted;
    }

   


    public function enterNode(Node $node){
        if($node instanceof Node\Stmt\If_ || $node instanceof Node\Stmt\Else_  || $node instanceof Node\Stmt\ElseIf_ ){
            return NodeTraverser::DONT_TRAVERSE_CHILDREN;
        } else if ($node instanceof Node\Stmt\Break_ || $node instanceof Node\Stmt\Continue_){
            $this->break = true;
        }

        if($node instanceof Node\Expr\Assign){
            if(!in_array($node->var->name,$this->guard)){
                $this->initialized["$".$node->var->name] = 1;
                return $node->var->setAttribute("initialized", "$" .$node->var->name);
            }
            
        }
    }


    public function afterTraverse(array $nodes){
        $this->break = false;
    }

    public function getPatterns(){
        return $this->patterns;
    }

    public function getI(){
        return $this->i;
    }
}