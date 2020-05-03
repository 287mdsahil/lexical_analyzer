Some information regarding the project.

### Project Structure 
```
.
├── bin
|   └── <will contain compiled objects>
├── docs
│   ├── automata.md
│   └── regex.md
├── README.md
├── src
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

1. ['algorithms'] - implementation of Thompson and Subset Construction Algorithm
2. [`automata`](docs/automata.md) - finite state machines
3. ['lexer'] - lexical analyzer
4. [`regex`](docs/regex.md) - regular expression parsing, and utilities like shunting yard algorithm and tree generation
5. ['utils'] - utility classes for support

### Compilation

Follow the above folder stucture. For compilation use `javac`. Mention the classpath (`-cp`), the destination (`-d`) and the encoding (`-encoding`).

```
syntax_analyzer>javac -cp src/ -d bin/ -encoding utf-8 src/regex/Regex.java
```

*Notes*: 

- The encoding should be `utf-8`. This is to support the epsilon (ε) symbol in the code.
- Wildcards (`*`, `*.java`, etc) may be used in the filenames to compile everything in one go.

### Execution

Use the `java` command. Link the compiled binaries in the classpath. Refer to classes by their fully qualified package name before the class name.

```
syntax_analyzer>java -cp bin/ regex.Regex "(a|b)*abc"
```

### Misc

**Oracle references (Java SE 8)**: [here](https://docs.oracle.com/javase/8/docs/technotes/tools/#basic).
