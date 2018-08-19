/*
 * Copyright 2011-2014 David Godfrey. All rights reserved.
 */
package com.godfrey.geometry.test.voronoi;

import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Vector;

import com.godfrey.geometry.test.voronoi.Site;
import com.godfrey.geometry.test.voronoi.exception.CircleEventException;
import com.godfrey.geometry.test.voronoi.exception.SiteEventException;
import com.godfrey.geometry.test.voronoi.exception.VoronoiConstructionException;

public class Voronoi {

	//Hack method of handling the degenerate case where two or more sites exist
	//at the start of the algorithm that share the same y-coordinate.
	//This constant adjusts the y-coordinate of such a site.
	private static final int SITE_CORRECTION = 5;
	private PriorityQueue<NewEvent> nQ;
	private StatusTree nT;
	private DCEL nD;
	private DCEL boundingBox;
	private Vector<Site> eventSites;
	private int numHalfInfiniteVertices;
	
	
	public Voronoi() {
		Comparator<NewEvent> ec = new NewEventComparator();
		nQ = new PriorityQueue<NewEvent>(10,ec);
		nT = new StatusTree();
		nD = new DCEL();
		eventSites = new Vector<Site>();
		boundingBox = new DCEL();
	}
		
	public Voronoi(List<Site> sites) {
		Comparator<NewEvent> ec = new NewEventComparator();
		eventSites = new Vector<Site>();
		nQ = new PriorityQueue<NewEvent>(10, ec);
		for(Site site : sites) {
			nQ.add(new SiteEvent(site));
			eventSites.add(site);
		}
		//This is a janky way of curbing the "two sites at the start of the sweep
		//share the same y-value" degeneracy. In reality, the slope of the sweep
		//line needs to be adjusted so that it's not horizontal, and the points should be sorted
		//again. Would need to apply a transformation matrix to all points, rotating such that
		//the sweep line is oriented horizontally, then sort by the transformed y-coordinate.
		//keep doing this until there are no two points that share the same y coordinate.
		if(!nQ.isEmpty()) {
			NewEvent ne = nQ.poll();
			if(!nQ.isEmpty() && Math.abs(ne.eventSite.getY() - nQ.peek().eventSite.getY()) < 0.00000001) {
				double x = ne.eventSite.getX();
				double y = ne.eventSite.getY();
				ne.setEventSite(x,y+SITE_CORRECTION);
				nQ.add(ne);
			}
			else
				nQ.add(ne);
		}

		nT = new StatusTree();
		nD = new DCEL();
		boundingBox = new DCEL();
	}

	public void newCompute(double LONGITUDE, double LATITUDE) throws VoronoiConstructionException {
		if(nQ.isEmpty())
			throw new VoronoiConstructionException("No events were supplied to the event queue");
		else 
			if(nQ.size() == 1) {
				BoundingBox boundingBox = createOuterEnvelope(null,LONGITUDE,LATITUDE);
				nD.addEdge(boundingBox.Q1.incidentEdge);
				nD.addEdge(boundingBox.Q2.incidentEdge);
				nD.addEdge(boundingBox.Q3.incidentEdge);
				nD.addEdge(boundingBox.Q4.incidentEdge);
				Face f = new Face();
				f.containedSite = eventSites.firstElement();
				f.incidentEdge = boundingBox.Q1.incidentEdge;
				nD.addFace(f);
			}
			else {
				while(!nQ.isEmpty()) {
					NewEvent e = nQ.poll();
					if(e.isSiteEvent())
						try {
							handleNewSiteEvent((SiteEvent) e);
						}
						catch (SiteEventException see) {
							throw new VoronoiConstructionException(see);
						}
					else {
						CircleEvent ce = (CircleEvent) e;
						try {
							if(ce.isValid())
								handleNewCircleEvent(ce);
							else
								throw new CircleEventException("Invalidated circle events still exist in queue");
						}
						catch (CircleEventException cee) {
							throw new VoronoiConstructionException(cee);
						}
					}
				}
				
				//Create a bounding box, using the remaining contents of the tree.
				//The remaining contents of the tree hold the half-infinite edges that
				//will be bound to the box.
				BoundingBox bb = newCreateOuterEnvelope(nT.newEmpty(-999999999999.0d),LONGITUDE,LATITUDE);
				nD.newAttachBoundingBox(bb);
				//There is now a complete list of all vertices that lie along the bounding box.
				//Find the vertex with the minimum y-value, and sort the vertices by polar angle w.r.t.
				//this vertex. If two vertices have the same polar angle, the tie is broken by taking the
				//maximum y-value. If the two vertices have the same y-value, the tie is broken further
				//by taking the minimum x-value.
				//This should create a sorted list of vertices that traverse the bounding box in a counter-clockwise
				//fashion.
				//What's left is to pop the top of the list, and connect the half-edges of the popped element to the
				//next element that resides on the stack
				nD.newCreateCellRecords();
			}
	}
	
	public DCEL newConstruct(double LONGITUDE, double LATITUDE) throws VoronoiConstructionException {
		if(nQ.isEmpty())
			throw new VoronoiConstructionException("No events were supplied to the event queue");
		else 
			if(nQ.size() == 1) {
				BoundingBox boundingBox = createOuterEnvelope(new Vector<Vertex>(),LONGITUDE,LATITUDE);
				nD.addEdge(boundingBox.Q1.incidentEdge);
				nD.addEdge(boundingBox.Q2.incidentEdge);
				nD.addEdge(boundingBox.Q3.incidentEdge);
				nD.addEdge(boundingBox.Q4.incidentEdge);
				Face f = new Face();
				f.containedSite = eventSites.firstElement();
				f.incidentEdge = boundingBox.Q1.incidentEdge;
				nD.addFace(f);
				return nD;
			}
			else {
				while(!nQ.isEmpty()) {
					NewEvent e = nQ.poll();
					if(e.isSiteEvent())
						try {
							handleNewSiteEvent((SiteEvent) e);
						}
						catch (SiteEventException see) {
							throw new VoronoiConstructionException(see);
						}
					else {
						CircleEvent ce = (CircleEvent) e;
						try {
							if(ce.isValid())
								handleNewCircleEvent(ce);
							else
								throw new CircleEventException("Invalidated circle events still exist in queue");
						}
						catch (CircleEventException cee) {
							throw new VoronoiConstructionException(cee);
						}
					}
				}
				
				//Create a bounding box, using the remaining contents of the tree.
				//The remaining contents of the tree hold the half-infinite edges that
				//will be bound to the box.
				BoundingBox bb = newCreateOuterEnvelope(nT.newEmpty(-9999.0d),LONGITUDE,LATITUDE);
				nD.newAttachBoundingBox(bb);
				//There is now a complete list of all vertices that lie along the bounding box.
				//Find the vertex with the minimum y-value, and sort the vertices by polar angle w.r.t.
				//this vertex. If two vertices have the same polar angle, the tie is broken by taking the
				//maximum y-value. If the two vertices have the same y-value, the tie is broken further
				//by taking the minimum x-value.
				//This should create a sorted list of vertices that traverse the bounding box in a counter-clockwise
				//fashion.
				//What's left is to pop the top of the list, and connect the half-edges of the popped element to the
				//next element that resides on the stack
				//nD.newCreateCellRecords();
			}
		return nD;
	}

	private void handleNewSiteEvent(SiteEvent e) throws SiteEventException {
		Site eventSite = e.getSite();
		if(nT.isEmpty())
			nT.newInsert(eventSite);
		else {
			Node intersectingArc = nT.newSearch(eventSite);

			if(intersectingArc.newHasCircleEvent())
				nQ.remove(intersectingArc.getCircleEvent());
			
			Node newArc = nT.newReplace(intersectingArc, eventSite);
			
			CircleEvent e1 = nT.newCheckBreakPointConvergence2(newArc.getPredecessor());
			CircleEvent e2 = nT.newCheckBreakPointConvergence2(newArc.getSuccessor());

			if(e1 != null)
				nQ.add(e1);
			if(e2 != null)
				nQ.add(e2);
		}
	}
	
	private void handleNewCircleEvent(CircleEvent ce) throws CircleEventException {
		
		Node arc = ce.getEventNode();
		Vertex v = nT.newRemove(arc);
		
		if(arc.getPredecessor().newHasCircleEvent()) {
			nQ.remove(arc.getPredecessor().getCircleEvent());
		}
		if(arc.getSuccessor().newHasCircleEvent()) {
			nQ.remove(arc.getSuccessor().getCircleEvent());
		}

		nD.addVertex(v);

		if(v.getIncidentEdge().getOrigin() != null && v.getIncidentEdge().getTwin().getOrigin() != null)
			nD.addEdge(v.getIncidentEdge());
		if(v.getIncidentEdge().getPrev().getOrigin() != null && v.getIncidentEdge().getPrev().getTwin().getOrigin() != null)
			nD.addEdge(v.getIncidentEdge().getPrev().getTwin());

		CircleEvent e1 = nT.newCheckBreakPointConvergence2(arc.getPredecessor());
		CircleEvent e2 = nT.newCheckBreakPointConvergence2(arc.getSuccessor());
		
		if(e1 != null)
			nQ.add(e1);
		if(e2 != null)
			nQ.add(e2);
	}

	/**
	 * Creates an outer envelope, containing all vertices and sites in this com.godfrey.geometry.voronoi diagram.
	 * 
	 * @param longitude A minimum/maximum longitudinal value for the envelope
	 * @param latitude A minimum/maximum latitudinal value for the envelope
	 * @return Returns a new rectangular {@link BoundingBox} either bounded by the input parameters
	 * 		   or, 
	 * @throws VoronoiConstructionException 
	 * 		   
	 */
	public BoundingBox createOuterEnvelope(Vector<Vertex> halfInfiniteVertices, double longitude, double latitude) throws VoronoiConstructionException {
		
		//Find the minimum and maximum y and x values from the set
		//of vertices and point sites.
		double minX = Double.MAX_VALUE;
		double maxX = Double.MIN_VALUE;
		double minY = Double.MAX_VALUE;
		double maxY = Double.MIN_VALUE;
		
		for(Vertex v : nD.getVertices()) {
			double x = v.getCoords().getX();
			double y = v.getCoords().getY();

			if(x < minX)
				minX = x;
			if(x > maxX)
				maxX = x;
			if(y < minY)
				minY = y;
			if(y > maxY)
				maxY = y;
		}

		for(Site s : eventSites) {
			double x = s.getLocation().getX();
			double y = s.getLocation().getY();

			if(x < minX)
				minX = x;
			if(x > maxX)
				maxX = x;
			if(y < minY)
				minY = y;
			if(y > maxY)
				maxY = y;
		}
		
		//Create four new vertices from these values.
		//Extend the maximum and minimums so that no point site
		//nor non-half-infinite edge will reside on the boundary
		if(maxX < longitude)
			maxX = longitude;
		if(maxY < latitude)
			maxY = latitude;
		if(minY > -latitude)
			minY = -latitude;
		if(minX > -longitude)
			minX = -longitude;
		
//		Vertex Q1 = new Vertex(new Coordinate(maxX+50,maxY+50));
//		Vertex Q2 = new Vertex(new Coordinate(maxX+50,minY-50));
//		Vertex Q3 = new Vertex(new Coordinate(minX-50,minY-50));
//		Vertex Q4 = new Vertex(new Coordinate(minX-50,maxY+50));
		
		Vertex Q1 = new Vertex(new Coordinate(2050,2050));
		Vertex Q2 = new Vertex(new Coordinate(2050,-2050));
		Vertex Q3 = new Vertex(new Coordinate(-2050,-2050));
		Vertex Q4 = new Vertex(new Coordinate(-2050,2050));
//		System.out.println("minX: " + minX);
//		System.out.println("maxX: " + maxX);
//		System.out.println("minY: " + minY);
//		System.out.println("maxY: " + maxY);
		
		//Initialize a bounding box with these vertices
		BoundingBox boundingBox = new BoundingBox(Q1,Q2,Q3,Q4);
		
		//Create lines segments for each of the four sides of the box
		DCELLineSegment left = new DCELLineSegment(Q1,Q2);
		DCELLineSegment bottom = new DCELLineSegment(Q2,Q3);
		DCELLineSegment right = new DCELLineSegment(Q3,Q4);
		DCELLineSegment top = new DCELLineSegment(Q4,Q1);
//		System.out.println("BoundingBox.computeBoundingBox : " + halfInfiniteVertices.size());
		//for each of the half-infinite edges left in the tree
		//we test for intersection with one of the four sides of the box

		for(Vertex v : halfInfiniteVertices) {
			//Create a line segment from one of the half infinite edge
//			System.out.println("BoundingBox.computeBoundingBox : " + v.getIncidentEdge().getTwin().getOrigin().getCoords().getX() + "," + v.getIncidentEdge().getTwin().getOrigin().getCoords().getY());
//			System.out.println("BoundingBox.computeBoundingBox : " + v.getIncidentEdge().getOrigin().getCoords().getX() + "," + v.getIncidentEdge().getOrigin().getCoords().getY());

			DCELLineSegment l = new DCELLineSegment(v.getIncidentEdge().getTwin().getOrigin(),v.getIncidentEdge().getOrigin());
//			System.out.println("v<"+v.getCoords().getX()+","+v.getCoords().getY()+">");

			//Test this segment for intersection with all four sides of the bounding box.
			Vertex leftIntersect = left.getIntersection(l);
			Vertex bottomIntersect = bottom.getIntersection(l);
			Vertex rightIntersect = right.getIntersection(l);
			Vertex topIntersect = top.getIntersection(l);

			//An intersection will yield a non-null value,
			//and will be unique to exactly one side of the bounding box, with the exception of a single half infinite edge.
			//A single half infinite edge ( i.e. a com.godfrey.geometry.voronoi built from a point set of size two, or n co-linear sites ), will bisect the bounding box.
			//
			//The vertex's coordinates must be updated to reflect the intersection point. A degeneracy exists when a line
			//segment ending at v intersects a vertex q_i of the bounding box. This degeneracy is not currently handled.
			if(leftIntersect!=null) {
				v.setCoords(leftIntersect.getCoords());
//				System.out.println("Left intersect");
				if(leftIntersect.getCoords().equals(Q3.getCoords()) || leftIntersect.getCoords().equals(Q4.getCoords()))
					System.out.println("Bounding box vertex intersect left");
			}
			else
				if(bottomIntersect!=null) {
					v.setCoords(bottomIntersect.getCoords());
//					System.out.println("Bottom intersect");
					if(bottomIntersect.getCoords().equals(Q3.getCoords()) || bottomIntersect.getCoords().equals(Q2.getCoords()))
						System.out.println("Bounding box vertex intersect Bottom");
				}
				else
					if(rightIntersect!=null) {
						v.setCoords(rightIntersect.getCoords());
//						System.out.println("Right intersect");
						if(rightIntersect.getCoords().equals(Q1.getCoords()) || rightIntersect.getCoords().equals(Q2.getCoords()))
							System.out.println("Bounding box vertex intersect right");
					}
					else
						if(topIntersect!=null) {
							v.setCoords(topIntersect.getCoords());
//							System.out.println("Top intersect");
							if(topIntersect.getCoords().equals(Q1.getCoords()) || topIntersect.getCoords().equals(Q4.getCoords()))
								System.out.println("Bounding box vertex intersect top");
						}
						else
							//The intersection happens at the corner of the bounding box
							throw new VoronoiConstructionException("Line segment intersection degeneracy");

//			System.out.println("v<"+v.getCoords().getX()+","+v.getCoords().getY()+">");
			//Add the half-infinite edge, and the vertex to the DCEL
			nD.addEdge(v.getIncidentEdge());
//			nD.addVertex(v);
			//Also add the vertex to the bounding box
			boundingBox.addVertex(v);
		}
		return boundingBox;
	}

	/**
	 * Creates an outer envelope, containing all vertices and sites in this com.godfrey.geometry.voronoi diagram.
	 * 
	 * @param longitude A minimum/maximum longitudinal value for the envelope
	 * @param latitude A minimum/maximum latitudinal value for the envelope
	 * @return Returns a new rectangular {@link BoundingBox} either bounded by the input parameters
	 * 		   or, 
	 * @throws VoronoiConstructionException 
	 * 		   
	 */
	public BoundingBox newCreateOuterEnvelope(Vector<BreakPoint> halfInfiniteVertices, double longitude, double latitude) throws VoronoiConstructionException {
		
		//Find the minimum and maximum y and x values from the set
		//of vertices and point sites.
		double minX = Double.MAX_VALUE;
		double maxX = -Double.MAX_VALUE;
		double minY = Double.MAX_VALUE;
		double maxY = -Double.MAX_VALUE;
		
		for(Vertex v : nD.getVertices()) {
			double x = v.getCoords().getX();
			double y = v.getCoords().getY();

			if(x < minX)
				minX = x;
			if(x > maxX)
				maxX = x;
			if(y < minY)
				minY = y;
			if(y > maxY)
				maxY = y;
		}

		for(Site s : eventSites) {
			double x = s.getLocation().getX();
			double y = s.getLocation().getY();

			if(x < minX)
				minX = x;
			if(x > maxX)
				maxX = x;
			if(y < minY)
				minY = y;
			if(y > maxY)
				maxY = y;
		}
		
		//Create four new vertices from these values.
		//Extend the maximum and minimums so that no point site
		//nor non-half-infinite edge will reside on the boundary
		if(maxX < longitude)
			maxX = longitude;
		if(maxY < latitude)
			maxY = latitude;
		if(minY > -latitude)
			minY = -latitude;
		if(minX > -longitude)
			minX = -longitude;
		
		Vertex Q1 = new Vertex(new Coordinate(maxX+50,maxY+50));
		Vertex Q2 = new Vertex(new Coordinate(maxX+50,minY-50));
		Vertex Q3 = new Vertex(new Coordinate(minX-50,minY-50));
		Vertex Q4 = new Vertex(new Coordinate(minX-50,maxY+50));
		
//		Vertex Q1 = new Vertex(new Coordinate(2500,2500));
//		Vertex Q2 = new Vertex(new Coordinate(2500,-2500));
//		Vertex Q3 = new Vertex(new Coordinate(-2500,-2500));
//		Vertex Q4 = new Vertex(new Coordinate(-2500,2500));
		
//		System.out.println();
//		System.out.println("minX: " + minX);
//		System.out.println("maxX: " + maxX);
//		System.out.println("minY: " + minY);
//		System.out.println("maxY: " + maxY);
//		System.out.println();
		//Initialize a bounding box with these vertices
		BoundingBox boundingBox = new BoundingBox(Q1,Q2,Q3,Q4);
		boundingBox.buildBox();
		//Create lines segments for each of the four sides of the box
		DCELLineSegment left = new DCELLineSegment(Q1,Q2);
		DCELLineSegment bottom = new DCELLineSegment(Q2,Q3);
		DCELLineSegment right = new DCELLineSegment(Q3,Q4);
		DCELLineSegment top = new DCELLineSegment(Q4,Q1);
//		System.out.println("BoundingBox.computeBoundingBox : " + halfInfiniteVertices.size());
		//for each of the half-infinite edges left in the tree
		//we test for intersection with one of the four sides of the box
		setNumHalfInfiniteVertices(halfInfiniteVertices.size()); 
		for(BreakPoint b : halfInfiniteVertices) {
			//Create a line segment from one of the half infinite edge
//			System.out.println("BoundingBox.computeBoundingBox : " + v.getIncidentEdge().getTwin().getOrigin().getCoords().getX() + "," + v.getIncidentEdge().getTwin().getOrigin().getCoords().getY());
//			System.out.println("BoundingBox.computeBoundingBox : " + v.getIncidentEdge().getOrigin().getCoords().getX() + "," + v.getIncidentEdge().getOrigin().getCoords().getY());

//			System.out.println("v<"+v.getCoords().getX()+","+v.getCoords().getY()+">");
			Vertex v = new Vertex(StatusTree.calculateArcIntersection(b.newLeftSite.getLocation(),b.newRightSite.getLocation(),-99999.9d));
			b.edge.origin = v;
			v.incidentEdge = b.edge;
//			b.edge.newIncidentSite = b.newLeftSite;
//			b.edge.twin.newIncidentSite = b.newRightSite;
			DCELLineSegment l = new DCELLineSegment(v,v.incidentEdge.twin.origin);

			//Test this segment for intersection with all four sides of the bounding box.
			Vertex leftIntersect = left.getIntersection(l);
			Vertex bottomIntersect = bottom.getIntersection(l);
			Vertex rightIntersect = right.getIntersection(l);
			Vertex topIntersect = top.getIntersection(l);

			//An intersection will yield a non-null value,
			//and will be unique to exactly one side of the bounding box, with the exception of a single half infinite edge.
			//A single half infinite edge ( i.e. a com.godfrey.geometry.voronoi built from a point set of size two, or n co-linear sites ), will bisect the bounding box.
			//
			//The vertex's coordinates must be updated to reflect the intersection point. A degeneracy exists when a line
			//segment ending at v intersects a vertex q_i of the bounding box. This degeneracy is not currently handled.
			if(leftIntersect!=null) {
				v.setCoords(leftIntersect.getCoords());
//				System.out.println("Left intersect");
				if(leftIntersect.getCoords().equals(Q3.getCoords()) || leftIntersect.getCoords().equals(Q4.getCoords()))
					System.out.println("Bounding box vertex intersect left");
			}
			else
				if(bottomIntersect!=null) {
					v.setCoords(bottomIntersect.getCoords());
//					System.out.println("Bottom intersect");
					if(bottomIntersect.getCoords().equals(Q3.getCoords()) || bottomIntersect.getCoords().equals(Q2.getCoords()))
						System.out.println("Bounding box vertex intersect Bottom");
				}
				else
					if(rightIntersect!=null) {
						v.setCoords(rightIntersect.getCoords());
//						System.out.println("Right intersect");
						if(rightIntersect.getCoords().equals(Q1.getCoords()) || rightIntersect.getCoords().equals(Q2.getCoords()))
							System.out.println("Bounding box vertex intersect right");
					}
					else
						if(topIntersect!=null) {
							v.setCoords(topIntersect.getCoords());
//							System.out.println("Top intersect");
							if(topIntersect.getCoords().equals(Q1.getCoords()) || topIntersect.getCoords().equals(Q4.getCoords()))
								System.out.println("Bounding box vertex intersect top");
						}
						else {;}
							//The intersection happens at the corner of the bounding box
							//throw new VoronoiConstructionException("Line segment intersection degeneracy");

//			System.out.println("v<"+v.getCoords().getX()+","+v.getCoords().getY()+">");
			//Add the half-infinite edge, and the vertex to the DCEL

			nD.addVertex(v);
			//Also add the vertex to the bounding box
			boundingBox.addVertex(v);
			
			nD.addEdge(v.getIncidentEdge());
			boundingBox.addIntersectingSegment(l);
		}
//		boundingBox.Q1 = new Vertex(new Coordinate(2050,2050));
//		boundingBox.Q2 = new Vertex(new Coordinate(2050,-2050));
//		boundingBox.Q3 = new Vertex(new Coordinate(-2050,-2050));
//		boundingBox.Q4 = new Vertex(new Coordinate(-2050,2050));
//		boundingBox.buildBox();
		return boundingBox;
	}

	/**
	 * @return the numHalfInfiniteVertices
	 */
	public int getNumHalfInfiniteVertices() {
		return numHalfInfiniteVertices;
	}

	/**
	 * @param numHalfInfiniteVertices the numHalfInfiniteVertices to set
	 */
	public void setNumHalfInfiniteVertices(int numHalfInfiniteVertices) {
		this.numHalfInfiniteVertices = numHalfInfiniteVertices;
	}

	
}
