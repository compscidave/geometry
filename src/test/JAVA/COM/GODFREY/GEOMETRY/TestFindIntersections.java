/*
 * Copyright 2011-2014 David Godfrey. All rights reserved.
 */
package com.godfrey.geometry;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.godfrey.geometry.test.voronoi.DCEL;
import com.godfrey.geometry.test.voronoi.DCELLineSegment;
import com.godfrey.geometry.test.voronoi.FindIntersections;
import com.godfrey.geometry.test.voronoi.LineSegmentIntersection;
import com.godfrey.geometry.test.voronoi.Vertex;

public class TestFindIntersections extends TestCase {

	private static final int RUNS = 100;
	FindIntersections algorithm;
	DCEL doublyConnectedEdgeList;
	private static final int LONGITUDE = 2000;
	private static final int LATITUDE = 2000;
	private static final int X_VELOCITY = 30;
	private static final int Y_VELOCITY = 30;
	
	@Before
	protected void setUp() {
		algorithm = new FindIntersections();

	}

	@After
	protected void tearDown() {
		algorithm = new FindIntersections();
	}
	
	@Test
	public void testSingleLineSegment() {
		algorithm.addSegment(new DCELLineSegment(new Vertex(1.0d,4983.39882d),new Vertex(2871.228d,-39884.387d)));
		algorithm.computeIntersections();
		assert(algorithm.getIntersections().isEmpty());
	}
	
	@Test
	public void testNonIntersectingLineSegments() {
		algorithm.addSegment(new DCELLineSegment(new Vertex(0.0d,0.0d),new Vertex(2871.228d,884.387d)));
		algorithm.addSegment(new DCELLineSegment(new Vertex(-223.3d,-2219.0d),new Vertex(-2.0d,-998.4d)));
		algorithm.computeIntersections();
		assertEquals(0,algorithm.getIntersections().size(),0);
	}
	
	@Test
	public void testProperIntersectingLineSegments() {
		algorithm.addSegment(new DCELLineSegment(new Vertex(-200.0d,200.0d),new Vertex(200.0d,-200.0d)));
		algorithm.addSegment(new DCELLineSegment(new Vertex(-200.0d,-200.0d),new Vertex(200.0d,200.0d)));
		algorithm.computeIntersections();
		assertEquals(1,algorithm.getIntersections().size(),0);
		assertEquals(0.0d,algorithm.getIntersections().get(0).getCoordinate().getLatitude(),0);
		assertEquals(0.0d,algorithm.getIntersections().get(0).getCoordinate().getLongitude(),0);
	}
	
	@Test
	public void testNonProperIntersectingLineSegments() {
		algorithm.addSegment(new DCELLineSegment(new Vertex(0.0d,0.0d),new Vertex(500.0d,-500.0d)));
		algorithm.addSegment(new DCELLineSegment(new Vertex(-300.0d,200.0d),new Vertex(500.0d,-500.0d)));
		algorithm.computeIntersections();
		assertEquals(1,algorithm.getIntersections().size(),0);
		assertEquals(500.0d,algorithm.getIntersections().get(0).getCoordinate().getLongitude(),0.0d);
		assertEquals(-500.0d,algorithm.getIntersections().get(0).getCoordinate().getLatitude(),0.0d);
	}
	
	@Test
	public void testIntersectingHoriztonalLineSegment() {
		algorithm.addSegment(new DCELLineSegment(new Vertex(-500.0d,0.0d),new Vertex(500.0d,0.0d)));
		algorithm.addSegment(new DCELLineSegment(new Vertex(-300.0d,-300.0d),new Vertex(300.0d,300.0d)));
		algorithm.computeIntersections();
		assertEquals(1,algorithm.getIntersections().size(),0);
		assertEquals(0.0d,algorithm.getIntersections().get(0).getCoordinate().getLongitude(),0);
		assertEquals(0.0d,algorithm.getIntersections().get(0).getCoordinate().getLatitude(),0);
	}
	
	@Test
	public void testIntersectingVerticleLineSegment() {
		assertTrue(true);
	}
	
	@Test
	public void testEventPointContainingMultipleSegments() {
		System.out.println("Testing Event Point Containing Multiple Segments");
		algorithm.addSegment(new DCELLineSegment(new Vertex(-200.0d,200.0d),new Vertex(200.0d,-200.0d)));
		algorithm.addSegment(new DCELLineSegment(new Vertex(-200.0d,-200.0d),new Vertex(200.0d,200.0d)));
		algorithm.addSegment(new DCELLineSegment(new Vertex(0.0d,300.0d),new Vertex(0.0d,-300.0d)));
		algorithm.addSegment(new DCELLineSegment(new Vertex(-300.0d,0.0d),new Vertex(300.0d,0.0d)));
		algorithm.computeIntersections();
		for(LineSegmentIntersection lsi : algorithm.getIntersections()) {
			System.out.println(lsi.getCoordinate());
			for(DCELLineSegment ls : lsi.getContainingSegments()) {
				System.out.println(ls);
			}
		}
		assertEquals(1,algorithm.getIntersections().size(),0);
		assertEquals(4,algorithm.getIntersections().elementAt(0).getContainingSegments().size(),0);
	}
	
	@Test
	public void testMultipleIntersectionsOnSingleSegment() {
		algorithm.addSegment(new DCELLineSegment(new Vertex(-500.0d,100.0d),new Vertex(-500.0d,-100.0)));
		algorithm.addSegment(new DCELLineSegment(new Vertex(-400.0d,100.0d),new Vertex(-400.0d,-100.0)));
		algorithm.addSegment(new DCELLineSegment(new Vertex(-300.0d,100.0d),new Vertex(-300.0d,-100.0)));
		algorithm.addSegment(new DCELLineSegment(new Vertex(-200.0d,100.0d),new Vertex(-200.0d,-100.0)));
		algorithm.addSegment(new DCELLineSegment(new Vertex(-100.0d,100.0d),new Vertex(-100.0d,-100.0)));
		algorithm.addSegment(new DCELLineSegment(new Vertex(0.0d,100.0d),new Vertex(0.0d,-100.0d)));
		algorithm.addSegment(new DCELLineSegment(new Vertex(-600.0d,0.0d),new Vertex(200.0d,0.0d)));
		algorithm.addSegment(new DCELLineSegment(new Vertex(-600.0d,0.0d),new Vertex(-600.0d,-300.0d)));
		algorithm.addSegment(new DCELLineSegment(new Vertex(-900.0d,300.0d),new Vertex(-500.0d,-100.0d)));
		algorithm.addSegment(new DCELLineSegment(new Vertex(200.0d,0.0d),new Vertex(200.0d,-500.0d)));
		algorithm.computeIntersections();
		System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
		System.out.println("*                  " + algorithm.getIntersections().size() + "                *");
		for(LineSegmentIntersection lsi : algorithm.getIntersections()) {
			System.out.println(lsi.getCoordinate());
			for(DCELLineSegment ls : lsi.getContainingSegments())
				System.out.println(ls);
		}
		System.out.println("**************************************************");
		assertEquals(9,algorithm.getIntersections().size(),0);
	}
	
	
	@Test
	public void testVoronoiArrangement() {
		algorithm.addSegment(new DCELLineSegment(new Vertex(-200.0d,200.0d), new Vertex(0.0d,0.0d)));
		algorithm.addSegment(new DCELLineSegment(new Vertex(200.0d,200.0d), new Vertex(0.0d,0.0d)));
		algorithm.addSegment(new DCELLineSegment(new Vertex(0.0d,0.0d), new Vertex(-30.0d,-50.0d)));
		algorithm.computeIntersections();
		assertEquals(1,algorithm.getIntersections().size(),0);
		
	}
	@Test
	public void testEndpointIntersectionWithProperIntersection() {
		algorithm.addSegment(new DCELLineSegment(new Vertex(-2050,2050),new Vertex(2050,2050)));
		algorithm.addSegment(new DCELLineSegment(new Vertex(-2050,2050),new Vertex(-2050,-2050)));
		algorithm.addSegment(new DCELLineSegment(new Vertex(-24480.442574,24480.442574),new Vertex(-100,100)));
		algorithm.computeIntersections();
		assertEquals(1,algorithm.getIntersections().size(),0);
		assertEquals(3,algorithm.getIntersections().elementAt(0).getContainingSegments().size(),0);
	}
	
	@Test
	public void test2EndpointIntersectionWithProperIntersection() {
		algorithm.addSegment(new DCELLineSegment(new Vertex(0,0),new Vertex(150,0)));
		algorithm.addSegment(new DCELLineSegment(new Vertex(0,0),new Vertex(0,-150)));
		algorithm.addSegment(new DCELLineSegment(new Vertex(-200,200),new Vertex(200,-200)));
		algorithm.computeIntersections();
		assertEquals(1,algorithm.getIntersections().size(),0);
		assertEquals(3,algorithm.getIntersections().elementAt(0).getContainingSegments().size(),0);
	}
	
	@Test
	public void testBookArrangement() {
		//S1
		algorithm.addSegment(new DCELLineSegment(new Vertex(-2.0d,5.0d),new Vertex(1.0,-2.5d)));
		//S2
		algorithm.addSegment(new DCELLineSegment(new Vertex(0.0d,0.0d),new Vertex(-1.0d,-5.0d)));
		//S3
		algorithm.addSegment(new DCELLineSegment(new Vertex(2.0d,3.5d),new Vertex(-2.0d,-3.5d)));
		//S4
		algorithm.addSegment(new DCELLineSegment(new Vertex(-3.5d,4.0d),new Vertex(0.0d,0.0d)));
		//S5
		algorithm.addSegment(new DCELLineSegment(new Vertex(-4.0d,1.0d),new Vertex(0.0d,0.0d)));
		//S7
		algorithm.addSegment(new DCELLineSegment(new Vertex(-5.0d,3.0d),new Vertex(-4.5d,-2.0d)));
		//S8
		algorithm.addSegment(new DCELLineSegment(new Vertex(2.5d,2.0d),new Vertex(1.5d,-2.0d)));
		algorithm.computeIntersections();
		assertEquals(1,algorithm.getIntersections().size(),0);
		assertEquals(5,algorithm.getIntersections().elementAt(0).getContainingSegments().size(),0);
	}
	
/*	@Ignore
	@Test
	public void testRandomArrangementFromVoronoi() {
		int [][] table = new int [RUNS][2];
		System.out.println("Testing random arrangement");

		for(int j = 0; j < RUNS; j++) {
			Random randomGenerator = new Random();
			randomGenerator.setSeed(System.currentTimeMillis());
			Vector<Site> sites = new Vector<Site>();
			for(int i = 0; i < 10; i++) {
				ObservableCab cab = new ObservableCab(new Driver(), new Vehicle());
	
				if(i%4==0)
					cab.setLocation(LONGITUDE*randomGenerator.nextFloat(),LATITUDE*randomGenerator.nextFloat());
				else
					if(i%4==1)
						cab.setLocation(-LONGITUDE*randomGenerator.nextFloat(),-LATITUDE*randomGenerator.nextFloat());
					else
						if(i%4==2)
							cab.setLocation(-LONGITUDE*randomGenerator.nextFloat(),LATITUDE*randomGenerator.nextFloat());
						else
							cab.setLocation(LONGITUDE*randomGenerator.nextFloat(),-LATITUDE*randomGenerator.nextFloat());
	//			cab.setVelocity(0.0d,0.0d);
				cab.setPreviousLocation(cab.getLocation());
				if(i%4==0)
					cab.setVelocity(X_VELOCITY*randomGenerator.nextFloat(),Y_VELOCITY*randomGenerator.nextFloat());
				else
					if(i%4==1)
						cab.setVelocity(-X_VELOCITY*randomGenerator.nextFloat(),-Y_VELOCITY*randomGenerator.nextFloat());
					else
						if(i%4==2)
							cab.setVelocity(-X_VELOCITY*randomGenerator.nextFloat(),Y_VELOCITY*randomGenerator.nextFloat());
						else
							cab.setVelocity(X_VELOCITY*randomGenerator.nextFloat(),-Y_VELOCITY*randomGenerator.nextFloat());
				sites.add(new TestSite(cab));
			}
			Voronoi v = new Voronoi(sites);

			try {
				doublyConnectedEdgeList = v.newConstruct(LONGITUDE, LATITUDE);
				table[j][0] = doublyConnectedEdgeList.getVertices().size();
				table[j][1] = doublyConnectedEdgeList.getIntersections().size();
//				for(Vertex vertex : doublyConnectedEdgeList.getVertices()) {
//					boolean found = false;
//					System.out.println("Searching for " + vertex);
//					for(LineSegmentIntersection lsi : doublyConnectedEdgeList.getIntersections()) {
//						if(lsi.getCoordinate().equals(vertex))
//							found = true;
//					}
//					if(!found)
//						fail();
//				}

//				assertEquals(doublyConnectedEdgeList.getIntersections().size(),doublyConnectedEdgeList.getVertices().size()+4,0);
			} catch (VoronoiConstructionException e) {
				System.out.flush();
				e.printStackTrace();
				fail();
			} catch (NoSuchElementException nse) {
				System.out.flush();
				nse.printStackTrace();
				fail();
			}

		}
		int correctlyComputed = 0;
		for(int j=0;j<RUNS;j++) {
			System.out.println(table[j][0] + "\t:\t" + table[j][1]);
			if(table[j][0]<=table[j][1])
				correctlyComputed++;
		}
		System.out.println("There were " + correctlyComputed + " runs that succeeded");
	}*/
}
