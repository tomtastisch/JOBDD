Great! Here’s the updated README.md file that includes the Apache 2.0 License information:

# JOBDD - Java Ordered Binary Decision Diagram

## Overview

**JOBDD** is a Java library designed for the creation and manipulation of Ordered Binary Decision Diagrams (OBDDs). OBDDs provide a compact representation of Boolean functions, making them particularly useful in formal verification, optimization of logical circuits, and decision-making processes in computer science.

## Features

- **Easy OBDD Creation**: Quickly define and manage OBDDs.
- **Logical Operations**: Perform AND, OR, NOT, and other logical operations on OBDDs.
- **Cycle Detection**: Automatically detects and prevents cycles during OBDD initialization.
- **Asynchronous Processing**: Utilizes reactive programming principles to handle large graph structures efficiently.
- **Flexible Comparison**: Supports both isomorphic and structural comparisons between OBDDs.

## Installation

### Prerequisites

- **Java 8 or higher**: Ensure you have a compatible Java version installed on your system.

### Dependencies

If you are using Maven, add the following dependency to your `pom.xml` file:

```xml
<dependency>
    <groupId>com.entwicklerheld</groupId>
    <artifactId>jobdd</artifactId>
    <version>1.0.0</version> <!-- Replace with the current version -->
</dependency>

Usage

Example Code

Here’s a simple example demonstrating how to create an OBDD, add nodes, set edges, and perform logical operations.

import com.entwicklerheld.applications.object.core.logical.Logical;
import com.entwicklerheld.applications.object.core.logical.comparator.OBDDComparator;
import com.entwicklerheld.applications.object.objects.OBDDGraph;
import com.entwicklerheld.applications.object.core.OBDDNode;

public class OBDDExample {
    public static void main(String[] args) {
        // Initialize two OBDDGraphs
        OBDDGraph graph1 = new OBDDGraph();
        OBDDGraph graph2 = new OBDDGraph();

        // Add nodes to the first OBDD
        OBDDNode nodeA1 = graph1.addNode(1); // Node for OBDD 1
        OBDDNode nodeA2 = graph1.addNode(2);
        graph1.setEdgeReference(nodeA1, nodeA2, true);

        // Initialize the first OBDD
        graph1.init();

        // Add nodes to the second OBDD
        OBDDNode nodeB1 = graph2.addNode(1); // Node for OBDD 2
        OBDDNode nodeB2 = graph2.addNode(2);
        graph2.setEdgeReference(nodeB1, nodeB2, true);

        // Initialize the second OBDD
        graph2.init();

        // Compare the two OBDDs using a logical operation
        OBDDComparator comparator = new OBDDComparator();
        boolean areEquivalent = comparator.compare(graph1, graph2, Logical.AND);

        System.out.println("Are the two OBDDs logically equivalent using AND? " + areEquivalent);
    }
}

Methods Overview

	•	compare(OBDD obdd1, OBDD obdd2, Logical logical): Compares two OBDDs based on the specified logical operation (e.g., AND, OR).
	•	isomorphicCompare(OBDD obdd1, OBDD obdd2): Checks if two OBDDs are isomorphic, meaning they have the same structure.
	•	traverse(OBDD obdd): Traverses the OBDD and returns the terminal nodes.

Unit Tests

The library comes with a comprehensive set of unit tests written using Spock Framework to ensure reliability and correctness. Below are some key tests included in the OBDDSpec class:

	1.	Initialization of OBDDs: Tests if the OBDD initializes correctly with true and false terminal nodes.
	2.	Cycle Detection: Tests that cycles are correctly detected and that exceptions are thrown when attempting to create a cyclic reference.
	3.	Edge Setting: Tests whether edges between nodes are set correctly.
	4.	Logical Operations Comparison: Tests the comparison of two OBDDs using logical operations.

Here are some examples of the tests:

def "should initialize OBDD with true and false terminal nodes"() {
    expect:
    graph.trueBranch != null
    graph.falseBranch != null
    graph.trueBranch instanceof TerminalNode
    graph.falseBranch instanceof TerminalNode
}

def "should detect cycles when they exist"() {
    when: "Adding nodes with a cycle"
    OBDDNode node1 = graph.addNode(1)
    OBDDNode node2 = graph.addNode(2)
    OBDDNode node3 = graph.addNode(3)
    graph.setEdgeReference(node1, node2, true)
    graph.setEdgeReference(node2, node3, true)
    graph.setEdgeReference(node3, node1, false) // creates a cycle
    then: "A cyclic reference should be detected"
    thrown(InvalidNodeReferenceException)
}

def "should compare two OBDDs using logical operations"() {
    given: "Two OBDDs"
    OBDDGraph graph2 = new OBDDGraph()
    OBDDNode nodeA1 = graph.addNode(1) // Node for OBDD 1
    OBDDNode nodeA2 = graph.addNode(2)
    OBDDNode nodeB1 = graph2.addNode(1) // Node for OBDD 2
    OBDDNode nodeB2 = graph2.addNode(2)
    graph.setEdgeReference(nodeA1, nodeA2, true)
    graph2.setEdgeReference(nodeB1, nodeB2, true)
    graph.init()
    graph2.init()
    when: "We compare the two OBDDs using the AND operation"
    OBDDComparator comparator = new OBDDComparator()
    boolean areEquivalentAnd = comparator.compare(graph, graph2, Logical.AND)
    then: "The OBDDs should be logically equivalent"
    areEquivalentAnd
}

Contributions

Contributions are welcome! If you wish to contribute to the development of the project, please fork the repository and submit a pull request.

License

This project is licensed under the Apache License 2.0.

Contact

For further information or questions, please contact the project maintainer tomtastisch.

### Changes Made:
- **License Section**: Updated to reflect the Apache 2.0 License instead of the MIT License.
  
Feel free to adjust any additional details as needed! If you need further modifications, let me know.
