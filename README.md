# qs

[![Build Status](https://travis-ci.org/qianshui423/qs.svg?branch=master)](https://travis-ci.org/qianshui423/qs)

A querystring parsing and stringifying library.

The idea for Java qs module comes from js [qs][1]

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


# License 📄

Copyright 2019 qianshui423

Licensed under the Apache License, Version 2.0 (the "License");you may not use this file except in compliance with the License.You may obtain a copy of the License at

> http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.See the License for the specific language governing permissions and limitations under the License.

[1]:https://github.com/ljharb/qs "qs"
