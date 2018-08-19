/*
 * Copyright 2011-2014 David Godfrey. All rights reserved.
 */
package com.godfrey.geometry.dcel.boundingbox;

import java.util.Collections;
import java.util.Stack;
import java.util.Vector;

import com.godfrey.geometry.dcel.halfedge.HalfEdge;
import com.godfrey.geometry.dcel.vertex.Vertex;
import com.godfrey.geometry.linesegment.LineSegment;
import com.godfrey.geometry.utils.comparators.PolarAngleComparator;


/**
 * A helper class. Class BoundingBox provides a sort method, that transforms
 * 
 * @author BriefThought
 *
 */
public class BoundingBox {

	public Vector<Vertex> boundingBox;
	public Stack<Vertex> bBoxStack;
	public Vertex center;
	
	//The corner vertex in the first quadrant
	public Vertex Q1;
	//The corner vertex in the second quadrant
	public Vertex Q2;
	//The corner vertex in the third quadrant
	public Vertex Q3;
	//The corner vertex in the fourth quadrant
	public Vertex Q4;
	
	public BoundingBox(Vertex v1, Vertex v2, Vertex v3, Vertex v4) {
		
//		System.out.println("v1<"+v1.getCoords().getX()+","+v1.getCoords().getY()+">");
//		System.out.println("v2<"+v2.getCoords().getX()+","+v2.getCoords().getY()+">");
//		System.out.println("v3<"+v3.getCoords().getX()+","+v3.getCoords().getY()+">");
//		System.out.println("v4<"+v4.getCoords().getX()+","+v4.getCoords().getY()+">");
		
		boundingBox = new Vector<Vertex>();
		
		HalfEdge e3 = new HalfEdge();
		HalfEdge e3t = new HalfEdge();

		e3.twin = e3t;
		e3t.twin = e3;

		e3.origin = v3;

		v3.setIncidentEdge(e3);
		
		Q1 = v1;
		Q2 = v2;
		Q3 = v3;
		Q4 = v4;
		
		boundingBox.add(v1);
		boundingBox.add(v2);
		//boundingBox.add(v3);
		boundingBox.add(v4);
		
		LineSegment Q1Q3 = new LineSegment(Q1,Q3);
		LineSegment Q2Q4 = new LineSegment(Q2,Q4);
		
		center = Q1Q3.getIntersection(Q2Q4);
		bBoxStack = new Stack<Vertex>();
	}

	public BoundingBox() {
		boundingBox = new Vector<Vertex>();
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

	public void sort() {
		//This comparator will sort by increasing polar angle w.r.t. Q3, from Q3 up to Q1.
		//Any vertex along the boundary counterclockwise from Q1 will be sorted in
		//reverse order ( clockwise from Q3 ) via the law of cosines
		PolarAngleComparator pac = new PolarAngleComparator(new LineSegment(center,Q3),Q1,Q3);
		Collections.sort(boundingBox, pac);
//		System.out.println("BoundingBox.connect() size " + boundingBox.size());
		
		//Thus, the vertices are operated on using two stacks in order to preserve correct order.
		Stack<Vertex> upperLeftBox = new Stack<Vertex>();
		
		while(!boundingBox.firstElement().equals(Q1))
			upperLeftBox.push(boundingBox.remove(0));
		while(!upperLeftBox.isEmpty())
			bBoxStack.push(upperLeftBox.pop());
		
		//The resulting stack stores the vertices that lie along the bounding box
		//in counter-clockwise order, ending with the lowest vertex above Q3, above and along the
		//line connecting Q3 and Q4 at the top of the stack, and starting with the vertex immediately
		//to the right of Q3, along a line connecting Q3 and Q2.
		bBoxStack.addAll(boundingBox);
//		System.out.println("BoundingBox.connect() size after adding all to stack : " + boundingBox.size());
		//walk along the boundary in clockwise order adding the unbounded edges to the outside of the
		//com.godfrey.geometry.voronoi
//		Stack<Vertex> bBoxStackReverse = new Stack<Vertex>();
//		while(!bBoxStack.empty())
//			bBoxStackReverse.push(bBoxStack.pop());
/*		Vertex previousVertex = Q3;
//		connectingHalfEdges.add(Q3.incidentEdge);
		boundingBox = new Vector<Vertex>();
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
			boundingBox.add(v);
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
		boundingBox.add(Q3);
		return connectingHalfEdges;	*/
	}

	public Vertex getCenter() {
		return center;
	}

}
