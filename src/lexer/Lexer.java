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
import automata.NFA;
import regex.Regex;
import regex.RegexTree;
import utils.Buffer;
import utils.StringEscapeUtils;

public class Lexer {
    public static class PairOfInts {
        public final int first, second;
        public PairOfInts(int first, int second) {
            this.first = first;
            this.second = second;
        }
    }

    private static char SEPARATOR = ' ';
    private static boolean CHECK_CRLF = System.lineSeparator().equals("\r\n");

    private boolean verbose;
    private List<String> tokenTypes;
    private List<Regex> regexes;
    private List<DFA> automata;

    private int id, row, col;
    private Buffer buffer;
    private List<List<Boolean>> inFinalStateCache;
    private FileReader programFileReader;
    private List<LexToken> tokens;

    public Lexer(String typeRegexPairsFilePath) {
        this(typeRegexPairsFilePath, true);
    }

    public Lexer(String typeRegexPairsFilePath, boolean verbose) {
        this.verbose = verbose;

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
        Regex r = new Regex(regex);
        RegexTree rt = new RegexTree(r);
        NFA nfa = Thompson.convert(rt);
        DFA dfa = SubsetConstruction.convert(nfa);

        if (verbose) {
            showDetails(tokenType, r, rt, nfa, dfa);
            System.out.println();
        }

        tokenTypes.add(tokenType);
        regexes.add(r);
        automata.add(dfa);
    }

    private void showDetails(String tokenType, Regex r, RegexTree rt, NFA nfa, DFA dfa) {
        System.out.println("Token Type: " + tokenType);
        System.out.println("Regex: " + r.getNormalizedString());
        System.out.println("Regex Tree:\n" + rt);
        System.out.println("NFA:\n" + nfa);
        System.out.println("DFA:\n" + dfa);
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

        while ((ch = buffer.get()) != -1) {
            advanceAutomata(ch); // advance those automata which are not in dead state

            if (allAutomataInDeadState()) {
                handlePossibleMatch();

                resetAllAutomata();
                resetFinalStatesCache();
            }
            else {
                updateFinalStatesCache();
            }
        }

        // we may have an outstanding match/non-match
        if (buffer.size() != 0)
            handlePossibleMatch();
    }

    private void setup(String programFilePath) {
        inFinalStateCache = new ArrayList<>();
        id = 0; row = col = 1;
        tokens = new ArrayList<>();
        try {
            programFileReader = new FileReader(programFilePath);
        }
        catch (FileNotFoundException e) {
            System.err.println("Program file not found");
            e.printStackTrace();
        }
        buffer = new Buffer(programFileReader);
    }

    private void cleanup() {
        inFinalStateCache = null;
        id = row = col = -1;
        tokens = null;
        buffer = null;
        try {
            programFileReader.close();
        }
        catch (IOException e) {
            System.err.println("I/O Error while closing program file");
            e.printStackTrace();
        }
    }

    private void handlePossibleMatch() {
        PairOfInts cacheAndAutomataIndices = getCacheAndAutomataIndicesOfMatch();
        if (cacheAndAutomataIndices != null) {
            handleMatch(cacheAndAutomataIndices);
        }
        else {
            String invalid = buffer.consume(inFinalStateCache.size() + 1);
            System.err.println(
                String.format("Invalid match @ row %d col %d: %s", 
                                row, col, StringEscapeUtils.escape(invalid))
            );
            updateRowCol(invalid);
        }
        buffer.reset();
    }

    private void handleMatch(PairOfInts cacheAndAutomataIndices) {
        String 
            lexeme = buffer.consume(cacheAndAutomataIndices.first + 1),
            type = tokenTypes.get(cacheAndAutomataIndices.second);

        // System.out.println(String.format("%d %d %d %s %s", id, row, col, type, StringEscapeUtils.escape(lexeme)));
        
        tokens.add(new LexToken(id++, row, col, type, lexeme));
        updateRowCol(lexeme);
    }

    private void updateRowCol(String str) {
        for (int idx = 0; idx < str.length(); ) {
            char ch = str.charAt(idx);

            if (CHECK_CRLF && ch == '\r') {
                if (idx < str.length() - 1) {
                    char next = str.charAt(idx + 1);
                    if (next == '\n') {
                        ch = next;
                        idx++;
                    }
                }
            }
            
            if (ch == '\n' || ch == '\r') {
                row++;
                col = 1;
            }
            else {
                col++;
            }
            idx++;
        }
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
        List<Boolean> temp = new ArrayList<>(automata.size());
        for (int idx = 0; idx < automata.size(); idx++)
            temp.add(automata.get(idx).isInFinalState());
        inFinalStateCache.add(temp);
    }

    private void resetFinalStatesCache() {
        inFinalStateCache = new ArrayList<>();
    }

    private PairOfInts getCacheAndAutomataIndicesOfMatch() {
        for (int cacheIdx = inFinalStateCache.size() - 1; cacheIdx >= 0; cacheIdx--)
            for (int autoIdx = 0; autoIdx < automata.size(); autoIdx++)
                if (inFinalStateCache.get(cacheIdx).get(autoIdx))
                    return new PairOfInts(cacheIdx, autoIdx);
        return null;
    }

    public static void main(String[] args) {
        Lexer lex = new Lexer(args[0]);

        lex.tokenize(args[1]);
    }
}
