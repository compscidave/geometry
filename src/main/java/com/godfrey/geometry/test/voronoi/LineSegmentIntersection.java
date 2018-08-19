/*
 * Copyright 2011-2014 David Godfrey. All rights reserved.
 */
package com.godfrey.geometry.test.voronoi;

import java.util.HashSet;
import java.util.Set;

public class LineSegmentIntersection {

	Set<DCELLineSegment> containingSegments;
	Vertex coordinate;
	
	public LineSegmentIntersection() {
		containingSegments = new HashSet<DCELLineSegment>();
	}
	
	public LineSegmentIntersection(Vertex coordinate) {
		containingSegments = new HashSet<DCELLineSegment>();
		this.coordinate = coordinate;
	}
	
	public void addSegment(DCELLineSegment dCELLineSegment) {
		containingSegments.add(dCELLineSegment);
	}
	
	public void setCoordinate(Vertex coordinate) {
		this.coordinate = coordinate;
	}
	
	public void setContainingSegments(Set<DCELLineSegment> containingSegments) {
		this.containingSegments = containingSegments;
	}
	
	public Set<DCELLineSegment> getContainingSegments() {
		return containingSegments;
	}
	
	public Vertex getCoordinate() {
		return coordinate;
	}
	
}
