/*
 * Copyright 2011-2014 David Godfrey. All rights reserved.
 */
package com.godfrey.geometry.test.voronoi;

import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Vector;

public class FindIntersections {

	private BinarySearchTree eventQueue;
	private LSIStatusTree statusTree;
	private Vector<LineSegmentIntersection> intersections;
	
	public FindIntersections() {
		eventQueue = BinarySearchTree.createTree();
		statusTree = new LSIStatusTree();
		intersections = new Vector<LineSegmentIntersection>();
	}
	
	public FindIntersections(Set<DCELLineSegment> dCELLineSegments) {
		eventQueue = BinarySearchTree.createTree();
		statusTree = new LSIStatusTree();
		for(DCELLineSegment ls : dCELLineSegments) {
			if(ls.getLength()==0)
				throw new IllegalArgumentException("Zero length line segment encountered");
			eventQueue.insert(new LineSegmentEvent(ls.getUpperEndpoint(),ls));
			eventQueue.insert(new LineSegmentEvent(ls.getLowerEndpoint(),null));
		}
		intersections = new Vector<LineSegmentIntersection>();
	}
	
	public void computeIntersections() {
//		System.out.println("computing intersections...");
		while(!eventQueue.isEmpty()) {
//			eventQueue.printInOrder();
			LineSegmentEvent lse = eventQueue.getNextEvent();
			LineSegmentIntersection lsi = null;
			lsi = HandleEventPoint(lse);
			if(lsi != null) {
				intersections.add(lsi);
			}
		}
//		System.out.println("Found : " + intersections.size() + " intersections");
	}
	
	private LineSegmentIntersection HandleEventPoint(LineSegmentEvent lse) {
/*		String eventType = "";
		if(lse.getSegments().isEmpty())
			eventType += "Lower Segment Endpoint Event";
		else
			eventType += "Upper Segment Endpoint Event";
		
		System.out.println("**************************************************************************************************");
		System.out.println("*           Handling " + eventType + ":" + lse.toString() + "*");
		System.out.println("**************************************************************************************************");
*/
		LineSegmentIntersection intersection = null;
		/*******************************************
		 * 				STEP ONE
		 ******************************************/
		Set<DCELLineSegment> upperSegments = lse.getSegments();
		
		/*******************************************
		 * 				STEP TWO
		 ******************************************/		
		Vector<DCELLineSegment> segments = statusTree.findSegmentsContainingPoint(lse.eventSite);
		Set<DCELLineSegment> lowerSegments = new HashSet<DCELLineSegment>();
		Set<DCELLineSegment> containingSegments = new HashSet<DCELLineSegment>();
//		System.out.print("Segments containing " + lse.eventSite.toString() + ": \n");
		for(DCELLineSegment ls : segments) {
//			System.out.print(ls + ", ");
			if(ls.getLowerEndpoint().equals(lse.getEventPoint())) {
//				System.out.println("ls belongs to lowerSegments");
				lowerSegments.add(ls);
			}
			else {
				containingSegments.add(ls);
//				System.out.println("ls belongs to containingSegments");
			}
		}
//		System.out.println();
		
		/*******************************************
		 * 				STEP THREE
		 ******************************************/
		Set<DCELLineSegment> unionizedSegments = new HashSet<DCELLineSegment>();
		unionizedSegments.addAll(upperSegments);
		unionizedSegments.addAll(lowerSegments);
		unionizedSegments.addAll(containingSegments);
		if(unionizedSegments.size() > 1) {
//			System.out.println("Intersection found at " + lse.eventSite + " involving " + unionizedSegments.size() + " segments:");
//			for(DCELLineSegment ls : unionizedSegments) {
//				System.out.println("\t" + ls);
//			}
			intersection = new LineSegmentIntersection(lse.getEventPoint());
			//Bad ... will need to check again if endpoints satisfy intersections.
			//Much better would be to store disjoint upper, lower and containing sets in LineSegmentIntersection
			/*******************************************
			 * 				STEP FOUR
			 ******************************************/
			intersection.containingSegments.addAll(unionizedSegments);
		}
		
		/*******************************************
		 * 				STEP FIVE
		 ******************************************/
		Set<DCELLineSegment> lowerAndContainingSegments = new HashSet<DCELLineSegment>();
		lowerAndContainingSegments.addAll(lowerSegments);
		lowerAndContainingSegments.addAll(containingSegments);
		
		if(lowerAndContainingSegments.isEmpty()) {
//			System.out.println("Nothing to remove .... ");
		}
		else {
			for(DCELLineSegment ls : lowerAndContainingSegments) {
//				System.out.println("Removing " + ls.toString());
//				statusTree.printInOrder();
//				statusTree.printLeafsInOrder();
//				statusTree.printIntersectionsInOrder(lse.eventSite);
				try {
					statusTree.remove(ls,lse.eventSite);
				}
				catch (NoSuchElementException nse) {
					//nse.printStackTrace();
					//throw nse;
				}
//				System.out.println("Removed  ");
//				statusTree.printInOrder();
//				statusTree.printLeafsInOrder();
//				statusTree.printIntersectionsInOrder(lse.eventSite);
			}

		}
		
		/*******************************************
		 * 				STEP SIX
		 ******************************************/
		Set<DCELLineSegment> upperAndContainingSegments = new HashSet<DCELLineSegment>();
		Set<DCELLineSegment> horizontalSegments = new HashSet<DCELLineSegment>();
		upperAndContainingSegments.addAll(upperSegments);
		upperAndContainingSegments.addAll(containingSegments);

		if(upperAndContainingSegments.isEmpty()) {
//			System.out.println("Nothing to insert .... ");
		}
		else {
			for(DCELLineSegment ls : upperAndContainingSegments) {
				if(ls.isHorizontal())
					horizontalSegments.add(ls);
					else {
//						System.out.println("Inserting " + ls.toString());
//						statusTree.printInOrder();
//						statusTree.printLeafsInOrder();
//						statusTree.printIntersectionsInOrder(lse.eventSite);
						statusTree.insert(ls,lse.eventSite);
//						System.out.println("Inserted " + ls.toString());
//						statusTree.printInOrder();
//						statusTree.printLeafsInOrder();
//						statusTree.printIntersectionsInOrder(lse.eventSite);
					}
			}
			for(DCELLineSegment ls : horizontalSegments) {
//				System.out.println("Inserting " + ls.toString());
//				statusTree.printInOrder();
//				statusTree.printLeafsInOrder();
//				statusTree.printIntersectionsInOrder(lse.eventSite);
				statusTree.insert(ls,lse.eventSite);
//				System.out.println("Inserted " + ls.toString());
//				statusTree.printInOrder();
//				statusTree.printLeafsInOrder();
//				statusTree.printIntersectionsInOrder(lse.eventSite);
			}

			/*******************************************
			 * 				STEP SEVEN (Order of containingSegments should now be reversed in statusTree)
			 ******************************************/
		}

		/*******************************************
		 * 				STEP EIGHT
		 ******************************************/
		if(upperAndContainingSegments.isEmpty()) {
//			System.out.println("upperAndContainingSegments is empty");
			/*******************************************
			 * 				STEP NINE
			 ******************************************/
			DCELLineSegment leftNeighbor = statusTree.getLeftNeighbor(lse.eventSite);
			DCELLineSegment rightNeighbor = statusTree.getRightNeighbor(lse.eventSite);
			/*******************************************
			 * 				STEP TEN
			 ******************************************/
			if(leftNeighbor != null && rightNeighbor != null)
				findNewEvent(leftNeighbor,rightNeighbor,lse.getEventPoint());
		}
		else {
//			System.out.println("upperAndContainingSegments is NON-empty");
			/*******************************************
			 * 				STEP ELEVEN
			 ******************************************/
			LSIStatusTreeNode leftMostSegment = statusTree.getLeftMostSegment(upperAndContainingSegments, lse.eventSite);
			/*******************************************
			 * 				STEP TWELVE/THIRTEEN
			 ******************************************///			if(leftMostSegment != null) {//				System.out.println("left " + leftMostSegment.segment);//			if(leftMostSegment.predecessor != null)//				System.out.println("left->predecessor " + leftMostSegment.predecessor.segment);//			}
			if(leftMostSegment != null && leftMostSegment.predecessor != null)
				findNewEvent(leftMostSegment.predecessor.segment,leftMostSegment.segment,lse.getEventPoint());

			/*******************************************
			 * 				STEP FOURTEEN
			 ******************************************/
			LSIStatusTreeNode rightMostSegment = statusTree.getRightMostSegment(upperAndContainingSegments, lse.eventSite);
			/*******************************************
			 * 				STEP FIFTEEN/SIXTEEN
			 ******************************************///			if(rightMostSegment != null) {//				System.out.println("right " + rightMostSegment.segment);//			if(rightMostSegment.successor != null)//				System.out.println("right->successor " + rightMostSegment.successor.segment);//			}
			if(rightMostSegment != null && rightMostSegment.successor != null)
				findNewEvent(rightMostSegment.successor.segment,rightMostSegment.segment,lse.getEventPoint());

		}
		return intersection;
	}

	private void findNewEvent(DCELLineSegment leftSegment, DCELLineSegment rightSegment, Vertex eventPoint) {

		Vertex intersection = leftSegment.getIntersection(rightSegment);

		if(intersection!=null) {
//			System.out.println("Found intersection");
			
			LineSegmentEvent lse = new LineSegmentEvent(intersection,null);
			if(!eventQueue.contains(lse) && (intersection.getY() < eventPoint.getY() || (intersection.getY() == eventPoint.getY() && intersection.getX() > eventPoint.getX()))) {
//				System.out.println("Inserting new intersection event: " + lse.getEventPoint().toString());
//				System.out.println("Right Segment: " + rightSegment.toString());
//				System.out.println("Left Segment : " + leftSegment.toString());
//				System.out.println("EventPoint   : " + eventPoint.toString());
//				System.out.println("Intersection : " + intersection.toString());
				if(leftSegment.getUpperEndpoint().equals(intersection))
					lse.addSegment(leftSegment);
				if(rightSegment.getUpperEndpoint().equals(intersection))
					lse.addSegment(rightSegment);
				eventQueue.insert(lse);
			}
			else {
				if(eventQueue.contains(lse)) {
//					System.out.println("Right Segment: " + rightSegment.toString());
//					System.out.println("Left Segment : " + leftSegment.toString());
//					System.out.println("EventPoint   : " + eventPoint.toString());
					LineSegmentEvent existingLSE = eventQueue.getEvent(lse);
//					System.out.println("Exisiting line segment event contains the following segments ");
//					for(DCELLineSegment ls : existingLSE.getSegments()) {
//						System.out.println(ls);
//					}
				}
//				System.out.println("event queue already contains this event, or the intersection point is above the sweep line and has already been handled");
			}
		}
		else {
//			if(leftSegment.isHorizontal() && rightSegment.isHorizontal())
//			System.out.println("no intersection found");
//			System.out.println("leftSegment : " + leftSegment);
//			System.out.println("rightSegment: " + rightSegment);
		}
	}

	public void printQueue() {
		eventQueue.printInOrder();
	}
	
	public void addSegment(DCELLineSegment segment) {
		eventQueue.insert(new LineSegmentEvent(segment.getUpperEndpoint(),segment));
		eventQueue.insert(new LineSegmentEvent(segment.getLowerEndpoint(),null));
	}
	
	public void addSegments(Vector<DCELLineSegment> segments) {
		for(DCELLineSegment segment : segments) {
			eventQueue.insert(new LineSegmentEvent(segment.getUpperEndpoint(),segment));
			eventQueue.insert(new LineSegmentEvent(segment.getLowerEndpoint(),null));		
		}
	}

	public Vector<LineSegmentIntersection> getIntersections() {
		return intersections;
	}
}
