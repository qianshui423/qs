# qs

[![Build Status](https://travis-ci.org/qianshui423/qs.svg?branch=master)](https://travis-ci.org/qianshui423/qs) [![codecov](https://codecov.io/gh/qianshui423/qs/branch/master/graph/badge.svg)](https://codecov.io/gh/qianshui423/qs)

A querystring parsing and stringifying library.

The idea for Java qs module comes from js [qs][1]

# Different from js qs

> PlainObjects is supported by default. 'plainObjects', 'allowPrototypes' and 'delimiter' parameter are not supported when parsing .

> UTF-8 is supported by default. Can't support set other charset when parsing or stringify.

> Can't support set 'indices' when stringify. I think that the parameter is conflict with arrayFormat's 'repeat'.

> Can't support skip add element for arrayFormat's 'indices' when parsing.

> Can't support set 'arrayLimit' when parsing. the implementation way does not need to rely on this.

> Can't support custom encoder when parsing or stringify.

# Remark

To make the document clearer, the examples are pseudo code.

# Usage

Test Case: UsageTest

## Parse

```text
QSObject qsObject = QS.parse('a=c');
```

## Stringify

```text
qsObject.toQString();
// or
QS.toQString(qsObject);
```

# Parsing Objects

Test Case: ParsingObjectsTest

```text
parser.parse(string, [ParseOptions]);
```

qs allows you to create nested objects within your query strings, by surrounding the name of sub-keys with square brackets []. For example, the string 'foo[bar]=baz' converts to:

```text
QS.parse('foo[bar]=baz');

{
    foo: {
        bar: 'baz'
    }
}
```

URI encoded strings work too:

```text
QS.parse('a%5Bb%5D=c');

{
    a: { b: 'c' }
}
```


You can also nest your objects, like 'foo[bar][baz]=foobarbaz':

```text
QS.parse('foo[bar][baz]=foobarbaz');

{
    foo: {
        bar: {
            baz: 'foobarbaz'
        }
    }
}
```

By default, when nesting objects qs will only parse up to 5 children deep. This means if you attempt to parse a string like 'a[b][c][d][e][f][g][h][i]=j' your resulting object will be:

```text
QS.parse('a[b][c][d][e][f][g][h][i]=j');

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

```text
QS.parse('a[b][c][d][e][f][g][h][i]=j', new ParseOptions.Builder().setDepth(1).build());

{ a: { b: { '[c][d][e][f][g][h][i]': 'j' } } }
```

The depth limit helps mitigate abuse when qs is used to parse user input, and it is recommended to keep it a reasonably small number.

For similar reasons, by default qs will only parse up to 1000 parameters. This can be overridden by passing a parameterLimit option:

```text
QS.parse('a=b&c=d', new ParseOptions.Builder().setParameterLimit(1).build());

{ a: 'b' }
```

To bypass the leading question mark, use ignoreQueryPrefix:

```text
QS.parse('?a=b&c=d', new ParseOptions.Builder().setIgnoreQueryPrefix(true).build());

{ a: 'b', c: 'd' }
```

Option allowDots can be used to enable dot notation:

```text
QS.parse('a.b=c', new ParseOptions.Builder().setAllowDots(true).build());

{ a: { b: 'c' } }
```

# Parsing Arrays

Test Case: ParsingArraysTest

qs can also parse arrays using a similar [] notation:

```text
QS.parse('a[]=b&a[]=c');

{ a: ['b', 'c'] }
```

You may specify an index as well:

```text
QS.parse('a[0]=c&a[1]=b');

{ a: ['b', 'c'] }
```

Note that an empty string is also a value, and will be preserved:

```text
QS.parse('a[]=&a[]=b');

{ a: ['', 'b'] }
```

```text
QS.parse('a[0]=b&a[1]=&a[2]=c');

{ a: ['b', '', 'c'] }
```

To disable array parsing entirely, set parseArrays to false.

```text
QS.parse('a[]=b', new ParseOptions.Builder().setParseArrays(false).build());

{ a: { '0': 'b' } }
```

If you mix notations, qs will merge the two items into an object:

```text
QS.parse('a[0]=b&a[b]=c');

{ a: { '0': 'b', b: 'c' } }
```

You can also create arrays of objects:

```text
QS.parse('a[][b]=c');

{ a: [{ b: 'c' }] }
```

Some people use comma to join array, qs can parse it:

```text
QS.parse('a=b,c', new ParseOptions.Builder().setComma(true).build());

{ a: ['b', 'c'] }
```

(this cannot convert nested objects, such as a={b:1},{c:d})

# Stringifying

Test Case: StringifyingTest

```text
QS.toQString(object, [options]);
```

When stringifying, qs by default URI encodes output. Objects are stringified as you would expect:

```text
ObjectEqual.equals(QS.toQString({ a: 'b' }), 'a=b');
ObjectEqual.equals(QS.toQString({ a: { b: 'c' } }), 'a%5Bb%5D=c');
```

This encoding can be disabled by setting the encode option to false:

```text
ObjectEqual.equals(QS.toQString({ a: { b: 'c' } }, new StringifyOptions.Builder().setEncode(false).build()), 'a[b]=c');
```

Encoding can be disabled for keys by setting the encodeValuesOnly option to true:

```text
ObjectEqual.equals(encodeValuesOnlyObject.toQString({ a: 'b', c: ['d', 'e=f'], f: [['g'], ['h']] }, new StringifyOptions.Builder().setEncodeValuesOnly(true).build()), 'a=b&c[0]=d&c[1]=e%3Df&f[0][0]=g&f[1][0]=h');
```

When arrays are stringified, by default they are given explicit indices:

```text
ObjectEqual.equals(QS.toQString({ a: ['b', 'c', 'd'] }, new StringifyOptions.Builder().setEncode(false).build()), 'a[0]=b&a[1]=c&a[2]=d');
```

You may use the arrayFormat option to specify the format of the output array:

```text
ObjectEqual.equals(QS.toQString({ a: ['b', 'c'] }, new StringifyOptions.Builder().setEncode(false).setArrayFormat(ArrayFormat.INDICES).build()), 'a[0]=b&a[1]=c');
ObjectEqual.equals(QS.toQString({ a: ['b', 'c'] }, new StringifyOptions.Builder().setEncode(false).setArrayFormat(ArrayFormat.BRACKETS).build()), 'a[]=b&a[]=c');
ObjectEqual.equals(QS.toQString({ a: ['b', 'c'] }, new StringifyOptions.Builder().setEncode(false).setArrayFormat(ArrayFormat.REPEAT).build()), 'a=b&a=c');
ObjectEqual.equals(QS.toQString({ a: ['b', 'c'] }, new StringifyOptions.Builder().setEncode(false).setArrayFormat(ArrayFormat.COMMA).build()), 'a=b,c');
```

When objects are stringified, by default they use bracket notation:

```text
ObjectEqual.equals(QS.toQString({ a: { b: { c: 'd', e: 'f' } } }, new StringifyOptions.Builder().setEncode(false).build()), 'a[b][c]=d&a[b][e]=f');
```

You may override this to use dot notation by setting the allowDots option to true:

```text
ObjectEqual.equals(QS.toQString({ a: { b: { c: 'd', e: 'f' } } }, new StringifyOptions.Builder().setEncode(false).setAllowDots(true).build()), 'a.b.c=d&a.b.e=f');
```

Empty strings and null values will omit the value, but the equals sign (=) remains in place:

```text
ObjectEqual.equals(QS.toQString({ a: '' }), 'a=');
```

Key with no values (such as an empty object or array) will return nothing:

```text
ObjectEqual.equals(QS.toQString({ a: [] }), '');
ObjectEqual.equals(QS.toQString({ a: {} }), '');
ObjectEqual.equals(QS.toQString({ a: [{}] }), '');
ObjectEqual.equals(QS.toQString({ a: { b: []} }), '');
ObjectEqual.equals(QS.toQString({ a: { b: {}} }), '');
```

The query string may optionally be prepended with a question mark:

```text
ObjectEqual.equals(QS.toQString({ a: 'b', c: 'd' }, new StringifyOptions.Builder().setAddQueryPrefix(true).build()), '?a=b&c=d');
```

# Handling of null values

Test Case: HandlingNullTest

By default, null values are treated like empty strings:

```text
ObjectEqual.equals(QS.toQString({ a: null, b: '' }), 'a=&b=');
```

Parsing does not distinguish between parameters with and without equal signs. Both are converted to empty strings.

```text
ObjectEqual.equals(QS.parse('a&b='), { a: '', b: '' });
```

To distinguish between null values and empty strings use the strictNullHandling flag. In the result string the null values have no = sign:

```text
ObjectEqual.equals(QS.toQString({ a: null, b: '' }, new StringifyOptions.Builder().setStrictNullHandling(true).build()), 'a&b=');
```

To parse values without = back to null use the strictNullHandling flag:

```text
ObjectEqual.equals(QS.parse('a&b=', new ParseOptions.Builder().setStrictNullHandling(true).build()), { a: null, b: '' });
```

To completely skip rendering keys with null values, use the skipNulls flag:

```text
ObjectEqual.equals(QS.toQString({ a: 'b', c: null}, new StringifyOptions.Builder().setSkipNulls(true).build()), 'a=b');
```

# License 📄

Copyright 2019 qianshui423

Licensed under the Apache License, Version 2.0 (the "License");you may not use this file except in compliance with the License.You may obtain a copy of the License at

> http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.See the License for the specific language governing permissions and limitations under the License.

[1]:https://github.com/ljharb/qs "qs"
