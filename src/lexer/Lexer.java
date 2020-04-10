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

public class Lexer {
    private static char SEPARATOR = ' ';

    private List<String> tokenTypes;
    private List<Regex> regexes;
    private List<DFA> automata;

    private int scannedChar;
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
            System.out.println("Definitions file not found");
            e.printStackTrace();
        }
        catch (IOException e) {
            System.out.println("I/O Error");
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
            System.out.println("I/O Error while scanning program file");
            e.printStackTrace();
        }
        List<LexToken> temp = new ArrayList<>(tokens);
        cleanup();
        return temp;
    }

    private void makeTokens() throws IOException {
        int ch;
        StringBuilder lexeme = new StringBuilder();

        while ((ch = getChar()) != -1) {
            advanceAutomata(ch); // advance those automata which are not in dead state

            if (allAutomataInDeadState()) {
                if (anyAutomatonWasInFinalState()) {
                    // we have a token in this case
                    handleMatch(lexeme);
                }
                else {
                    // TODO: what happens here? i have no idea...
                    consumeChar();
                }

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
    }

    private void setup(String programFilePath) {
        inFinalStateCache = new ArrayList<>();
        for (int i = 0; i < automata.size(); i++)
            inFinalStateCache.add(false);
        scannedChar = -1;
        tokens = new ArrayList<>();
        try {
            programFileReader = new BufferedReader(new FileReader(programFilePath));
        }
        catch (FileNotFoundException e) {
            System.out.println("Program file not found");
            e.printStackTrace();
        }
    }

    private void cleanup() {
        inFinalStateCache = null;
        scannedChar = -1;
        tokens = null;
        try {
            programFileReader.close();
        }
        catch (IOException e) {
            System.out.println("I/O Error while closing program file");
            e.printStackTrace();
        }
    }

    private void handleMatch(StringBuilder lexeme) {
        String type = "Unknown";
        for (int idx = 0; idx < automata.size(); idx++) {
            if (inFinalStateCache.get(idx)) {
                type = tokenTypes.get(idx);
                break;
            }
        }

        System.out.println(String.format("TOKEN: type->%s, lexeme->%s", type, lexeme.toString()));
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