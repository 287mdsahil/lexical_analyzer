package automata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;

import utils.StringEscapeUtils;

public class NFA {
    private int numberOfStates, startState;
    private Set<Integer> currentStates, finalStates;
    private ArrayList<Map<Character, Set<Integer>>> normalTransitions;
    private ArrayList<Set<Integer>> epsilonTransitions;

    public NFA(int numberOfStates, int startState, Collection<Integer> finalStates) {
        if (startState >= numberOfStates || !areValuesInRange(numberOfStates, finalStates))
            throw new IllegalArgumentException("States values must be less than number of states");

        this.numberOfStates = numberOfStates;
        this.startState = startState;
        this.currentStates = new TreeSet<>();
        this.finalStates = new TreeSet<>(finalStates);

        initNormalTransitions();
        initEpsilonTransitions();
    }

    public NFA(NFA other) {
        this.numberOfStates = other.numberOfStates;
        this.startState = other.startState;
        this.currentStates = new TreeSet<>(other.currentStates);
        this.finalStates = new TreeSet<>(other.finalStates);

        initNormalTransitions(other.normalTransitions);
        initEpsilonTransitions(other.epsilonTransitions);
    }

    private void initNormalTransitions() {
        normalTransitions = new ArrayList<>(numberOfStates);
        for (int idx = 0; idx < numberOfStates; idx++)
            normalTransitions.add(new TreeMap<>());
    }

    private void initNormalTransitions(ArrayList<Map<Character, Set<Integer>>> transitions) {
        normalTransitions = new ArrayList<>(numberOfStates);
        for (int idx = 0; idx < numberOfStates; idx++) {
            normalTransitions.add(new TreeMap<>());
            for (Map.Entry<Character, Set<Integer>> e : transitions.get(idx).entrySet()) {
                normalTransitions.get(idx).put(e.getKey(), new TreeSet<>(e.getValue()));
            }
        }
    }

    private void initEpsilonTransitions() {
        epsilonTransitions = new ArrayList<>(numberOfStates);
        for (int idx = 0; idx < numberOfStates; idx++)
            epsilonTransitions.add(new TreeSet<>());
    }

    private void initEpsilonTransitions(ArrayList<Set<Integer>> transitions) {
        epsilonTransitions = new ArrayList<>(numberOfStates);
        for (int idx = 0; idx < numberOfStates; idx++)
            epsilonTransitions.add(new TreeSet<>(transitions.get(idx)));
    }

    // check for 0 <= values[i] < max
    private static boolean areValuesInRange(int max, Iterable<Integer> values) {
        for (int v : values)
            if (v < 0 || v >= max)
                return false;

        return true;
    }

    public void addNormalTransition(int from, char on, int to) {
        if (from < 0 || from >= numberOfStates || to < 0 || to >= numberOfStates)
            throw new IllegalArgumentException("Invalid state value");

        Map<Character, Set<Integer>> transitions = normalTransitions.get(from);
        transitions.putIfAbsent(on, new TreeSet<>());
        transitions.get(on).add(to);
    }

    public void addEpsilonTransition(int from, int to) {
        if (from < 0 || from >= numberOfStates || to < 0 || to >= numberOfStates)
            throw new IllegalArgumentException("Invalid state value");

        Set<Integer> states = epsilonTransitions.get(from);
        states.add(to);
    }

    public ArrayList<Map<Character, Set<Integer>>> getNormalTransitions() {
        return normalTransitions;
    }

    public ArrayList<Set<Integer>> getEpsilonTransitions() {
        return epsilonTransitions;
    }

    public Set<Integer> epsilonClosure(int state) {
        if (state < 0 || state >= numberOfStates)
            throw new IllegalArgumentException("Invalid state value");

        return epsilonClosure(Collections.singleton(state));
    }

    public Set<Integer> epsilonClosure(Set<Integer> states) {
        if (!areValuesInRange(numberOfStates, states))
            throw new IllegalArgumentException("Invalid state value");

        Set<Integer> epsilonClosure = new TreeSet<>(states);

        Stack<Integer> stk = new Stack<>();
        stk.addAll(states);

        while (!stk.empty()) {
            Integer from = stk.pop();

            for (Integer to : epsilonTransitions.get(from)) {
                if (!epsilonClosure.contains(to)) {
                    epsilonClosure.add(to);
                    stk.push(to);
                }
            }
        }

        return epsilonClosure;
    }

    public Set<Integer> move(Set<Integer> states, char on) {
        if (!areValuesInRange(numberOfStates, states))
            throw new IllegalArgumentException("Invalid state value");

        Set<Integer> reachableStates = new TreeSet<>();

        for (Integer state : states) {
            Set<Integer> reachableFromCurrent = normalTransitions.get(state).get(on);
            if (reachableFromCurrent != null)
                reachableStates.addAll(reachableFromCurrent);
        }

        return reachableStates;
    }

    public void advance(char symbol) {
        currentStates = epsilonClosure(move(currentStates, symbol));
    }

    public void advance(String s) {
        for (int idx = 0; idx < s.length(); idx++) {
            advance(s.charAt(idx));
        }
    }

    public void reset() {
        currentStates = epsilonClosure(startState);
    }

    public int getNumberOfStates() {
        return numberOfStates;
    }

    public void increaseNumberOfStates(int by) {
        if (by <= 0)
            throw new IllegalArgumentException("Number of states must increase by some positive value");

        for (int idx = 0; idx < by; idx++) {
            normalTransitions.add(new TreeMap<>());
            epsilonTransitions.add(new TreeSet<>());
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

    public Set<Integer> getCurrentStates() {
        return new TreeSet<>(currentStates);
    }

    public void setFinalStates(Collection<Integer> finalStates) {
        if (!areValuesInRange(numberOfStates, finalStates))
            throw new IllegalArgumentException("Invalid state value");

        this.finalStates = new TreeSet<>(finalStates);
    }

    public boolean isInFinalState() {
        for (Integer state : currentStates)
            if (finalStates.contains(state))
                return true;

        return false;
    }

    public Set<Character> getAlphabet() {
        Set<Character> alphabet = new TreeSet<>();

        for (Map<Character, Set<Integer>> t : normalTransitions) {
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
        sb.append("    Epsilon Transitions: \n");
        sb.append(String.format("        %5s %s\n", "STATE", "TRANSITIONS"));
        for (int idx = 0; idx < epsilonTransitions.size(); idx++) {
            sb.append(
                String.format("        %5d %s\n", 
                    idx, StringEscapeUtils.escape(epsilonTransitions.get(idx).toString())
                )
            );
        }
        sb.append("    Normal Transitions: \n");
        for (int idx = 0; idx < normalTransitions.size(); idx++) {
            sb.append("        State " + idx + "\n");
            sb.append(String.format("            %6s %s\n", "SYMBOL", "TRANSITIONS"));
            for (Map.Entry<Character, Set<Integer>> e : normalTransitions.get(idx).entrySet()) {
                sb.append(
                    String.format(
                        "            %6s %s\n", 
                        StringEscapeUtils.getRepresentation(e.getKey()),
                        StringEscapeUtils.escape(e.getValue().toString())
                    )
                );
            }
        }
        sb.setLength(sb.length() - 1);
        return sb.toString();
    }

    public static void main(String[] args) {
        NFA nfa = new NFA(5, 0, List.of(2, 4));

        nfa.addEpsilonTransition(0, 1);
        nfa.addEpsilonTransition(0, 3);
        nfa.addNormalTransition(1, 'a', 2);
        nfa.addNormalTransition(2, 'a', 2);
        nfa.addNormalTransition(3, 'b', 4);
        nfa.addNormalTransition(4, 'b', 4);
        nfa.addNormalTransition(4, 'c', 4);
        nfa.reset();

        System.out.println("Alphabet: " + nfa.getAlphabet());
        System.out.println(nfa.currentStates);
        System.out.println();

        for (int i = 0; i < args[0].length(); i++) {
            nfa.advance(args[0].charAt(i));

            System.out.println(nfa.currentStates);
            System.out.println();
        }
    }
}
