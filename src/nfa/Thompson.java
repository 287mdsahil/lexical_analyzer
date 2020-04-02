package nfa;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;
import java.lang.Math;

import automata.NFA;
import regex.Regex;
import regex.RegexTree;
import regex.RegexTreeNode;
import regex.RegexTreeNodeType;

public class Thompson {
    Thompson() {
        System.out.println("Initialize Thompson Algorithm");
    }

    public NFA epsilon(char val, int inVal, int outVal) {
        Collection<Integer> out = new ArrayList<Integer>();
        out.add(outVal);
        NFA n = new NFA(2, inVal, out);
        n.addEpsilonTransition(inVal, outVal);
        return n;
    }

    public NFA single(char val, int inVal, int outVal) {
        Collection<Integer> out = new ArrayList<Integer>();
        out.add(outVal);
        NFA n = new NFA(2, inVal, out);
        n.addNormalTransition(inVal, val, outVal);
        return n;
    }

    public NFA kleene(NFA s) {

        int totalStates = s.getNumberOfStates() + 2;
        NFA n = new NFA(totalStates, 0, new TreeSet<>(Arrays.asList(totalStates - 1)));

        ArrayList<Map<Character, Set<Integer>>> snormal = s.getNormalTransition();
        ArrayList<Set<Integer>> sepsilon = s.getEpsilonTransition();
        int increment = 1;
        for (int i = 0; i < snormal.size(); i++) {
            Map<Character, Set<Integer>> trans = snormal.get(i);
            for (Map.Entry<Character, Set<Integer>> entry : trans.entrySet()) {
                Set<Integer> temp = entry.getValue();
                for (Integer j : temp) {
                    //System.out.println("[[" + (i + increment) + ", " + (j.intValue() + increment) + "]]");
                    n.addNormalTransition((i + increment), entry.getKey(), (j.intValue() + increment));
                }
            }
        }
        for (int i = 0; i < sepsilon.size(); i++) {
            Set<Integer> trans = sepsilon.get(i);

            for (Integer j : trans) {
                //System.out.println("epsilon[[" + (i + increment) + ", " + (j.intValue() + increment) + "]]");
                n.addEpsilonTransition((i + increment), (j.intValue() + increment));
            }
        }
        int s_start = s.getStartState() + increment;
        int s_final = s.getFinalStates().iterator().next().intValue() + increment;
        n.addEpsilonTransition(s_final, s_start);
        n.addEpsilonTransition(0, 1);
        n.addEpsilonTransition(s_final, totalStates - 1);
        n.addEpsilonTransition(0, totalStates - 1);

        n.setStartState(0);
        n.setFinalStates(new TreeSet<>(Arrays.asList(totalStates - 1)));
        return n;
    }

    public NFA union(NFA s, NFA t) {
        int totalState = s.getNumberOfStates() + t.getNumberOfStates() + 2;
        NFA n = new NFA(totalState, 0, Arrays.asList(totalState - 1));

        ArrayList<Map<Character, Set<Integer>>> tnormal = t.getNormalTransition();
        ArrayList<Set<Integer>> tepsilon = t.getEpsilonTransition();
        ArrayList<Map<Character, Set<Integer>>> snormal = s.getNormalTransition();
        ArrayList<Set<Integer>> sepsilon = s.getEpsilonTransition();
        int increment = 1;

        // copy all transitions from s
        for (int i = 0; i < snormal.size(); i++) {
            Map<Character, Set<Integer>> trans = snormal.get(i);
            for (Map.Entry<Character, Set<Integer>> entry : trans.entrySet()) {
                Set<Integer> temp = entry.getValue();
                for (Integer j : temp) {
                    //System.out.println("[[" + (i + increment) + ", " + (j.intValue() + increment) + "]]");
                    n.addNormalTransition((i + increment), entry.getKey(), (j.intValue() + increment));
                }
            }
        }
        for (int i = 0; i < sepsilon.size(); i++) {
            Set<Integer> trans = sepsilon.get(i);

            for (Integer j : trans) {
                //System.out.println("epsilon[[" + (i + increment) + ", " + (j.intValue() + increment) + "]]");
                n.addEpsilonTransition((i + increment), (j.intValue() + increment));
            }

        }
        n.addEpsilonTransition(0, 1);
        // copy all transitions from t
        increment = increment + s.getNumberOfStates();
        for (int i = 0; i < tnormal.size(); i++) {
            Map<Character, Set<Integer>> trans = tnormal.get(i);
            for (Map.Entry<Character, Set<Integer>> entry : trans.entrySet()) {
                Set<Integer> temp = entry.getValue();
                for (Integer j : temp) {
                    //System.out.println("[[" + (i + increment) + ", " + (j.intValue() + increment) + "]]");
                    n.addNormalTransition((i + increment), entry.getKey(), (j.intValue() + increment));
                }
            }
        }
        for (int i = 0; i < tepsilon.size(); i++) {
            Set<Integer> trans = tepsilon.get(i);

            for (Integer j : trans) {
                //System.out.println("epsilon[[" + (i + increment) + ", " + (j.intValue() + increment) + "]]");
                n.addEpsilonTransition((i + increment), (j.intValue() + increment));
            }
        }
        n.addEpsilonTransition(0, s.getNumberOfStates() + 1);
        int sfinal, tfinal;
        sfinal = s.getFinalStates().iterator().next().intValue();
        tfinal = t.getFinalStates().iterator().next().intValue();
        n.addEpsilonTransition(sfinal + 1, totalState - 1);
        n.addEpsilonTransition(tfinal + increment, totalState - 1);
        n.setFinalStates(new TreeSet<>(Arrays.asList(totalState - 1)));
        return n;
    }
    
    public NFA concat(NFA s, NFA t) {
        NFA n = new NFA(s);
        Set<Integer> f = s.getFinalStates();
        n.increaseNumberOfStates(t.getNumberOfStates() - 1);// except the start state of t
        int start = t.getStartState();
        ArrayList<Map<Character, Set<Integer>>> normal = t.getNormalTransition();
        ArrayList<Set<Integer>> epsilon = t.getEpsilonTransition();
        int increment = s.getNumberOfStates() - 1;
        System.out.println("Increment value is " + increment);

        // copy all the transitions of t into n, except the one from start state of t
        for (int i = 0; i < normal.size(); i++) {
            if (i != start) {
                Map<Character, Set<Integer>> trans = normal.get(i);
                for (Map.Entry<Character, Set<Integer>> entry : trans.entrySet()) {
                    Set<Integer> temp = entry.getValue();
                    for (Integer j : temp) {
                        //System.out.println("[[" + (i + increment) + ", " + (j.intValue() + increment) + "]]");
                        n.addNormalTransition((i + increment), entry.getKey(), (j.intValue() + increment));
                    }
                }
            } else// from start state of t
            {
                Map<Character, Set<Integer>> trans = normal.get(i);
                for (Map.Entry<Character, Set<Integer>> entry : trans.entrySet()) {
                    Set<Integer> temp = entry.getValue();
                    for (Integer j : temp) {
                        f.forEach((elem) -> {
                            //System.out.println("[.[" + elem + ", " + (j.intValue() + increment) + "].]");
                            n.addNormalTransition(elem, entry.getKey(), (j.intValue() + increment));// make transition
                                                                                                    // from final of s
                        });
                    }
                }
            }
        }

        for (int i = 0; i < epsilon.size(); i++) {
            if (i != start) {
                Set<Integer> trans = epsilon.get(i);

                for (Integer j : trans) {
                    //System.out.println("epsilon[[" + (i + increment) + ", " + (j.intValue() + increment) + "]]");
                    n.addEpsilonTransition((i + increment), (j.intValue() + increment));
                }

            } else// from start state of t
            {
                Set<Integer> trans = epsilon.get(i);
                for (Integer j : trans) {
                    f.forEach((elem) -> {
                        //System.out.println("epsilon[.[" + elem + ", " + (j.intValue() + increment) + "].]");
                        n.addEpsilonTransition(elem, (j.intValue() + increment));// make transition from final of s
                    });
                }
            }
        }

        int fin = t.getFinalStates().iterator().next().intValue();
        n.setFinalStates(new TreeSet<>(Arrays.asList(fin + increment)));
        return n;
    }
    
    public NFA traverse(RegexTreeNode root) {

        if (root.getLeftChild() == null && root.getRightChild() == null) {
            if (root.getType() == RegexTreeNodeType.CHAR) {
                // add NFA for transition on a single character
                NFA s = single(root.getValue(), 0, 1);
                return s;
            } else {
                // add NFA for epsilon transition
                NFA e = epsilon(root.getValue(), 0, 1);
                return e;
            }
        }

        if (root.getType() == RegexTreeNodeType.CONCAT) {
            RegexTreeNode left = root.getLeftChild();
            RegexTreeNode right = root.getRightChild();
            NFA c = concat(traverse(left), traverse(right));
            return c;
        } else if (root.getType() == RegexTreeNodeType.UNION) {
            RegexTreeNode left = root.getLeftChild();
            RegexTreeNode right = root.getRightChild();
            NFA u = union(traverse(left), traverse(right));
            return u;
        } else if (root.getType() == RegexTreeNodeType.CLOSURE) {
            RegexTreeNode left = root.getLeftChild();
            NFA cl = kleene(traverse(left));
            return cl;
        }

        System.out.println("Should not print this!");
        return new NFA(2, 0, List.of(1));
    }
    
    public static void main(String args[]) {
        String str = "a*a";
        Thompson t = new Thompson();
        System.out.println(str);
        Regex reg = new Regex(str);
        RegexTree tree = new RegexTree(reg);
        System.out.println(tree);

        NFA imran = t.traverse(tree.getRoot());
        imran.reset();

        System.out.println(imran.getNumberOfStates());
        System.out.println(imran.getStartState());
        System.out.println(imran.getFinalStates());
        System.out.println(imran.getCurrentStates());

        //testing
        System.out.println("NFA iteration");
        for (int i = 0; i < args[0].length(); i++) {
            imran.advance(args[0].charAt(i));
            System.out.println(imran.getCurrentStates());
            System.out.println();
        }
    }
}
