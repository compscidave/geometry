/*
 * Copyright 2011-2014 David Godfrey. All rights reserved.
 */
package com.godfrey.geometry.voronoi.event;

import java.util.Comparator;

import com.godfrey.geometry.point.Point;

public class EventComparator<T> implements Comparator<Event<T>> {

	//Standard comparator sorts from small to large...
	//In this algorithm, events are handled from top ( largest y )
	//to bottom ( smallest y ) in the plane.
	public int compare(Event e0, Event e1) {
		if(((Point) e0.getSite()).getY() < ((Point) e1.getSite()).getY())
			return 1;
		else
			if(((Point) e0.getSite()).getY() > ((Point) e1.getSite()).getY())
				return -1;
			else
				return 0;
	}

}
