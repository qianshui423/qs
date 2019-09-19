# qs

[![Build Status](https://travis-ci.org/qianshui423/qs.svg?branch=master)](https://travis-ci.org/qianshui423/qs)

A querystring parsing and stringifying library.

The idea for Java qs module comes from js [qs][1]

# Different from js qs

> PlainObjects is supported by default. 'plainObjects', 'allowPrototypes' and 'delimiter' parameter are not supported when parsing .

> UTF-8 is supported by default. Can't support set other charset when parsing or stringify.

> Can't support set 'indices' when stringify. I think that the parameter is conflict with arrayFormat's 'indices'.

> Can't support skip add element for arrayFormat's 'indices' when parsing.

> Can't support set 'arrayLimit' when parsing. the implementation way does not need to rely on this.

# Usage

Test Case: UsageTest

## Parse

```java
QSObject qsObject = QS.parse("a=c");
```

## Stringify

```java
qsObject.toQString();
// or
QS.toQString(qsObject);
```

# Parsing Objects

Test Case: ParsingObjectsTest

```java
parser.parse(string, [ParseOptions]);
```

qs allows you to create nested objects within your query strings, by surrounding the name of sub-keys with square brackets []. For example, the string "foo[bar]=baz" converts to:

```java
QS.parse("foo[bar]=baz");
```

parse result

```text
{
    foo: {
        bar: 'baz'
    }
}
```

URI encoded strings work too:

```java
QS.parse("a%5Bb%5D=c");
```

parse result

```text
{
    a: { b: 'c' }
}
```

You can also nest your objects, like "foo[bar][baz]=foobarbaz":

```java
QS.parse("foo[bar][baz]=foobarbaz");
```

parse result

```text
{
    foo: {
        bar: {
            baz: 'foobarbaz'
        }
    }
}
```

By default, when nesting objects qs will only parse up to 5 children deep. This means if you attempt to parse a string like 'a[b][c][d][e][f][g][h][i]=j' your resulting object will be:

```java
QS.parse("a[b][c][d][e][f][g][h][i]=j");
```

parse result

```text
{
    a: {
        b: {
            c: {
                d: {
                    e: {
                        f: {
                            '[g][h][i]': 'j'
                        }
                    }
                }
            }
        }
    }
}
```

This depth can be overridden by passing a depth option to qs.parse(string, [options]):

```java
QS.parse("a[b][c][d][e][f][g][h][i]=j", new ParseOptions.Builder().setDepth(1).build());
```

```text
{ a: { b: { '[c][d][e][f][g][h][i]': 'j' } } }
```

The depth limit helps mitigate abuse when qs is used to parse user input, and it is recommended to keep it a reasonably small number.

For similar reasons, by default qs will only parse up to 1000 parameters. This can be overridden by passing a parameterLimit option:

```java
QS.parse("a=b&c=d", new ParseOptions.Builder().setParameterLimit(1).build());
```

parse result

```text
{ a: 'b' }
```

To bypass the leading question mark, use ignoreQueryPrefix:

```java
QS.parse("?a=b&c=d", new ParseOptions.Builder().setIgnoreQueryPrefix(true).build());
```

parse result

```text
{ a: 'b', c: 'd' }
```

Option allowDots can be used to enable dot notation:

```java
QS.parse("a.b=c", new ParseOptions.Builder().setAllowDots(true).build());
```

parse result

```text
{ a: { b: 'c' } }
```

# Parsing Arrays

qs can also parse arrays using a similar [] notation:

```java
QS.parse("a[]=b&a[]=c");
```

parse result

```text
{ a: ['b', 'c'] }
```

You may specify an index as well:

```java
QS.parse("a[0]=c&a[1]=b");
```

parse result

```text
{ a: ['b', 'c'] }
```

Note that an empty string is also a value, and will be preserved:

```java
QS.parse("a[]=&a[]=b");
```

parse result

```text
{ a: ['', 'b'] }
```

```java
QS.parse("a[0]=b&a[1]=&a[2]=c");
```

parse result

```text
{ a: ['b', '', 'c'] }
```

To disable array parsing entirely, set parseArrays to false.

```java
QS.parse("a[]=b", new ParseOptions.Builder().setParseArrays(false).build());
```

parse result

```text
{ a: { '0': 'b' } }
```

If you mix notations, qs will merge the two items into an object:

```java
QS.parse("a[0]=b&a[b]=c");
```

parse result

```text
{ a: { '0': 'b', b: 'c' } }
```

You can also create arrays of objects:

```java
QS.parse("a[][b]=c");
```

parse result

```text
{ a: [{ b: 'c' }] }
```

Some people use comma to join array, qs can parse it:

```java
QS.parse("a=b,c", new ParseOptions.Builder().setComma(true).build());
```

parse result

```text
{ a: ['b', 'c'] }
```

(this cannot convert nested objects, such as a={b:1},{c:d})


# License 📄

Copyright 2019 qianshui423

Licensed under the Apache License, Version 2.0 (the "License");you may not use this file except in compliance with the License.You may obtain a copy of the License at

> http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.See the License for the specific language governing permissions and limitations under the License.

[1]:https://github.com/ljharb/qs "qs"
