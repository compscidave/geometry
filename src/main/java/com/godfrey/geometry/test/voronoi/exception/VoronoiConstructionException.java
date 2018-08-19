/*
 * Copyright 2011-2014 David Godfrey. All rights reserved.
 */
package com.godfrey.geometry.test.voronoi.exception;

@SuppressWarnings("serial")
public class VoronoiConstructionException extends Exception {
	
	protected String except;

	public VoronoiConstructionException(CircleEventException e1) {
		e1.fillInStackTrace();
	}

	public VoronoiConstructionException(SiteEventException e1) {
		e1.fillInStackTrace();
	}

	public VoronoiConstructionException(StackTraceElement[] stackTrace) {
		// TODO Auto-generated constructor stub
	}

	public VoronoiConstructionException(String string) {
		super(string);
		except = string;
	}
}
