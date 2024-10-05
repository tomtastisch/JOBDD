package com.entwicklerheld.applications.object.objects.node;

import com.entwicklerheld.applications.object.core.OBDDNode;
import com.entwicklerheld.applications.object.core.OBDDObject;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Represents a terminal node in the Ordered Binary Decision Diagram (OBDDGraph).
 * A terminal node holds a boolean value and does not have branches. Terminal nodes
 * represent the leaf nodes in the OBDD structure, typically associated with a final
 * decision value (true or false).
 *
 * <p>This class extends {@link OBDDNode}, inheriting its structure while overriding
 * certain methods to reflect the unique behavior of terminal nodes, such as having
 * no branches.</p>
 *
 * <p>The {@link TerminalNode} class uses a {@link AtomicBoolean} to ensure that
 * a warning message about the lack of branches is logged only once, even when multiple
 * threads attempt to retrieve branches from the terminal node.</p>
 */
@Slf4j
@EqualsAndHashCode(callSuper = true)
public class TerminalNode extends OBDDNode {

    /**
     * The boolean value that this terminal node holds. This value is associated with
     * the decision outcome represented by this terminal node.
     */
    private final boolean value;

    /**
     * An atomic flag used to ensure that the log warning about the terminal node
     * not having branches is only printed once, even when multiple threads attempt
     * to retrieve branches from this node.
     */
    private static final AtomicBoolean hasLogged = new AtomicBoolean(false);

    /**
     * Constructs a new {@link TerminalNode} with the specified variable, parent node, and value.
     *
     * @param variable The decision variable associated with this node. This typically represents
     *                 the decision point in the OBDD structure.
     * @param parent   The parent {@link OBDDObject} of this node, representing the node's position
     *                 in the OBDD structure.
     * @param value    The boolean value held by this terminal node, representing the final decision
     *                 outcome (true or false).
     */
    public TerminalNode(int variable, OBDDObject<?, ?> parent, boolean value) {
        super(variable, parent);
        this.value = value;
    }

    @Override
    public Optional<OBDDNode> get(boolean branch) {
        return Optional.ofNullable(warnAndReturn());
    }

    @Override
    public OBDDNode getTrueBranch() {
        return warnAndReturn();
    }

    @Override
    public OBDDNode getFalseBranch() {
        return warnAndReturn();
    }

    /**
     * Logs a warning that terminal nodes do not have branches and ensures this warning
     * is only logged once. This method returns {@code null} because terminal nodes do
     * not have true or false branches.
     *
     * @return Always returns {@code null}, as terminal nodes have no branches.
     */
    private OBDDNode warnAndReturn() {
        if (hasLogged.compareAndSet(false, true)) {
            log.error("A terminal node hasn't branches");
        }
        return null;
    }

    public boolean isTrue() {
        return value;
    }
    public boolean isFalse() {
        return !value;
    }

    @Override
    public boolean getValue() {
        return value;
    }

    @Override
    public String toString() {
        return super.toString().replaceAll("\\bnull\\b", "\"\"");
    }
}