/*
 * Copyright 2011-2014 David Godfrey. All rights reserved.
 */
package com.godfrey.geometry.dcel;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

import com.godfrey.geometry.dcel.boundingbox.BoundingBox;
import com.godfrey.geometry.dcel.face.Face;
import com.godfrey.geometry.dcel.halfedge.HalfEdge;
import com.godfrey.geometry.dcel.vertex.Vertex;


public class DCEL {

	Vector<Vertex> vertices;
	Vector<Face> faces;
	Vector<HalfEdge> edges;
	BoundingBox boundingBox;
	
	public DCEL() {
		vertices = new Vector<Vertex>();
		faces = new Vector<Face>();
		edges = new Vector<HalfEdge>();
	}
	
	protected void addVertex(Vertex v) {
		vertices.add(v);
	}
	
	public void addEdge(HalfEdge e) {
		edges.add(e);
	}
	
	public void addFace(Face f) {
		faces.add(f);
	}
	
	public Vector<Vertex> getVertices() {
		return vertices;
	}

	public Vector<Face> getFaces() {
		return faces;
	}

	public Vector<HalfEdge> getEdges() {
		return edges;
	}
	
	public void attachBoundingBox(BoundingBox boundingBox) {
		this.boundingBox = boundingBox;
		this.boundingBox.sort();
		//boundingBox.connect();
		Vertex previousVertex = boundingBox.Q3;
		while(!boundingBox.bBoxStack.empty()) {
			//Walk along the bounding box, attaching the half infinite vertices
			//to the bounding box, along with the corners of the bounding box.
			Vertex v = boundingBox.bBoxStack.pop();
//			System.out.println("Handling vertex <"+v.getCoords().getX()+","+v.getCoords().getY()+">");
			HalfEdge e = new HalfEdge();
			HalfEdge f = new HalfEdge();
			e.origin = v;
			f.origin = previousVertex;
			e.twin = f;
			f.twin = e;
			e.next = previousVertex.getIncidentEdge();
			previousVertex.getIncidentEdge().prev = e;
			
			if(!(v.equals(boundingBox.Q1) || v.equals(boundingBox.Q2) || v.equals(boundingBox.Q3) || v.equals(boundingBox.Q4))) {
				e.prev = v.getIncidentEdge().twin;
				e.incidentSite = v.getIncidentEdge().twin.incidentSite;
				v.getIncidentEdge().twin.next = e;
			}
			else
				v.setIncidentEdge(e);
			
			if(!(previousVertex.equals(boundingBox.Q1) || previousVertex.equals(boundingBox.Q2) || previousVertex.equals(boundingBox.Q3) || previousVertex.equals(boundingBox.Q4))) {
				f.prev = previousVertex.getIncidentEdge().twin.next.twin;
				previousVertex.getIncidentEdge().twin.next.twin.next = f;
				
			}
			else {
				f.prev = previousVertex.getIncidentEdge().twin;
				previousVertex.getIncidentEdge().twin.next = f;
			}
			
			edges.add(e);
			previousVertex = v;
			vertices.add(v);
		}
		
		if(!(previousVertex.equals(boundingBox.Q1) || previousVertex.equals(boundingBox.Q2) || previousVertex.equals(boundingBox.Q3) || previousVertex.equals(boundingBox.Q4))) {
			boundingBox.Q3.getIncidentEdge().twin.prev = previousVertex.getIncidentEdge().twin.next.twin;
			previousVertex.getIncidentEdge().twin.next.twin.next = boundingBox.Q3.getIncidentEdge().twin;
		}
		else {
			boundingBox.Q3.getIncidentEdge().twin.prev = previousVertex.getIncidentEdge().twin;
			previousVertex.getIncidentEdge().twin.next = boundingBox.Q3.getIncidentEdge().twin;

		}
		
		boundingBox.Q3.getIncidentEdge().twin.origin = previousVertex;
		boundingBox.Q3.getIncidentEdge().next = previousVertex.getIncidentEdge();
		previousVertex.getIncidentEdge().prev = boundingBox.Q3.getIncidentEdge();
		edges.add(boundingBox.Q3.getIncidentEdge());
		vertices.add(boundingBox.Q3);
		
/*		Vertex v = vertices.firstElement();
		Set<Vertex> vistedVertices = new TreeSet<Vertex>();
		while(!vistedVertices.contains(v)) {
			vistedVertices.add(v);
			v = v.incidentEdge.twin.next.origin;
		}
		if(vistedVertices.size() == 4) {
			throw new RuntimeException("Minimum num of vertices on bounding box");
		}*/

	}

	public BoundingBox getBoundingBox() {
		return boundingBox;
	}

	public void createCellRecords() {
		/**
		 * The following commented code was used as the DCEL was being constructed.
		 * The first objective was to ensure that the unbounded face was connected using "next" pointers
		 * The second objective was to ensure that the unbounded face was connected using "prev" pointers
		 * The third objective was to ensure that all of the half-infinite faces (outer faces) were
		 * linked correctly.
		 * 
		 * To create the cell records, we need only to visit each vertex, and traverse each of the half edges
		 * that are incident to that vertex. Each vertex has exactly one edge that's incident to it, but there are
		 * three half-edges that share a common vertex ( HalfEdge.origin ).
		 */
//		Vertex v = D.getBoundingBox().getQ3();
//		Set<Point> rim = new HashSet<Point>();
//		//Walk along the outer boundary clockwise
//		HalfEdge e = v.getIncidentEdge().getTwin();
//		Vector<HalfEdge> outerFaceIncidentEdges = new Vector<HalfEdge>();
//		rim.add(e.getOrigin().getCoords());
//		while(!rim.contains(v.getCoords())) {
//			System.out.println("e->origin<"+e.getOrigin().getCoords().getX()+","+e.getOrigin().getCoords().getY()+">");
//			rim.add(e.getOrigin().getCoords());
//			outerFaceIncidentEdges.add(e);
//			e = e.getPrev();
//		}
//		
//		for(Point p : rim) {
//			System.out.println("<"+p.getX()+","+p.getY()+">");
//		}
//		
//		//Walk along the outer boundary counter-clockwise
//		rim = new HashSet<Point>();
//		e = v.getIncidentEdge().getTwin().getNext().getNext();
//		rim.add(e.getOrigin().getCoords());
//		while(!rim.contains(v.getCoords())) {
//			System.out.println("e->origin<"+e.getOrigin().getCoords().getX()+","+e.getOrigin().getCoords().getY()+">");
//			rim.add(e.getOrigin().getCoords());
//			e = e.getNext();
//		}
//		for(Point p : rim) {
//			System.out.println("<"+p.getX()+","+p.getY()+">");
//		}
//
//		Set<Vertex> visitedVertices = new HashSet<Vertex>();
//		
//		//Walk along all the outer faces...
//		Vector<Vertex> boundingVertices = D.getBoundingBox().getBoundingBox();
//		System.out.println("Bounding box size" + boundingVertices.size());
//		for(Vertex u : boundingVertices) {
//			HalfEdge uIncident = u.getIncidentEdge().getNext();
//			System.out.println("Walking from u<"+u.getCoords().getX()+","+u.getCoords().getY()+">");
//			if(uIncident.getIncidentFace()==null) {
//				Face f = new Face();
//				while(!(uIncident.getOrigin().getCoords().equals(u.getCoords()))) {
//					System.out.println("\t uIncident<"+uIncident.getOrigin().getCoords().getX()+","+uIncident.getOrigin().getCoords().getY()+">");
//					uIncident.setIncidentFace(f);
//					uIncident = uIncident.getNext();
//				}
//				uIncident.setIncidentFace(f);
//				System.out.println("\t uIncident<"+uIncident.getOrigin().getCoords().getX()+","+uIncident.getOrigin().getCoords().getY()+">");
//				f.setIncidentEdge(uIncident);
//				faces.add(f);
//			}
//			else
//				System.out.println("This face already exists in the DCEL");
//			visitedVertices.add(u);
//		}
//		System.out.println("Finished walking the outer faces");
		Random randomColor = new Random();
		Set<Vertex> visitedVertices = new HashSet<Vertex>();
		//Walk along all other cycles in the graph ( Inner vertices )
		for(Vertex u : vertices) {
			//We've already visited all vertices along the bounding box...
			if(!visitedVertices.contains(u)) {
				Set<HalfEdge> visitedHalfEdges = new HashSet<HalfEdge>();
				//Traverse the edges connected to this vertex
//				System.out.println("Walking from u<"+u.getCoords().getX()+","+u.getCoords().getY()+">");
				HalfEdge g = u.getIncidentEdge();
				while(!visitedHalfEdges.contains(u.getIncidentEdge())) {
					HalfEdge uIncident = g.next;
					if(uIncident.incidentFace == null) {
						Face f = new Face();
						while(!(uIncident.origin.equals(u))) {
//							System.out.println("\t uIncident<"+uIncident.origin.coords.getX()+","+uIncident.origin.coords.getY()+">");
							uIncident.incidentFace = f;
							uIncident = uIncident.next;
							if(uIncident.incidentSite != null) {
								f.setContainedSite(uIncident.incidentSite);
							}
						}
						uIncident.incidentFace = f;
//						System.out.println("\t uIncident<"+uIncident.getOrigin().getCoords().getX()+","+uIncident.getOrigin().getCoords().getY()+">");
						f.incidentEdge = uIncident;
						f.setColor((double)randomColor.nextInt(256)/256.0, (double)randomColor.nextInt(256)/256.0, (double)randomColor.nextInt(256)/256.0);
						faces.add(f);
					}
//					else
//						System.out.println("This face already exists in the DCEL");
					visitedVertices.add(u);
					visitedHalfEdges.add(g);
					g = u.getIncidentEdge().twin.next;
				}
			}
//			else
//				System.out.println("This vertex has already been visited");
			visitedVertices.add(u);
		}
		//System.out.println("There are : " + faces.size() + " faces");
		
	}
	
}
