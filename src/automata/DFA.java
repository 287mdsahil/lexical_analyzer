package automata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import utils.StringEscapeUtils;

public class DFA {
    public static final int DEAD_STATE = -1;

    private int numberOfStates, startState, currentState;
    private Set<Integer> finalStates;
    private ArrayList<Map<Character, Integer>> transitions;

    public DFA(int numberOfStates, int startState, Collection<Integer> finalStates) {
        if (startState >= numberOfStates || !areValuesInRange(numberOfStates, finalStates))
            throw new IllegalArgumentException("States values must be less than number of states");

        this.numberOfStates = numberOfStates;
        this.startState = startState;
        this.currentState = startState;
        this.finalStates = new TreeSet<>(finalStates);

        initTransitions();
    }

    public DFA(DFA other) {
        this.numberOfStates = other.numberOfStates;
        this.startState = other.startState;
        this.currentState = other.currentState;
        this.finalStates = new TreeSet<>(other.finalStates);

        initTransitions(other.transitions);
    }

    private void initTransitions() {
        transitions = new ArrayList<>(numberOfStates);
        for (int idx = 0; idx < numberOfStates; idx++) transitions.add(new TreeMap<>());
    }

    private void initTransitions(ArrayList<Map<Character, Integer>> otherTransitions) {
        transitions = new ArrayList<>(numberOfStates);
        for (int idx = 0; idx < numberOfStates; idx++) {
            transitions.add(new TreeMap<>());
            for (Map.Entry<Character, Integer> e : otherTransitions.get(idx).entrySet()) {
                transitions.get(idx).put(e.getKey(), e.getValue());
            }
        }
    }

    private static boolean areValuesInRange(int max, Iterable<Integer> values) {
        for (int v : values) if (v < 0 || v >= max) return false;
        return true;
    }

    public void addTransition(int from, char on, int to) {
        if (from < 0 || from >= numberOfStates || to < 0 || to >= numberOfStates)
            throw new IllegalArgumentException("Invalid state value");

        Map<Character, Integer> transition = transitions.get(from);
        transition.put(on, to);
    }

    public void advance(char ch) {
        if (isInDeadState()) throw new IllegalStateException("Advancing while in dead state");

        Integer nextState = transitions.get(currentState).get(ch);
        if (nextState == null) {
            currentState = DEAD_STATE;
        } else {
            currentState = nextState;
        }
    }

    public boolean isInDeadState() {
        return currentState == DEAD_STATE;
    }

    public void reset() {
        currentState = startState;
    }

    public int getNumberOfStates() {
        return numberOfStates;
    }

    public void increaseNumberOfStates(int by) {
        if (by <= 0)
            throw new IllegalArgumentException("Number of states must increase by some positive value");

        for (int idx = 0; idx < by; idx++) {
            transitions.add(new TreeMap<>());
        }

        numberOfStates += by;
    }

    public int getStartState() {
        return startState;
    }

    public void setStartState(int startState) {
        if (startState < 0 || startState >= numberOfStates)
            throw new IllegalArgumentException("Invalid state value");

        this.startState = startState;
    }

    public Set<Integer> getFinalStates() {
        return new TreeSet<>(finalStates);
    }

    public void setFinalStates(Collection<Integer> finalStates) {
        if (!areValuesInRange(numberOfStates, finalStates))
            throw new IllegalArgumentException("Invalid state value");

        this.finalStates = new TreeSet<>(finalStates);
    }

    public boolean isInFinalState() {
        return finalStates.contains(currentState);
    }

    public Set<Character> getAlphabet() {
        Set<Character> alphabet = new TreeSet<>();

        for (Map<Character, Integer> t : transitions) {
            alphabet.addAll(t.keySet());
        }

        return alphabet;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("    Start State: " + startState + "\n");
        sb.append("    Final States: " + finalStates + "\n");
        sb.append("    Alphabet: " + StringEscapeUtils.escape(getAlphabet().toString()) + "\n");
        sb.append("    Transitions: " + "\n");
        sb.append(String.format("        %5s %s\n", "STATE", "TRANSITIONS"));
        for (int idx = 0; idx < transitions.size(); idx++) {
            sb.append(String.format("        %5d %s\n", idx, StringEscapeUtils.escape(transitions.get(idx).toString())));
        }
        sb.setLength(sb.length() - 1);

        return sb.toString();
    }

    public static void main(String[] args) {
        DFA dfa = new DFA(5, 0, Arrays.asList(2, 4));

        dfa.addTransition(0, 'a', 1);
        dfa.addTransition(1, 'a', 2);
        dfa.addTransition(2, 'a', 3);
        dfa.addTransition(3, 'b', 4);
        dfa.addTransition(4, 'b', 4);
        dfa.reset();

        System.out.println(dfa.currentState);
        System.out.println();

        for (int i = 0; i < args[0].length(); i++) {
            dfa.advance(args[0].charAt(i));

            System.out.println(dfa.currentState);
            System.out.println();
        }
    }
}
