/*
 * Copyright 2011-2014 David Godfrey. All rights reserved.
 */
package com.godfrey.geometry.test.voronoi;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Vector;

import com.godfrey.geometry.point.Point;

public class LSIStatusTree {

	public static final double TOLERANCE = 0.00005;
	private LSIStatusTreeNode root;
	private String debug;

	public LSIStatusTree() {
		root = null;
	}

	public static LSIStatusTree createTree() {
		return new LSIStatusTree();
	}
	
	public void insert(DCELLineSegment value, Point eventSite) {
		debug = new String();
		if(root==null)
			root = new LSIStatusTreeNode(value);
		else {
			LSIStatusTreeNode node = insert(new LSIStatusTreeNode(value), root, eventSite);
			//At this point, node is simply the passed parameter, with the added information
			//of which leaf node it descended from - found in node.parent - and which position
			//relative to the descendant it should be placed - found in node.isLeftChild.
			if(node.isLeftChild) { 
				node.parent.rightChild = new LSIStatusTreeNode(node.parent.segment);
				node.parent.segment = node.segment;
				node.parent.leftChild = node;
				node.parent.rightChild.parent = node.parent;
				node.parent.rightChild.isLeftChild = false;
				node.parent.rightChild.predecessor = node;
				node.parent.rightChild.successor = node.parent.successor;
				node.parent.isLeftChild = node.parent.isLeftChild;
				node.successor = node.parent.rightChild;
				node.predecessor = node.parent.predecessor;
				if(node.parent.predecessor != null)
					node.parent.predecessor.successor = node;
				if(node.parent.successor != null)
					node.parent.successor.predecessor = node.parent.rightChild;
				node.parent.parent = node.parent.parent;
			}
			else {
				node.parent.segment = node.parent.segment;
				node.parent.leftChild = new LSIStatusTreeNode(node.parent.segment);
				node.parent.rightChild = node;
				node.parent.leftChild.isLeftChild = true;
				node.parent.leftChild.parent = node.parent;
				node.parent.rightChild.predecessor = node.parent.leftChild;
				node.parent.rightChild.successor = node.parent.successor;
				node.parent.isLeftChild = node.parent.isLeftChild;

				if(node.parent.predecessor != null)
					node.parent.predecessor.successor = node.parent.leftChild;
				node.parent.leftChild.predecessor = node.parent.predecessor;
				if(node.parent.successor != null)
					node.parent.successor.predecessor = node;
				node.parent.leftChild.successor = node;
				node.parent.parent = node.parent.parent;
				LSIStatusTreeNode lca = getLeastCommonAncestor(node.parent,node.parent.successor);
				if(lca != null)
					lca.segment = node.segment;
			}
			node.parent.predecessor = null;
			node.parent.successor = null;
		}
	}

	private LSIStatusTreeNode insert(LSIStatusTreeNode node, LSIStatusTreeNode queryNode, Point eventSite) {

		if(queryNode==null) {
//			if(queryNode.segment.isCollinearWith(eventSite)) {
//				if(queryNode.segment.isLeftOf(node.segment.getLowerEndpoint().getCoords())) {
//					node.isLeftChild = true;
//					//The segment contained in the parent node that this node decended from must be updated
//					//with the node's segment.
//					internalNode.segment = node.segment;
//
//					node.predecessor = previousLeafNode.predecessor;
//					if(previousLeafNode.predecessor != null)
//						previousLeafNode.predecessor.successor = node;
//					node.successor = previousLeafNode;
//
//					internalNode.parent = previousLeafNode.parent;
//					internalNode.leftChild = node;
//					internalNode.rightChild = previousLeafNode;
//
//					if(previousLeafNode.parent != null)
//						internalNode.isLeftChild = previousLeafNode.isLeftChild;
//
//					//Should not need to update pointers for queryNode.parent.parent.child = internalNode
//					//since the return value in the recursive procedure does this automatically
//
//					previousLeafNode.parent = internalNode;
//					previousLeafNode.isLeftChild = false;
//					previousLeafNode.predecessor = node;
//					
//					node.parent = internalNode;				
//
//				}
//				else {
//					node.isLeftChild = false;
//					//The new leaf node shall be the right child of a new internal node
//					/**
//					 * When adding a right leaf node to the tree, the least common ancestor of
//					 * the old node and its successor lca(queryNode,successor) must be updated to maintain consistency
//					 * within the tree. The lca(queryNode,successor) contains the guide segment queryNode.segment,
//					 * and must be changed to newleaf.segment
//					 **/
//					LSIStatusTreeNode lca = getLeastCommonAncestor(previousLeafNode,previousLeafNode.successor);
//					if(lca != null)
//						lca.segment = node.segment;
//					node.predecessor = previousLeafNode;
//					node.successor = previousLeafNode.successor;
//					if(previousLeafNode.successor != null)
//						previousLeafNode.successor.predecessor = node;
//					node.isLeftChild = false;
//
//					internalNode.segment = previousLeafNode.segment;
//					internalNode.parent = previousLeafNode.parent;
//					if(previousLeafNode.parent != null)
//						internalNode.isLeftChild = previousLeafNode.isLeftChild;
//
//					internalNode.leftChild = previousLeafNode;
//					internalNode.rightChild = node;
//					
//					previousLeafNode.isLeftChild = true;
//					previousLeafNode.successor = node;
//					
//					previousLeafNode.parent = internalNode;
//					node.parent = internalNode;
//				}
//			}
//			else {
//				if(queryNode.segment.isLeftOf(eventSite)) {
//					node.isLeftChild = true;
//					//The segment contained in the parent node that this node decended from must be updated
//					//with the node's segment.
//					internalNode.segment = node.segment;
//
//					node.predecessor = previousLeafNode.predecessor;
//					if(previousLeafNode.predecessor != null)
//						previousLeafNode.predecessor.successor = node;
//					node.successor = previousLeafNode;
//
//					internalNode.parent = previousLeafNode.parent;
//					internalNode.leftChild = node;
//					internalNode.rightChild = previousLeafNode;
//
//					if(previousLeafNode.parent != null)
//						internalNode.isLeftChild = previousLeafNode.isLeftChild;
//
//					//Should not need to update pointers for queryNode.parent.parent.child = internalNode
//					//since the return value in the recursive procedure does this automatically
//
//					previousLeafNode.parent = internalNode;
//					previousLeafNode.isLeftChild = false;
//					previousLeafNode.predecessor = node;
//					
//					node.parent = internalNode;				
//
//				}
//				else {
//					node.isLeftChild = false;
//					//The new leaf node shall be the right child of a new internal node
//					/**
//					 * When adding a right leaf node to the tree, the least common ancestor of
//					 * the old node and its successor lca(queryNode,successor) must be updated to maintain consistency
//					 * within the tree. The lca(queryNode,successor) contains the guide segment queryNode.segment,
//					 * and must be changed to newleaf.segment
//					 **/
//					LSIStatusTreeNode lca = getLeastCommonAncestor(previousLeafNode,previousLeafNode.successor);
//					if(lca != null)
//						lca.segment = node.segment;
//					node.predecessor = previousLeafNode;
//					node.successor = previousLeafNode.successor;
//					if(previousLeafNode.successor != null)
//						previousLeafNode.successor.predecessor = node;
//					node.isLeftChild = false;
//
//					internalNode.segment = previousLeafNode.segment;
//					internalNode.parent = previousLeafNode.parent;
//					if(previousLeafNode.parent != null)
//						internalNode.isLeftChild = previousLeafNode.isLeftChild;
//
//					internalNode.leftChild = previousLeafNode;
//					internalNode.rightChild = node;
//					
//					previousLeafNode.isLeftChild = true;
//					previousLeafNode.successor = node;
//					
//					previousLeafNode.parent = internalNode;
//					node.parent = internalNode;
//				}
//			}

			return node;
		}
		else {
			node.parent = queryNode;
/*			double xIntersect = calculateIntersection(eventSite,queryNode.segment, 0);
			double segmentXIntersect = calculateIntersection(eventSite,node.segment,0);
//			if(Double.compare(xIntersect,segmentXIntersect) <= 0)
//				System.out.println("double.compare = " + Double.compare(xIntersect,segmentXIntersect));
//
//			System.out.println("xIntersect = " + xIntersect);
			if(queryNode.segment.isCollinearWith(eventSite)) {
				if(segmentXIntersect <= xIntersect)
					return insert(node,queryNode.leftChild,eventSite);
				else
					return insert(node,queryNode.rightChild,eventSite);
			}
			else {
				if(segmentXIntersect <= xIntersect)
					return insert(node,queryNode.leftChild,eventSite);
				else
					return insert(node,queryNode.rightChild,eventSite);
			}*/
			
			int orientation = queryNode.segment.orientation(eventSite);
			if(orientation==-1) {
//				System.out.println("eventSite is left of segment");
				node.isLeftChild = true;
				return insert(node,queryNode.leftChild,eventSite);
			}
			else
				if(orientation==1) {
					node.isLeftChild = false;
//					System.out.println("eventSite is right of segment");
					return insert(node,queryNode.rightChild,eventSite);
				}
				else {
//				System.out.println("eventSite belongs to segment");
				if(queryNode.segment.compareToLower(node.segment)) {
					node.isLeftChild = true;
//					System.out.println("compareToLower returned true");
					return insert(node,queryNode.leftChild,eventSite);
				}
				else {
					node.isLeftChild = false;
//					System.out.println("eventSite is equal to or right of queryNode.segment.lowerEndpoint");
					return insert(node,queryNode.rightChild,eventSite);
				}
			}

			
			
/*			if(queryNode.segment.isLeftOf(eventSite)) {
				System.out.println("eventSite is left of queryNode.segment");
				node.isLeftChild = true;
				return insert(node,queryNode.leftChild,eventSite);
			}
			else
				if(queryNode.segment.isCollinearWith(eventSite)) {
					System.out.println("queryNode.segment contains eventSite");
					if(queryNode.segment.compareToLower(node.segment)) {
						System.out.println("node.segment.lowerEndpoint <= queryNode.segment.lowerEndpoint");
						node.isLeftChild = true;
						return insert(node,queryNode.leftChild,eventSite);
					}
					else {
						System.out.println("node.segment.lowerEndpoint > queryNode.segment.lowerEndpoint");
						node.isLeftChild = false;
						return insert(node,queryNode.rightChild,eventSite);
					}
				}
				else {
					System.out.println("eventSite is right of queryNode.segment");
					node.isLeftChild = false;
					return insert(node,queryNode.rightChild,eventSite);
				}
*/			
			
/*			if(queryNode.segment.isCollinearWith(eventSite)) {
				//We are within range of segments containing the eventSite. Now, we must determine where the segment being inserted
				//lies in the ordering of segments containing the event site. If the insertion segment is horizontal, it must lie
				//to the right of all other points containing eventSite, otherwise, we insert based on the order of the segments
				//lower-endpoint's x-values, from smallest to largest.
				System.out.println("insert " + queryNode.segment + " is collinear with " + eventSite);
				if(queryNode.segment.isLeftOf(node.segment.getLowerEndpoint().getCoords()) || queryNode.segment.isHorizontal()) {
					node.isLeftChild = true;
					System.out.println("insert going left");
					return insert(node,queryNode.leftChild,eventSite);
				}
				else {
					node.isLeftChild = false;
					System.out.println("insert going left");
					return insert(node,queryNode.rightChild,eventSite);
				}
			}
			else {
				System.out.println("insert " + queryNode.segment + " is not collinear with " + eventSite);
				if(queryNode.segment.isLeftOf(eventSite)) {
					node.isLeftChild = true;
					System.out.println("insert going left");
					return insert(node,queryNode.leftChild,eventSite);
				}
				else {
					node.isLeftChild = false;
					System.out.println("insert going left");
					return insert(node,queryNode.rightChild,eventSite);
				}
			}*/
		}
	}

	public void remove(DCELLineSegment value, Point eventSite) {
		debug = new String();
		if(!root.isLeaf()) {
			LSIStatusTreeNode removalNode = remove(value, root, eventSite);
			LSIStatusTreeNode lcaSucc = getLeastCommonAncestor(removalNode,removalNode.successor);
			LSIStatusTreeNode lcaPred = getLeastCommonAncestor(removalNode.predecessor,removalNode);
			if (removalNode.isLeftChild) {
				if(removalNode.predecessor != null)
					removalNode.predecessor.successor = removalNode.successor;
				//There has to be a successor if the root is not a leaf and the removalNode is a left child.
				removalNode.successor.predecessor = removalNode.predecessor;
				if(removalNode.parent.parent!=null) {
					if(removalNode.parent.isLeftChild) {
						removalNode.parent.rightChild.isLeftChild = true;
						removalNode.parent.parent.leftChild = removalNode.parent.rightChild;
					}
					else {
						removalNode.parent.rightChild.isLeftChild = false; //not necesseary, but consistent
						removalNode.parent.parent.rightChild = removalNode.parent.rightChild;
					}		
				}
				else
					root = removalNode.parent.rightChild;
				removalNode.parent.rightChild.parent = removalNode.parent.parent;
				removalNode.parent.clear();
				removalNode.clear();
			}
			else {
				if(removalNode.successor != null)
					removalNode.successor.predecessor = removalNode.predecessor;
				//There has to be a predecessor if the root is not a leaf and removalNOde is a right child.
				removalNode.predecessor.successor = removalNode.successor;

				//the segment stored in lcaSucc must be updated to reflect the new right-most leaf in its left subtree
				//This code could also be placed within the above conditional, since if removalNode has a successor
				//then it also has a least common ancestor whose internal segment must be updated.
				if(lcaSucc != null)
					lcaSucc.segment = removalNode.predecessor.segment;
				//Now we update the parent pointers and move the left subtree of removalNode.parent up to
				//removalNode.parent's level. Again, if removalNode has a successor, then its parent is a child of some internal node
				if(removalNode.parent.parent!=null) {
					if(removalNode.parent.isLeftChild) {
						removalNode.parent.leftChild.isLeftChild = true; //again, not necessary, but consistent
						removalNode.parent.parent.leftChild = removalNode.parent.leftChild;
					}
					else {
						removalNode.parent.leftChild.isLeftChild = false;
						removalNode.parent.parent.rightChild = removalNode.parent.leftChild;
					}		
				}
				else
					root = removalNode.parent.leftChild;
				removalNode.parent.leftChild.parent = removalNode.parent.parent;
				removalNode.parent.clear();
				removalNode.clear();
				}
		}
		else {
			root.clear();
			root = null;
		}
	}

/*	public boolean contains(DCELLineSegment value) {
		return valueOf(find(value, root)) != null;
	}*/

/*	*//**
	 * Calculates the intersection point between a {@link DCELLineSegment} and the sweep line.
	 * The location of the sweep line is obtained from @param operand. During an insert, the sweep line
	 * is located at the y-position of the upper endpoint of @param operand, and during a removal
	 * the sweep line is located at the y-position of the lower endpoint of @param operand. The value passed
	 * to @param opcode signifies which endpoint to use when deriving the sweep line. It is not enough to allow
	 * the caller to determine which endpoint to pass, since the insertion or removal of a horizontal
	 * segment will require additional work. As such, the responsibility is delegated to the callee.
	 * 
	 * @param operand 	The segment being operated on within the tree
	 * @param operator	The operating segment
	 * @param opcode	0 for an insert operation, 1 for a removal operation
	 * @return The x-intersect of the intersection between the sweep line, and the operator.
	 * 
	 * For horizontal segments, the lower endpoint is always used
	 *//*
	private double calculateIntersection(Coordinate eventSite, DCELLineSegment operator, int opcode) {
		DCELLineSegment sweepLine = new DCELLineSegment(new Vertex(-99999999.0d,eventSite.y),new Vertex(999999999.0d,eventSite.y));
		Vertex intersect = operator.getIntersection(sweepLine);
		
		if(intersect == null) {
				// TODO Auto-generated catch block
//				if(Math.abs(eventSite.y - operator.getUpperEndpoint().y) <= TOLERANCE || Math.abs(eventSite.y - operator.getLowerEndpoint().y) <= TOLERANCE)
//					System.out.println("Intersection happens at an endpoint of the operator? returning NaN");
				//Intersection does not necessarily happen at an endpoint of the operator.
				if(operator.isHorizontal()) {
//					System.out.println("Horizontal operator, lower endpoint " + operator.getLowerEndpoint());
					return eventSite.x;
				}
				
//				System.out.println("\n*********************************************************************");
//				System.out.println("* Operator: " + operator + "*");
//				System.out.println("* sweepLine : " + sweepLine + "                         *");
//				System.out.println("* sweepLine position " + sweepLine.getLowerEndpoint().y + "*");
//				System.out.println("*********************************************************************");
//				System.out.println();
				return eventSite.x;
			}
		else {
//			System.out.println("Intersection at " + intersect);
//			System.out.println("operator: " + operator);

			return intersect.x;
		}
		
	}*/

	private LSIStatusTreeNode remove(DCELLineSegment segment, LSIStatusTreeNode queryNode, Point eventSite) {
		if(queryNode.isLeaf()) {

/*			if(queryNode.isLeftChild){
				if(queryNode.parent!=null)
				if(queryNode.predecessor != null)
					queryNode.predecessor.successor = queryNode.successor;
				if(queryNode.successor!=null)
					queryNode.successor.predecessor = queryNode.predecessor;
				queryNode = queryNode.rightChild;
			}
			else {
				LSIStatusTreeNode lcaSucc = getLeastCommonAncestor(queryNode,queryNode.successor);
				if(lcaSucc != null)
					lcaSucc.segment = queryNode.predecessor.segment;
				if(queryNode.predecessor != null)
					queryNode.predecessor.successor = queryNode.successor;
				if(queryNode.successor!=null)
					queryNode.successor.predecessor = queryNode.predecessor;
				queryNode = queryNode.leftChild;
			}*/
			if(!segment.equals(queryNode.segment)) {
/*				System.out.println("LSIStatusTree.remove(DCELLineSegment,LSIStatusTreeNode,DCELLineSegment) is at a leaf node");
				System.out.println("Segment : " + segment);
				System.out.println("entry.segment : " + queryNode.segment);
				System.out.println("eventSite : " + eventSite);*/
				//printInOrder();
				StringBuilder msg = new StringBuilder();
				msg.append(this.toString());
//				PrintStream console = System.out;
//				PrintStream out = new PrintStream(new BufferedOutputStream(System.out));
//				System.setOut(out);
//				BufferedOutputStream br = new BufferedOutputStream(out);
//				printInOrder();
//				System.out.flush();
//				System.setOut(console);
				msg.append("Segment: " + segment + "\n");
				msg.append("entry.segment: " + queryNode.segment + "\n");
				msg.append("eventSite: " + eventSite + "\n");
				debug.concat(msg.toString());
				throw new NoSuchElementException("\n" + segment + " not found \n" + msg.toString());
/*				queryNode = queryNode.successor;
				if(queryNode!=null && queryNode.segment.equals(segment))
					return queryNode;
				else {

				}*/
			}
			else
				return queryNode;
		}
		else {
/*			double xIntersect = calculateIntersection(eventSite,queryNode.segment, 0);
			double segmentXIntersect = calculateIntersection(eventSite,segment,0);
//			if(Double.compare(xIntersect,segmentXIntersect) <= 0)
//				System.out.println("double.compare = " + Double.compare(xIntersect,segmentXIntersect));
//
//			System.out.println("xIntersect = " + xIntersect);
			if(queryNode.segment.isCollinearWith(eventSite)) {
				if(segmentXIntersect <= xIntersect)
					return remove(segment,queryNode.leftChild,eventSite);
				else
					return remove(segment,queryNode.rightChild,eventSite);
			}
			else {
				if(segmentXIntersect <= xIntersect)
					return remove(segment,queryNode.leftChild,eventSite);
				else
					return remove(segment,queryNode.rightChild,eventSite);
			}*/
			
			int orientation = queryNode.segment.orientation(eventSite);
			if(orientation < 0 || queryNode.segment.equals(segment)) {
//				System.out.println("eventSite is left of segment");
				return remove(segment,queryNode.leftChild,eventSite);
			}
			else //{
/*				System.out.println("eventSite is equal to or right of queryNode.segment.lowerEndpoint");
				return remove(segment,queryNode.rightChild,eventSite);*/
				if(orientation==1) {
//					System.out.println("eventSite is right of segment");
					return remove(segment,queryNode.rightChild,eventSite);
				}
				else {
//				System.out.println("eventSite belongs to segment");
				if(queryNode.segment.compareToUpper(segment)) {
//					System.out.println("compareToLower returned true, going left");
					return remove(segment,queryNode.leftChild,eventSite);
				}
				else {
//					System.out.println("eventSite is equal to or right of queryNode.segment.upperEndpoint");
					return remove(segment,queryNode.rightChild,eventSite);
				}
			}
			
			
			
/*			
			if(queryNode.segment.isLeftOf(eventSite) || queryNode.segment.equals(segment)) {
				System.out.println("eventSite is left of queryNode.segment");
				return remove(segment,queryNode.leftChild,eventSite);
			}
			else
				if(queryNode.segment.isCollinearWith(eventSite)) {
					System.out.println("queryNode.segment contains eventSite");
					if(queryNode.segment.compareToUpper(segment)) {
						System.out.println("segment lower endpoint less than equal to queryNode.segment lower endpoint");
						return remove(segment,queryNode.leftChild,eventSite);
					}
					else {
						System.out.println("segment lower endpoint greater than queryNode.segment lower endpoint");
						return remove(segment,queryNode.rightChild,eventSite);
					}
				}
				else {
					System.out.println("eventSite is right of queryNode.segment");
					return remove(segment,queryNode.rightChild,eventSite);
				}
			
*/			
/*			if(segment.equals(queryNode.segment))
				return remove(segment,queryNode.leftChild,eventSite);
			else
			if(queryNode.segment.isCollinearWith(eventSite)) {
				System.out.println("eventSite : " + eventSite + " is collinear with: " + queryNode.segment);
				if(segment.isHorizontal())
					return remove(segment,queryNode.leftChild,eventSite);
				else
				if(queryNode.segment.isLeftOf(segment.getLowerEndpoint().getCoords()) || queryNode.segment.isHorizontal()) {
					System.out.println("going left");
					return remove(segment,queryNode.leftChild,eventSite);
				}
				else {
					System.out.println("going right");
					return remove(segment,queryNode.rightChild,eventSite);
				}
			}
			else {
				System.out.println("eventSite : " + eventSite + " is not collinear with: " + queryNode.segment);
				if(queryNode.segment.isLeftOf(eventSite)) {
					System.out.println("going left");
					return remove(segment,queryNode.leftChild,eventSite);
				}
				else {
					System.out.println("going right");
					return remove(segment,queryNode.rightChild,eventSite);
				}
				
			}*/
		}
	}

	public Vector<DCELLineSegment> findSegmentsContainingPoint(Point eventSite) {

		Vector<DCELLineSegment> segments = new Vector<DCELLineSegment>();
		if(root==null)
			return segments;
//		System.out.println("Searching for segments containing : " + eventSite);

		LSIStatusTreeNode node = findSegmentsContainingPoint(root,eventSite);
		LSIStatusTreeNode bary = node.successor;
		if(node != null) {
			while(node.segment.contains(eventSite)) {
				segments.add(node.segment);
				node = node.predecessor;
				if(node==null)
					break;
			}
		}
		if(bary != null) {
			while(bary.segment.contains(eventSite)) {
				segments.add(bary.segment);
				bary = bary.successor;
				if(bary==null)
					break;
				
			}
		}
//		System.out.println("Found " + segments.size() + " segments containing " + eventSite);
		return segments;
	}
	
	private LSIStatusTreeNode findSegmentsContainingPoint(LSIStatusTreeNode entry, Point eventSite) {
		if(entry.isLeaf()) {
//			System.out.println("findSegmentsContainingPoint settled at : " + entry.segment);
			return entry;
		}
		else {
/*			double xIntersect = calculateIntersection(point,entry.segment, 0);
			double segmentXIntersect = point.x;
			if(Double.compare(xIntersect,segmentXIntersect) <= 0)
				System.out.println("double.compare = " + Double.compare(xIntersect,segmentXIntersect));

			System.out.println("xIntersect = " + xIntersect);
			if(segmentXIntersect <= xIntersect)
				return findSegmentsContainingPoint(entry.leftChild,point);
			else
				return findSegmentsContainingPoint(entry.rightChild,point);
*/			
			int orientation = entry.segment.orientation(eventSite);
			if(orientation <= 0)
				return findSegmentsContainingPoint(entry.leftChild,eventSite);
			else
				return findSegmentsContainingPoint(entry.rightChild,eventSite);
				
		}
	}

	private LSIStatusTreeNode getLeastCommonAncestor(LSIStatusTreeNode nodeA, LSIStatusTreeNode nodeB) {
	    // 1. trace one node to the root
	    Set<LSIStatusTreeNode> set = new HashSet<LSIStatusTreeNode>();
	    
	    LSIStatusTreeNode t = nodeA;
	    while (t != null) {
	        set.add(t);
	        t = t.parent;
	    }
	 
	    // 2. trace another node towards to root. The common ancestors are those
	    // nodes also in the set from step 1.
	    LSIStatusTreeNode s = nodeB;
	    while (s != null) {
	        if (set.contains(s)) {
	        	return s;
	        }
	        s = s.parent;
	    }
	    return null;
	}
	
	public boolean isEmpty() {
		return root==null ? true : false;
	}
	
	/**
	 * Get the left neighbor along the sweep line given a query point lying on the sweep line
	 * 
	 * @param eventSite	The query point
	 * @return	A {@link DCELLineSegment} lying to the immediate left of the query point.
	 */
	public DCELLineSegment getLeftNeighbor(Point eventSite) {
		if(root==null)
			return null;
		LSIStatusTreeNode node = findSegmentsContainingPoint(root,eventSite);
		if(node.predecessor != null)
			return node.predecessor.segment;
		else
			return null;
	}
	
	/**
	 * Get the right neighbor along the sweep line given a query point lying on the sweep line
	 * 
	 * @param eventSite	The query point
	 * @return	A {@link DCELLineSegment} lying to the immediate right of the query point.
	 */
	public DCELLineSegment getRightNeighbor(Point eventSite) {
		if(root==null)
			return null;
		LSIStatusTreeNode node = findSegmentsContainingPoint(root,eventSite);
		if(node.successor != null)
			return node.successor.segment;
		else
			return null;

	}

	/**
	 * Returns the left most segment of a given set of segments in the status structure.
	 * 
	 * @param segments	The set of segments for which the caller wants returned the left most segment 
	 * @param eventSite	The event site which is being handled by the caller
	 * @return	Returns the left most segment of the given set of segments
	 */
	public LSIStatusTreeNode getLeftMostSegment(Set<DCELLineSegment> segments, Point eventSite) {
		//Caller guarantees that segments has at least one member
		Iterator<DCELLineSegment> itor = segments.iterator();
		DCELLineSegment bleh = itor.next();
		LSIStatusTreeNode node = findSegmentsContainingPoint(root,eventSite);
//		if(node.predecessor!=null)
//		System.out.println("getLeftMostSegment node.predecessor : " + node.predecessor.segment);
//		System.out.println("getLeftMostSegment node segment : " + node.segment);
//		System.out.println("getLeftMostSegment bleh segment : " + bleh);
		LSIStatusTreeNode tempNode = node;
		while(segments.contains(node.segment)) {
			tempNode = node;
			node = node.predecessor;
			if(node == null)
				break;
		}
		return tempNode;
	}
	
	/**
	 * Returns the right most segment of a given set of segments in the status structure.
	 * 
	 * @param segments	The set of segments for which the caller wants returned the right-most segment 
	 * @param eventSite	The event site which is being handled by the caller
	 * @return	Returns the right most segment of the given set of segments
	 */
	public LSIStatusTreeNode getRightMostSegment(Set<DCELLineSegment> segments, Point eventSite) {
		//Caller guarantees that segments has at least one member
		Iterator<DCELLineSegment> itor = segments.iterator();
		DCELLineSegment bleh = itor.next();
		LSIStatusTreeNode node = findSegmentsContainingPoint(root,eventSite);
		LSIStatusTreeNode tempNode = node;
		while(segments.contains(node.segment)) {
			tempNode = node;
			node = node.successor;
			if(node == null)
				break;
		}
		return tempNode;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("************************************************");
		sb.append("*    Printing StatusTree Through Successors    *");
		sb.append("************************************************");
		sb.append("\n");
		int currentDepth = 0;
		if(root != null)
			sb.append(printLeafsInOrder(root,currentDepth));
		sb.append("\n");
		sb.append("************************************************");
		sb.append("* Finished StatusTree Print Through Successor  *");
		sb.append("************************************************");
		sb.append("\n");
		return sb.toString();
	}

	private void printInOrder(LSIStatusTreeNode entry,int currentDepth) {
		if (entry != null) {
			printInOrder(entry.leftChild,currentDepth+1);
			String whitespace = "\t";
			for(int i=0;i<currentDepth;i++)
				System.out.print(whitespace);
			if(entry.isLeaf())
				System.out.print(entry.segment);
			else
				System.out.print(entry.segment);
			System.out.print("\n");
			printInOrder(entry.rightChild,currentDepth+1);
		}
		currentDepth-=1;
	}

	public void printInOrder() {
		System.out.println("************************************************");
		System.out.println("*             Printing StatusTree              *");
		System.out.println("************************************************");
		System.out.println();
		int currentDepth = 0;
		if(root != null)
			printInOrder(root,currentDepth);
		System.out.println();
		System.out.println("************************************************");
		System.out.println("*         Finished StatusTree Print            *");
		System.out.println("************************************************");
	}
	
	public void printLeafsInOrder() {
		System.out.println("************************************************");
		System.out.println("*    Printing StatusTree Through Successors    *");
		System.out.println("************************************************");
		System.out.println();
		int currentDepth = 0;
		if(root != null)
			System.out.print(printLeafsInOrder(root,currentDepth));
		System.out.println();
		System.out.println("************************************************");
		System.out.println("* Finished StatusTree Print Through Successor  *");
		System.out.println("************************************************");
	}

	private String printLeafsInOrder(LSIStatusTreeNode root2, int currentDepth) {
		StringBuilder sb = new StringBuilder();
		if(root2.isLeaf())
			sb.append(root2.segment + "\n");
		else {
			while(root2.leftChild != null)
				root2 = root2.leftChild;
			while(root2 != null) {
				sb.append(root2.segment + "\n");
				root2 = root2.successor;
			}
		}
		return sb.toString();
	}

}