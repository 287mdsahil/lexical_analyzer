Some information regarding the project.

### Project Structure 

```
.
├── bin
|   └── <will contain compiled objects>
├── README.md
└── src
    ├── algorithms
    │   ├── SubsetConstruction.java
    │   └── Thompson.java
    ├── automata
    │   ├── DFA.java
    │   └── NFA.java
    ├── lexer
    │   ├── Lexer.java
    │   └── LexToken.java
    ├── Main.java
    ├── regex
    │   ├── InfixToPostfix.java
    │   ├── Regex.java
    │   ├── RegexSpecialChar.java
    │   ├── RegexToken.java
    │   ├── RegexTokenType.java
    │   ├── RegexTree.java
    │   ├── RegexTreeNode.java
    │   └── RegexTreeNodeType.java
    └── utils
        ├── Buffer.java
        └── StringEscapeUtils.java
```

### Package Information

1. `algorithms` - implementation of thompson and subset construction algorithms
2. `automata` - finite state machines like NFA and DFA
3. `lexer` - lexical analyzer
4. `regex` - regular expression parsing, and utilities like shunting yard algorithm and tree generation
5. `utils` - utility classes for support like buffer and string escaping

### UML Diagram

![Image failed to load](./resources/uml/uml.png)

### Compilation

Follow the above folder stucture. For compilation use `javac`. Mention the classpath (`-cp`), the destination (`-d`) and the encoding (`-encoding`).

```
syntax_analyzer>javac -cp src/ -d bin/ -encoding utf-8 src/Main.java
```

*Notes*: 

- The code is compatible with **Java 8 and upwards**.
- The encoding should be `utf-8`. This is to support the epsilon (ε) symbol in the code.
- Some Windows machines may not support `utf-8` encoding. We recommend compilation and execution on Linux or Mac machines.
- Wildcards (`*`, `*.java`, etc) may be used in the filenames to compile everything in one go.

### Execution

Use the `java` command. Link the compiled binaries in the classpath. Refer to classes by their fully qualified package name before the class name. The `<output_symbol_table_file>` is optional; if not given, the symbol table will be printed to the console. The argument `-v` is an optional flag, which when enabled displays the intermediate regex trees, NFAs and DFAs on the console.

```
syntax analyzer>java -cp bin/ Main <regex_file> <program_file> [<output_symbol_table_file>] [-v]
```
### Regex file syntax

In the regex file we can add our token descriptions. Each line contains one token description. The syntax is as follows:

```
<token_name_1> <regex_exp_1>
<token_name_2> <regex_exp_2>
...
```

##### Rules

- In token name do not use spaces since it is a seperator.
- Special chars like `*`, `.` are reserved and have special meaning, like `*` means closure. In order to use these, use escape chars like `\*`.
- All symbols allowed in regex expression.
- `[azAZ09]` type ranges supported. For example `[az]` denotes all characters from `a` to `z`.
- Common escape sequence chars like `\n` `\r` `\t` are supported.
- Unicode escape supported e.g. `\u0020` is space.

##### Supported special regex chars

- Bracket close `)`
- Bracket open `(`
- Closure `*`
- Concat `.`
- Epsilon `ε`
- Escape `\`
- Range close `]`
- Range open`[`
- Union `|`
    
##### Example regex file

```
KEYWORD int|float|return|for|if|else
INTEGER [09][09]*
FLOAT (([09][09]*\.[09]*)|([09]*\.[09][09]*))(ε|((e|E)(+|-|ε)[09][09]*))
IDENTIFIER (_|[AZaz])(_|[09AZaz])*
PUNCTUATOR {|}|\(|\)|;|,
WHITESPACE (\u0020|\t|\r|\n)(\u0020|\t|\r|\n)*
ASSIGN_OP =
REL_OP <|>|==|<=|>=
ARITH_OP +|-|/|\*
```

### Part 1: Regular expressions to identify identifiers, integers and floats.

| Type | Actual Regex (Lex Format)  | Regex Used (In Our Regex File) |
| :---: | :---: | :---: |
| `KEYWORD` | `if\|int\|float\|for` | `if\|int\|float\|for` |
| `IDENTIFIER` | `[_A-Za-z][_0-9A-Za-z]*` | `(_\|[AZaz])(_\|[09AZaz])*` |
| `INTEGER` | `(+\|-)?[0-9][0-9]*` | `(+\|-\|ε)[09][09]*` |
| `FLOAT` | `(+\|-)?(([0-9]+\.[0-9]*)\|([0-9]*\.[0-9]+))((e\|E)(+\|-)?[0-9]+))?` | `(+\|-\|ε)(([09][09]*\.[09]*)\|([09]*\.[09][09]*))(ε\|((e\|E)(+\|-\|ε)[09][09]*))` |

