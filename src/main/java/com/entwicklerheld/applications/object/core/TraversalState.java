package com.entwicklerheld.applications.object.core;

import lombok.Data;
import org.apache.commons.collections4.SetUtils;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * The {@code TraversalState} class represents the state of a traversal operation
 * within a graph, specifically an Ordered Binary Decision Diagram (OBDD).
 * This class is designed to keep track of the current node being traversed,
 * the iterator for the next nodes in the traversal sequence, and a set of
 * visited nodes. The use of this class helps in preventing cycles during
 * traversal, ensuring that each node is visited only once.
 *
 * <p>
 * The {@code TraversalState} class encapsulates the following:
 * <ul>
 *     <li>The current node being processed in the traversal.</li>
 *     <li>An iterator for the next nodes to be visited, allowing for efficient traversal.</li>
 *     <li>A synchronized set of visited nodes, ensuring thread-safe operations
 *     when working with potentially concurrent traversals.</li>
 * </ul>
 * </p>
 *
 * <p>
 * The class is primarily used in the context of graph traversal algorithms, where
 * it provides the necessary information to track the state of the traversal
 * process. This is particularly important in graph structures like OBDDs,
 * where cycles may occur due to the nature of the relationships between nodes.
 * </p>
 *
 * <p>
 * Example usage:
 * <pre>
 *     OBDDObject<?, ?> startNode = ...; // Some starting node
 *     TraversalState state = new TraversalState(startNode);
 * </pre>
 * In this example, a new instance of {@code TraversalState} is created
 * with the specified starting node, ready for traversal operations.
 * </p>
 *
 * @see OBDDObject
 */
@Data
public class TraversalState {
    /**
     * The current node being traversed in the graph.
     * This node represents the point in the traversal where
     * the algorithm is currently operating.
     */
    OBDDObject<?, ?> currentNode;

    /**
     * An iterator for the next nodes to be visited in the traversal.
     * This iterator facilitates the process of moving through the
     * graph and allows for sequential access to the next nodes
     * based on the traversal logic defined elsewhere in the program.
     */
    private Iterator<OBDDObject<?, ?>> nextNodesIterator;

    /**
     * A synchronized set that keeps track of the nodes that have already
     * been visited during the traversal. This prevents cycles in the
     * graph by ensuring that each node is only processed once.
     * <p>
     * The use of a synchronized set ensures thread safety, making this
     * traversal state suitable for concurrent execution scenarios where
     * multiple threads may interact with the same graph structure.
     * </p>
     */
    private final Set<OBDDObject<?, ?>> visited = SetUtils.synchronizedSet(new LinkedHashSet<>());

    /**
     * Constructs a {@code TraversalState} with the specified starting node.
     *
     * @param startNode The node from which to begin traversal.
     *                  This node becomes the current node for the traversal state.
     */
    public TraversalState(OBDDObject<?, ?> startNode) {
        this.currentNode = startNode;
    }
}