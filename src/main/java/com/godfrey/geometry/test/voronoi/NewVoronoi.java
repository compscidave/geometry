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

public class NewVoronoi {

	private PriorityQueue<NewEvent> nQ;
	private StatusTree nT;
	private DCEL nD;
	private Vector<Site> eventSites;
	
	public NewVoronoi() {
		Comparator<NewEvent> ec = new NewEventComparator();
		nQ = new PriorityQueue<NewEvent>(10,ec);
		nT = new StatusTree();
		nD = new DCEL();
		eventSites = new Vector<Site>();
	}
		
	public NewVoronoi(List<Site> sites) {
		Comparator<NewEvent> ec = new NewEventComparator();
		eventSites = new Vector<Site>();
		nQ = new PriorityQueue<NewEvent>(10, ec);
		double maxY = Double.MIN_VALUE;
		boolean degeneracy = false;
		for(Site site : sites) {
			nQ.add(new SiteEvent(site));
			if(site.getLocation().getY() > maxY) {
				maxY = site.getLocation().getX();
				degeneracy = false;
			}
			else
				if(site.getLocation().getY() == maxY)
					degeneracy = true;
			eventSites.add(site);
		}
		//This is a janky way of curbing the "two sites at the start of the sweep
		//share the same y-value" degeneracy. In reality, the slope of the sweep
		//line needs to be adjusted so that it's not horizontal.
		if(degeneracy) {
			nQ.peek().getEventSite().setY(maxY+0.0005);
		}
		nT = new StatusTree();
		nD = new DCEL();
	}

	public void newCompute() throws VoronoiConstructionException {
		if(nQ.isEmpty())
			throw new VoronoiConstructionException("No events were supplied to the event queue");
		else
/*			if(nQ.size() == 1) {
				NewEvent e = nQ.poll();
				Coordinate c = e.eventSite;
				NewBoundingBox boundingBox = new NewBoundingBox(new Vertex(new Coordinate(50,50)),
																new Vertex(new Coordinate(50,-50)),
																new Vertex(new Coordinate(-50,50)),
																new Vertex(new Coordinate(-50,-50)));
				
				nD.addEdge(boundingBox.Q1.incidentEdge);
				nD.addEdge(boundingBox.Q2.incidentEdge);
				nD.addEdge(boundingBox.Q3.incidentEdge);
				nD.addEdge(boundingBox.Q4.incidentEdge);
				
				Face f = new Face();
				f.incidentSite = eventSites.firstElement();
				f.incidentEdge = boundingBox.Q1.incidentEdge;
				
				nD.addFace(f);
			}
			else {*/
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
//			for(Vertex v : nT.newEmpty()) {
//				nD.addVertex(v);
//				nD.addEdge(v.incidentEdge);
//			}
			//Create a bounding box, using the remaining contents of the tree.
			//The remaining contents of the tree hold the half-infinite edges that
			//will be bound to the box.
//			nD.newAttachBoundingBox(newCalculateBoundingVertices(nT.newEmpty(),LONGITUDE,LATITUDE));
			//There is now a complete list of all vertices that lie along the bounding box.
			//Find the vertex with the minimum y-value, and sort the vertices by polar angle w.r.t.
			//this vertex. If two vertices have the same polar angle, the tie is broken by taking the
			//maximum y-value. If the two vertices have the same y-value, the tie is broken further
			//by taking the minimum x-value.
			//This should create a sorted list of vertices that traverse the bounding box in a counter-clockwise
			//fashion.
			//What's left is to pop the top of the list, and connect the half-edges of the popped element to the
			//next element that resides on the stack
//			nD.newCreateCellRecords();
	}
/*	
	public DCEL newConstruct(double LONGITUDE, double LATITUDE) throws VoronoiConstructionException {
		if(nQ.isEmpty())
			throw new VoronoiConstructionException("No events were supplied to the event queue");
		else
			if(nQ.size() == 1) {
				BoundingBox boundingBox = newCalculateBoundingVertices(null,LONGITUDE,LATITUDE);
				nD.addEdge(boundingBox.Q1.incidentEdge);
				nD.addEdge(boundingBox.Q2.incidentEdge);
				nD.addEdge(boundingBox.Q3.incidentEdge);
				nD.addEdge(boundingBox.Q4.incidentEdge);
				Face f = new Face();
				f.incidentSite = eventSites.firstElement();
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
				nD.newAttachBoundingBox(newCalculateBoundingVertices(nT.newEmpty(),LONGITUDE,LATITUDE));
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
		return nD;
	}
*/
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
	
	public DCEL getnD() {
		return nD;
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

	public NewBoundingBox createBoundingBox(double longitude, double latitude) throws VoronoiConstructionException {
		
		//Find the minimum and maximum y and x values from the set
		//of vertices and point sites.
		double minX = 999999999999999.0;
		double maxX = -9999999999999999.0;
		double minY = 999999999999999.0;
		double maxY = -9999999999999999.0;
		
		for(Vertex v : nD.getVertices()) {
			double x = v.getCoords().getLongitude();
			double y = v.getCoords().getLatitude();

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
		
		//Add a buffer
		Vertex Q1 = new Vertex(new Coordinate(maxX+50,maxY+50));
		Vertex Q2 = new Vertex(new Coordinate(maxX+50,minY-50));
		Vertex Q3 = new Vertex(new Coordinate(minX-50,minY-50));
		Vertex Q4 = new Vertex(new Coordinate(minX-50,maxY+50));
		
//		System.out.println("minX: " + minX);
//		System.out.println("maxX: " + maxX);
//		System.out.println("minY: " + minY);
//		System.out.println("maxY: " + maxY);
		
		//Return a new bounding box with these vertices
		return new NewBoundingBox(Q1,Q2,Q3,Q4);
	}

}
