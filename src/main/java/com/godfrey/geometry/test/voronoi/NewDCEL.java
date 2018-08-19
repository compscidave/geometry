/*
 * Copyright 2011-2014 David Godfrey. All rights reserved.
 */
package com.godfrey.geometry.test.voronoi;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

public class NewDCEL {

	private Vector<Vertex> vertices;
	private Vector<Face> faces;
	private Vector<HalfEdge> edges;
	private BoundingBox boundingBox;
	
	public NewDCEL() {
		vertices = new Vector<Vertex>();
		faces = new Vector<Face>();
		edges = new Vector<HalfEdge>();
	}
	
	public void addVertex(Vertex v) {
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

	public void newAttachBoundingBox(BoundingBox boundingBox) {
		this.boundingBox = boundingBox;
//		this.boundingBox.buildStack();
		//boundingVertices.connect();
//		System.out.println("DCEL.attachBoundingBox : " + this.boundingBox.bBoxStack.size());
		Vertex previousVertex = boundingBox.Q3;

		while(!this.boundingBox.bBoxStack.empty()) {
			//Walk along the bounding box, attaching the half infinite vertices
			//to the bounding box, along with the corners of the bounding box.
			Vertex v = this.boundingBox.bBoxStack.pop();
			System.out.println("Handling vertex <"+v.getCoords().getLongitude()+","+v.getCoords().getLatitude()+">");
			HalfEdge e = new HalfEdge();
			HalfEdge f = new HalfEdge();
			e.origin = v;
			f.origin = previousVertex;
			e.twin = f;
			f.twin = e;
			e.next = previousVertex.incidentEdge;
			previousVertex.incidentEdge.prev = e;
			
			if(!(v.equals(boundingBox.Q1) || v.equals(boundingBox.Q2) || v.equals(boundingBox.Q3) || v.equals(boundingBox.Q4))) {
				e.prev = v.incidentEdge.twin;
				e.newIncidentSite = v.incidentEdge.twin.newIncidentSite;
				v.incidentEdge.twin.next = e;
			}
			else
				v.setIncidentEdge(e);
			
			if(!(previousVertex.equals(boundingBox.Q1) || previousVertex.equals(boundingBox.Q2) || previousVertex.equals(boundingBox.Q3) || previousVertex.equals(boundingBox.Q4))) {
				f.prev = previousVertex.incidentEdge.twin.next.twin;
				previousVertex.incidentEdge.twin.next.twin.next = f;
				
			}
			else {
				f.prev = previousVertex.incidentEdge.twin;
				previousVertex.incidentEdge.twin.next = f;
				previousVertex.incidentEdge.newIncidentSite = v.incidentEdge.newIncidentSite;
			}
			
			edges.add(e);
			previousVertex = v;
			vertices.add(v);
		}
		
		if(!(previousVertex.equals(boundingBox.Q1) || previousVertex.equals(boundingBox.Q2) || previousVertex.equals(boundingBox.Q3) || previousVertex.equals(boundingBox.Q4))) {
			boundingBox.Q3.incidentEdge.twin.prev = previousVertex.incidentEdge.twin.next.twin;
			previousVertex.incidentEdge.twin.next.twin.next = boundingBox.Q3.incidentEdge.twin;
		}
		else {
			boundingBox.Q3.incidentEdge.twin.prev = previousVertex.incidentEdge.twin;
			previousVertex.incidentEdge.twin.next = boundingBox.Q3.incidentEdge.twin;

		}
		
		boundingBox.Q3.incidentEdge.twin.origin = previousVertex;
		boundingBox.Q3.incidentEdge.next = previousVertex.incidentEdge;
		previousVertex.incidentEdge.prev = boundingBox.Q3.incidentEdge;
		edges.add(boundingBox.Q3.incidentEdge);
		vertices.add(boundingBox.Q3);
	}
	
	public BoundingBox getBoundingBox() {
		return boundingBox;
	}
	
	public void newCreateCellRecords() {
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
/*		Vertex v = D.getBoundingBox().getQ3();
		Set<Coordinate> rim = new HashSet<Coordinate>();
		//Walk along the outer boundary clockwise
		HalfEdge e = v.getIncidentEdge().getTwin();
		Vector<HalfEdge> outerFaceIncidentEdges = new Vector<HalfEdge>();
		rim.add(e.getOrigin().getCoords());
		while(!rim.contains(v.getCoords())) {
			System.out.println("e->origin<"+e.getOrigin().getCoords().getLongitude()+","+e.getOrigin().getCoords().getLatitude()+">");
			rim.add(e.getOrigin().getCoords());
			outerFaceIncidentEdges.add(e);
			e = e.getPrev();
		}
		
		for(Coordinate p : rim) {
			System.out.println("<"+p.getLongitude()+","+p.getLatitude()+">");
		}
		
		//Walk along the outer boundary counter-clockwise
		rim = new HashSet<Coordinate>();
		e = v.getIncidentEdge().getTwin().getNext().getNext();
		rim.add(e.getOrigin().getCoords());
		while(!rim.contains(v.getCoords())) {
			System.out.println("e->origin<"+e.getOrigin().getCoords().getLongitude()+","+e.getOrigin().getCoords().getLatitude()+">");
			rim.add(e.getOrigin().getCoords());
			e = e.getNext();
		}
		for(Coordinate p : rim) {
			System.out.println("<"+p.getLongitude()+","+p.getLatitude()+">");
		}

		Set<Vertex> visitedVertices = new HashSet<Vertex>();
		
		//Walk along all the outer faces...
		System.out.println("Bounding box size: " + boundingVertices.boundingBox.size());
		for(Vertex u : boundingVertices.boundingBox) {
			HalfEdge uIncident = u.getIncidentEdge().getNext();
			System.out.println("Walking from u<"+u.getCoords().getLongitude()+","+u.getCoords().getLatitude()+">");
			if(uIncident.getIncidentFace()==null) {
				Face f = new Face();
				while(!(uIncident.getOrigin().getCoords().equals(u.getCoords()))) {
					System.out.println("\t uIncident<"+uIncident.getOrigin().getCoords().getLongitude()+","+uIncident.getOrigin().getCoords().getLatitude()+">");
					uIncident.setIncidentFace(f);
					uIncident = uIncident.getNext();
				}
				uIncident.setIncidentFace(f);
				System.out.println("\t uIncident<"+uIncident.getOrigin().getCoords().getLongitude()+","+uIncident.getOrigin().getCoords().getLatitude()+">");
				f.setIncidentEdge(uIncident);
				faces.add(f);
			}
			else
				System.out.println("This face already exists in the DCEL");
			visitedVertices.add(u);
		}
		System.out.println("Finished walking the outer faces");
		*/
		Random randomColor = new Random();
		Set<Vertex> visitedVertices = new HashSet<Vertex>();
		//Walk along all other cycles in the graph ( Inner vertices )
		for(Vertex u : vertices) {
			//We've already visited all vertices along the bounding box...
			if(!visitedVertices.contains(u)) {
				Set<HalfEdge> visitedHalfEdges = new HashSet<HalfEdge>();
				//Traverse the edges connected to this vertex
				System.out.println("Walking from u<"+u.getCoords().getLongitude()+","+u.getCoords().getLatitude()+">");
				HalfEdge g = u.incidentEdge;
				while(!visitedHalfEdges.contains(u.incidentEdge)) {
					HalfEdge uIncident = g.next;
					if(uIncident.incidentFace == null) {
						Face f = new Face();
						boolean foundIncidentCab = false;
						while(!(uIncident.origin.equals(u))) {
							System.out.println("\t uIncident<"+uIncident.origin.getLongitude()+","+uIncident.origin.getLatitude()+">");
							uIncident.incidentFace = f;
							uIncident = uIncident.next;
							if(uIncident.newIncidentSite != null) {
								f.setContainedSite(uIncident.newIncidentSite);
								foundIncidentCab = true;
							}
							else
								System.out.println("A half-edge exists which does not hold site information...:<" + uIncident.origin.getLongitude() + "," + uIncident.origin.getLatitude() + ">");
						}
						if(!foundIncidentCab)
							System.out.println("Could not attach site information to face with uIncident : <" + uIncident.getOrigin().getCoords().getLongitude() + "," + uIncident.getOrigin().getCoords().getLatitude() + "> and destination <" + uIncident.getTwin().getOrigin().getCoords().getLongitude() + "," + uIncident.getTwin().getOrigin().getCoords().getLatitude() + ">");
						else
							System.out.println("Incident Cab Successfully Found");
						uIncident.incidentFace = f;
						System.out.println("\t uIncident<"+uIncident.getOrigin().getCoords().getLongitude()+","+uIncident.getOrigin().getCoords().getLatitude()+">");
						f.incidentEdge = uIncident;
						f.setColor((double)randomColor.nextInt(256)/256.0, (double)randomColor.nextInt(256)/256.0, (double)randomColor.nextInt(256)/256.0);
						faces.add(f);
					}
					else
						System.out.println("This face already exists in the DCEL");
					visitedVertices.add(u);
					visitedHalfEdges.add(g);
					g = u.incidentEdge.twin.next;
				}
			}
			else
				System.out.println("This vertex has already been visited");
			visitedVertices.add(u);
		}
		System.out.println("There are : " + faces.size() + " faces");
	}
	
}
