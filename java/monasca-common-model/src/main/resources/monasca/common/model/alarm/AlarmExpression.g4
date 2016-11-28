/*
 * (C) Copyright 2014-2016 Hewlett Packard Enterprise Development LP
 * Copyright 2016 FUJITSU LIMITED
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
    | expression and expression # andExpr
    | expression or expression # orExpr
    | '(' expression ')' # parenExpr
    ;


function
    : functionType '(' compoundIdentifier (',' deterministic)? (',' period)? ')'
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
    | LAST
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
    : (txt)+ '=' (txt)+
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
    | LAST
    ;

period
    : INTEGER
    ;

deterministic
    : 'deterministic'
    ;

literal
    : DECIMAL
    | INTEGER
    ;

repeat
    : INTEGER
    ;

txt
    : TXT
    | keyword
    | INTEGER
    | DECIMAL
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

LAST
    : [lL][aA][sS][tT]
    ;

INTEGER
    : DIGIT+
    ;

DECIMAL
    : '-'?DIGIT+('.'DIGIT+)?
    ;

TXT
  : ~('}' | '{' | '&' | '|' | '>' | '<' | '=' | ',' | ')' | '(' | ' ' | '"' )+
  ;

STRING
  : '"' .*? '"'
  ;

fragment
DIGIT
  : '\u0030'..'\u0039'       // 0-9
  ;

WS : [ \t\r\n]+ -> skip ;

FALL_THROUGH
  : . {if(true) {throw new IllegalArgumentException("IllegalCharacter: " + getText());}}
  ;
