/**
 *
	Course: 151055 Software Engineering intro, Lab
	Student1: Avi margali 305645137
    Student2: Yeuda Nuiman 301759692
    Teacher: Dan Zilberstein
 */
package com.ud_avi.raytrace;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ud_avi.raytrace.geometries.Geometry;
import com.ud_avi.raytrace.primitives.*;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.fail;
import com.ud_avi.raytrace.elements.Camera;
import com.ud_avi.raytrace.geometries.Plane;
import com.ud_avi.raytrace.scene.Scene;


public class PlaneTests {
	private Plane plane1 = new Plane(new Point3D(1,1,1), new Vector(1,1,1), Color.WHITE, new Material(500, 0.5, 1, 1, 100));
	private Plane plane2 = new Plane(new Point3D(1,0,0),new Point3D(0,1,0),new Point3D(1,1,0), new Color(255,255,255), new Material(2,3,0, 4, 0));
	private Plane plane3 = new Plane(new Point3D(0,0,0),new Vector(0,1,0), new Color(255,255,255), new Material(2,3,0, 4, 0));
	private Plane plane4 = new Plane(new Point3D(0,0,0),new Point3D(1,1,2),new Point3D(1,0,1), new Color(255,255,255), new Material(2,3,0, 4, 0));
	private Plane plane5 = new Plane(new Point3D(0,0,1),new Vector(-1,-1,1), new Color(255,255,255), new Material(2,3,0, 4, 0));
	private Plane plane6 = new Plane(new Point3D(0,0,1),new Point3D(1,0,2),new Point3D(0,1,2), new Color(255,255,255), new Material(2,3,0, 4, 0));

	private Vector normalXY = new Vector(0,0,-1);
	private Vector normalXZ = new Vector(0,1,0);
	private Vector justNormal = new Vector(1,1,-1).normalize();
	@Test
	public void testConstructor() {
		//linerDep
		try {
			new Plane(new Point3D(1,2,3), new Point3D(2,4,6), new Point3D(3,6,9), Color.WHITE, new Material(500, 0.5, 1, 1, 100));
			fail("expected linear dependencies found exception");
		}
		catch (Exception e) {}
		//zeroVec
		try {
			new Plane(new Point3D(0,0,0), new Point3D(0,0,0), new Point3D(1,2,3), Color.WHITE, new Material(500, 0.5, 1, 1, 100));
			fail("expected more then one point is zero exception");
		}catch (Exception e) {}
		//zero_e_Vec
		try {
			new Plane(new Point3D(0.00000001,0.00000001,0.00000001), new Point3D(0.00000001,0.00000001,0.00000001), new Point3D(1,2,3), Color.WHITE, new Material(500, 0.5, 1, 1, 100));
			fail("expected more then one point is zero exception");
		}catch (Exception e) {}
		//zero_me_Vec
		try {
			new Plane(new Point3D(-0.99999999,-0.99999999,-0.99999999), new Point3D(-0.99999999,-0.99999999,-0.99999999), new Point3D(1,2,3), Color.WHITE, new Material(500, 0.5, 1, 1, 100));
			fail("expected more then one point is zero exception");
		}catch (Exception e) {}
		//equalPoints
		try {
			new Plane(new Point3D(1,3,4), new Point3D(1,3,4), new Point3D(2, 5, 9), Color.WHITE, new Material(500, 0.5, 1, 1, 100));
			fail("expected linear depenencies found exception");
		}catch (Exception e) {}
		//notZero
		try {
			new Plane(new Point3D(0.001,0.001,0.001), new Point3D(0,0,0), new Point3D(1,3,2), Color.WHITE, new Material(500, 0.5, 1, 1, 100));
			new Plane(new Point3D(-0.001,-0.001,-0.001), new Point3D(0,0,0), new Point3D(1,3,2), Color.WHITE, new Material(500, 0.5, 1, 1, 100));
		} catch (Exception e) {
			fail("Expected no Exception");
		}
	}
	
	@Test
	public void testGetNormal() {
		assertEquals(normalXY, plane2.getNormal(new Point3D(2,2,0)));
		assertEquals(normalXY, plane2.getNormal(new Point3D(1,0,0))); // normal for the same point as plane point
		assertEquals(normalXZ, plane3.getNormal(new Point3D(1,0,1)));
		assertEquals(normalXZ, plane3.getNormal(new Point3D(0,0,0))); // normal for the same point as plane point, and point 0
		assertEquals(justNormal, plane4.getNormal(new Point3D(1,3,4))); // Normal for crooked plane
		assertEquals(plane6.getNormal(new Point3D(1,1,3)), plane5.getNormal(new Point3D(1,2,4))); // check that both normals are the same - same plane

	}
	@Test
	public void testFindIntersections() {
		List<Point3D> list = new ArrayList<>();
		Scene scene = new Scene("test scene");
        Camera camera = new Camera(new Point3D(0,0,0.5),new Vector(0,-1,0),new Vector(0,0,-1));
        scene.setCamera(camera);
        scene.setDistance(4);
        //1 point
        list=getIntersections(scene,plane1);
        assertEquals(1, list.size());
        Plane frontOfCamera = new Plane(new Point3D(0, 0,-4), new Vector(new Point3D(0, 0, -1)), Color.WHITE, new Material(500, 0.5, 1, 1, 100));
        //9 points plane in front of camera
        list=getIntersections(scene,frontOfCamera);
        assertEquals(9, list.size());
        //0 points plane contains camera
        Plane cameraDirection = new Plane(new Point3D(0,0,1), new Vector(new Point3D(1,0,0)), Color.WHITE, new Material(500, 0.5, 1, 1, 100));
        list=getIntersections(scene,cameraDirection);
        assertEquals(0, list.size());
        //0 points plane behind camera
        Plane behindCamera = new Plane(new Point3D(0, 0,1), new Point3D(1,0,1), new Point3D(0,-2,1), Color.WHITE, new Material(500, 0.5, 1, 1, 100));
        list=getIntersections(scene,behindCamera);
        assertEquals(0, list.size());
	}
	@SuppressWarnings("Duplicates")
	private List<Point3D> getIntersections(Scene scene, Plane plane){
        List<Point3D> list = new ArrayList<>();
        for(int i = 1 ; i < 4 ;++i) {
            for (int j = 1; j < 4; ++j) {
                Ray r = scene.getCamera().constructRay(3, 3, i, j, scene.getDistance(), 9, 9);
				Map<Geometry, List<Point3D>> map = plane.findIntersections(r);
				if(!map.isEmpty())
					list.addAll(map.get(plane));
            }
        }
        return list;
    }
}
