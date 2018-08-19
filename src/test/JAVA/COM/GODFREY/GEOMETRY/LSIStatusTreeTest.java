/*
 * Copyright 2011-2014 David Godfrey. All rights reserved.
 */
package com.godfrey.geometry;

import static org.junit.Assert.*;
import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.godfrey.geometry.test.voronoi.LSIStatusTree;

public class LSIStatusTreeTest extends TestCase {

	LSIStatusTree tree;
	
	@Before
	public void setUp() {
		tree = new LSIStatusTree();
	}
	
	@Test
	public void testInsertIntoEmptyTree() {
		//tree.insert(value, eventSite);
	}
	
	@After
	public void tearDown() {
		
	}
	
	
}
