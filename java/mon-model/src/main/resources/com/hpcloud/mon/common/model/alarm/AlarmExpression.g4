/*
 * Alarm expression parser (for maas-api support)
 * Todd Walk, Hewlett Packard Cloud Services, 2013
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
    : LT
    | LT_S
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
    : identifier
    ;

dimensionList
    : dimension (',' dimension)*
    ;

dimension
    : identifier '=' ext_identifier
    ;

identifier
	: IDENTIFIER
    | keyword
	;

ext_identifier
	: IDENTIFIER
	| EXT_IDENTIFIER
    | INTEGER
    | keyword
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

literal
    : INTEGER
    ;

period
    : INTEGER
    ;

repeat
    : INTEGER
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

IDENTIFIER
  : (LETTER|UNDERSCORE) (LETTER|DIGIT|UNDERSCORE|DASH|PERIOD)*
  ;

EXT_IDENTIFIER
  : (LETTER|DIGIT|UNDERSCORE|DASH|PERIOD)+
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
