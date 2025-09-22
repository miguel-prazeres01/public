<?php

include '../PHP-Parser/vendor/autoload.php';
include 'visitor.php';

use PhpParser\Error;
use PhpParser\NodeDumper;
use PhpParser\ParserFactory;
use PhpParser\PrettyPrinter;
use PhpParser\{Node, NodeTraverser, NodeVisitorAbstract,NodeFinder};


if ($argc != 3){
    echo "Wrong parameters";
    return;
}


$jsonDecoder = new PhpParser\JsonDecoder();
$prettyPrinter = new PrettyPrinter\Standard;




//Reading files from argv
//--------------------------------------------------------------------
$file_slice = fopen($argv[1], "r");
$file_vuln_patterns = fopen($argv[2], "r");

$file_slice_size = filesize($argv[1]);
$slice = fread($file_slice, $file_slice_size);
echo $slice;
fclose($file_slice);

$file_vuln_patterns_size = filesize($argv[2]);
$vuln_patterns = fread($file_vuln_patterns, $file_vuln_patterns_size);
fclose($file_vuln_patterns);
//---------------------------------------------------------------------


//vulnerability patterns
$patterns = $jsonDecoder->decode($vuln_patterns);


//Receives tainted nodes ,removes repeated elements and parses them for output
function normalizeTainted(array $states){
    $tainted_sinks = array();

    foreach ($states as $state) {
        if($state != NULL){
            #var_dump($state);
            $i = $state->getI();
            #var_dump($i);
            $sinks = $state->getPatterns()[$i]["sinks"];
            $sources = $state->getPatterns()[$i]["sources"];
            $tainted = $state->getTainted();
            $initialized = $state->getInitialized();
            $init_tainted = $state->getInitTainted();
            $sanitized = $state->getSanitized();
            
            
            for ($k = 0 ; $k < count($sinks) ; $k++) {
                if(key_exists($sinks[$k], $tainted)){
                    if(!key_exists($sinks[$k], $tainted_sinks) || $tainted_sinks[$sinks[$k]] == NULL){
                        $tainted_sinks[$sinks[$k]] = array();
                    }
                    

                    $vars = explode(",", $tainted[$sinks[$k]]);
                    $vars = array_values(array_unique($vars, SORT_REGULAR));
                    

                    for ($j = 0 ; $j < count($vars) ; $j++){
                        for ($l = 0 ; $l < count($sources) ; $l++){

                            //adds sources
                            if ($vars[$j] == $sources[$l]){
                                if(!in_array($vars[$j],$tainted_sinks[$sinks[$k]])){
                                    array_push($tainted_sinks[$sinks[$k]], $vars[$j]); 
                                }
                                
                            } 
                        }

                        //adds not initialized in the end
                        if (!key_exists($vars[$j],$initialized) && $vars[$j]!="" && $vars[$j][0]== "$"){
                            if(!in_array($vars[$j],$tainted_sinks[$sinks[$k]])){
                                array_push($tainted_sinks[$sinks[$k]], $vars[$j]); 
                            }
                            
                        //adds not initialized missing
                        } if(key_exists($vars[$j], $init_tainted)){
                            if(!in_array($vars[$j],$tainted_sinks[$sinks[$k]])){
                                array_push($tainted_sinks[$sinks[$k]], $vars[$j]); 
                            }
                        }
                    }

                }
            }

        }
        

    }

    return $tainted_sinks;


}




//Receives sanitized nodes ,removes repeated elements and parses them for output
function normalizedSanitized(array $states){
    $sanitized_vars = array();

    foreach ($states as $state) {
        if($state != NULL){
            $i = $state->getI();
            $sinks = $state->getPatterns()[$i]["sinks"];
            $sources = $state->getPatterns()[$i]["sources"];
            $tainted = $state->getTainted();
            $initialized = $state->getInitialized();
            $sanitized = $state->getSanitized();

        
            
            for ($k = 0 ; $k < count($sinks) ; $k++) {
                if(key_exists($sinks[$k], $tainted)){
                    $tainted_vars = explode(",", $tainted[$sinks[$k]]);
            
                    if(count($sanitized) > 0){
                        foreach (array_keys($sanitized) as $sanitizers){
                        
                                if(!in_array($sanitizers, $tainted_vars)){
                                    continue;
                                }

                                if(!key_exists($sinks[$k], $sanitized_vars) || $sanitized_vars[$sinks[$k]] == NULL){
                                    $sanitized_vars[$sinks[$k]] = array();
                                }

                                $vars_san = explode(",", $sanitized[$sanitizers]);
                                
                                for ($l = 0 ; $l < count($vars_san) ; $l++){
                                    if(!key_exists($vars_san[$l], $sanitized_vars[$sinks[$k]]) ||  $sanitized_vars[$sinks[$k]][$vars_san[$l]] == NULL || !key_exists($l, $sanitized_vars[$sinks[$k]][$vars_san[$l]])){
                                        $sanitized_vars[$sinks[$k]][$vars_san[$l]][$l] = array();
                                    }
                                }

                                for ($l = 0 ; $l < count($vars_san) ; $l++){
                                    if(!in_array($sanitizers,$sanitized_vars[$sinks[$k]][$vars_san[$l]][$l] )){
                                        array_push($sanitized_vars[$sinks[$k]][$vars_san[$l]][$l], $sanitizers);
                                    } 
                                
                                }

                                
                            
                        }
                    }


                }
            }
            
            
           
        }
        

    }
    return $sanitized_vars;

}
     


$output = array();


try {

    $ast = $jsonDecoder->decode($slice);
 
    
    $idx = 0;
    for ($i=0 ; $i < count($patterns) ; $i++){
        $visitor = new MyVisitorIdeia($patterns, $i);
        
        $traverser = new NodeTraverser;

        $traverser->addVisitor($visitor);

        $stmts = $traverser->traverse($ast);

        $normalized_tainted[$i] = normalizeTainted($visitor->getFinal());

        $normalized_sanitized[$i] = normalizedSanitized($visitor->getFinal());

            
        //parsing output
        foreach (array_keys($normalized_tainted[$i]) as $sink){

            foreach ($normalized_tainted[$i][$sink] as $vuln){

                $output[$idx]["vulnerability"] = $patterns[$i]["vulnerability"];
                $output[$idx]["source"] = $vuln;
                $output[$idx]["sink"] = $sink;
                //missing
                $output[$idx]["unsanitized flows"] = "yes";

                $sanitized_flows = array();
                if(key_exists($sink,$normalized_sanitized[$i])){
                    if(key_exists($vuln,$normalized_sanitized[$i][$sink])){
                        foreach($normalized_sanitized[$i][$sink][$vuln] as $sanitizers){
                            array_push($sanitized_flows, $sanitizers); 
                        }
                    }
                    
                    
                } 

                if(count($sanitized_flows) > 0){
                    $sanitized_flows = array_unique($sanitized_flows, SORT_REGULAR);
                    $output[$idx]["sanitized flows"] = $sanitized_flows;
                } else {
                    $output[$idx]["sanitized flows"] = [];
                }
                
                $idx++;
                
            }


        }

            
        
    }

    $output_encode = json_encode($output , JSON_PRETTY_PRINT);
    
    echo $output_encode ."\n";


} catch (Error $error) {
    echo "Parse error: {$error->getMessage()}\n";
    return;
}



