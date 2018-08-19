/*
 * Copyright 2011-2014 David Godfrey. All rights reserved.
 */
package com.godfrey.geometry.test.voronoi;

import com.godfrey.geometry.point.Point;
import com.godfrey.geometry.test.voronoi.Site;

public class SiteEvent extends NewEvent {

	Site site;
	
	public SiteEvent(Site site) {
		super(site.getLocation(),true);
		this.site = site;
	}
	
	public Point getLocation() {
		return site.getLocation();
	}
	
	public Site getSite() {
		return site;
	}
	
}
