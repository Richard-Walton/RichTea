grammar RichTea;

options	{ 	output=AST;
		k=2; // Needed to correctly match implicitAttributes in the attribute_list rule
		ASTLabelType=Tree;
		language=Java;
		backtrack=true;	}
						
tokens {	FUNCTION; 
		CHILDREN; ATTRIBUTES;
		ATTRIBUTE; NAME; VALUE;
		BRANCHES; BRANCH;
		ARRAY; VARIABLE;
		PROPERTY_LOOKUP; NATIVE_METHOD_CALL; 
		LAST_RETURNED_VALUE; 
		EXECUTABLE_FUNCTION_ATTRIBUTE; 
		TERNARY_OPERATOR; NEGATE;	}
			
@header {package richTea.antlr;}
@lexer::header {package richTea.antlr;}

program
	: 	function
	;
	
function
	:	ID OPEN_PAREN function_data? CLOSE_PAREN SEMI_COLON?
			-> ^(FUNCTION ^(NAME ID) function_data?)
	|	OPEN_PAREN function_data? CLOSE_PAREN SEMI_COLON?
			-> ^(FUNCTION ^(NAME ID["scope"]) function_data?)
	;

function_data
	:	attribute_list branch_list	
	;
	
attribute_list
	:	attributes+=implicit_attribute? (COMMA? attributes+=attribute)*
			-> ^(ATTRIBUTES ^(ATTRIBUTE $attributes)*)
	;
	
attribute
	:	ID (COLON | ASSIGN)! expression	
			->	^(NAME ID) ^(VALUE expression)
	;

implicit_attribute
	:	expression
			->	^(NAME ID["implicitAttribute"]) ^(VALUE expression)
	;	
	
branch_list
	:	branches+=implicitBranch? (COMMA? branches+=branch)*
			-> ^(BRANCHES ^(BRANCH $branches)*)
	;
	
branch
	:	HASH? (name=ID | name=STRING) OPEN_BRACE function* CLOSE_BRACE
			->	^(NAME $name) ^(CHILDREN function*)
	;

implicitBranch 
	:	HASH? OPEN_BRACE function* CLOSE_BRACE
			->	^(NAME ID["implicitBranch"]) ^(CHILDREN function*)
	;
	
/*	EXPRESSION EVALUATION	*/
	
expression
	:	logical_expression (QUESTION_MARK logical_expression COLON logical_expression) -> ^(TERNARY_OPERATOR logical_expression+)
	|	logical_expression
	;
	
logical_expression
	:	boolean_expression (OR^ boolean_expression)*
	;

boolean_expression
	:	equality_expression (AND^ equality_expression)*
	;
	
equality_expression
	: relational_expression (( EQ | NEQ )^ relational_expression)*
	;
	
relational_expression
	:	additive_expression (( LT | LTEQ | GT | GTEQ )^ additive_expression)*
	;
	
additive_expression
	:	multiplicative_expression (( PLUS_EQUALS | MINUS_EQUALS | PLUS | MINUS )^ multiplicative_expression)*
	;
	
multiplicative_expression
	:	power_expression (( MULTIPLY_EQUALS | DIVIDE_EQUALS | MULTIPLY | DIVIDE | MODULUS )^ power_expression)*
	;
	
power_expression
	:	unary_expression ( POWER^ unary_expression)*
	;

unary_expression
	:	primary_expression
	|	NOT^ primary_expression
	|	MINUS primary_expression -> ^(NEGATE primary_expression)
	;
	
primary_expression
	:	OPEN_PAREN! logical_expression CLOSE_PAREN!
	|	data_type
	;
	
data_type 
	:	NUMBER
	|	BOOLEAN
	|	STRING
	|	NULL
	| 	variable
	|	array
	|	function
	|	executable_function_attribute
	;
		
variable
	:	elements+=lookup_chain_root (PERIOD elements+=lookup_chain_element)*
			->	^(VARIABLE $elements+)
	;

lookup_chain_root
	:	ID
			->	^(PROPERTY_LOOKUP STRING['"' + $ID.text + '"'])
	|	UNDERSCORE
			->	LAST_RETURNED_VALUE
	;
	
lookup_chain_element
	:	lookup_chain_root
	|	ID OPEN_PAREN expression_list? CLOSE_PAREN
			->	^(NATIVE_METHOD_CALL ^(NAME ID) ^(ATTRIBUTES expression_list?))
	|	OPEN_BRACE expression CLOSE_BRACE
			->	^(PROPERTY_LOOKUP expression)
	;
	
array
	:	OPEN_BOX expression_list? CLOSE_BOX
			->	^(ARRAY expression_list?)
	;

expression_list
	:	(expression (COMMA expression)* )
			->	expression*
	;
	
executable_function_attribute
	: 	AT function
			->	^(EXECUTABLE_FUNCTION_ATTRIBUTE function)
	;

/*	TOKENS	*/

NUMBER
 	:	INTEGER | FLOAT
 	;
 	
STRING
    	:	'"' ( ESC_SEQ | ~('\\'|'"') )* '"'
   	;

BOOLEAN
 	:	'true' 
 	|	'false'
	;

NULL
	:	'null'
	;

ID  	:	UNDERSCORE? LETTER (LETTER | INTEGER | UNDERSCORE)*
    	;

COMMENT
	:	'//' ~('\n'|'\r')* '\r'? '\n' {$channel=HIDDEN;}
  	|	'/*' ( options {greedy=false;} : . )* '*/' {$channel=HIDDEN;}
   	;

WHITESPACE
	:	('\r' | '\n' | '\r\n' | ' ' | '\t' ) {$channel=HIDDEN;} 
	;

COMMA	:	','	;
PERIOD	:	'.'	;
HASH	:	'#'	;
AT	:	'@'	;
UNDERSCORE	:	'_';

PLUS_EQUALS	:	PLUS ASSIGN;
MULTIPLY_EQUALS	:	MULTIPLY ASSIGN;
MINUS_EQUALS	:	MINUS ASSIGN;
DIVIDE_EQUALS	:	DIVIDE ASSIGN;

PLUS	:	'+'	;
MINUS	:	'-'	;
MULTIPLY:	'*'	;
DIVIDE	:	'/'	;
MODULUS	:	'%'	;
POWER	:	'^'	;

OR	:	'||'	;
AND	:	'&&'	;
GT	:	'>'	;
GTEQ	:	'>='	;
LT	:	'<'	;
LTEQ	:	'<='	;
EQ	:	'=='	;
NEQ	:	'!='	;
NOT	:	'!'	;

ASSIGN		:	'=' 	;
COLON		:	':'	;
SEMI_COLON	: 	';'	;
QUESTION_MARK	:	'?'	;

OPEN_PAREN 	:	'(' 	;
CLOSE_PAREN 	:	')' 	;

OPEN_BRACE	:	'{'	;
CLOSE_BRACE	:	'}'	;

OPEN_BOX	:	'['	;
CLOSE_BOX	:	']'	;

/*	FRAGMENTS	*/

fragment
INTEGER
	:	'0'..'9'+
	;
	
fragment
FLOAT
	:	INTEGER+ '.' INTEGER*
	;
	
fragment
LETTER
	:	'a'..'z'
	|	'A'..'Z'
	;
	
fragment
ESC_SEQ 
	:	'\\' ('b'|'t'|'n'|'f'|'r'|'\"'|'\''|'\\')
	;