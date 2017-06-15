package com.enation.test.db;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

 public class MathEval   
{   
    public static void main(String[] args)   
    {   
        Context cx = Context.enter();   
        try  
        {   
            Scriptable scope = cx.initStandardObjects();   
            String str = "var w=4334;15+(w-1000)/500*5";   
             Object result = cx.evaluateString(scope, str, null, 1, null);   
             double res = Context.toNumber(result);   
             //System.out.println(res);   
         }   
         finally  
         {   
             Context.exit();   
         }   
     }   
 }  
