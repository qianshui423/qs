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

UNESCAPED_CH = [^\[\]=,\.&]

LEFT_SQUARE = \[
RIGHT_SQUARE = \]
EQUAL_SIGN = =
COMMA = ,
DOT = \.
AND = &
%%

<YYINITIAL> {
          {UNESCAPED_CH}+	{ return new QSToken(QSToken.TYPE_VALUE, yytext()); }
}
<YYINITIAL> {LEFT_SQUARE} 	{ return new QSToken(QSToken.TYPE_LEFT_SQUARE, yytext()); }
<YYINITIAL> {RIGHT_SQUARE} 	{ return new QSToken(QSToken.TYPE_RIGHT_SQUARE, yytext()); }
<YYINITIAL> {EQUAL_SIGN}    { return new QSToken(QSToken.TYPE_EQUAL_SIGN, yytext()); }
<YYINITIAL> {COMMA}			{ return new QSToken(QSToken.TYPE_COMMA, yytext()); }
<YYINITIAL> {DOT}			{ return new QSToken(QSToken.TYPE_DOT, yytext()); }
<YYINITIAL> {AND}	 		{ return new QSToken(QSToken.TYPE_AND, yytext()); }

