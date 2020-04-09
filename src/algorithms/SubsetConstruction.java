package algorithms;

import automata.DFA;
import automata.NFA;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.LinkedList;

public class SubsetConstruction {

    public static DFA convert(final NFA n) {
        NFA nfa = new NFA(n);

        DFA dfa = new DFA(1, 0, new TreeSet<>());

        int start = nfa.getStartState();
        Set<Character> alphabets = nfa.getAlphabets();

        // Initialization
        Map<Integer, Set<Integer>> subsets = new TreeMap<>();
        subsets.put(0, nfa.epsilonClosure(start));
        LinkedList<Integer> unmarkedDfaStates = new LinkedList<Integer>();
        unmarkedDfaStates.push(0);


        // Loop to form subsets
        while (!unmarkedDfaStates.isEmpty()) {
            int curDfaState = unmarkedDfaStates.pop();
            Set<Integer> curNfaStates = subsets.get(curDfaState);

            for (Character a : alphabets) {
                Set<Integer> u = nfa.epsilonClosure(nfa.move(curNfaStates, a));
                if (u.isEmpty()) continue;

                Integer newDfaState = null;
                if (!subsets.containsValue(u)) {
                    newDfaState = subsets.size();
                    subsets.put(newDfaState, u);
                    unmarkedDfaStates.push(newDfaState);

                    dfa.increaseNumberOfStates(1);

                    // check if new state is a final state
                    boolean isFinal = false;
                    for (int nfaState : u) {
                        if (nfa.getFinalStates().contains(nfaState)) {
                            isFinal = true;
                            break;
                        }
                    }
                    if (isFinal) {
                        Set<Integer> dfaFinalStates = dfa.getFinalStates();
                        dfaFinalStates.add(newDfaState);
                        dfa.setFinalStates(dfaFinalStates);
                    }
                } else {
                    for (Integer k : subsets.keySet()) {
                        if (subsets.get(k).equals(u)) {
                            newDfaState = k;
                            break;
                        }
                    }
                }

                dfa.addTransition(curDfaState, a, newDfaState);
            }
        }
        return dfa;
    }

    public static void main(String[] args) {
        NFA nfa = new NFA(5, 0, Arrays.asList(2, 4));

        nfa.addEpsilonTransition(0, 1);
        nfa.addEpsilonTransition(0, 3);
        nfa.addNormalTransition(1, 'a', 2);
        nfa.addNormalTransition(2, 'a', 2);
        nfa.addNormalTransition(3, 'b', 4);
        nfa.addNormalTransition(4, 'b', 4);
        nfa.reset();

        DFA dfa = convert(nfa);

        System.out.println("Dfa no of states:" + dfa.getNumberOfStates());

        for (int i = 0; i < args[0].length(); i++) {
            dfa.advance(args[0].charAt(i));
        }

        if (dfa.isInFinalState()) System.out.println("String accepted");
        else System.out.println("String rejected");
    }
}
