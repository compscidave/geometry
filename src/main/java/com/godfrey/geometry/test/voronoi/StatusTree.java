/*
 * Copyright 2011-2014 David Godfrey. All rights reserved.
 */
package com.godfrey.geometry.test.voronoi;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import com.godfrey.geometry.point.Point;
import com.godfrey.geometry.test.voronoi.Site;
import com.godfrey.geometry.test.voronoi.exception.CircleEventException;
import com.godfrey.geometry.test.voronoi.exception.SiteEventException;


public class StatusTree {

	private Node root;
	
	public StatusTree() {
		root = null;
	}
	
	public StatusTree(Node root) {
		this.root = root;
	}
	
	public boolean isEmpty() {
		return (root == null? true : false);
	}

	/**
	 * Recursively searches the status tree for the arc that lies directly above @param site
	 * @param site The site encountered by the sweep line
	 * @return The arc that will be split by the incoming site
	 */
	public Node newSearch(Site site) {
		return newSearch(root,site.getLocation());
	}

	private Node newSearch(Node node, Point point) {
		if(node.isLeaf())
			return node;
		else {
			Coordinate intersection = calculateArcIntersection(node.breakPoint.newLeftSite.getLocation(), node.breakPoint.newRightSite.getLocation(), point.getY());
			if(point.getY() < intersection.getX())
				return newSearch(node.leftChild, point);
			else 
				return newSearch(node.rightChild, point);
		}
	}

	/**
	 * Guaranteed to be at least three arcs ( leaf nodes ) and two internal nodes in the tree when called.
	 * @param node
	 * @return
	 * @throws CircleEventException
	 */
	public Vertex newRemove(Node node) throws CircleEventException {
		Node lcaPred = getLeastCommonAncestor(node.predecessor,node);
		Node lcaSucc = getLeastCommonAncestor(node, node.successor);
		Node lcaPredSucc = getLeastCommonAncestor(node.predecessor, node.successor);
		
		Vertex v = new Vertex(node.getCircleEventOrigin());
		
		HalfEdge eNew = new HalfEdge();
		HalfEdge fNew = new HalfEdge();
		eNew.twin = fNew;
		fNew.twin = eNew;				
		
		fNew.newIncidentSite = node.successor.eventSite;
		eNew.newIncidentSite = node.predecessor.eventSite;
		lcaPred.breakPoint.edge.prev = lcaSucc.breakPoint.edge.twin;
		lcaSucc.breakPoint.edge.twin.next = lcaPred.breakPoint.edge;

		lcaPred.breakPoint.edge.twin.next = eNew;
		eNew.prev = lcaPred.breakPoint.edge.twin;
		lcaSucc.breakPoint.edge.prev = fNew;
		fNew.next = lcaSucc.breakPoint.edge;

		eNew.origin = v;
		lcaPred.breakPoint.edge.origin = v;
		v.incidentEdge = lcaPred.breakPoint.edge;
		lcaSucc.breakPoint.edge.origin = v;

		if(lcaPred.equals(lcaPredSucc)) {
			//This means that the least common ancestor of the successor and the disappearing arc
			//is the parent of the disappearing arc.
			//All references to lcaSucc and node should be removed
			//Move the right child of the parent up one level
			if(node.parent.parent!=null) {
				if(lcaSucc.parent.rightChild.equals(lcaSucc))
					lcaSucc.parent.rightChild = lcaSucc.rightChild;
				else
					lcaSucc.parent.leftChild = lcaSucc.rightChild;
				
				lcaSucc.rightChild.parent = lcaSucc.parent;
			}
			else
				throw new CircleEventException("Attempted removal of an arc with no left predecessor");
		}
		else 
			if(lcaSucc.equals(lcaPredSucc)) {
				//This means that the least common ancestor of the predecessor and the disappearing arc
				//is the parent of the disappearing arc
				//All references to lcaPred and node should be removed
				//Move the left child of the parent up one level.
				if(node.parent.parent!=null) {
					if(lcaPred.parent.rightChild.equals(lcaPred))
						lcaPred.parent.rightChild = lcaPred.leftChild;
					else
						lcaPred.parent.leftChild = lcaPred.leftChild;
					
					lcaPred.leftChild.parent = lcaPred.parent;
				}
				else
					throw new CircleEventException("Attempted removal of an arc with no right successor");
			}
			else
				throw new CircleEventException("Incorrect status structure");
		
		lcaPredSucc.breakPoint = new BreakPoint(node.predecessor.eventSite,node.successor.eventSite,fNew);
		
		node.predecessor.successor = node.successor;
		node.successor.predecessor = node.predecessor;
		
		if(node.predecessor.newHasCircleEvent()) {
			node.predecessor.newCircleEvent.isValid = false;
		}
		if(node.successor.newHasCircleEvent()) {
			node.successor.newCircleEvent.isValid = false;
		}
		
		return v;
	}
	

	
	public static Coordinate calculateArcIntersection(Coordinate point, Coordinate point2, double y) {
		Coordinate res = new Coordinate(point.getX(),point.getY());
		Coordinate p = new Coordinate(point.getX(),point.getY());

		double z0 = 1/(2*(point.getY() - y));
		double z1 = 1/(2*(point2.getY() - y));

		if (point.getY() == point2.getY())
			res.setLongitude((point.getX() + point2.getX()) / 2);
		else if (point2.getY() == y)
			res.setLongitude(point2.getX());
		else if (point.getY() == y) {
			res.setLongitude(point.getX());
			p = point2;
		}
		else {
			double a = z0 - z1;
			double b = 2*(-point.getX()*z0+point2.getX()*z1);
			double c = (z0*(point.getX()*point.getX()+point.getY()*point.getY()-y*y))
					-(z1*(point2.getX()*point2.getX()+point2.getY()*point2.getY()-y*y));
			res.setLongitude(( -b + Math.sqrt(b*b - 4*a*c)) / (2*a));
		}

		res.setLatitude(z0*(res.getX()*res.getX() - 2*(p.getX()*res.getX()) + p.getX()*p.getX() + p.getY()*p.getY() - y*y));
		return res;
	}
	
	public void newInsert(Site eventSite) throws SiteEventException {
		if(root == null)
			root = new Node(eventSite);
		else
			throw new SiteEventException("This method should only be called on an empty tree");
	}
	
	public Node newReplace(Node node, Site site) {
		//Create three new leaf nodes.
		Node leftArc = new Node(node.eventSite);
		Node middleArc = new Node(site);
		Node rightArc = new Node(node.eventSite);
		
		HalfEdge e = new HalfEdge();
		HalfEdge f = new HalfEdge();
		e.setTwin(f);
		f.setTwin(e);
		e.newIncidentSite = site;
		f.newIncidentSite = node.eventSite;
		
		//Two new breakpoints
		Node leftBreakPoint = new Node(new BreakPoint(node.eventSite,site,e));
		Node rightBreakPoint = new Node(new BreakPoint(site,node.eventSite,f));
		
		leftArc.parent = leftBreakPoint;
		leftArc.predecessor = node.predecessor;
		leftArc.successor = middleArc;
		
		rightArc.parent = rightBreakPoint;
		rightArc.predecessor = middleArc;
		rightArc.successor = node.successor;
		
		middleArc.parent = rightBreakPoint;
		middleArc.predecessor = leftArc;
		middleArc.successor = rightArc;
		
		leftBreakPoint.leftChild = leftArc;
		leftBreakPoint.rightChild = rightBreakPoint;
		if(node.parent == null)
			root = leftBreakPoint;
		else {
			leftBreakPoint.parent = node.parent;
			if(node.parent.leftChild.equals(node))
				node.parent.leftChild = leftBreakPoint;
			else
				node.parent.rightChild = leftBreakPoint;
		}
		
		rightBreakPoint.rightChild = rightArc;
		rightBreakPoint.leftChild = middleArc;
		rightBreakPoint.parent = leftBreakPoint;
		
		if(node.predecessor != null)
			node.predecessor.successor = leftArc;
		if(node.successor != null)
			node.successor.predecessor = rightArc;

		return middleArc;
	}

	public CircleEvent newCheckBreakPointConvergence2(Node node) {
		if(node.predecessor == null)
			return null;
		else
			if(node.successor == null)
				return null;
			else {

				//check for break point convergence where t2 is the right node in the tuple.
				Point a = node.predecessor.eventSite.getLocation();
				Point b = node.eventSite.getLocation();
				Point c = node.successor.eventSite.getLocation();

				if ((b.getX()-a.getX())*(c.getY()-a.getY()) - (c.getX()-a.getX())*(b.getY()-a.getY()) > 0) {
					return null;
				}
				else {
					if(a.equals(b)) {
						return null;
					}
					else {
						double A = (a.getX()*a.getX())+(a.getY()*a.getY());
						double B = (b.getX()*b.getX())+(b.getY()*b.getY());
						double C = (c.getX()*c.getX())+(c.getY()*c.getY());
						double E = b.getY()-c.getY();
						double F = c.getY()-a.getY();
						double G = a.getY()-b.getY();

						double D = 2*((a.getX()*E)+(b.getX()*F)+(c.getX()*G));

						if(D == 0) {
							//the points are co-linear
							return null;
						}
						else {
							Coordinate origin = new Coordinate((A*E+B*F+C*G)/D,
													 (A*(c.getX()-b.getX())+B*(a.getX()-c.getX())+C*(b.getX()-a.getX()))/D);
							Coordinate diff = new Coordinate(origin.getX()-a.getX(),origin.getY()-a.getY());
							Coordinate newEventSite = new Coordinate(origin.getX(),
														   origin.getY() - Math.sqrt(diff.getX()*diff.getX()+diff.getY()*diff.getY()));
							CircleEvent circleEvent = new CircleEvent(node,newEventSite,origin);
							circleEvent.setValid(true);
							node.newCircleEvent = circleEvent;
							return circleEvent;
						}
					}
				}
			}
	}
	
	public Vector<BreakPoint> newEmpty(double sweepLinePosition) {
		Vector<BreakPoint> breakPoints = new Vector<BreakPoint>();
		if(!root.isLeaf())
			newEmptyTree(root,breakPoints,sweepLinePosition);
		return breakPoints;
	}
	
	private Vector<BreakPoint> newEmptyTree(Node node, Vector<BreakPoint> breakPoints, double sweepLinePosition) {
		if(!node.leftChild.isLeaf())
			breakPoints = newEmptyTree(node.leftChild,breakPoints,sweepLinePosition);
		if(!node.rightChild.isLeaf())
			breakPoints = newEmptyTree(node.rightChild,breakPoints,sweepLinePosition);
		

		if(!node.isLeaf()) {
//		if(node.breakPoint.edge.twin.origin == null && node.breakPoint.edge.origin == null)
//			System.out.println("Infinite Edge encountered");
//		else
//			if(node.breakPoint.edge.twin.origin == null || node.breakPoint.edge.origin == null)
//				System.out.println("Half infinite edge encountered");
//		Vertex v = new Vertex(new Coordinate(calculateArcIntersection(node.breakPoint.newLeftSite.getLocation(),node.breakPoint.newRightSite.getLocation(),-9999999999999.9)));
//		node.breakPoint.edge.origin = v;
//		v.incidentEdge = node.breakPoint.edge;
//		System.out.println("new vertex <" + v.coords.getX() + ", " + v.coords.getY() + ">");
//		System.out.println("leftSite: <" + node.breakPoint.newLeftSite.getLocation().getX() + "," + node.breakPoint.newLeftSite.getLocation().getY() + ">");
//		System.out.println("rightSite: <" + node.breakPoint.newRightSite.getLocation().getX() + "," + node.breakPoint.newRightSite.getLocation().getY() + ">");
		breakPoints.add(node.breakPoint);
		}
		return breakPoints;
	}
	
/*	
	public Vector<BreakPoint> empty2() {
		Vector<BreakPoint> breakPoints = new Vector<BreakPoint>();
		return emptyTree2(root,breakPoints);
	}
	
	private Vector<BreakPoint> emptyTree2(StatusTreeNode node,Vector<BreakPoint> breakPoints) {

		if(!node.leftChild.isLeaf())
			breakPoints = emptyTree2(node.leftChild,breakPoints);
		if(!node.rightChild.isLeaf())
			breakPoints = emptyTree2(node.rightChild,breakPoints);
		breakPoints.add(node.breakPoint);
		return breakPoints;
	}
*/
	
	public Node getLeastCommonAncestor(Node nodeA, Node nodeB) {
	    // 1. trace one node to the root
	    Set<Node> set = new HashSet<Node>();
	    
	    Node t = nodeA;
	    while (t != null) {
	        set.add(t);
	        t = t.parent;
	    }
	 
	    // 2. trace another node towards to root. The common ancestors are those
	    // nodes also in the set from step 1.
	    Node s = nodeB;
	    while (s != null) {
	        if (set.contains(s)) {
	        	return s;
	        }
	        s = s.parent;
	    }
	    return null;
	}
	
	public void inOrderArcPrint() {
		Node iter = root.leftChild;
		if(iter == null)
			System.out.print("<"+root.getSite().getLocation().getX()+","+root.getSite().getLocation().getY()+">     ");
		while(iter.leftChild!=null)
			iter = iter.leftChild;
		while(iter!=null) {
			System.out.print("<"+iter.getSite().getLocation().getX()+","+iter.getSite().getLocation().getY()+">     ");
			iter = iter.successor;
		}
	}


}

