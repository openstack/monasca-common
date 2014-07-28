/*
 * Copyright (c) 2014 Hewlett-Packard Development Company, L.P.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

grammar AlarmExpression;

start
    : expression EOF
    ;

expression 
    : compoundIdentifier relational_operator literal # relationalExprFwd
    | function relational_operator literal ('times' repeat)? # relationalExprFuncFwd
    | literal relational_operator compoundIdentifier # relationalExprBwd
    | literal relational_operator function ('times' repeat)? # relationalExprFuncBwd
    | expression and expression # andExpr
    | expression or expression # orExpr
    | '(' expression ')' # parenExpr
    ;


function
    : functionType '(' compoundIdentifier (',' period)? ')'
    ;

relational_operator
    : lt
    | lte
    | gt
    | gte
    ;

lt
    : LT_S
    | LT
    ;

lte
    : LTE
    | LTE_S
    ;

gt
    : GT
    | GT_S
    ;

gte
    : GTE
    | GTE_S
    ;

and
    : AND
    | AND_S
    ;

or
    : OR
    | OR_S
    ;

functionType
    : MIN
    | MAX
    | SUM
    | CNT
    | AVG
    ;

primary
    : literal
    | compoundIdentifier
    ;

compoundIdentifier
    : namespace ('{' (dimensionList)? '}')?
    ;

namespace
    : txt
    ;

dimensionList
    : dimension (',' dimension)*
    ;

dimension
    : txt '=' txt
    ;

keyword
    : LT
    | LTE
    | GT
    | GTE
    | AND
    | OR
    | MIN
    | MAX
    | SUM
    | CNT
    | AVG
	;

period
    : INTEGER
    ;

literal
    : INTEGER
    ;

repeat
    : INTEGER
    ;

txt
    : TXT
    | keyword
    | INTEGER 
    | STRING
    ;
LT
	: [lL][tT]
	;

LT_S
	: '<'
	;

LTE
	: [lL][tT][eE]
	;

LTE_S
	: '<='
	;

GT
	: [gG][tT]
	;

GT_S
	: '>'
	;

GTE
	: [gG][tT][eE]
	;
	
GTE_S
	: '>='
	;
	
AND
	: [aA][nN][dD] 
	;
	
AND_S
	: '&&'
	;
	
OR
	: [oO][rR]
	;

OR_S
	: '||'
	;

MIN
    : [mM][iI][nN]
    ;

MAX
    : [mM][aA][xX]
    ;

SUM
    : [sS][uU][mM]
    ;

CNT
    : [cC][oO][uU][nN][tT]
    ;

AVG
    : [aA][vV][gG]
    ;

INTEGER
    : DIGIT+
    ;

TXT
  : [//a-zA-Z_$/\\0-9]~('\''|';' | '}' | '{' | '=' | ','| '&' | ')' | '(' |' '| '"' )+
  ;

STRING
  : '"' .*? '"'
  ; 

fragment
LETTER
  : '\u0041'..'\u005a'       // A-Z
  | '\u0061'..'\u007a'       // a-z
  ;

fragment
DIGIT
  : '\u0030'..'\u0039'       // 0-9
  ;

fragment
UNDERSCORE
 	: '\u005f'               // _
 	;

fragment
DASH
	: '-'
	;
	
fragment
PERIOD
	: '.'
	;

WS : [ \t\r\n]+ -> skip ;