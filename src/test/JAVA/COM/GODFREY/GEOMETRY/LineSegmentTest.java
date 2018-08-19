/*
 * Copyright 2011-2014 David Godfrey. All rights reserved.
 */
package com.godfrey.geometry;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.godfrey.geometry.test.voronoi.Coordinate;
import com.godfrey.geometry.test.voronoi.DCELLineSegment;
import com.godfrey.geometry.test.voronoi.Vertex;


public class LineSegmentTest extends TestCase {
	
	@Before
	public void setUp() {
		
	}
	
	@Test
	public void testUpperEndpoint() {
		DCELLineSegment ls = new DCELLineSegment(new Vertex(29834.23,-29384.124), new Vertex(-8283.421,83923.3829));
		assertTrue(ls.getUpperEndpoint().getY() >= ls.getLowerEndpoint().getY());
	}
	
	@Test
	public void testFlipEndpoints() {
		DCELLineSegment ls = new DCELLineSegment(new Vertex(29834.23,-29384.124), new Vertex(-8283.421,83923.3829));
		ls.flipEndpoints();
		assertTrue(ls.getUpperEndpoint().getY() < ls.getLowerEndpoint().getY());
	}
	
	@Test
	public void testIsNotHorizontal() {
		DCELLineSegment ls = new DCELLineSegment(new Vertex(29834.23,-29384.124), new Vertex(-8283.421,83923.3829));
		assertEquals(false,ls.isHorizontal());
	}
	
	@Test
	public void testIsHorizontal() {
		DCELLineSegment ls = new DCELLineSegment(new Vertex(29834.23,-29384.124), new Vertex(-8283.421,-29384.124));
		assertEquals(true,ls.isHorizontal());
	}
	
	@Test
	public void testIsLeftOfPointLeftOfLineSegment() {
		Coordinate point = new Coordinate(0.0,0.0);
		DCELLineSegment ls = new DCELLineSegment(new Vertex(0.0,5.0), new Vertex(20.0,-5.0));		
		assertEquals(-1,ls.orientation(point));
	}
	
	@Test
	public void testIsLeftOfPointRightOfLineSegment() {
		Coordinate point = new Coordinate(0.0,0.0);
		DCELLineSegment ls = new DCELLineSegment(new Vertex(0.0,5.0), new Vertex(-20.0,-5.0));		
		assertEquals(1,ls.orientation(point));
	}
	
	@Test
	public void testIsLeftOfPointLeftOfHorizontalSegmentUpperEndpoint() {
		Coordinate point = new Coordinate(-20.0,0.0);
		DCELLineSegment ls = new DCELLineSegment(new Vertex(-10.0,0.0), new Vertex(10.0,0.0));
		assertEquals(-1,ls.orientation(point));
	}
	
	@Test
	public void testIsLeftOfSegmentContainingPoint() {
		com.godfrey.geometry.test.voronoi.Coordinate point = new Coordinate(0.0d,0.0d);
		DCELLineSegment ls = new DCELLineSegment(new Vertex(-300.0,300.0), new Vertex(300.0d,-300.0));
		assertEquals(false,ls.isLeftOf(point));
	}
	
	//This test case may not be needed
	//
/*	@Test
	public void testIsLeftOfPointWithinHorizontalSegmentIsFalse() {
		Coordinate point = new Coordinate(-5.0,0.0);
		DCELLineSegment ls = new DCELLineSegment(new Vertex(-10.0,0.0), new Vertex(10.0,0.0));
		assertEquals(false,ls.isLeftOf(point));
	}*/
	
	//This should never occur during the line-segment intersection sweep algorithm ( with horizontal sweep line ),
	//since the horizontal segment would have been removed indefinitely before any event site to the right of the
	//horizontal segments lower endpoint.
	@Test
	public void testIsLeftOfPointRightOfHorizontalSegmentLowerEndpoint() {
		Coordinate point = new Coordinate(15.0,0.0);
		DCELLineSegment ls = new DCELLineSegment(new Vertex(-10.0,0.0), new Vertex(10.0,0.0));
		assertEquals(1,ls.orientation(point));
	}
	
	@Test
	public void testLineSegmentContainsPoint() {
		Coordinate point = new Coordinate(0.0,0.0);
		DCELLineSegment ls = new DCELLineSegment(new Vertex(-20.0,20.0), new Vertex(10.0,-10.0));		
		assertEquals(0,ls.orientation(point));			
	}
	
	@Test
	public void testProperIntersection() {
		DCELLineSegment ls = new DCELLineSegment(new Vertex(-20.0,-20.0), new Vertex(20.0,20.0));
		DCELLineSegment lr = new DCELLineSegment(new Vertex(-20.0,20.0), new Vertex(20.0,-20.0));
		Vertex v = ls.getIntersection(lr);
		assertTrue(v.getX() == 0 && v.getY() == 0);
	}
	
	@Test
	public void testGetIntersectionNonProperIntersection() {
		DCELLineSegment ls = new DCELLineSegment(new Vertex(-20.0,-20.0), new Vertex(20.0,20.0));
		DCELLineSegment lr = new DCELLineSegment(new Vertex(-20.0,20.0), new Vertex(30.0,20.0));
		Vertex v = ls.getIntersection(lr);
		assertTrue(v.getX() == 20 && v.getY() == 20);
	}
	
	@Test
	public void testGetIntersectionCoincidentEndpointIntersection() {
		DCELLineSegment ls = new DCELLineSegment(new Vertex(-20.0,-20.0), new Vertex(20.0,20.0));
		DCELLineSegment lr = new DCELLineSegment(new Vertex(20.0,20.0), new Vertex(30.0,0.0));
		Vertex v = ls.getIntersection(lr);
		assertTrue(v.getX() == 20 && v.getY() == 20);		
	}
	
	@Test
	public void testGetIntersectionNullIntersection() {
		DCELLineSegment ls = new DCELLineSegment(new Vertex(-20.0,-20.0), new Vertex(20.0,20.0));
		DCELLineSegment lr = new DCELLineSegment(new Vertex(-20.0,40.0), new Vertex(30.0,20.0));
		Vertex v = ls.getIntersection(lr);
		assertNull(v);
	}
	
	@Test
	public void testGetIntersectionSpecificLineSegments() {
		DCELLineSegment ls = new DCELLineSegment(new Vertex(-24480.442574193, 24480.442574193), new Vertex(-100.0, 100.0));
		DCELLineSegment lr = new DCELLineSegment(new Vertex(-9999999, 2050.0), new Vertex(9999999, 2050.0));
		Vertex v = ls.getIntersection(lr);
		System.out.println(v);
		assertTrue(v.getX() == -2050.0 && v.getY() == 2050.0);
	}
	
	@After
	public void tearDown() {
		
	}

}
