/*
 * Copyright 2011-2014 David Godfrey. All rights reserved.
 */
package com.godfrey.geometry.test.voronoi;

import java.util.Collections;
import java.util.Stack;
import java.util.Vector;

public class NewBoundingBox {

	Vector<Vertex> boundingVertices;
	Stack<Vertex> bBoxStack;
	Vertex center;
	
	Vector<DCELLineSegment> lineStrips;
	//The corner vertex in the first quadrant
	Vertex Q1;
	//The corner vertex in the second quadrant
	Vertex Q2;
	//The corner vertex in the third quadrant
	Vertex Q3;
	//The corner vertex in the fourth quadrant
	Vertex Q4;
	
	public NewBoundingBox() {
		boundingVertices = new Vector<Vertex>();
		bBoxStack = new Stack<Vertex>();
		lineStrips = new Vector<DCELLineSegment>();
		center = new Vertex(new Coordinate(0.0d,0.0d));
	}
	
	public NewBoundingBox(Vertex v1, Vertex v2, Vertex v3, Vertex v4) {
		
//		System.out.println("v1<"+v1.getCoords().getX()+","+v1.getCoords().getY()+">");
//		System.out.println("v2<"+v2.getCoords().getX()+","+v2.getCoords().getY()+">");
//		System.out.println("v3<"+v3.getCoords().getX()+","+v3.getCoords().getY()+">");
//		System.out.println("v4<"+v4.getCoords().getX()+","+v4.getCoords().getY()+">");
		
		boundingVertices = new Vector<Vertex>();
		bBoxStack = new Stack<Vertex>();
		
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
		
		e1.origin = v1;
		e2.origin = v2;
		e3.origin = v3;
		e4.origin = v4;

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
		
		v1.incidentEdge = e1;
		v2.incidentEdge = e2;
		v3.incidentEdge = e3;
		v4.incidentEdge = e4;
		
		Q1 = v1;
		Q2 = v2;
		Q3 = v3;
		Q4 = v4;
		
		boundingVertices.add(v1);
		boundingVertices.add(v2);
		
		/**
		 * PolarAngleComparator, used to order the vertices that lie
		 * around the bounding box, relies on Vertex Q3 as a guide vertex.
		 * As such, the operation Collections.sort(Vector<Vertex> boundingVertices, PolarAngleComparator pac)
		 * may be undefined for a Vector<Vertex> parameter containing Q3. Thus, Q3(v3) is added only
		 * after the rest of the bounding box has been connected to the DCEL.
		 */
		/*boundingVertices.add(v3);*/
		
		boundingVertices.add(v4);
		
		lineStrips.add(new DCELLineSegment(Q1,Q2));
		lineStrips.add(new DCELLineSegment(Q2,Q3));
		lineStrips.add(new DCELLineSegment(Q3,Q4));
		lineStrips.add(new DCELLineSegment(Q4,Q1));
		
		DCELLineSegment Q1Q3 = new DCELLineSegment(Q1,Q3);
		DCELLineSegment Q2Q4 = new DCELLineSegment(Q2,Q4);
		
		center = Q1Q3.getIntersection(Q2Q4);

	}

	public void buildStack() {
		//This comparator will sort by increasing polar angle w.r.t. Q3, from Q3 up to Q1.
		//Any vertex along the boundary counterclockwise from Q1 will be sorted in
		//reverse order ( clockwise from Q3 ) via the law of cosines
		PolarAngleComparator pac = new PolarAngleComparator(new DCELLineSegment(center,Q3),Q1,Q3);
		Collections.sort(boundingVertices, pac);
//		System.out.println("BoundingBox.connect() size " + boundingVertices.size());
		
		//Thus, the vertices are operated on using two stacks in order to preserve correct order.
		Stack<Vertex> upperLeftBox = new Stack<Vertex>();

		while(boundingVertices.firstElement()!=Q1)
			upperLeftBox.push(boundingVertices.remove(0));

		while(!upperLeftBox.isEmpty())
			bBoxStack.push(upperLeftBox.pop());
		
		//The resulting stack stores the vertices that lie along the bounding box
		//in counter-clockwise order, ending with the lowest vertex above Q3, above and along the
		//line connecting Q3 and Q4 at the top of the stack, and starting with the vertex immediately
		//to the right of Q3, along a line connecting Q3 and Q2 on the bottom of the stack.
		bBoxStack.addAll(boundingVertices);
	}

	public void addVertex(Vertex v) {
		boundingVertices.add(v);
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
	
	public Vertex getCenter() {
		return center;
	}

	
}
