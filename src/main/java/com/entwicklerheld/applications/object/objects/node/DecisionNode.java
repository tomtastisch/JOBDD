package com.entwicklerheld.applications.object.objects.node;

import com.entwicklerheld.applications.object.core.OBDDNode;
import com.entwicklerheld.applications.object.core.OBDDObject;
import lombok.NonNull;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Represents a decision node in the Ordered Binary Decision Diagram (OBDDGraph).
 * A decision node corresponds to a specific variable and has two branches,
 * representing the true and false outcomes based on that variable.
 */

public class DecisionNode extends OBDDNode {

    public DecisionNode(int variable, OBDDObject<?, ?> parent, OBDDNode trueBranche, OBDDNode falseBranche) {
        this(variable, parent, Pair.of(trueBranche, falseBranche));
    }

    /**
     * Constructs a DecisionNode with the specified variable, parent node, and branches.
     *
     * @param variable The decision variable associated with this node.
     * @param parent   The parent OBDDObject of this node.
     * @param branches A pair of branches (true and false) represented by OBDDNode instances.
     *                 Must not be null.
     */
    public DecisionNode(int variable, OBDDObject<?, ?> parent, @NonNull Pair<OBDDNode, OBDDNode> branches) {
        super(variable, parent, branches);
    }

    @Override
    public boolean getValue() {
        return false;
    }

    @Override
    public OBDDNode getTrueBranch() {
        return super.getTrueBranch();
    }

    @Override
    public OBDDNode getFalseBranch() {
        return super.getFalseBranch();
    }
}