package algorithms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import automata.NFA;
import regex.Regex;
import regex.RegexTree;
import regex.RegexTreeNode;
import regex.RegexTreeNodeType;

public class Thompson {

    public static NFA epsilon(char val) {
        NFA n = new NFA(2, 0, Collections.singleton(1));
        n.addEpsilonTransition(0, 1);
        return n;
    }

    public static NFA single(char val) {
        NFA n = new NFA(2, 0, Collections.singleton(1));
        n.addNormalTransition(0, val, 1);
        return n;
    }

    public static NFA kleene(NFA s) {
        int totalStates = s.getNumberOfStates() + 2;
        NFA n = new NFA(totalStates, 0, Collections.singleton(totalStates-1));
        ArrayList<Map<Character, Set<Integer>>> snormal = s.getNormalTransitions();
        ArrayList<Set<Integer>> sepsilon = s.getEpsilonTransitions();
        int increment = 1;
        for (int i = 0; i < snormal.size(); i++) {
            for (Map.Entry<Character, Set<Integer>> entry : snormal.get(i).entrySet()) {
                for (Integer j : entry.getValue())
                    n.addNormalTransition((i + increment), entry.getKey(), (j.intValue() + increment));
            }
        }
        for (int i = 0; i < sepsilon.size(); i++) {
            for (Integer j : sepsilon.get(i))
                n.addEpsilonTransition((i + increment), (j.intValue() + increment));
        }
        n.addEpsilonTransition(s.getFinalStates().iterator().next().intValue() + increment,
                s.getStartState() + increment);
        n.addEpsilonTransition(0, 1);
        n.addEpsilonTransition(s.getFinalStates().iterator().next().intValue() + increment, totalStates - 1);
        n.addEpsilonTransition(0, totalStates - 1);
        n.setStartState(0);
        n.setFinalStates(new TreeSet<>(Arrays.asList(totalStates - 1)));
        return n;
    }

    public static NFA union(NFA s, NFA t) {
        int totalState = s.getNumberOfStates() + t.getNumberOfStates() + 2;
        NFA n = new NFA(totalState, 0, Collections.singleton(totalState-1));

        ArrayList<Map<Character, Set<Integer>>> tnormal = t.getNormalTransitions();
        ArrayList<Set<Integer>> tepsilon = t.getEpsilonTransitions();
        ArrayList<Map<Character, Set<Integer>>> snormal = s.getNormalTransitions();
        ArrayList<Set<Integer>> sepsilon = s.getEpsilonTransitions();
        int increment = 1;
        for (int i = 0; i < snormal.size(); i++) {
            for (Map.Entry<Character, Set<Integer>> entry : snormal.get(i).entrySet()) {
                for (Integer j : entry.getValue())
                    n.addNormalTransition((i + increment), entry.getKey(), (j.intValue() + increment));
            }
        }
        for (int i = 0; i < sepsilon.size(); i++) {
            for (Integer j : sepsilon.get(i))
                n.addEpsilonTransition((i + increment), (j.intValue() + increment));
        }
        n.addEpsilonTransition(0, 1);
        increment = increment + s.getNumberOfStates();
        for (int i = 0; i < tnormal.size(); i++) {
            for (Map.Entry<Character, Set<Integer>> entry : tnormal.get(i).entrySet()) {
                for (Integer j : entry.getValue())
                    n.addNormalTransition((i + increment), entry.getKey(), (j.intValue() + increment));
            }
        }
        for (int i = 0; i < tepsilon.size(); i++) {
            for (Integer j : tepsilon.get(i))
                n.addEpsilonTransition((i + increment), (j.intValue() + increment));
        }
        n.addEpsilonTransition(0, s.getNumberOfStates() + 1);
        n.addEpsilonTransition(s.getFinalStates().iterator().next().intValue() + 1, totalState - 1);
        n.addEpsilonTransition(t.getFinalStates().iterator().next().intValue() + increment, totalState - 1);
        n.setFinalStates(new TreeSet<>(Arrays.asList(totalState - 1)));
        return n;
    }

    public static NFA concat(NFA s, NFA t) {
        NFA n = new NFA(s);
        Set<Integer> f = s.getFinalStates();
        n.increaseNumberOfStates(t.getNumberOfStates() - 1);
        int start = t.getStartState();
        ArrayList<Map<Character, Set<Integer>>> normal = t.getNormalTransitions();
        ArrayList<Set<Integer>> epsilon = t.getEpsilonTransitions();
        int increment = s.getNumberOfStates() - 1;

        for (int i = 0; i < normal.size(); i++) {
            for (Map.Entry<Character, Set<Integer>> entry : normal.get(i).entrySet()) {
                for (Integer j : entry.getValue()) {
                    if (i != start)
                        n.addNormalTransition((i + increment), entry.getKey(), (j.intValue() + increment));
                    else
                        n.addNormalTransition(f.iterator().next().intValue(), entry.getKey(),
                                (j.intValue() + increment));
                }
            }
        }

        for (int i = 0; i < epsilon.size(); i++) {
            for (Integer j : epsilon.get(i)) {
                if (i != start)
                    n.addEpsilonTransition((i + increment), (j.intValue() + increment));
                else
                    n.addEpsilonTransition(f.iterator().next().intValue(), (j.intValue() + increment));
            }
        }

        n.setFinalStates(new TreeSet<>(Arrays.asList(t.getFinalStates().iterator().next().intValue() + increment)));
        return n;
    }

    public static NFA traverse(RegexTreeNode root) {

        if (root.getLeftChild() == null && root.getRightChild() == null)
            return (root.getType() == RegexTreeNodeType.CHAR) ? single(root.getValue()) : epsilon(root.getValue());

        if (root.getType() == RegexTreeNodeType.CONCAT)
            return concat(traverse(root.getLeftChild()), traverse(root.getRightChild()));
        else if (root.getType() == RegexTreeNodeType.UNION)
            return union(traverse(root.getLeftChild()), traverse(root.getRightChild()));
        else if (root.getType() == RegexTreeNodeType.CLOSURE)
            return kleene(traverse(root.getLeftChild()));
        else
            throw new IllegalStateException("Invalid Node type");
    }

    public static NFA convert(Regex regex) {
        RegexTree tree = new RegexTree(regex);

        return traverse(tree.getRoot());
    }

    public static void main(String args[]) {
        String str = "a*a";
        System.out.println(str);
        Regex reg = new Regex(str);
        RegexTree tree = new RegexTree(reg);
        System.out.println(tree);

        NFA imran = traverse(tree.getRoot());
        imran.reset();

        System.out.println(imran.getNumberOfStates());
        System.out.println(imran.getStartState());
        System.out.println(imran.getFinalStates());
        System.out.println(imran.getCurrentStates());

        // testing
        System.out.println("NFA iteration");
        for (int i = 0; i < args[0].length(); i++) {
            imran.advance(args[0].charAt(i));
            System.out.println(imran.getCurrentStates());
            System.out.println();
        }
    }
}
