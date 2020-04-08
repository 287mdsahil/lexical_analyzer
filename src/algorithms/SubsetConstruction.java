package algorithms;

import automata.DFA;
import automata.NFA;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class SubsetConstruction {

  public static DFA convert(final NFA n) {
    NFA nfa = new NFA(n);

    DFA dfa = new DFA(1, 0, new TreeSet<>());

    int start = nfa.getStartState();
    Set<Character> alphabets = nfa.getAlphabets();
    System.out.println(alphabets);

    // Initialization
    Map<Integer, Set<Integer>> subsets = new TreeMap<>();
    subsets.put(0, nfa.epsilonClosure(start));
    List<Integer> unmarked_dfa_states = new ArrayList<>();
    unmarked_dfa_states.add(0);

    // Loop to form subsets
    while (!unmarked_dfa_states.isEmpty()) {
      int cur_dfa_state = unmarked_dfa_states.get(0);
      unmarked_dfa_states.remove(0);
      Set<Integer> cur_nfa_states = subsets.get(cur_dfa_state);

      for (Character a : alphabets) {
        Set<Integer> u = nfa.epsilonClosure(nfa.move(cur_nfa_states, a));
        System.out.println("Ec:" + u + " of:" + cur_nfa_states + " on:" + a);
        if (!u.isEmpty()) {
          Integer new_dfa_state = null;
          if (!subsets.containsValue(u)) {
            new_dfa_state = subsets.size();
            subsets.put(new_dfa_state, u);
            unmarked_dfa_states.add(new_dfa_state);
            System.out.print("New Subset:");
            System.out.println(u);

            dfa.increaseNumberOfStates(1);
          } else {
	    for(Integer k : subsets.keySet()) {
              if(subsets.get(k).equals(u)) {
                new_dfa_state = k;
                break;
              }              
	    }
          }

          dfa.addTransition(cur_dfa_state, a, new_dfa_state);
          System.out.print("Transition added:");
          System.out.println("From:" + cur_dfa_state + " to:" + new_dfa_state + " on:" + a);
        }
      }

      System.out.println("Unmarked:" + unmarked_dfa_states);
    }

    System.out.println(subsets);

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

    System.out.println(dfa.getNumberOfStates());
    System.out.println(dfa.transitions);


    for (int i = 0; i < args[0].length(); i++) {
      dfa.advance(args[0].charAt(i));

      System.out.println(dfa.currentState);
      // System.out.println();
    }
  }
}
