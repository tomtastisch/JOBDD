package com.entwicklerheld.applications.object.objects;

import com.entwicklerheld.applications.object.core.AbstractOBDDGraph;
import com.entwicklerheld.applications.object.core.OBDDNode;
import com.entwicklerheld.applications.object.core.OBDDObject;
import com.entwicklerheld.applications.object.core.logical.comparator.OBDDComparator;
import com.entwicklerheld.applications.object.exception.NodeNotFoundException;
import com.entwicklerheld.applications.object.objects.edge.Edge;
import com.entwicklerheld.applications.object.objects.node.DecisionNode;
import com.entwicklerheld.applications.object.objects.node.TerminalNode;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.security.InvalidParameterException;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * The OBDDGraph class represents an Ordered Binary Decision Diagram (OBDD),
 * which is a data structure used for efficiently representing Boolean functions.
 * This class provides functionality to create and manage nodes and edges in
 * the OBDD, allowing for the manipulation and querying of its structure.
 *
 * <p>An OBDD consists of nodes that are either decision nodes or terminal nodes.
 * Terminal nodes represent the final values of the Boolean function (true or false),
 * while decision nodes make decisions based on variable values.</p>
 *
 * <p>This class employs logging to track the status of the graph and utilizes
 * Lombok annotations to enhance code clarity and reduce boilerplate.</p>
 */
@Slf4j
@EqualsAndHashCode(callSuper = true)
public class OBDDGraph extends AbstractOBDDGraph<OBDDGraph, OBDDNode, OBDDNode> {

    OBDDComparator comparator = new OBDDComparator();

    private final String msg = "node with id not exists";

    /**
     * Constructs a new OBDDGraph instance, initializing the terminal nodes
     * for both true and false branches. The true branch is assigned a variable
     * value of -1, and the false branch is assigned a value of -2.
     */
    public OBDDGraph() {
        this.setTrueBranch(new TerminalNode(-1, this, true));
        this.setFalseBranch(new TerminalNode(-2, this, false));
        log.info("OBDDGraph initialized with true branch (-1) and false branch (-2).");
    }

    /**
     * Establishes a reference edge between two nodes in the OBDD using their
     * respective integer identifiers. This method throws an exception if either
     * node is not found in the graph.
     *
     * @param source the identifier of the source node from which the edge originates
     * @param target the identifier of the target node to which the edge points
     * @param branch a boolean indicating if the edge is a true branch (true)
     *               or a false branch (false)
     * @return the created Edge object representing the connection between the two nodes
     * @throws InvalidParameterException if either node is not found in the graph
     */
    public Edge setEdgeReference(int source, int target, boolean branch) {
        OBDDNode sourceNode = resolveNodeReference(source)
                .orElseThrow(() -> new NodeNotFoundException(msg));
        OBDDNode targetNode = resolveNodeReference(target)
                .orElseThrow(() -> new NodeNotFoundException(msg));
        return setEdgeReference(sourceNode, targetNode, branch);
    }

    /**
     * Establishes an edge reference between two specified OBDD nodes.
     *
     * @param source the source node from which the edge originates
     * @param target the target node to which the edge points
     * @param branch a boolean indicating if the edge is a true branch (true)
     *               or a false branch (false)
     * @return the created Edge object representing the connection between the two nodes
     */
    public Edge setEdgeReference(@NonNull OBDDNode source, @NonNull OBDDNode target, boolean branch) {
        // Ensure the target node is valid and exists in the unique table
        target = Objects.requireNonNull(this.getUniqueTable().getOrDefault(target.getVariable(), target));

        // Remove the current graph as a parent node if it exists
        if (target.getParentsAsSet().contains(this)) {
            removeEdgeReference(this, target);
        }

        // Establish the edge from source to target
        source.set(branch, target);
        target.addParent(source, branch);

        return new Edge(source, target, branch);
    }

    /**
     * Retrieves the boolean value indicating whether an edge exists between
     * the specified source and target node identifiers.
     *
     * @param source the identifier of the source node
     * @param target the identifier of the target node
     * @return true if the edge exists, false otherwise
     * @throws InvalidParameterException if either node is not found in the graph
     */
    public boolean getEdgeReference(int source, int target) {
        return getEdgeReference(
                resolveNodeReference(source)
                        .orElseThrow(() -> new NodeNotFoundException(msg)),
                resolveNodeReference(target)
                        .orElseThrow(() -> new NodeNotFoundException(msg))
        );
    }

    /**
     * Checks if an edge exists between the specified source and target nodes.
     *
     * @param source the source node
     * @param target the target node
     * @return true if an edge exists from source to target, false otherwise
     */
    public boolean getEdgeReference(@NonNull OBDDNode source, @NonNull OBDDNode target) {
        return Boolean.parseBoolean(target.getParents().get(source).name().toLowerCase());
    }

    /**
     * Removes the edge reference between two nodes identified by their
     * integer identifiers.
     *
     * @param source the identifier of the source node
     * @param target the identifier of the target node
     * @throws InvalidParameterException if either node is not found in the graph
     */
    public void removeEdgeReference(int source, int target) {
        removeEdgeReference(
                resolveNodeReference(source)
                        .orElseThrow(() -> new NodeNotFoundException(msg)),
                resolveNodeReference(target)
                        .orElseThrow(() -> new NodeNotFoundException(msg))
        );
    }

    /**
     * Removes the edge reference between two specified OBDD objects.
     *
     * @param source the source OBDD object
     * @param target the target OBDD object
     */
    public void removeEdgeReference(@NonNull OBDDObject<?, ?> source, @NonNull OBDDObject<?, ?> target) {
        target.removeParent(source);
    }

    /**
     * Adds a new node to the OBDD with the specified variable value.
     * This method automatically connects the new node to the true and
     * false branches of the graph.
     *
     * @param variable the variable value for the new node
     * @return the created OBDDNode object
     */
    public OBDDNode addNode(int variable) {
        return addNode(variable, getTrueBranch(), getFalseBranch());
    }

    /**
     * Adds a new node to the OBDD with the specified variable value,
     * using the provided true and false branches.
     * If a node with the specified variable already exists,
     * the existing node is returned.
     *
     * @param variable    the variable value for the new node
     * @param trueBranch  the node to be set as the true branch
     * @param falseBranch the node to be set as the false branch
     * @return the created or existing OBDDNode object
     */
    public OBDDNode addNode(int variable, @NonNull OBDDNode trueBranch, @NonNull OBDDNode falseBranch) {
        return addNode(variable, this, trueBranch, falseBranch); // Call the more general addNode method
    }

    /**
     * Adds a new node to the OBDD with the specified variable value,
     * the parent node, and true and false branches.
     * It checks if a node with the same variable value exists and
     * reuses it if available, optimizing memory usage.
     *
     * @param variable    the variable value for the new node
     * @param parent      the parent OBDD object, which is the instance of the graph itself
     * @param trueBranch  the node to be set as the true branch
     * @param falseBranch the node to be set as the false branch
     * @return the created or existing OBDDNode object
     */
    public OBDDNode addNode(int variable,
                            @NonNull OBDDObject<?, ?> parent,
                            @Nullable OBDDNode trueBranch,
                            @Nullable OBDDNode falseBranch) {

        AtomicReference<OBDDNode> creation = new AtomicReference<>();

        log.info("Initialization of the new node in the tree structure with variable: {}", variable);

        // Attempt to add or retrieve the node with the given variable
        return getUniqueTable().computeIfAbsent(variable, n -> {
            log.info("\t- Creating new OBDDNode for variable: {}", variable);
            creation.set(new DecisionNode(variable, parent, trueBranch, falseBranch)); // Create a new decision node
            return creation.get(); // Return the newly created node
        });
    }

    public OBDDNode changeNode(OBDDNode updateNode, boolean overwriteBranches) {
        return getUniqueTable().computeIfPresent(
                updateNode.getVariable(),
                (var, node) -> {
                    log.info("\t- Updating node with variable: {}", var);

                    if(!overwriteBranches) {
                        updateNode.set(true, node.getTrueBranch());
                        updateNode.set(false, node.getFalseBranch());
                    }

                    return updateNode;
                }
        );
    }

    /**
     * Retrieves the true branch of the OBDD.
     *
     * @return the true branch as an OBDDNode
     */
    @Override
    public OBDDNode getTrueBranch() {
        return super.getTrueBranch();
    }

    /**
     * Retrieves the false branch of the OBDD.
     *
     * @return the false branch as an OBDDNode
     */
    @Override
    public OBDDNode getFalseBranch() {
        return super.getFalseBranch();
    }

    /**
     * Closes the OBDDGraph and disposes of any resources associated with it.
     * This method is intended for cleanup and should be called when the graph
     * is no longer necessary to prevent memory leaks.
     */
    @Override
    public void close() {
        this.getScheduler().dispose(); // Dispose of the scheduler resources
    }

    /**
     * Resolves a node reference by its identifier, returning an Optional
     * containing the corresponding OBDDNode if found. If the node does not
     * exist in the graph, an empty Optional is returned.
     *
     * @param nodeReference the identifier of the node to resolve
     * @return an Optional containing the corresponding OBDDNode object,
     * or an empty Optional if the node is not found
     * @throws InvalidParameterException if the node with the specified identifier does not exist
     */
    @Override
    protected Optional<OBDDNode> resolveNodeReference(int nodeReference) {
        return Optional.ofNullable(getUniqueTable().get(nodeReference));
    }

    public int compareTo(AbstractOBDDGraph<?, ?, ?> other) {
        return comparator.compare(this, other);
    }
}