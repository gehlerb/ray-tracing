/**
 *
	Course: 151055 Software Engineering intro, Lab
	Student1: Avi margali 305645137
    Student2: Yeuda Nuiman 301759692
    Teacher: Dan Zilberstein
 */
package com.ud_avi.raytrace.geometries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ud_avi.raytrace.primitives.Point3D;
import com.ud_avi.raytrace.primitives.Ray;

/**
 * Geometries is class that hold collection of Geometry
 *
 */
public class Geometries implements Intersectable {
	private List<Geometry> geometriesList = new ArrayList<>();
	
	/* (non-Javadoc)
	 * @see com.ud_avi.raytrace.geometries.Intersectable#findIntersections(com.ud_avi.raytrace.primitives.Ray)
	 */
	public Map<Geometry, List<Point3D>> findIntersections(Ray ray) {
		Map<Geometry, List<Point3D>> intersections  = new HashMap<>();
		for(Geometry geometry: geometriesList) {
			intersections.putAll(geometry.findIntersections(ray));//findIntersections can be empty
		}
		return intersections;
	}

	/**
	 * Add geometry into Geometries
	 * @param geometry object
	 */
	public void addGeometry(Geometry geometry) {
		geometriesList.add(geometry);
	}

}
