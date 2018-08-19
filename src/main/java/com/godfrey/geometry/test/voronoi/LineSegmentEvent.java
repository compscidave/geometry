/*
 * Copyright 2011-2014 David Godfrey. All rights reserved.
 */
package com.godfrey.geometry.test.voronoi;

import java.util.HashSet;
import java.util.Set;

public class LineSegmentEvent extends NewEvent implements Comparable<LineSegmentEvent> {

	private Set<DCELLineSegment> upperSegments = new HashSet<DCELLineSegment>();
	
	private Vertex eventPoint;
	
	public LineSegmentEvent(Vertex eventPoint, DCELLineSegment dCELLineSegment) {
		super(eventPoint.getCoords(),false);
		this.setEventPoint(eventPoint);
		if(dCELLineSegment != null)
			upperSegments.add(dCELLineSegment);
	}

	public boolean isUpperEndpointEvent() {
		return upperSegments.isEmpty() ? true : false;
	}
	
	public void addSegment(DCELLineSegment segment) {
		upperSegments.add(segment);
	}
	
	/**
	 * @return the eventPoint
	 */
	public Vertex getEventPoint() {
		return eventPoint;
	}

	/**
	 * @param eventPoint the eventPoint to set
	 */
	public void setEventPoint(Vertex eventPoint) {
		this.eventPoint = eventPoint;
	}

	/**
	 * @return the upperSegments
	 */
	public Set<DCELLineSegment> getSegments() {
		return upperSegments;
	}

	/**
	 * @param upperSegments the upperSegments to set
	 */
	public void setSegments(Set<DCELLineSegment> segments) {
		this.upperSegments = segments;
	}

	public void addSegment(Set<DCELLineSegment> segments) {
		this.upperSegments.addAll(segments);
	}

	public int compareTo(LineSegmentEvent lse) {
		if(eventPoint.getLatitude() > lse.eventPoint.getLatitude() ||
				(eventPoint.getLatitude() == lse.eventPoint.getLatitude() &&
						eventPoint.getLongitude() < lse.eventPoint.getLongitude()))
			return -1;
		else
			if(eventPoint.equals(lse.eventPoint))
				return 0;
			else
				return 1;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("LineSegmentEvent <"+eventPoint.getLongitude()+","+eventPoint.getLatitude()+">\n");
		int i = 1;
		for(DCELLineSegment ls : upperSegments) {
			sb.append("\t segment " + i + ": "+ls.toString()+"\n");
			i++;
		}
		return sb.toString();
	}

}
