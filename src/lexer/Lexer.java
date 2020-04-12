package lexer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import algorithms.SubsetConstruction;
import algorithms.Thompson;
import automata.DFA;
import regex.Regex;
import utils.StringEscapeUtils;

public class Lexer {
    private static char SEPARATOR = ' ';

    private List<String> tokenTypes;
    private List<Regex> regexes;
    private List<DFA> automata;

    private int scannedChar, id;
    private StringBuilder lexeme;
    private List<Boolean> inFinalStateCache;
    private BufferedReader programFileReader;
    private List<LexToken> tokens;

    public Lexer(String typeRegexPairsFilePath) {
        tokenTypes = new ArrayList<>();
        regexes = new ArrayList<>();
        automata = new ArrayList<>();
        
        init(typeRegexPairsFilePath);
    }

    private void init(String typeRegexPairsFilePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(typeRegexPairsFilePath))) {
            for (String line = br.readLine(); line != null; line = br.readLine()) {
                int splitAt = line.indexOf(SEPARATOR);
                if (splitAt == -1)
                    throw new IllegalArgumentException("Line does not contain separator");

                String 
                    type = line.substring(0, splitAt), 
                    regex = line.substring(splitAt + 1);
                updateDefinitions(type, regex);
            }
        }
        catch (FileNotFoundException e) {
            System.err.println("Definitions file not found");
            e.printStackTrace();
        }
        catch (IOException e) {
            System.err.println("I/O Error");
            e.printStackTrace();
        }
    }

    private void updateDefinitions(String tokenType, String regex) {
        tokenTypes.add(tokenType);

        Regex r = new Regex(regex);
        regexes.add(r);
        
        DFA dfa = SubsetConstruction.convert(Thompson.convert(r));
        automata.add(dfa);
    }

    public List<LexToken> tokenize(String programFilePath) {
        setup(programFilePath);
        try {
            makeTokens();
        } 
        catch (IOException e) {
            System.err.println("I/O Error while scanning program file");
            e.printStackTrace();
        }
        List<LexToken> temp = new ArrayList<>(tokens);
        cleanup();
        return temp;
    }

    private void makeTokens() throws IOException {
        int ch;
        lexeme = new StringBuilder();

        while ((ch = getChar()) != -1) {
            advanceAutomata(ch); // advance those automata which are not in dead state

            if (allAutomataInDeadState()) {
                handlePossibleMatch();

                resetAllAutomata();
                resetFinalStatesCache();
                lexeme = new StringBuilder();
            }
            else {
                lexeme.append((char) ch);
                consumeChar();
                updateFinalStatesCache();
            }
        }

        // we may have an outstanding match/non-match
        handlePossibleMatch();
    }

    private void setup(String programFilePath) {
        inFinalStateCache = new ArrayList<>();
        for (int i = 0; i < automata.size(); i++)
            inFinalStateCache.add(false);
        scannedChar = -1;
        id = 0;
        lexeme = null;
        tokens = new ArrayList<>();
        try {
            programFileReader = new BufferedReader(new FileReader(programFilePath));
        }
        catch (FileNotFoundException e) {
            System.err.println("Program file not found");
            e.printStackTrace();
        }
    }

    private void cleanup() {
        inFinalStateCache = null;
        scannedChar = -1;
        id = -1;
        lexeme = null;
        tokens = null;
        try {
            programFileReader.close();
        }
        catch (IOException e) {
            System.err.println("I/O Error while closing program file");
            e.printStackTrace();
        }
    }

    private void handlePossibleMatch() {
        if (anyAutomatonWasInFinalState()) {
            // we have a token in this case
            handleMatch(lexeme);
        }
        else {
            // here we have a non match
            // we can have a partially matched lexeme: handle the invalid match
            // but don't consume the current symbol;
            // or the lexeme length is zero, meaning that a single symbol
            // moved all the automata to a dead state: handle the invalid symbol
            // and consume the symbol
            if (lexeme.length() != 0) {
                System.err.println(
                    "Invalid lexeme: " + StringEscapeUtils.escape(lexeme.toString())
                );
            }
            else {
                if (scannedChar != -1) // at the end
                    System.err.println(
                        "Invalid symbol: " 
                        + StringEscapeUtils.getRepresentation((char) scannedChar)
                    );
                consumeChar();
            }
        }
    }

    private void handleMatch(StringBuilder lexeme) {
        String type = "UNKNOWN";
        for (int idx = 0; idx < automata.size(); idx++) {
            if (inFinalStateCache.get(idx)) {
                type = tokenTypes.get(idx);
                break;
            }
        }

        String temp = lexeme.toString();

        System.out.println(String.format("%d %s %s", id, type, StringEscapeUtils.escape(temp)));
        
        tokens.add(new LexToken(id++, type, temp));
    }

    private int getChar() throws IOException {
        if (scannedChar != -1) 
            return scannedChar;
        
        scannedChar = programFileReader.read();
        return scannedChar;
    }

    private void consumeChar() {
        scannedChar = -1;
    }

    private void advanceAutomata(int ch) {
        if (ch < 0)
            throw new IllegalArgumentException("Trying to advance automata on invalid symbol");
        
        char c = (char) ch;
        for (DFA dfa : automata)
            if (!dfa.isInDeadState())
                dfa.advance(c);
    }

    private void resetAllAutomata() {
        for (DFA dfa : automata)
            dfa.reset();
    }

    private boolean allAutomataInDeadState() {
        for (DFA dfa : automata)
            if (!dfa.isInDeadState())
                return false;
        return true;
    }

    private void updateFinalStatesCache() {
        for (int idx = 0; idx < automata.size(); idx++)
            inFinalStateCache.set(idx, automata.get(idx).isInFinalState());
    }

    private void resetFinalStatesCache() {
        for (int idx = 0; idx < inFinalStateCache.size(); idx++)
            inFinalStateCache.set(idx, false);
    }

    private boolean anyAutomatonWasInFinalState() {
        for (Boolean b : inFinalStateCache)
            if (b)
                return true;
        return false;
    }

    public static void main(String[] args) {
        Lexer lex = new Lexer(args[0]);

        lex.tokenize(args[1]);
    }
}
