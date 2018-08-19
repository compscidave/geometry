/*
 * Copyright 2011-2014 David Godfrey. All rights reserved.
 */
package com.godfrey.geometry.test.voronoi;

import java.util.Comparator;

public class NewEventComparator implements Comparator<NewEvent> {

	//Standard comparator sorts from small to large...
	//In this algorithm, events are handled from top ( largest y )
	//to bottom ( smallest y ) in the plane.
	public int compare(NewEvent e0, NewEvent e1) {
		if(e0.getEventSite().getY() < e1.getEventSite().getY())
			return 1;
		else
			if(e0.getEventSite().getY() > e1.getEventSite().getY())
				return -1;
			else
				//The y-values are the same
				if(e0.getEventSite().getX() > e1.getEventSite().getX())
					return 1;
				else
					if(e0.getEventSite().getX() < e1.getEventSite().getX())
						return -1;
					else
						return 0;
	}

}
