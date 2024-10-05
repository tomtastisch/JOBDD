package com.entwicklerheld.applications.object.core;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.checkerframework.checker.nullness.qual.Nullable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Represents an abstract object in the Ordered Binary Decision Diagram (OBDDGraph) structure.
 * <p>
 * The {@code OBDDObject} class serves as the fundamental building block of an Ordered Binary Decision Diagram (OBDD),
 * which is a data structure commonly used in computer science for representing and manipulating Boolean functions.
 * Each instance of {@code OBDDObject} represents a node in this diagram and can have a parent node, a true branch,
 * and a false branch, allowing for a structured decision-making process based on Boolean variables.
 * </p>
 *
 * <p>
 * OBDDs are widely used in fields such as symbolic model checking, hardware verification, and logic synthesis, where
 * it is crucial to efficiently represent and manipulate Boolean functions. The structure of an OBDD ensures
 * compactness and facilitates fast operations by removing redundant nodes and sharing sub-graphs.
 * </p>
 *
 * <p>
 * This abstract class uses generics to enable flexibility and type safety when working with various types of
 * OBDD nodes. Subclasses can specify the exact types for the nodes and ensure compatibility within the graph.
 * </p>
 *
 * @param <O> The concrete type of OBDDObject that extends this class. This allows derived classes to maintain
 *            type consistency and type safety within their implementations.
 * @param <T> The type of the child nodes (trueBranch and falseBranch) within the OBDDGraph structure. This
 *            provides flexibility in defining different types of child nodes, facilitating extension and
 *            customization of the OBDD structure.
 */
@Slf4j
@Data
@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class OBDDObject<O extends OBDDObject<O, T>, T extends OBDDObject<T, ?>> implements Comparable<O> {

    protected enum TruthValue {
        TRUE, FALSE, UNKNOWN
    }

    /**
     * A list of parent nodes that reference this OBDDObject within the OBDDGraph structure.
     * <p>
     * In OBDD structures, it is common for nodes to have parent-child relationships, forming a directed acyclic graph.
     * This list holds references to the parent nodes, allowing for upward traversal in the graph. This can be useful
     * for operations such as backtracking or when implementing algorithms like OBDD reduction, where knowing the parent
     * nodes is necessary to modify the structure.
     * </p>
     *
     * <p>
     * This field is excluded from the {@code equals} and {@code hashCode} methods, as the equality of nodes in an OBDD
     * should depend only on the branches they reference (trueBranch and falseBranch), and not on their position in
     * the graph.
     * </p>
     */
    @EqualsAndHashCode.Exclude
    private final Map<OBDDObject<?, ?>, TruthValue> parents = new ConcurrentHashMap<>();

    /**
     * The true branch of the OBDDGraph, which represents the path taken if the decision variable associated
     * with this node evaluates to {@code true}.
     * <p>
     * This field is of type {@code T}, which allows the structure to accommodate various types of nodes in the graph.
     * The trueBranch points to the next node in the decision-making process when the evaluated Boolean condition is true.
     * If {@code null}, this branch is considered to be a leaf or end-point.
     * </p>
     */
    private T trueBranch;

    /**
     * The false branch of the OBDDGraph, which represents the path taken if the decision variable associated
     * with this node evaluates to {@code false}.
     * <p>
     * Like the {@code trueBranch}, this field is of type {@code T}, and it points to the node that should be traversed
     * if the associated decision variable is false. If {@code null}, this branch is treated as a leaf or terminal node.
     * </p>
     */
    private T falseBranch;

    /**
     * A unique identifier for each OBDDObject instance.
     * <p>
     * The {@code id} field is used to assign a unique identifier to each instance of the {@code OBDDObject} class.
     * This identifier is generated using a hash of a randomly generated UUID. The uniqueness of the identifier is
     * guaranteed across instances, ensuring that each node in the OBDDGraph can be differentiated, even if they
     * have the same structure or content.
     * </p>
     *
     * <p>
     * This field is immutable and set during the creation of the object. It is primarily used for comparing objects
     * and sorting them, as the {@link #compareTo(OBDDObject)} method relies on the {@code id} to maintain a consistent
     * ordering of nodes in the OBDDGraph.
     * </p>
     *
     * <p>
     * Since this field is automatically initialized and is final, it is not exposed through setters, preserving
     * the integrity of the unique identifier.
     * </p>
     */
    @Setter(AccessLevel.NONE)
    private long id = UUID.randomUUID().hashCode();

    /**
     * Constructs a new OBDDObject with the specified parent node and decision branches (trueBranch and falseBranch).
     * <p>
     * This constructor initializes the OBDDObject by setting its parent and its true and false branches.
     * It validates the structural integrity of the node by ensuring that the trueBranch and falseBranch are not
     * the same non-null object. In an OBDD, having identical true and false branches would indicate redundancy,
     * which violates the structure of a well-formed OBDD.
     * </p>
     *
     * @param parent      The parent node in the OBDDGraph. This establishes the hierarchical relationship between
     *                    this node and its parent.
     * @param trueBranch  The node that represents the true branch of the decision tree.
     * @param falseBranch The node that represents the false branch of the decision tree.
     * @throws IllegalArgumentException if the trueBranch and falseBranch are the same non-null instance, which
     *                                  would violate the structural integrity of the OBDD.
     */
    public OBDDObject(@NonNull OBDDObject<?, ?> parent, @Nullable T trueBranch, @Nullable T falseBranch) {

        this.trueBranch = assignUniqueNode(trueBranch);
        this.falseBranch = assignUniqueNode(falseBranch);

        parents.put(assignUniqueNode(parent), TruthValue.UNKNOWN);
    }

    /**
     * Retrieves the branch (either true or false) based on the provided boolean value.
     * <p>
     * This method provides a convenient way to access the true or false branch of the OBDDObject
     * based on the given boolean parameter. If {@code true}, the trueBranch is returned; if {@code false},
     * the falseBranch is returned. If the requested branch is {@code null}, an empty {@code Optional} is returned.
     * This approach avoids potential {@code NullPointerExceptions} and provides a safe way to handle branches that
     * may not exist.
     * </p>
     *
     * @param branch A boolean value indicating which branch to retrieve: {@code true} for the true branch,
     *               {@code false} for the false branch.
     * @return An {@code Optional} containing the corresponding branch, or an empty {@code Optional} if the branch is {@code null}.
     */
    public Optional<T> get(boolean branch) {
        return Optional.ofNullable(branch ? trueBranch : falseBranch);
    }

    public void add(boolean branch, T node) {

        this.set(branch).accept(assignUniqueNode(node));
    }

    /**
     * Sets the specified value for the branch indicated by the boolean parameter.
     * <p>
     * This method allows you to set either the true or false branch of the OBDDObject
     * based on the value of the {@code branch} parameter. If {@code branch} is {@code true},
     * the value will be assigned to the true branch; if {@code branch} is {@code false},
     * the value will be assigned to the false branch.
     * </p>
     *
     * <p>
     * Internally, the method delegates to either {@link #setTrueBranch(T)} or {@link #setFalseBranch(T)}
     * depending on the value of {@code branch}. This ensures clean, type-safe assignments and avoids the need
     * for conditional logic within your code.
     * </p>
     *
     * @param branch A boolean value indicating which branch to set: {@code true} for the true branch,
     *               {@code false} for the false branch.
     * @param value  The value to assign to the specified branch. Must not be {@code null}.
     * @return The current instance of {@code OBDDObject} for method chaining.
     * @throws NullPointerException if the provided value is {@code null}.
     */
    public OBDDObject<O, T> set(boolean branch, @NonNull T value) {
        set(branch).accept(value);
        return this;
    }

    /**
     * Provides a {@code Consumer} for setting the appropriate branch.
     * <p>
     * This helper method returns a {@code Consumer} that represents either the setter for the true branch or
     * the false branch, based on the value of the {@code branch} parameter. By returning a {@code Consumer},
     * this method abstracts away the need for explicit conditionals in the calling code and allows for
     * functional-style usage.
     * </p>
     *
     * <p>
     * This is an internal utility method used by {@link #set(boolean, T)} to choose the correct setter
     * for the true or false branch. The returned {@code Consumer} will either point to {@link #setTrueBranch(T)}
     * or {@link #setFalseBranch(T)}, depending on the {@code branch} parameter.
     * </p>
     *
     * @param branch A boolean value indicating which branch should be set: {@code true} for the true branch,
     *               {@code false} for the false branch.
     * @return A {@code Consumer} that accepts a value to be assigned to the specified branch.
     */
    private Consumer<T> set(boolean branch) {
        return branch ? this::setTrueBranch : this::setFalseBranch;
    }

    public void addParent(T parent, boolean branch) {
        parents.put(assignUniqueNode(parent), TruthValue.valueOf(String.valueOf(branch).toUpperCase()));
    }

    public void removeParent(OBDDObject<?, ?> parent) {
        parents.remove(parent);
    }

    public Set<OBDDObject<?, ?>> getParentsAsSet() {
        return parents.keySet();
    }

    private <N extends OBDDObject<?, ?>> N assignUniqueNode(N node) {
        assignUniqueNodes(getCompleteCollectionWith(node))
                .doOnError(e -> log.error("No two identical objects can function as branches or parents.", e))
                .subscribe();

        return (N) node;
    }

    private OBDDObject<?, ?>[] getCompleteCollectionWith(OBDDObject<?, ?> node) {

        List<OBDDObject<?, ?>> nodes = new ArrayList<>();

        CollectionUtils.addAll(nodes, this.getBranches());
        CollectionUtils.addAll(nodes, this.getParents().keySet().stream().toList());
        nodes.add(node);

        return nodes.stream().filter(Objects::isNull).toArray(OBDDObject[]::new);
    }

    public List<OBDDObject<?, ?>> getBranches() {
        return Arrays.asList(trueBranch, falseBranch);
    }

    /**
     * Validates the uniqueness of the provided nodes (parent, trueBranch, falseBranch) to ensure that
     * no duplicates exist within the OBDDGraph structure.
     * <p>
     * This method checks the provided nodes (which are typically the parent, trueBranch, and falseBranch) to make sure
     * that no node is used more than once. In an OBDD, each node should have unique references to its true and false
     * branches. If duplicates are found, an {@code IllegalArgumentException} is thrown, preventing the creation of
     * structurally invalid OBDDs.
     * </p>
     *
     * @param nodes An array of OBDDObject instances (typically the parent, trueBranch, and falseBranch) to be checked
     *              for uniqueness.
     * @throws IllegalArgumentException if any of the provided nodes are duplicates.
     */
    protected Mono<Void> assignUniqueNodes(OBDDObject<?, ?>... nodes) {
        // Use Flux to process the nodes and ensure they are unique, throwing an exception if duplicates are found.
        return Flux.fromArray(ArrayUtils.removeAllOccurrences(nodes, null))
                .filter(Objects::nonNull)
                .groupBy(Function.identity()) // Group nodes by identity (i.e., the same instance)
                .flatMap(group -> group.count()
                        .filter(count -> count > 1) // Find duplicates
                        .flatMap(count -> Mono.error(new IllegalArgumentException("Duplicate branches: " + group.key())))
                )
                .then();
    }

    @Override
    public int compareTo(O other) {
        return Long.compare(this.getId(), other.getId());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        OBDDObject<?, ?> other = (OBDDObject<?, ?>) obj;
        return Objects.equals(trueBranch, other.trueBranch) && Objects.equals(falseBranch, other.falseBranch);
    }

    @Override
    public int hashCode() {
        return Objects.hash(trueBranch, falseBranch);
    }
}