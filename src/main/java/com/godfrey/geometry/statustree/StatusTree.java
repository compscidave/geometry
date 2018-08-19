/*
 * Copyright 2011-2014 David Godfrey. All rights reserved.
 */
package com.godfrey.geometry.statustree;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import com.godfrey.geometry.dcel.halfedge.HalfEdge;
import com.godfrey.geometry.dcel.vertex.Vertex;
import com.godfrey.geometry.point.Point;
import com.godfrey.geometry.statustree.breakpoint.BreakPoint;
import com.godfrey.geometry.statustree.node.StatusTreeNode;
import com.godfrey.geometry.voronoi.event.Event;
import com.godfrey.geometry.voronoi.exception.CircleEventException;
import com.godfrey.geometry.voronoi.exception.SiteEventException;


public class StatusTree {

	private StatusTreeNode root;
	
	public StatusTree() {
		root = null;
	}
	
	public StatusTree(StatusTreeNode root) {
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
	public StatusTreeNode search(Point site) {
		return search(root,site);
	}

	private StatusTreeNode search(StatusTreeNode statusTreeNode, Point site) {
		if(statusTreeNode.isLeaf())
			return statusTreeNode;
		else {
			Point intersection = calculateIntersection(statusTreeNode.breakPoint.leftSite, statusTreeNode.breakPoint.rightSite, site.getY());
			if(site.getX() < intersection.getX())
				return search(statusTreeNode.leftChild, site);
			else 
				return search(statusTreeNode.rightChild, site);
		}
	}
	
	public Vertex remove(StatusTreeNode statusTreeNode) throws CircleEventException {
		StatusTreeNode lcaPred = getLeastCommonAncestor(statusTreeNode.predecessor,statusTreeNode);
		StatusTreeNode lcaSucc = getLeastCommonAncestor(statusTreeNode, statusTreeNode.successor);
		StatusTreeNode lcaPredSucc = getLeastCommonAncestor(statusTreeNode.predecessor, statusTreeNode.successor);
		
		Vertex v = new Vertex(statusTreeNode.getCircleEventOrigin());
		
		HalfEdge eNew = new HalfEdge();
		HalfEdge fNew = new HalfEdge();
		eNew.twin = fNew;
		fNew.twin = eNew;				
		
		fNew.incidentSite = statusTreeNode.successor.site;
		lcaPred.breakPoint.edge.prev = lcaSucc.breakPoint.edge.twin;
		lcaSucc.breakPoint.edge.twin.next = lcaPred.breakPoint.edge;

		lcaPred.breakPoint.edge.twin.next = eNew;
		eNew.prev = lcaPred.breakPoint.edge.twin;
		lcaSucc.breakPoint.edge.prev = fNew;
		fNew.next = lcaSucc.breakPoint.edge;

		eNew.origin = v;
		lcaPred.breakPoint.edge.origin = v;
		v.setIncidentEdge(lcaPred.breakPoint.edge);
		lcaSucc.breakPoint.edge.origin = v;

		if(lcaPred.equals(lcaPredSucc)) {
			//This means that the least common ancestor of the successor and the disappearing arc
			//is the parent of the disappearing arc.
			//All references to lcaSucc and node should be removed
			//Move the right child of the parent up one level
			if(statusTreeNode.parent.parent!=null) {
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
				if(statusTreeNode.parent.parent!=null) {
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
		
		lcaPredSucc.breakPoint = new BreakPoint(statusTreeNode.predecessor.site,statusTreeNode.successor.site,fNew);
		
		statusTreeNode.predecessor.successor = statusTreeNode.successor;
		statusTreeNode.successor.predecessor = statusTreeNode.predecessor;
		
		if(statusTreeNode.predecessor.hasCircleEvent()) {
			statusTreeNode.predecessor.circleEvent.setValid(false);
		}
		if(statusTreeNode.successor.hasCircleEvent()) {
			statusTreeNode.successor.circleEvent.setValid(false);
		}
		
		return v;
		
	}
	
	public void insert(Point site) throws SiteEventException {
		if(root == null)
			root = new StatusTreeNode(site);
		else
			throw new SiteEventException("This method should only be called on an empty tree");
	}
	
	public Point calculateIntersection(Point leftSite, Point rightSite, double y) {
		Point res = new Point(leftSite.getX(),leftSite.getY());
		Point p = new Point(leftSite.getX(),leftSite.getY());

		double z0 = 1/(2*(leftSite.getY() - y));
		double z1 = 1/(2*(rightSite.getY() - y));

		if (leftSite.getY() == rightSite.getY())
			res.setX((leftSite.getX() + rightSite.getX()) / 2);
		else if (rightSite.getY() == y)
			res.setX(rightSite.getX());
		else if (leftSite.getY() == y) {
			res.setX(leftSite.getX());
			p = rightSite;
		}
		else {
			double a = z0 - z1;
			double b = 2*(-leftSite.getX()*z0+rightSite.getX()*z1);
			double c = (z0*(leftSite.getX()*leftSite.getX()+leftSite.getY()*leftSite.getY()-y*y))
					-(z1*(rightSite.getX()*rightSite.getX()+rightSite.getY()*rightSite.getY()-y*y));
			res.setX(( -b + Math.sqrt(b*b - 4*a*c)) / (2*a));
		}

		res.setY(z0*(res.getX()*res.getX() - 2*(p.getX()*res.getX()) + p.getX()*p.getX() + p.getY()*p.getY() - y*y));
		return res;
		}

	public StatusTreeNode replace(StatusTreeNode statusTreeNode, Point site) {

		//Create three new leaf nodes.
		StatusTreeNode leftArc = new StatusTreeNode(statusTreeNode.site);
		StatusTreeNode middleArc = new StatusTreeNode(site);
		StatusTreeNode rightArc = new StatusTreeNode(statusTreeNode.site);
		
		HalfEdge e = new HalfEdge();
		HalfEdge f = new HalfEdge();
		e.setTwin(f);
		f.setTwin(e);
		e.incidentSite = site;
		f.incidentSite = statusTreeNode.site;
		StatusTreeNode leftBreakPoint = new StatusTreeNode(new BreakPoint(statusTreeNode.site,site,e));
		StatusTreeNode rightBreakPoint = new StatusTreeNode(new BreakPoint(site,statusTreeNode.site,f));
		
		leftArc.parent = leftBreakPoint;
		leftArc.predecessor = statusTreeNode.predecessor;
		leftArc.successor = middleArc;
		
		rightArc.parent = rightBreakPoint;
		rightArc.predecessor = middleArc;
		rightArc.successor = statusTreeNode.successor;
		
		middleArc.parent = rightBreakPoint;
		middleArc.predecessor = leftArc;
		middleArc.successor = rightArc;
		
		leftBreakPoint.leftChild = leftArc;
		leftBreakPoint.rightChild = rightBreakPoint;
		if(statusTreeNode.parent == null)
			root = leftBreakPoint;
		else {
			leftBreakPoint.parent = statusTreeNode.parent;
			if(statusTreeNode.parent.leftChild.equals(statusTreeNode))
				statusTreeNode.parent.leftChild = leftBreakPoint;
			else
				statusTreeNode.parent.rightChild = leftBreakPoint;
		}
		
		rightBreakPoint.rightChild = rightArc;
		rightBreakPoint.leftChild = middleArc;
		rightBreakPoint.parent = leftBreakPoint;
		
		if(statusTreeNode.predecessor != null)
			statusTreeNode.predecessor.successor = leftArc;
		if(statusTreeNode.successor != null)
			statusTreeNode.successor.predecessor = rightArc;

		return middleArc;
	}

	public Event checkBreakPointConvergence2(StatusTreeNode statusTreeNode) {
		if(statusTreeNode.predecessor == null)
			return null;
		else
			if(statusTreeNode.successor == null)
				return null;
			else {

				//check for break point convergence where t2 is the right node in the tuple.
				Point a = statusTreeNode.predecessor.site;
				Point b = statusTreeNode.site;
				Point c = statusTreeNode.successor.site;

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
							Point origin = new Point((A*E+B*F+C*G)/D,
													 (A*(c.getX()-b.getX())+B*(a.getX()-c.getX())+C*(b.getX()-a.getX()))/D);
							Point diff = new Point(origin.getX()-a.getX(),origin.getY()-a.getY());
							Point newEventSite = new Point(origin.getX(),
														   origin.getY() - Math.sqrt(diff.getX()*diff.getX()+diff.getY()*diff.getY()));
							Event circleEvent = new Event(newEventSite,origin,statusTreeNode);
							circleEvent.setValid(true);
							statusTreeNode.circleEvent = circleEvent;
							return circleEvent;
						}
					}
				}
			}
	}
	
	public Vector<Vertex> empty() {
		Vector<Vertex> breakPoints = new Vector<Vertex>();
		if(!root.isLeaf())
			emptyTree(root,breakPoints);
		return breakPoints;
	}
	
	private Vector<Vertex> emptyTree(StatusTreeNode statusTreeNode,Vector<Vertex> breakPoints) {

		if(!statusTreeNode.leftChild.isLeaf())
			breakPoints = emptyTree(statusTreeNode.leftChild,breakPoints);
		if(!statusTreeNode.rightChild.isLeaf())
			breakPoints = emptyTree(statusTreeNode.rightChild,breakPoints);
		
		Vertex v = new Vertex(new Point(calculateIntersection(statusTreeNode.breakPoint.leftSite,statusTreeNode.breakPoint.rightSite,-999999999999999.0)));
		statusTreeNode.breakPoint.edge.origin = v;
		
		v.setIncidentEdge(statusTreeNode.breakPoint.edge);
		breakPoints.add(v);
		return breakPoints;
	}
	
	public Vector<BreakPoint> empty2() {
		Vector<BreakPoint> breakPoints = new Vector<BreakPoint>();
		return emptyTree2(root,breakPoints);
	}
	
	private Vector<BreakPoint> emptyTree2(StatusTreeNode statusTreeNode,Vector<BreakPoint> breakPoints) {

		if(!statusTreeNode.leftChild.isLeaf())
			breakPoints = emptyTree2(statusTreeNode.leftChild,breakPoints);
		if(!statusTreeNode.rightChild.isLeaf())
			breakPoints = emptyTree2(statusTreeNode.rightChild,breakPoints);
		breakPoints.add(statusTreeNode.breakPoint);
		return breakPoints;
	}
	
	public StatusTreeNode getLeastCommonAncestor(StatusTreeNode nodeA, StatusTreeNode nodeB) {
	    // 1. trace one node to the root
	    Set<StatusTreeNode> set = new HashSet<StatusTreeNode>();
	    
	    StatusTreeNode t = nodeA;
	    while (t != null) {
	        set.add(t);
	        t = t.parent;
	    }
	 
	    // 2. trace another node towards to root. The common ancestors are those
	    // nodes also in the set from step 1.
	    StatusTreeNode s = nodeB;
	    while (s != null) {
	        if (set.contains(s)) {
	        	return s;
	        }
	        s = s.parent;
	    }
	    return null;
	}
	
	public void inOrderArcPrint() {
		StatusTreeNode iter = root.leftChild;
//		if(iter == null)
//			System.out.print("<"+root.getPointSite().getX()+","+root.getPointSite().getY()+">     ");
//		while(iter.leftChild!=null)
//			iter = iter.leftChild;
//		while(iter!=null) {
//			System.out.print("<"+iter.getPointSite().getX()+","+iter.getPointSite().getY()+">     ");
//			iter = iter.successor;
//		}
		
	}
	
	/*	private StatusTreeNode insert(StatusTreeNode node, Point site) {
	if (node.isLeaf()) {
		node = replace(node, site);
	}
	else {
		Point intersection = calculateIntersection(node.breakPoint.leftSite, node.breakPoint.rightSite, site.getY());
			
		if(site.getX() < intersection.getX())
			node.leftChild = insert(node.leftChild, site);
		else
			node.rightChild = insert(node.rightChild, site);
	}
	return node;
}*/

}

