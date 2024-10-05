package com.entwicklerheld.applications.object.core.logical.comparator;

import com.entwicklerheld.applications.object.core.AbstractOBDDGraph;
import com.entwicklerheld.applications.object.core.OBDDObject;
import com.entwicklerheld.applications.object.core.TraversalState;
import com.entwicklerheld.applications.object.core.logical.Logical;
import com.entwicklerheld.applications.object.exception.NotInitializedException;
import com.entwicklerheld.applications.object.objects.node.DecisionNode;
import com.entwicklerheld.applications.object.objects.node.TerminalNode;
import lombok.Data;
import lombok.NonNull;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SynchronousSink;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * The OBDDComparator class implements the Comparator interface for comparing
 * instances of AbstractOBDDGraph. This class is designed to perform logical comparisons
 * between Ordered Binary Decision Diagrams (OBDDs), which are utilized in
 * formal logic and computer science.
 * <p>
 * The primary objectives of the class are:
 * 1. To perform logical comparisons between OBDD graphs using logical operations
 * (AND, OR, etc.).
 * 2. To determine the size of the OBDD graphs to assist in comparisons.
 * </p>
 * This class enables developers to compare OBDD graphs to identify their logical
 * equivalences and differences, which is useful in various applications such as
 * optimization, formal verification, and decision analysis.
 * <p>
 * Reactive Programming:
 * <br>
 * This class employs reactive programming principles primarily through the use of
 * Reactor types such as {@link Flux} and {@link Mono}. This allows for non-blocking,
 * asynchronous processing of large graph structures, which is particularly beneficial
 * for operations on complex data like OBDDs.
 * <br>
 * Flux: A publisher type that emits 0 to n elements.
 * Mono: A publisher type that emits either 0 or 1 element.
 * <br>
 * The usage of {@code blockLast()} synchronizes the reactive flow, ensuring that the
 * comparison is completed before proceeding. While this may simplify certain operations,
 * it can lead to blocking behavior that could be improved in future refactorings for
 * a fully asynchronous approach.
 */
@Data
public class OBDDComparator implements Comparator<AbstractOBDDGraph<?, ?, ?>> {

    /**
     * Compares two OBDD graphs for logical equivalence and relative size.
     * <br>
     * This method uses the logical AND operation to compare the two OBDDs.
     * It blocks the execution by utilizing {@code blockLast()} to wait for the result
     * of the comparison. In a fully asynchronous setup, this blocking could be avoided,
     * but for the purpose of this method, it ensures that the comparison is completed
     * before the size comparison is performed.
     *
     * @param o1 The first OBDD instance to compare.
     * @param o2 The second OBDD instance to compare.
     * @return 0 if the OBDDs are equivalent; a negative integer if o1 is smaller
     * than o2; a positive integer if o1 is larger than o2.
     */
    @Override
    public int compare(AbstractOBDDGraph<?, ?, ?> o1, AbstractOBDDGraph<?, ?, ?> o2) {
        // Logical AND operation to compare the OBDDs
        return compare(o1, o2, Logical.AND) ?
                0 :
                Integer.compare(o1.getNodes().size(), o2.getNodes().size());
    }

    /**
     * Compares two OBDD graphs using a specified logical operation.
     * <br>
     * This method is the core of the comparison logic, leveraging asynchronous
     * traversal and comparison of the two OBDD
     * logical operations (e.g., AND, OR) to evaluate equivalence.
     * <br>
     * Note on the use of {@code blockLast()}:
     * <br>
     * {@code blockLast()} is used here to block and wait for the last value from
     * the reactive flow, ensuring that the comparison is completed. This synchronizes
     * the otherwise asynchronous process. In future refactoring, this blocking behavior
     * could be improved to better utilize the advantages of reactivity.
     *
     * @param obdd1   The first OBDD instance to compare.
     * @param obdd2   The second OBDD instance to compare.
     * @param logical The logical operation to apply (AND, OR, etc.).
     * @return True if the two OBDDs are equivalent, according to the specified
     * logical operation; otherwise, false.
     */
    public boolean compare(@NonNull AbstractOBDDGraph<?, ?, ?> obdd1,
                           @NonNull AbstractOBDDGraph<?, ?, ?> obdd2,
                           @NonNull Logical logical) {

        if (obdd1.isInitialized() && obdd2.isInitialized()) {
            boolean b1 = Objects.requireNonNull(compare(obdd1, obdd2, logical, true).block());
            boolean b2 = Objects.requireNonNull(compare(obdd1, obdd2, logical, false).block());
            return logical.getOperation().apply(b1, b2);
        }
        throw new NotInitializedException("One of the OBDDs is not initialized");
    }

    /**
     * Compares two OBDDs based on a specified logical operation.
     * <br>
     * This method returns a Flux of Boolean values resulting from comparing
     * the nodes of the two OBDD instances using the specified logical operation.
     * The comparison is performed in a top-down traversal manner.
     *
     * @param obdd1   The first OBDD instance to compare.
     * @param obdd2   The second OBDD instance to compare.
     * @param logical The logical operation to apply (AND, OR, etc.).
     * @param branch  A boolean indicating whether to consider branches during
     *                the comparison.
     * @return A Flux of Boolean values indicating the results of the node comparisons.
     */
    private Mono<Boolean> compare(@NonNull AbstractOBDDGraph<?, ?, ?> obdd1,
                                  @NonNull AbstractOBDDGraph<?, ?, ?> obdd2,
                                  @NonNull Logical logical,
                                  boolean branch) {

        return Flux.zip(
                        traverse(obdd1.getRoot(), branch),
                        traverse(obdd2.getRoot(), branch)
                ).map(tuple -> logical.getOperation().apply(tuple.getT1().getValue(), tuple.getT2().getValue()))
                .last();
    }

    /**
     * Traverses the OBDD graph starting from a given node in a specified direction.
     * <br>
     * This method will traverse the graph until it reaches a TerminalNode,
     * returning the last TerminalNode encountered during the traversal.
     *
     * @param startNode The starting node of the OBDD graph for traversal.
     * @param branch    A boolean indicating whether to consider branches during
     *                  the traversal.
     * @return A Mono of the last TerminalNode encountered during the traversal.
     */
    private Mono<TerminalNode> traverse(OBDDObject<?, ?> startNode, boolean branch) {

        return Flux.generate(() -> new TraversalState(startNode), (TraversalState state, SynchronousSink<OBDDObject<?, ?>> sink) -> {

                    // Retrieve the current node from the traversal state
                    OBDDObject<?, ?> current = state.getCurrentNode();

                    if (!state.getVisited().add(current)) {
                        sink.complete();
                        return state;
                    }

                    // Emit the current node
                    sink.next(current);

                    // If the current node is a DecisionNode, continue traversing its branches
                    if (current instanceof DecisionNode) {
                        if (state.getNextNodesIterator() == null || !state.getNextNodesIterator().hasNext()) {
                            // Retrieve the next nodes based on the traversal direction
                            OBDDObject<?, ?> nextNode = current.get(branch).orElse(null);

                            // If there are no further nodes to traverse, complete the Flux stream
                            if (Objects.isNull(nextNode)) {
                                sink.complete();
                                return state;
                            }

                            Collection<OBDDObject<?, ?>> singletonNodeList = List.of(nextNode);
                            // Set the iterator for the next nodes to traverse
                            state.setNextNodesIterator(singletonNodeList.iterator());
                        }

                        // Move to the next node in the iteration
                        state.setCurrentNode(state.getNextNodesIterator().next());
                    } else {
                        // If the current node is not a TerminalNode, complete the traversal
                        sink.complete();
                    }

                    return state; // Return the updated state for the next iteration
                })
                .filter(node -> node instanceof TerminalNode) // Filter out only TerminalNodes
                .cast(TerminalNode.class) // Cast the node to TerminalNode
                .next(); // Only take the first TerminalNode and convert the Flux into a Mono
    }

    /**
     * Compares two TerminalNodes using the specified logical operation.
     * <br>
     * This method applies the logical operation to the values of the two
     * TerminalNodes to determine if they are logically equivalent.
     *
     * @param arg0    The first TerminalNode to compare.
     * @param arg1    The second TerminalNode to compare.
     * @param logical The logical operation to apply (AND, OR, etc.).
     * @return True if the values of the two TerminalNodes are logically equivalent;
     * otherwise, false.
     */
    private boolean compareNodes(TerminalNode arg0, TerminalNode arg1, Logical logical) {
        return logical.getOperation().apply(arg0.getValue(), arg1.getValue());
    }
}