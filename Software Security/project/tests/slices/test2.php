<?php

   if($c > 0) {
      $a = b();
      if ($c < 3) {
         $a = f($a);
      }
      else{
        if($a == 0){
            $a = b();
        }
        $not_initialized="xxx"; 
      }
      
   }
   else {
        $a = "x";
        $c = d($a);
   }

   e($a, $c);

   // tip: sources, sanitizers and sinks can appear inside branches, and they can be nested

?>