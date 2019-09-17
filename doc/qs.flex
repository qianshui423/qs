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

UNESCAPED_CH = [^&]

AND = &
%%

<YYINITIAL> {UNESCAPED_CH}+	        { return new QSToken(QSToken.TYPE_VALUE, yytext()); }
<YYINITIAL> {AND}	 		        { return new QSToken(QSToken.TYPE_AND, yytext()); }

