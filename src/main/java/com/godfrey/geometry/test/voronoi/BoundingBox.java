/*
 * Copyright 2011-2014 David Godfrey. All rights reserved.
 */
package com.godfrey.geometry.test.voronoi;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;


/**
 * A helper class. Class BoundingBox provides a sort method, that transforms
 * 
 * @author BriefThought
 *
 */
public class BoundingBox {

	Vector<Vertex> boundingBox;
	Stack<Vertex> bBoxStack;
	Vertex center;
	Set<DCELLineSegment> boundingSegments;
	Set<DCELLineSegment> intersectingSegments;
	DCEL D;
	
	//The corner vertex in the first quadrant
	Vertex Q1;
	//The corner vertex in the second quadrant
	Vertex Q2;
	//The corner vertex in the third quadrant
	Vertex Q3;
	//The corner vertex in the fourth quadrant
	Vertex Q4;
	
	public BoundingBox(Vertex v1, Vertex v2, Vertex v3, Vertex v4) {
		
		D = new DCEL();
//		System.out.println("v1<"+v1.getCoords().getX()+","+v1.getCoords().getY()+">");
//		System.out.println("v2<"+v2.getCoords().getX()+","+v2.getCoords().getY()+">");
//		System.out.println("v3<"+v3.getCoords().getX()+","+v3.getCoords().getY()+">");
//		System.out.println("v4<"+v4.getCoords().getX()+","+v4.getCoords().getY()+">");
		
		boundingBox = new Vector<Vertex>();
		bBoxStack = new Stack<Vertex>();
		boundingSegments = new HashSet<DCELLineSegment>();
		intersectingSegments = new HashSet<DCELLineSegment>();
		Q1 = v1;
		Q2 = v2;
		Q3 = v3;
		Q4 = v4;
		
//		System.out.println("v1<"+v1.getCoords().getX()+","+v1.getCoords().getY()+">");
//		System.out.println("v2<"+v2.getCoords().getX()+","+v2.getCoords().getY()+">");
//		System.out.println("v3<"+v3.getCoords().getX()+","+v3.getCoords().getY()+">");
//		System.out.println("v4<"+v4.getCoords().getX()+","+v4.getCoords().getY()+">");
		
//		boundingBox = new Vector<Vertex>();
//		
//		HalfEdge e3 = new HalfEdge();
//		HalfEdge e3t = new HalfEdge();
//
//		e3.twin = e3t;
//		e3t.twin = e3;
//
//		e3.origin = v3;
//
//		v3.incidentEdge = e3;
//		
//		Q1 = v1;
//		Q2 = v2;
//		Q3 = v3;
//		Q4 = v4;
//		
//		boundingBox.add(v1);
//		boundingBox.add(v2);
//		//boundingBox.add(v3);
//		boundingBox.add(v4);
//		
//		DCELLineSegment Q1Q3 = new DCELLineSegment(Q1,Q3);
//		DCELLineSegment Q2Q4 = new DCELLineSegment(Q2,Q4);
//		
//		center = Q1Q3.getIntersection(Q2Q4);
//		bBoxStack = new Stack<Vertex>();
	}

	public BoundingBox() {
		boundingBox = new Vector<Vertex>();
		bBoxStack = new Stack<Vertex>();
		D = new DCEL();
		center = new Vertex(new Coordinate(0.0d,0.0d));
	}
	
	public void buildBox() {
		
		HalfEdge e1 = new HalfEdge();
		HalfEdge e1t = new HalfEdge();
		HalfEdge e2 = new HalfEdge();
		HalfEdge e2t = new HalfEdge();
		HalfEdge e3 = new HalfEdge();
		HalfEdge e3t = new HalfEdge();
		HalfEdge e4 = new HalfEdge();
		HalfEdge e4t = new HalfEdge();
		
		e1.twin = e1t;
		e1t.twin = e1;
		e2.twin = e2t;
		e2t.twin = e2;
		e3.twin = e3t;
		e3t.twin = e3;
		e4.twin = e4t;
		e4t.twin = e4;
		
		e1.origin = Q1;
		e2.origin = Q2;
		e3.origin = Q3;
		e4.origin = Q4;
		
		e1t.origin = Q2;
		e2t.origin = Q3;
		e3t.origin = Q4;
		e4t.origin = Q1;

		//Edges trace a clockwise path along the bounded portion of the subdivision
		e1.next = e4;
		e4.next = e3;
		e3.next = e2;
		e2.next = e1;
		
		e1.prev = e2;
		e2.prev = e3;
		e3.prev = e4;
		e4.prev = e1;
		
		//Edges trace a counterclockwise path along the unbounded portion of the subdivision
		e1t.next = e2t;
		e2t.next = e3t;
		e3t.next = e4t;
		e4t.next = e1t;
		
		e1t.prev = e4t;
		e4t.prev = e3t;
		e3t.prev = e2t;
		e2t.prev = e1t;
		
		Q1.incidentEdge = e1;
		Q2.incidentEdge = e2;
		Q3.incidentEdge = e3;
		Q4.incidentEdge = e4;
		
		boundingBox.add(Q1);
		boundingBox.add(Q2);
		boundingBox.add(Q3);		
		boundingBox.add(Q4);
		
		/**
		 * PolarAngleComparator, used to order the vertices that lie
		 * around the bounding box, relies on Vertex Q3 as a guide vertex.
		 * As such, the operation Collections.sort(Vector<Vertex> boundingVertices, PolarAngleComparator pac)
		 * may be undefined for a Vector<Vertex> parameter containing Q3. Thus, Q3(v3) is added only
		 * after the rest of the bounding box has been connected to the DCEL.
		 */

		boundingSegments.add(new DCELLineSegment(Q1,Q2));
		boundingSegments.add(new DCELLineSegment(Q2,Q3));
		boundingSegments.add(new DCELLineSegment(Q3,Q4));
		boundingSegments.add(new DCELLineSegment(Q4,Q1));

		
		DCELLineSegment Q1Q3 = new DCELLineSegment(Q1,Q3);
		DCELLineSegment Q2Q4 = new DCELLineSegment(Q2,Q4);
		
		center = Q1Q3.getIntersection(Q2Q4);
	}
	public void addIntersectingSegment(DCELLineSegment intersectingSegment) {
		intersectingSegments.add(intersectingSegment);
	}
	/**
	 * @return the q1
	 */
	public Vertex getQ1() {
		return Q1;
	}

	/**
	 * @return the q2
	 */
	public Vertex getQ2() {
		return Q2;
	}

	/**
	 * @return the q3
	 */
	public Vertex getQ3() {
		return Q3;
	}

	/**
	 * @return the q4
	 */
	public Vertex getQ4() {
		return Q4;
	}
	
	public void addVertex(Vertex v) {
		boundingBox.add(v);
	}

	public void buildStack() {
		//This comparator will sort by increasing polar angle w.r.t. Q3, from Q3 up to Q1.
		//Any vertex along the boundary counterclockwise from Q1 will be sorted in
		//reverse order ( clockwise from Q3 ) via the law of cosines
		PolarAngleComparator pac = new PolarAngleComparator(new DCELLineSegment(center,Q3),Q1,Q3);
		Collections.sort(boundingBox, pac);
//		System.out.println("BoundingBox.connect() size " + boundingVertices.size());
		
		//Thus, the vertices are operated on using two stacks in order to preserve correct order.
		Stack<Vertex> upperLeftBox = new Stack<Vertex>();

		while(boundingBox.firstElement()!=Q1)
			upperLeftBox.push(boundingBox.remove(0));

		while(!upperLeftBox.isEmpty())
			bBoxStack.push(upperLeftBox.pop());
		
		//The resulting stack stores the vertices that lie along the bounding box
		//in counter-clockwise order, ending with the lowest vertex above Q3, above and along the
		//line connecting Q3 and Q4 at the top of the stack, and starting with the vertex immediately
		//to the right of Q3, along a line connecting Q3 and Q2.
		bBoxStack.addAll(boundingBox);
//		System.out.println("BoundingBox.connect() size after adding all to stack : " + boundingVertices.size());
		//walk along the boundary in clockwise order adding the unbounded edges to the outside of the
		//com.godfrey.geometry.voronoi
//		Stack<Vertex> bBoxStackReverse = new Stack<Vertex>();
//		while(!bBoxStack.empty())
//			bBoxStackReverse.push(bBoxStack.pop());
/*		Vertex previousVertex = Q3;
//		connectingHalfEdges.add(Q3.incidentEdge);
		boundingVertices = new Vector<Vertex>();
		while(!bBoxStack.empty()) {
			//Walk along the bounding box, attaching the half infinite vertices
			//to the bounding box, along with the corners of the bounding box.
			Vertex v = bBoxStack.pop();
//			System.out.println("Handling vertex <"+v.getCoords().getX()+","+v.getCoords().getY()+">");
			HalfEdge e = new HalfEdge();
			HalfEdge f = new HalfEdge();
			e.origin = v;
			f.origin = previousVertex;
			e.twin = f;
			f.twin = e;
			e.next = previousVertex.incidentEdge;
			previousVertex.incidentEdge.prev = e;
			
			if(!(v.coords.equals(Q1.coords) || v.coords.equals(Q2.coords) || v.coords.equals(Q3.coords) || v.coords.equals(Q4.coords))) {
				e.prev = v.incidentEdge.twin;
				v.incidentEdge.twin.next = e;
			}
			else
				v.setIncidentEdge(e);
			
			if(!(previousVertex.coords.equals(Q1.coords) || previousVertex.coords.equals(Q2.coords) || previousVertex.coords.equals(Q3.coords) || previousVertex.coords.equals(Q4.coords))) {
				f.prev = previousVertex.incidentEdge.twin.next.twin;
				previousVertex.incidentEdge.twin.next.twin.next = f;
				
			}
			else {
				f.prev = previousVertex.incidentEdge.twin;
				previousVertex.incidentEdge.twin.next = f;
			}
			
			connectingHalfEdges.add(e);
			previousVertex = v;
			boundingVertices.add(v);
		}
		
		if(!(previousVertex.coords.equals(Q1.coords) || previousVertex.coords.equals(Q2.coords) || previousVertex.coords.equals(Q3.coords) || previousVertex.coords.equals(Q4.coords))) {
			Q3.incidentEdge.twin.prev = previousVertex.incidentEdge.twin.next.twin;
			previousVertex.incidentEdge.twin.next.twin.next = Q3.incidentEdge.twin;
		}
		else {
			Q3.incidentEdge.twin.prev = previousVertex.incidentEdge.twin;
			previousVertex.incidentEdge.twin.next = Q3.incidentEdge.twin;

		}
		
		Q3.incidentEdge.twin.origin = previousVertex;
		Q3.incidentEdge.next = previousVertex.incidentEdge;
		previousVertex.incidentEdge.prev = Q3.incidentEdge;
		connectingHalfEdges.add(Q3.incidentEdge);
		boundingVertices.add(Q3);
		return connectingHalfEdges;	*/
	}

}
