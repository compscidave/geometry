/*
 * Copyright 2011-2014 David Godfrey. All rights reserved.
 */
package com.godfrey.geometry.test.voronoi;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

import com.godfrey.geometry.test.voronoi.exception.VoronoiConstructionException;

public class DCEL {

	private Vector<Vertex> vertices;
	private Vector<Face> faces;
	private Vector<HalfEdge> edges;
	private BoundingBox boundingBox;
	private Vector<LineSegmentIntersection> intersections;
	
	public DCEL() {
		vertices = new Vector<Vertex>();
		faces = new Vector<Face>();
		edges = new Vector<HalfEdge>();
		intersections = new Vector<LineSegmentIntersection>();
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

	public void newAttachBoundingBox(BoundingBox boundingBox) throws VoronoiConstructionException {
//		this.boundingBox = boundingBox;
//		this.boundingBox.buildStack();
		Set<DCELLineSegment> linesegments = new HashSet<DCELLineSegment>();
		vertices.add(boundingBox.Q1);
		vertices.add(boundingBox.Q2);
		vertices.add(boundingBox.Q3);
		vertices.add(boundingBox.Q4);
		edges.add(boundingBox.Q1.incidentEdge);
		edges.add(boundingBox.Q2.incidentEdge);
		edges.add(boundingBox.Q3.incidentEdge);
		edges.add(boundingBox.Q4.incidentEdge);
		linesegments.addAll(boundingBox.boundingSegments);
		linesegments.addAll(boundingBox.intersectingSegments);
//		fi.printQueue();
/*
		for(HalfEdge e : edges) {
			DCELLineSegment ls = new DCELLineSegment(e.origin,e.twin.origin);
			if(ls.getLength() != 0.0d)
				linesegments.add(ls);
			else
				System.out.println(ls);
		}	*/	
		FindIntersections fi = new FindIntersections(linesegments);
		fi.computeIntersections();
		intersections = fi.getIntersections();
//		int unaccountedIntersections = 0;
//		Set<Vertex> setVertices = new HashSet<Vertex>();
//		for(Vertex v : vertices) {
//			boolean intersectionAccounted = false;
//			for(LineSegmentIntersection lsi : intersections) {
//				setVertices.add(v);
//				if(lsi.coordinate.distance(v) < 1)
//					intersectionAccounted = true;
//			}
//			if(!intersectionAccounted) {
//				unaccountedIntersections++;
//				System.out.println("Unaccounted intersection!");
//				System.out.println(v);
//				throw new VoronoiConstructionException("Unable to compute intersection for : " + v);
//			}
//		}
//		System.out.println("There are  " + setVertices.size() + " unique vertices");
//		System.out.println("There were " + unaccountedIntersections + " unaccounted intersections");
//		for(LineSegmentIntersection lsi : intersections) {
//			System.out.println("Intersection point : " + lsi.coordinate.toString());
//			for(DCELLineSegment ls : lsi.containingSegments){ 
//				System.out.println("\t" + ls.toString());
//			}
//		}
 /*		//boundingVertices.connect();
//		System.out.println("DCEL.attachBoundingBox : " + this.boundingBox.bBoxStack.size());
		Vertex previousVertex = this.boundingBox.Q3;

		while(!this.boundingBox.bBoxStack.empty()) {
			//Walk along the bounding box, attaching the half infinite vertices
			//to the bounding box, along with the corners of the bounding box.
			Vertex v = this.boundingBox.bBoxStack.pop();
//			System.out.println("Handling vertex <"+v.getCoords().getLongitude()+","+v.getCoords().getLatitude()+">");
			HalfEdge e = new HalfEdge();
			HalfEdge f = new HalfEdge();
			e.origin = v;
			f.origin = previousVertex;
			e.twin = f;
			f.twin = e;
			e.next = previousVertex.incidentEdge;
			previousVertex.incidentEdge.prev = e;
			
			if(!(v.coords.equals(this.boundingBox.Q1.coords) || v.coords.equals(this.boundingBox.Q2.coords) || v.coords.equals(this.boundingBox.Q3.coords) || v.coords.equals(this.boundingBox.Q4.coords))) {
				e.prev = v.incidentEdge.twin;
				e.newIncidentSite = v.incidentEdge.twin.newIncidentSite;
				v.incidentEdge.twin.next = e;
			}
			else
				v.setIncidentEdge(e);
			
			if(!(previousVertex.coords.equals(this.boundingBox.Q1.coords) || previousVertex.coords.equals(this.boundingBox.Q2.coords) || previousVertex.coords.equals(this.boundingBox.Q3.coords) || previousVertex.coords.equals(this.boundingBox.Q4.coords))) {
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
		
		if(!(previousVertex.coords.equals(this.boundingBox.Q1.coords) || previousVertex.coords.equals(this.boundingBox.Q2.coords) || previousVertex.coords.equals(this.boundingBox.Q3.coords) || previousVertex.coords.equals(this.boundingBox.Q4.coords))) {
			this.boundingBox.Q3.incidentEdge.twin.prev = previousVertex.incidentEdge.twin.next.twin;
			previousVertex.incidentEdge.twin.next.twin.next = this.boundingBox.Q3.incidentEdge.twin;
		}
		else {
			this.boundingBox.Q3.incidentEdge.twin.prev = previousVertex.incidentEdge.twin;
			previousVertex.incidentEdge.twin.next = this.boundingBox.Q3.incidentEdge.twin;

		}
		
		this.boundingBox.Q3.incidentEdge.twin.origin = previousVertex;
		this.boundingBox.Q3.incidentEdge.next = previousVertex.incidentEdge;
		previousVertex.incidentEdge.prev = this.boundingBox.Q3.incidentEdge;
		edges.add(this.boundingBox.Q3.incidentEdge);
		vertices.add(this.boundingBox.Q3);*/
	}
	
	public Vector<LineSegmentIntersection> getIntersections() {
		return intersections;
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
		Set<Location> rim = new HashSet<Location>();
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
		
		for(Location p : rim) {
			System.out.println("<"+p.getLongitude()+","+p.getLatitude()+">");
		}
		
		//Walk along the outer boundary counter-clockwise
		rim = new HashSet<Location>();
		e = v.getIncidentEdge().getTwin().getNext().getNext();
		rim.add(e.getOrigin().getCoords());
		while(!rim.contains(v.getCoords())) {
			System.out.println("e->origin<"+e.getOrigin().getCoords().getLongitude()+","+e.getOrigin().getCoords().getLatitude()+">");
			rim.add(e.getOrigin().getCoords());
			e = e.getNext();
		}
		for(Location p : rim) {
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
//				System.out.println("Walking from u<"+u.getCoords().getLongitude()+","+u.getCoords().getLatitude()+">");
				HalfEdge g = u.incidentEdge;
				while(!visitedHalfEdges.contains(u.incidentEdge)) {
					HalfEdge uIncident = g.next;
					if(uIncident.incidentFace == null) {
						Face f = new Face();
						boolean foundIncidentCab = false;
						while(!(uIncident.origin.getCoords().equals(u.getCoords()))) {
//							System.out.println("\t uIncident<"+uIncident.origin.coords.getLongitude()+","+uIncident.origin.coords.getLatitude()+">");
							uIncident.incidentFace = f;
							uIncident = uIncident.next;
							if(uIncident.newIncidentSite != null) {
								f.setContainedSite(uIncident.newIncidentSite);
								foundIncidentCab = true;
							}
//							else
//								System.out.println("A half-edge exists which does not hold site information...:<" + uIncident.origin.coords.getLongitude() + "," + uIncident.origin.coords.getLatitude() + ">");
						}
//						if(!foundIncidentCab)
//							System.out.println("Could not attach site information to face with uIncident : <" + uIncident.getOrigin().getCoords().getLongitude() + "," + uIncident.getOrigin().getCoords().getLatitude() + "> and destination <" + uIncident.getTwin().getOrigin().getCoords().getLongitude() + "," + uIncident.getTwin().getOrigin().getCoords().getLatitude() + ">");
//						else
//							System.out.println("Incident Cab Successfully Found");
						uIncident.incidentFace = f;
//						System.out.println("\t uIncident<"+uIncident.getOrigin().getCoords().getLongitude()+","+uIncident.getOrigin().getCoords().getLatitude()+">");
						f.incidentEdge = uIncident;
						f.setColor((double)randomColor.nextInt(256)/256.0, (double)randomColor.nextInt(256)/256.0, (double)randomColor.nextInt(256)/256.0);
						faces.add(f);
					}
//					else
//						System.out.println("This face already exists in the DCEL");
					visitedVertices.add(u);
					visitedHalfEdges.add(g);
					g = u.incidentEdge.twin.next;
				}
			}
			else
				System.out.println("This vertex has already been visited");
			visitedVertices.add(u);
		}
//		System.out.println("There are : " + faces.size() + " faces");
	}
	
}
