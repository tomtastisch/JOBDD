package com.entwicklerheld.applications.object.core;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Objects;

/**
 * Represents a node in the Ordered Binary Decision Diagram (OBDDGraph).
 * Each OBDDNode corresponds to a specific decision variable and includes branches
 * that represent the true and false outcomes of that decision.
 *
 * <p>
 * This class is abstract and serves as a base for more concrete implementations of nodes in the OBDDGraph structure.
 * It provides methods for managing edges between nodes and abstract methods that must be implemented
 * by subclasses to define their behavior.
 * </p>
 *
 * <p>
 * Key features of the OBDDNode class include:
 * <ul>
 *     <li>Representation of decision variables and their associated branches.</li>
 *     <li>Management of edges to connect nodes, allowing traversal through the decision diagram.</li>
 *     <li>Comparison capabilities to order nodes based on their decision variable.</li>
 *     <li>Logging of edge additions and removals for tracking graph structure changes.</li>
 * </ul>
 * </p>
 */
@Slf4j
@Getter
public abstract class OBDDNode extends OBDDObject<OBDDNode, OBDDNode> {

    /**
     * The decision variable associated with this OBDDGraph node.
     * <p>
     * This variable represents the specific decision that this node is associated with.
     * Nodes can be compared based on this variable to establish ordering in the OBDDGraph.
     * </p>
     */
    final int variable;

    /**
     * Constructs an OBDDNode with a specified variable and null branches.
     *
     * <p>
     * This constructor initializes the node with a given variable and sets the parent.
     * It also creates null branches for true and false nodes, indicating that no connections
     * are established initially.
     * </p>
     *
     * @param variable The decision variable for this node.
     * @param parent   The parent OBDDObject of this node.
     */
    public OBDDNode(int variable, OBDDObject<?, ?> parent) {
        this(variable, parent, Pair.of(null, null));
    }

    /**
     * Constructs an OBDDNode with a specified variable and branches.
     *
     * <p>
     * This constructor allows for the initialization of the node with specific branches
     * for true and false outcomes. The branches are established as pairs, facilitating
     * the construction of the decision diagram.
     * </p>
     *
     * @param variable The decision variable for this node.
     * @param parent   The parent OBDDObject of this node.
     * @param branches A pair representing the true and false branches.
     */
    public OBDDNode(int variable, @NonNull OBDDObject<?, ?> parent, @NonNull Pair<OBDDNode, OBDDNode> branches) {
        this(variable, parent, branches.getLeft(), branches.getRight());
    }

    /**
     * Constructs an OBDDNode with specified variable, parent, true branch, and false branch.
     *
     * <p>
     * This constructor is the most comprehensive, allowing full initialization of the node
     * with a specified variable, parent, and the true and false branches.
     * It also automatically establishes edges between the current node and the provided branches.
     * </p>
     *
     * @param variable  The decision variable for this node.
     * @param parent    The parent node of this OBDDGraph node.
     * @param trueNode  The true branch of this node.
     * @param falseNode The false branch of this node.
     */
    public OBDDNode(int variable, @NonNull OBDDObject<?, ?> parent, @Nullable OBDDNode trueNode, @Nullable OBDDNode falseNode) {
        super(parent, trueNode, falseNode);
        this.variable = variable;
    }

    /**
     * Abstract method to retrieve the value of the node.
     * Implementations should define how to evaluate the value based on the node's context.
     *
     * @return The boolean value of this node.
     *
     * <p>
     * Subclasses must implement this method to provide the specific logic required to
     * evaluate the decision represented by this node. This is a critical part of the OBDDGraph's
     * functionality, allowing for the evaluation of decisions based on the structure of the diagram.
     * </p>
     */
    public abstract boolean getValue();

    /**
     * Returns a string representation of this OBDDNode.
     *
     * @return A formatted string describing the node, including its variable and branches.
     *
     * <p>
     * This method provides a human-readable representation of the OBDDNode,
     * facilitating debugging and logging of the OBDDGraph structure.
     * </p>
     */
    @Override
    public String toString() {
        String wrapper = StringUtils.join(this.getClass().getSimpleName(),
                "(%s, trueBranch:%s, falseBranch:%s)");

        return wrapper.formatted(this.variable,
                cleanableTransformText(true),
                cleanableTransformText(false));
    }

    /**
     * Transforms the branch representation into a clean string format.
     *
     * @param branch A boolean indicating whether to fetch the true or false branch.
     * @return A string representation of the variable in the specified branch or an empty string if none exists.
     *
     * <p>
     * This private method is used to generate a clean output for the true and false branches
     * when creating the string representation of the node. It checks if the branch exists
     * and returns the variable of the corresponding node or an empty string if the branch is null.
     * </p>
     */
    private String cleanableTransformText(boolean branch) {
        return this.get(branch).map(OBDDNode::getVariable).map(String::valueOf).orElse("''");
    }

    @Override
    public int compareTo(OBDDNode other) {
        return Integer.compare(this.getVariable(), other.getVariable());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), variable); // Fügen Sie hier weitere relevante Attribute hinzu
    }
}
