package com.qs.core.parser;

%%

%{
  int getPosition(){
	  return yychar;
  }
%}

%class QSLex
%type QSToken
%unicode

%char

INT = [-]?[0-9]+
DOUBLE = {INT}((\.[0-9]+)?([eE][-+]?[0-9]+)?)
BOOLEAN = true|false
NULL = null
UNESCAPED_CH = [^\[\]=,\.&]

LEFT_SQUARE = \[
RIGHT_SQUARE = \]
EQUAL_SIGN = =
COMMA = ,
DOT = \.
AND = &
%%

<YYINITIAL> {
          {INT}				{ Long val=Long.valueOf(yytext()); return new QSToken(QSToken.TYPE_VALUE, val); }
          {DOUBLE}			{ Double val=Double.valueOf(yytext()); return new QSToken(QSToken.TYPE_VALUE, val); }
          {BOOLEAN}		    { Boolean val=Boolean.valueOf(yytext()); return new QSToken(QSToken.TYPE_VALUE, val); }
          {NULL}			{ return new QSToken(QSToken.TYPE_VALUE, null); }
          {UNESCAPED_CH}+	{ return new QSToken(QSToken.TYPE_VALUE, yytext()); }
}
<YYINITIAL> {LEFT_SQUARE} 	{ return new QSToken(QSToken.TYPE_LEFT_SQUARE, null); }
<YYINITIAL> {RIGHT_SQUARE} 	{ return new QSToken(QSToken.TYPE_RIGHT_SQUARE, null); }
<YYINITIAL> {EQUAL_SIGN}    { return new QSToken(QSToken.TYPE_EQUAL_SIGN, null); }
<YYINITIAL> {COMMA}			{ return new QSToken(QSToken.TYPE_COMMA, null); }
<YYINITIAL> {DOT}			{ return new QSToken(QSToken.TYPE_DOT, null); }
<YYINITIAL> {AND}	 		{ return new QSToken(QSToken.TYPE_AND, null); }

