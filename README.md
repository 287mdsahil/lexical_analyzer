Some information regarding the project.

### Project Structure 

```
syntax_analyzer/
    bin/    // use this to hold the compiled .class files, but do not upload them!
    docs/   // documentation
    src/    // this will contain java sources, subfolders in here denote packages
        algorithms/
        automata/
        lexer/
        regex/
        utils/
        Main.java
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

### Part 1 Table (change name)

| Type | actualRegex  | regexUsed  |
| :----: | :-------------------------------: | :---------------------------------------: |
| KEYWORD | int &#124; float &#124; return | int &#124; float &#124; return |
| IDENTIFIER | [_A-Za-z][_0-9A-Za-z]* | (_&#124;[AZaz])(_&#124;[09AZaz])* |
| INTEGER | (+&#124;-)?[0-9][0-9]* | (+&#124;-&#124;ε)[09][09]* |
| FLOAT | (+&#124;-)?(([0-9]+&#92;.[0-9]*)&#124;([0-9]*&#92;.[0-9]+))((e&#124;E)(+&#124;-)[0-9]+)) | (+&#124;-&#124;ε)(([09][09]*&#92;.[09]*)&#124;([09]*&#92;.[09][09]*))(ε&#124;((e&#124;E)(+&#124;-&#124;ε)[09][09]*)) |


### Misc

**Oracle references (Java SE 8)**: [here](https://docs.oracle.com/javase/8/docs/technotes/tools/#basic).
