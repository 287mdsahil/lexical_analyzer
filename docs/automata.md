Info regarding `automata` package.

[`Back to README.md`](../README.md)

## NFA

**Note**: States of the NFA are integers in the range `[0, numberOfStates)`. Any violation will generate an exception in the respective constructor/method.

```java
public NFA(int numberOfStates, int startState, Collection<Integer> finalStates)
```

Make a new `NFA` instance. Parameters are self-explanatory.

The NFA is in an empty configuration when this contructor is used. The `reset()` method must be **explicitly called** after defining the transitions to move the NFA to the start configuration. 

This behaviour is mainly due to the fact that the transitions of the NFA are not known at this point. Hence the closures cannot be computed.

```java
public NFA(NFA other)
```

Copy constructor. The NFA created is configured **identical** to the NFA `other`, and must be reset to the start configuration **explicitly** if desired.

```java
public void addNormalTransition(int from, char on, int to)
```

Add a directed edge (transition) from state `from` to state `to` on input symbol `on`.

```java
public void addEpsilonTransition(int from, int to)
```

Add an epsilon transition from state `from` to state `to`.

```java
public Set<Integer> epsilonClosure(int state)
```

Find the epsilon closure for the state `state`.

```java
public Set<Integer> epsilonClosure(Set<Integer> states)
```

Find the epsilon closure for the set of states `states`.

```java
public Set<Integer> move(Set<Integer> states, char on)
```

Find the set of states to which there is a transition on symbol `on` from some state in `states`.

```java
public void advance(char symbol)
```

Simulate one NFA step on the given input symbol.

```java
public void advance(String s)
```

Simulate several NFA moves on the given string. Uses `advance(char symbol)` for each successive move.

```java
public void reset()
```

Reset the NFA to the start configuration.

```java
public int getNumberOfStates()
```

Self-explanatory.

```java
public void increaseNumberOfStates(int by)
```

Increase the number of states by a value `by`.

```java
public int getStartState()
```

Self-explanatory.

```java
public void setStartState(int startState)
```

Self-explanatory.

```java
public Set<Integer> getFinalStates()
```

Self-explanatory.

```java
public void setFinalStates(Collection<Integer> finalStates)
```

Self-explanatory.

```java
public boolean isInFinalState()
```

Check whether the NFA is in the final state.

```java
public static void main(String[] args)
```

Basic runner/tester.

## DFA

**Note**: States of the NFA are integers in the range `[0, numberOfStates)`. Any violation will generate an exception in the respective constructor/method.

### Availabe public methods

```java
public DFA(int numberOfStates, int startState, Collection<Integer> finalStates)
```
Constructor

```java
public DFA(DFA other)
```
Copy Constructor. The new dfa created will be exactly the same as the other dfa. The current states will also be the same.

```java
public void addTransition(int from, char on, int to)
```
Adds a transition to the dfa.

```java
public void advance(char ch)
```
Advances the dfa. Takes the input character as arguement.

```java
public void reset()
```
Resets the current state to the start state.

```java
public int getNumberOfStates()
```
Returns the number of states.

```java
public void increaseNumberOfStates(int by)
```
Increases the number of states by the given arguement. (The arguement must be a positive integer)

```java
public int getStartState()
```
Returns the Id(integer) of the start state.

```java
public void setStartState(int startState)
```
Sets the state corresponding to the provided id as the start state.

```java
public Set<Integer> getFinalStates()
```
Returns the set of final states.

```java
public void setFinalStates(Collection<Integer> finalStates)
```
Sets the states corresponding to the provided collection of integer ids as the finals states.

```java
public boolean isInFinalState()
```
Returns true if the current state is a final state. Else returns false.

```java
public static void main(String[] args)
```
A simple tester function
