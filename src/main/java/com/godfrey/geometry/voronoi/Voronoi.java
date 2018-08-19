/*
 * Copyright 2011-2014 David Godfrey. All rights reserved.
 */
package com.godfrey.geometry.voronoi;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Vector;

import com.godfrey.geometry.dcel.DCEL;
import com.godfrey.geometry.dcel.boundingbox.BoundingBox;
import com.godfrey.geometry.dcel.face.Face;
import com.godfrey.geometry.dcel.vertex.Vertex;
import com.godfrey.geometry.linesegment.LineSegment;
import com.godfrey.geometry.point.Point;
import com.godfrey.geometry.site.Site;
import com.godfrey.geometry.statustree.StatusTree;
import com.godfrey.geometry.statustree.node.StatusTreeNode;
import com.godfrey.geometry.test.voronoi.Coordinate;
import com.godfrey.geometry.voronoi.event.Event;
import com.godfrey.geometry.voronoi.event.EventComparator;
import com.godfrey.geometry.voronoi.exception.CircleEventException;
import com.godfrey.geometry.voronoi.exception.SiteEventException;
import com.godfrey.geometry.voronoi.exception.VoronoiConstructionException;

public class Voronoi<T extends Point> extends DCEL {

	private PriorityQueue<Event<T>> Q;
	private StatusTree T;
	private Vector<T> sites;
	private BoundingBox boundingBox;
	private double maxY = Double.MIN_VALUE;
	private boolean degeneracy = false;
	
	public Voronoi() {
		super();
		Comparator<Event<T>> ec = new EventComparator();
		Q = new PriorityQueue<Event<T>>(10,ec);
		setT(new StatusTree());
		sites = new Vector<T>();
	}
		
	public Voronoi(Vector<T> points) {
		super();
		Comparator<Event<T>> ec = new EventComparator();
		Q = new PriorityQueue<Event<T>>(10, ec);
		sites = points;

		for(T p : points) {
			Q.add(new Event<T>(p));
			if(p.getY() > maxY) {
				maxY = p.getY();
				degeneracy = false;
			}
			else
				if(p.getY() == maxY)
					degeneracy = true;
		}
		if(degeneracy) {
			Q.peek().getEventSite().setY(maxY+0.0005);
			degeneracy = false;
		}
		T = new StatusTree();
	}
	
	public void addSite(T site) {
		if(site.getY() > maxY) {
			maxY = site.getY();
			degeneracy = false;
		}
		else
			if(site.getY() == maxY)
				degeneracy = true;
		Q.add(new Event<T>(site));
		if(degeneracy) {
			Q.peek().getEventSite().setY(maxY+0.0005);
			degeneracy = false;
		}
		sites.add(site);
	}

	protected void compute() throws VoronoiConstructionException {
		if(Q.isEmpty()) {
			//the com.godfrey.geometry.voronoi diagram is the bounding box
			//with no site in its interior
			boundingBox = computeBoundingBox(new Vector<Vertex>());
		}
		else {
			if(Q.size() == 1) {
				//the diagram is the bounding box with one site in its interior
				boundingBox = computeBoundingBox(new Vector<Vertex>());
				addEdge(boundingBox.getQ1().getIncidentEdge());
				addEdge(boundingBox.getQ2().getIncidentEdge());
				addEdge(boundingBox.getQ3().getIncidentEdge());
				addEdge(boundingBox.getQ4().getIncidentEdge());
				Face f = new Face();
				f.setContainedSite(sites.firstElement());
				f.setIncidentEdge(boundingBox.getQ1().getIncidentEdge());
				addFace(f);
			}
			else {
				while(!Q.isEmpty()) {
					Event<Point> e = (Event<Point>) Q.poll();
					if(e.isSiteEvent())
						try {
							handleSiteEvent(e);
						}
						catch (SiteEventException see) {
							throw new VoronoiConstructionException(see);
						}
					else {
						try {
							if(e.isValid())
								handleCircleEvent(e);
							else
								throw new CircleEventException("Invalidated circle events still exist in queue");
						}
						catch (CircleEventException cee) {
							throw new VoronoiConstructionException(cee);
						}
					}
				}
				boundingBox = computeBoundingBox(T.empty());
			}
		}
		//Create a bounding box, using the remaining contents of the tree.
		//The remaining contents of the tree hold the half-infinite edges that
		//will be bound to the box.
		attachBoundingBox(boundingBox);
		//There is now a complete list of all vertices that lie along the bounding box.
		//Find the vertex with the minimum y-value, and sort the vertices by polar angle w.r.t.
		//this vertex. If two vertices have the same polar angle, the tie is broken by taking the
		//maximum y-value. If the two vertices have the same y-value, the tie is broken further
		//by taking the minimum x-value.
		//This should create a sorted list of vertices that traverse the bounding box in a counter-clockwise
		//fashion.
		//What's left is to pop the top of the list, and connect the half-edges of the popped element to the
		//next element that resides on the stack
		createCellRecords();
	}
	
	private void handleSiteEvent(Event<Point> e) throws SiteEventException {
		Point eventSite = (Point) e.getEventSite();
		if(T.isEmpty())
			T.insert(eventSite);
		else {
			StatusTreeNode intersectingArc = T.search(eventSite);

			if(intersectingArc.hasCircleEvent())
				Q.remove(intersectingArc.getEvent());
			
			StatusTreeNode newArc = T.replace(intersectingArc, eventSite);
			
			Event e1 = T.checkBreakPointConvergence2(newArc.getPredecessor());
			Event e2 = T.checkBreakPointConvergence2(newArc.getSuccessor());

			if(e1 != null)
				Q.add(e1);
			if(e2 != null)
				Q.add(e2);
		}
	}
	
	// This version of the method models HalfEdge manipulation after C++ version;
	private void handleCircleEvent(Event e) throws CircleEventException {
		
		StatusTreeNode arc = e.getEventNode();
		Vertex v = T.remove(arc);
		
		if(arc.getPredecessor().hasCircleEvent()) {
			Q.remove(arc.getPredecessor().getCircleEvent());
		}
		if(arc.getSuccessor().hasCircleEvent()) {
			Q.remove(arc.getSuccessor().getCircleEvent());
		}

		addVertex(v);

		if(v.getIncidentEdge().getOrigin() != null && v.getIncidentEdge().getTwin().getOrigin() != null)
			addEdge(v.getIncidentEdge());
		if(v.getIncidentEdge().getPrev().getOrigin() != null && v.getIncidentEdge().getPrev().getTwin().getOrigin() != null)
			addEdge(v.getIncidentEdge().getPrev().getTwin());

		Event e1 = T.checkBreakPointConvergence2(arc.getPredecessor());
		Event e2 = T.checkBreakPointConvergence2(arc.getSuccessor());
		
		if(e1 != null)
			Q.add(e1);
		if(e2 != null)
			Q.add(e2);
	}
/*	
	private void createCellRecords() {
		Vertex v = D.getBoundingBox().getQ3();
		Set<Point> rim = new HashSet<Point>();
		//Walk along the outer boundary clockwise
		HalfEdge e = v.getIncidentEdge().getTwin();
		Vector<HalfEdge> outerFaceIncidentEdges = new Vector<HalfEdge>();
		rim.add(e.getOrigin().getCoords());
		while(!rim.contains(v.getCoords())) {
			System.out.println("e->origin<"+e.getOrigin().getCoords().getX()+","+e.getOrigin().getCoords().getY()+">");
			rim.add(e.getOrigin().getCoords());
			outerFaceIncidentEdges.add(e);
			e = e.getPrev();
		}
		
		for(Point p : rim) {
			System.out.println("<"+p.getX()+","+p.getY()+">");
		}
		
		//Walk along the outer boundary counter-clockwise
		rim = new HashSet<Point>();
		e = v.getIncidentEdge().getTwin().getNext().getNext();
		rim.add(e.getOrigin().getCoords());
		while(!rim.contains(v.getCoords())) {
			System.out.println("e->origin<"+e.getOrigin().getCoords().getX()+","+e.getOrigin().getCoords().getY()+">");
			rim.add(e.getOrigin().getCoords());
			e = e.getNext();
		}
		for(Point p : rim) {
			System.out.println("<"+p.getX()+","+p.getY()+">");
		}

		Set<Vertex> visitedVertices = new HashSet<Vertex>();
		
		//Walk along all the outer faces...
		Vector<Vertex> boundingVertices = D.getBoundingBox().getBoundingBox();
		System.out.println("Bounding box size" + boundingVertices.size());
		for(Vertex u : boundingVertices) {
			HalfEdge uIncident = u.getIncidentEdge().getNext();
			System.out.println("Walking from u<"+u.getCoords().getX()+","+u.getCoords().getY()+">");
			if(uIncident.getIncidentFace()==null) {
				Face f = new Face();
				while(!(uIncident.getOrigin().getCoords().equals(u.getCoords()))) {
					System.out.println("\t uIncident<"+uIncident.getOrigin().getCoords().getX()+","+uIncident.getOrigin().getCoords().getY()+">");
					uIncident.setIncidentFace(f);
					uIncident = uIncident.getNext();
				}
				uIncident.setIncidentFace(f);
				System.out.println("\t uIncident<"+uIncident.getOrigin().getCoords().getX()+","+uIncident.getOrigin().getCoords().getY()+">");
				f.setIncidentEdge(uIncident);
				D.addFace(f);
			}
			else
				System.out.println("This face already exists in the DCEL");
			visitedVertices.add(u);
		}
		System.out.println("Finished walking the outer faces");
		
		//Walk along all other cycles in the graph ( Inner vertices )
		for(Vertex u : D.getVertices()) {
			//We've already visited all vertices along the bounding box...
			if(!visitedVertices.contains(u)) {
				Set<HalfEdge> visitedHalfEdges = new HashSet<HalfEdge>();
				//Traverse the edges connected to this vertex
				System.out.println("Walking from u<"+u.getCoords().getX()+","+u.getCoords().getY()+">");
				HalfEdge g = u.getIncidentEdge();
				while(!visitedHalfEdges.contains(g)) {
					HalfEdge uIncident = g.getNext();
					if(uIncident.getIncidentFace()==null) {
						Face f = new Face();
						while(!(uIncident.getOrigin().getCoords().equals(u.getCoords()))) {
							System.out.println("\t uIncident<"+uIncident.getOrigin().getCoords().getX()+","+uIncident.getOrigin().getCoords().getY()+">");
							uIncident.setIncidentFace(f);
							uIncident = uIncident.getNext();
						}
						uIncident.setIncidentFace(f);
						System.out.println("\t uIncident<"+uIncident.getOrigin().getCoords().getX()+","+uIncident.getOrigin().getCoords().getY()+">");
						f.setIncidentEdge(uIncident);
						D.addFace(f);
					}
					else
						System.out.println("This face already exists in the DCEL");
					visitedVertices.add(u);
					visitedHalfEdges.add(g);
					g = u.getIncidentEdge().getTwin().getNext();
				}
			}
			else
				System.out.println("This vertex has already been visited");
			visitedVertices.add(u);
		}
		System.out.println("There are : " + D.getFaces().size());
	}
	*/

	private BoundingBox computeBoundingBox(Vector<Vertex> halfInfiniteEdges) throws VoronoiConstructionException {
		
		//Find the minimum and maximum y and x values from the set
		//of vertices and point sites.
		double minX = 999999999999999.0;
		double maxX = -9999999999999999.0;
		double minY = 999999999999999.0;
		double maxY = -9999999999999999.0;
		
		for(Vertex v : getVertices()) {
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

		for(Point p : sites) {
			double x = p.getX();
			double y = p.getY();

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
		Vertex Q1 = new Vertex(new Point(maxX+50,maxY+50));
		Vertex Q2 = new Vertex(new Point(maxX+50,minY-50));
		Vertex Q3 = new Vertex(new Point(minX-50,minY-50));
		Vertex Q4 = new Vertex(new Point(minX-50,maxY+50));
		
		//System.out.println("minX: " + minX);
		//System.out.println("maxX: " + maxX);
		//System.out.println("minY: " + minY);
		//System.out.println("maxY: " + maxY);
		
		//Initialize a bounding box with these vertices
		BoundingBox boundingBox;
		boundingBox = new BoundingBox(Q1,Q2,Q3,Q4);

		//Create lines segments for each of the four sides of the box
		LineSegment left = new LineSegment(Q1,Q2);
		LineSegment bottom = new LineSegment(Q2,Q3);
		LineSegment right = new LineSegment(Q3,Q4);
		LineSegment top = new LineSegment(Q4,Q1);
		
		//for each of the half-infinite edges left in the tree
		//we test for intersection with one of the four sides of the box
		for(Vertex v : halfInfiniteEdges) {
			
			//Create a line segment from one of the half infinite edge
			LineSegment l = new LineSegment(v.getIncidentEdge().getTwin().getOrigin(),v.getIncidentEdge().getOrigin());
			//System.out.println("v<"+v.getCoords().getX()+","+v.getCoords().getY()+">");
			//Test this segment for intersection with all four sides of the bounding box.
			Vertex leftIntersect = left.getIntersection(l);
			Vertex bottomIntersect = bottom.getIntersection(l);
			Vertex rightIntersect = right.getIntersection(l);
			Vertex topIntersect = top.getIntersection(l);
			
			//An intersection will yield a non-null value,
			//and will be unique to exactly one side of the bounding box.
			//The vertex's coordinates must be updated to reflect the intersection point.
			if(leftIntersect!=null) {
				v.setCoords(leftIntersect.getCoords());
				if(leftIntersect.getCoords().equals(Q3.getCoords()) || leftIntersect.getCoords().equals(Q4.getCoords()))
					System.out.println("Bounding box vertex intersect left");
			}
			else
				if(bottomIntersect!=null) {
					v.setCoords(bottomIntersect.getCoords());
					if(bottomIntersect.getCoords().equals(Q3.getCoords()) || bottomIntersect.getCoords().equals(Q2.getCoords()))
						System.out.println("Bounding box vertex intersect Bottom");
				}
				else
					if(rightIntersect!=null) {
						v.setCoords(rightIntersect.getCoords());
						if(rightIntersect.getCoords().equals(Q1.getCoords()) || rightIntersect.getCoords().equals(Q2.getCoords()))
							System.out.println("Bounding box vertex intersect right");
					}
					else
						if(topIntersect!=null) {
							v.setCoords(topIntersect.getCoords());
							if(topIntersect.getCoords().equals(Q1.getCoords()) || topIntersect.getCoords().equals(Q4.getCoords()))
								System.out.println("Bounding box vertex intersect top");
						}
						else {
							//A degeneracy can occur if the intersection occurs precisely on a vertex of the bounding box.
							//This VoronoiConstructionException doesn't necessarily infer that the degeneracy was encountered,
							//but must be thrown to protect the integrity of the data structure.
							//consumers should catch and handle the exception.
							System.out.println(v.getCoords().getX() + " " + v.getCoords().getY());
							throw new VoronoiConstructionException("Line segment intersection degeneracy");
						}

//			System.out.println("v<"+v.getCoords().getX()+","+v.getCoords().getY()+">");
			//Add the half-infinite edge, and the vertex to the DCEL
			addEdge(v.getIncidentEdge());
			//Also add the vertex to the bounding box
			boundingBox.addVertex(v);
		}
		return boundingBox;
	}

/*	private void checkBreakPointConvergence2(StatusTreeNode node) {
		if(node.getPredecessor() == null)
			return;
		else
			if(node.getSuccessor() == null)
				return;
			else {

				//check for break point convergence where t2 is the right node in the tuple.
				Point a = node.getPredecessor().getPointSite();
				Point b = node.getPointSite();
				Point c = node.getSuccessor().getPointSite();

				if ((b.getX()-a.getX())*(c.getY()-a.getY()) - (c.getX()-a.getX())*(b.getY()-a.getY()) > 0) {
					return;
				}
				else {
					if(a.equals(b)) {
						return;
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
							return;
						}
						else {
							Point origin = new Point((A*E+B*F+C*G)/D,
													 (A*(c.getX()-b.getX())+B*(a.getX()-c.getX())+C*(b.getX()-a.getX()))/D);
							Point diff = new Point(origin.getX()-a.getX(),origin.getY()-a.getY());
							Point newEventSite = new Point(origin.getX(),
														   origin.getY() - Math.sqrt(diff.getX()*diff.getX()+diff.getY()*diff.getY()));
							Event circleEvent = new Event(newEventSite,origin,node);
							circleEvent.setValid(true);
							//node.circleEvent = circleEvent;
							Q.add(circleEvent);
						}
					}
				}
			}

	}
	
	
	private void checkBreakPointConvergence(StatusTreeNode t2) {
		if(t2.predecessor.predecessor != null ) {
			//check for break point convergence where t2 is the right node in the tuple.
			Point a = new Point(t2.predecessor.predecessor.pointSite.getX(),t2.predecessor.predecessor.pointSite.getY());
			Point b = new Point(t2.predecessor.pointSite.getX(),t2.predecessor.pointSite.getY());
			Point c = new Point(t2.getPointSite().getX(),t2.getPointSite().getY());
		
			if ((b.getX()-a.getX())*(c.getY()-a.getY()) - (c.getX()-a.getX())*(b.getY()-a.getY()) > 0) {
				t2.predecessor.circleEvent = null;
			}
			else {
				if(a.equals(b)) {
					t2.predecessor.circleEvent = null;
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
						t2.predecessor.circleEvent = null;
					}
					else {
						Point origin = new Point((A*E+B*F+C*G)/D,
									 			 (A*(c.getX()-b.getX())+B*(a.getX()-c.getX())+C*(b.getX()-a.getX()))/D);
						Point diff = new Point(origin.getX()-a.getX(),origin.getY()-a.getY());
						Point newEventSite = new Point(origin.getX(),
													   origin.getY() - Math.sqrt(diff.getX()*diff.getX()+diff.getY()*diff.getY()));
						Event circleEvent = new Event(newEventSite,origin,t2.predecessor);
						circleEvent.setValid(true);
						t2.predecessor.circleEvent = circleEvent;
						System.out.println("The new arc's predecessor has a cirlce event");
						Q.add(circleEvent);
					}
				}
			}
		}
		else
			t2.predecessor.circleEvent = null;
		if(t2.successor.successor != null) {
			//check for break point convergence where t2 is the left node in the tuple.
			Point a = new Point(t2.getPointSite().getX(),t2.getPointSite().getY());
			Point b = new Point(t2.successor.pointSite.getX(),t2.successor.pointSite.getY());
			Point c = new Point(t2.successor.successor.pointSite.getX(),t2.successor.successor.pointSite.getY());
		
			if ((b.getX()-a.getX())*(c.getY()-a.getY())- (c.getX()-a.getX())*(b.getY()-a.getY()) > 0) {
				t2.successor.circleEvent = null;
			}
			else {
				if(a.equals(b)) {
					t2.successor.circleEvent = null;
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
						t2.successor.circleEvent = null;
					}
					else {
						//Calculate the origin of the circle event
						Point origin = new Point((A*E+B*F+C*G)/D,
									 			 (A*(c.getX()-b.getX())+B*(a.getX()-c.getX())+C*(b.getX()-a.getX()))/D);
						// <origin-a>
						Point diff = new Point(origin.getX()-a.getX(),origin.getY()-a.getY());
						//The lowest y-value along the circumference of the circle is the event site
						//where the two breakpoints will converge, creating a vertex in the DCEL
						Point newEventSite = new Point(origin.getX(),
													origin.getY() - Math.sqrt(diff.getX()*diff.getX()+ diff.getY()*diff.getY()));
						//Create a new circle event, pass the StatusTreeNode
						Event circleEvent = new Event(newEventSite,origin,t2.successor);
						circleEvent.setValid(true);
						t2.successor.circleEvent = circleEvent;
						System.out.println("The new arcs successor has a cirlce Event");
						Q.add(circleEvent);
					}
				}
			}
		}
		else
			t2.successor.circleEvent = null;
	}*/

	/**
	 * @return the t
	 */
	protected StatusTree getT() {
		return T;
	}

	/**
	 * @param t the t to set
	 */
	protected void setT(StatusTree t) {
		T = t;
	}
	
	//future module interface
	public static void main(String [] args) throws VoronoiConstructionException{
		Vector<Point> points = new Vector<Point>();
		points.add(new Point(0.0,0.0));
		points.add(new Point(-4.3,5.2));
		points.add(new Point(10.63, -12.9));
//		points.add(new Point(8.234, -22.2983));
//		points.add(new Point(28.28, 71.9283));
//		points.add(new Point(-13.29, 29.24));
//		points.add(new Point(19.392, -87.3721));
//		points.add(new Point(78.234, -72.2983));
//		points.add(new Point(28.28, 51.9283));
//		points.add(new Point(-93.29, 23.24));
//		points.add(new Point(14.392, -47.331));
//		points.add(new Point(92.234, -74.23));
//		points.add(new Point(2.28, 57.9283));
		int degenerateCount = 0;
		for(int j = 0; j < 10000; j++) {
			points = new Vector<Point>();
			Random randomGenerator = new Random();
			for(int i = 0 ; i < 1000 ; i++ ) {
				if(i%4==0)
					points.add(new Point(5000*randomGenerator.nextFloat(),2813*randomGenerator.nextFloat()));
				else
					if(i%4==1)
						points.add(new Point(-5000*randomGenerator.nextFloat(),-2813*randomGenerator.nextFloat()));
					else
						if(i%4==2)
							points.add(new Point(-5000*randomGenerator.nextFloat(),2813*randomGenerator.nextFloat()));
						else
							points.add(new Point(5000*randomGenerator.nextFloat(),-2813*randomGenerator.nextFloat()));
			}
			Voronoi<Point> v = new Voronoi<Point>(points);
			try {
				v.compute();
			}
			catch (VoronoiConstructionException vce) {
				degenerateCount++;
				//vce.printStackTrace();
			}
		}
		System.out.println("Caught " + degenerateCount + " degeneracies");

	}

	public T findNearestSite(Coordinate beginCoords) {
		T returnSite = null;
		for(Face f : getFaces()) {
			if(f.contains(beginCoords)) {
				returnSite = (T) f.containedSite;
			}
		}
		return returnSite;
	}

	public Face findNearestEnclosingSite(Coordinate beginCoords) {
		Face returnSite = null;
		for(Face f : getFaces()) {
			if(f.contains(beginCoords)) {
				returnSite = f;
			}
		}
		return returnSite;
	}
	
	public List<T> findNearestSite(Coordinate beginCoords, int i) {
		//TODO: Delaunay traversal?
		ArrayList<T> retList = new ArrayList<T>();
		Face<T> containedFace = null;
		for(Face<T> f : getFaces()) {
			if(f.contains(beginCoords)) {
				containedFace = f;
			}
		}
		if(containedFace != null) {
			for(Face<T> f : containedFace.getNeighbors()) {
				retList.add(f.getContainedSite());
			}
		}
		
		return retList;
	}
	
	protected void clearSites() {
		sites.clear();
	}
	
	protected Iterator<T> getSitesIterator() {
		return sites.iterator();
	}
}
