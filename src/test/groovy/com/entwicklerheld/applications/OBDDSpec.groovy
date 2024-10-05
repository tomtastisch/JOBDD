package com.entwicklerheld.applications

import com.entwicklerheld.applications.object.core.logical.Logical
import com.entwicklerheld.applications.object.core.logical.comparator.OBDDComparator
import com.entwicklerheld.applications.object.objects.OBDDGraph
import com.entwicklerheld.applications.object.core.OBDDNode
import com.entwicklerheld.applications.object.exception.InvalidNodeReferenceException
import com.entwicklerheld.applications.object.exception.InvalidRootNodeException
import com.entwicklerheld.applications.object.objects.node.TerminalNode
import spock.lang.Specification
import spock.lang.Subject

class OBDDSpec extends Specification {

    @Subject
    OBDDGraph graph

    def setup() {
        graph = new OBDDGraph()
    }

    def "should initialize OBDD with true and false terminal nodes"() {
        expect:
        graph.trueBranch != null
        graph.falseBranch != null

        graph.trueBranch instanceof TerminalNode
        graph.falseBranch instanceof TerminalNode
    }

    def "should not throw an exception when no cycles exist"() {
        when: "Adding nodes without creating cycles"
        OBDDNode node1 = graph.addNode(1)
        OBDDNode node2 = graph.addNode(2)
        OBDDNode node3 = graph.addNode(3)

        and: "set node references"
        graph.setEdgeReference(node1, node2, true)
        graph.setEdgeReference(node2, node3, true)

        and: "initialize OBDD"
        graph.init()

        then: "No cyclic references should be detected"
        notThrown(InvalidNodeReferenceException)
    }

    def "should detect cycles when they exist"() {
        when: "Adding nodes with a cycle"
        OBDDNode node1 = graph.addNode(1)
        OBDDNode node2 = graph.addNode(2)
        OBDDNode node3 = graph.addNode(3)
        OBDDNode node4 = graph.addNode(4)
        OBDDNode node5 = graph.addNode(5)

        and: "set node references to create a cycle"
        graph.setEdgeReference(node1, node2, true)
        graph.setEdgeReference(node2, node3, true)
        graph.setEdgeReference(node3, node4, false)
        graph.setEdgeReference(node4, node5, true)
        graph.setEdgeReference(node5, node1, false) // creates a cycle

        and: "initialize OBDD"
        graph.init()

        then: "A cyclic reference should be detected"
        thrown(InvalidNodeReferenceException)
    }

    def "should throw an exception when no edges are set between nodes"() {
        when: "Adding nodes without creating edges"
        OBDDNode node1 = graph.addNode(1)
        OBDDNode node2 = graph.addNode(2)
        OBDDNode node3 = graph.addNode(3)

        and: "initialize OBDD without setting any edge references"
        graph.init()

        then: "An exception should be thrown due to missing edges"
        thrown(InvalidRootNodeException)
    }

    void "should not throw an exception when adding a node with an existing ID"() {
        when: "Adding a node with a unique ID"
        OBDDNode node1 = graph.addNode(1)

        and: "Attempting to add a node with the same ID"
        OBDDNode nodeDuplicate = graph.addNode(1)

        and: "initialize OBDD"
        graph.init()

        then: "No cyclic references should be detected"
        notThrown(StackOverflowError)

        and: "The duplicate node should be the same instance as the original node"
        node1.is(nodeDuplicate)
    }

    def "should set edges correctly between nodes"() {
        when: "Adding nodes and setting edges"
        OBDDNode node1 = graph.addNode(1)
        OBDDNode node2 = graph.addNode(2)
        OBDDNode node3 = graph.addNode(3)

        and: "set edges"
        graph.setEdgeReference(node1, node2, true)
        graph.setEdgeReference(node2, node3, false)

        and: "initialize OBDD"

        graph.init()

        then: "The edges should be correctly set"
        graph.getEdgeReference(node1, node2)
        !graph.getEdgeReference(node2, node3)
    }

    def "should compare two OBDDs using logical operations"() {
        given: "Two OBDDs"
        OBDDGraph graph2 = new OBDDGraph()

        and: "add nodes to the first OBDD"
        OBDDNode nodeA1 = graph.addNode(1) // Node for OBDD 1
        OBDDNode nodeA2 = graph.addNode(2)

        and: "add nodes to the second OBDD"
        OBDDNode nodeB1 = graph2.addNode(1) // Node for OBDD 2
        OBDDNode nodeB2 = graph2.addNode(2)

        and: "set edge references in both OBDDs"
        graph.setEdgeReference(nodeA1, nodeA2, true)
        graph2.setEdgeReference(nodeB1, nodeB2, true)

        and: ""
        graph.init()
        graph2.init()

        when: "We compare the two OBDDs using the AND operation"
        OBDDComparator comparator = new OBDDComparator()
        boolean areEquivalentAnd = comparator.compare(graph, graph2, Logical.OR)

        then: "The OBDDs should be logically equivalent when AND is applied"
        areEquivalentAnd
    }
}