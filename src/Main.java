import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lexer.LexToken;
import lexer.Lexer;

public class Main {
    public static void main(String[] args) {
        if (args.length != 2 && args.length != 3) {
            showExecutionFormat();
            return;
        }

        String regexFilePath = args[0], programFilePath = args[1], outputFilePath = null;
        if (args.length == 3) outputFilePath = args[2];

        Lexer lexer = new Lexer(regexFilePath);

        System.out.println("Starting tokenization. Warnings will be emitted on invalid matches.\n");

        List<LexToken> allTokens = lexer.tokenize(programFilePath),
            nonWhitespaceTokens = filterWhitespaces(allTokens);

        System.out.println("\nFinished tokenization.");

        if (outputFilePath == null) {
            System.out.println("\n\nTokens Found:");
            dumpTokens(nonWhitespaceTokens);
        }
        else {
            System.out.println("\n\nWriting tokens to file: " + outputFilePath);
            writeToFile(nonWhitespaceTokens, outputFilePath);
        }

    }

    private static List<LexToken> filterWhitespaces(List<LexToken> tokens) {
        List<LexToken> filteredTokens = new ArrayList<>();

        for (LexToken token : tokens) {
            if (!token.tokenType.equals("WHITESPACE"))
                filteredTokens.add(token);
        }
        
        return filteredTokens;
    }

    private static void showExecutionFormat() {
        System.out.println(
            "Format: java -cp classpath Main regexFilePath programFilePath [outputFilePath]"
        );
    }

    private static void dumpTokens(List<LexToken> tokens) {
        System.out.println(LexToken.getFormattedHeading());
        for (LexToken token : tokens) {
            System.out.println(token.getFormattedString());
        }
    }

    private static void writeToFile(List<LexToken> tokens, String outputFilePath) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(outputFilePath))) {
            bw.write(LexToken.getFormattedHeading());
            bw.newLine();

            for (LexToken token : tokens) {
                bw.write(token.getFormattedString());
                bw.newLine();
            }
        }
        catch (IOException e) {
            System.err.println("I/O error while writing output file.");
        }
    }
}