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

UNESCAPED_CH = [^=&]

EQUAL_SIGN = =
AND = &
%%

<YYINITIAL> {UNESCAPED_CH}+	        { return new QSToken(QSToken.TYPE_VALUE, yytext()); }
<YYINITIAL> {EQUAL_SIGN}            { return new QSToken(QSToken.TYPE_EQUAL_SIGN, yytext()); }
<YYINITIAL> {AND}	 		        { return new QSToken(QSToken.TYPE_AND, yytext()); }

