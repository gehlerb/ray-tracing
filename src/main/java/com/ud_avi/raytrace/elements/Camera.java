/**
 *
 Course: 151055 Software Engineering intro, Lab
 Student1: Avi margali 305645137
 Student2: Yeuda Nuiman 301759692
 Teacher: Dan Zilberstein
 */
package com.ud_avi.raytrace.elements;

import com.ud_avi.raytrace.primitives.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Camera is the Class that represent the Camera in the scene
 */
public class Camera {
   private Point3D _centerPoint;
   private Vector _upVector;
   private Vector _toVector;
   private Vector _rightVector;
   private double _focalLength;
   private double _apertureSize;
    private int _numOfDOFRays = 24;
    private Boolean _isDOF = false;

    // ***************** Constructors ********************** //

    /**
     * Constructor
     * @param centerPoint center point of the camera
     * @param upVector up direction
     * @param toVector to direction
     */
    public Camera(Point3D centerPoint, Vector upVector, Vector toVector) {
        double scalar = upVector.dotProduct(toVector);
        if(!Coordinate.ZERO.equals(scalar)){ //check if they are orthogonal
            throw new IllegalArgumentException("Vector not orthogonal");
        }
        _centerPoint = new Point3D(centerPoint);
        _upVector = new Vector(upVector).normalize();
        _toVector = new Vector(toVector).normalize();
        _rightVector = _toVector.crossProduct(_upVector).normalize(); //calculate the right direction
    }
    // ***************** Getters/Setters ********************** //

    /**
     *
     * @return the center point
     */
    public Point3D getCenterPoint() {
        return _centerPoint;
    }

    /**
     *
     * @return get the up direction vector
     */
    public Vector getUpVector() {
        return _upVector;
    }

    /**
     *
     * @return get the to direction vector
     */
    public Vector getToVector() {
        return _toVector;
    }

    /**
     *
     * @return get the right direction vector
     */
    public Vector getRightVector() {
        return _rightVector;
    }

    /**
     * @return the focal length
     */
    public double getFocalLength() {
        return _focalLength;
    }

    /**
     * @param focalLength the focal length
     */
    public void setFocalLength(double focalLength) {
        if (focalLength > 0)
            _focalLength = focalLength;
        else
            throw new IllegalArgumentException("Focal Length can't be 0");
    }

    /**
     * @return the aperture size
     */
    public double getApertureSize() {
        return _apertureSize*2;
    }

    /**
     * @param apertureSize the aperture size
     */
    public void setApertureSize(double apertureSize) {
        if (apertureSize > 0)
            _apertureSize = apertureSize/2;
        else
            throw new IllegalArgumentException("Aperture Size can't be 0");
    }

    /**
     * @return is the camera have the DOF effect
     */
    public Boolean getDOF() {
        return _isDOF;
    }

    /**
     * @param dof is the camera have the DOF effect
     */
    public void setDOF(Boolean dof) {
        _isDOF = dof;
    }

    /**
     * @return the num of rays per pixel
     */
    public int getNumOfRays() {
        int num = 1;
       if (_isDOF)
           num =+ _numOfDOFRays;
       return num;
    }

    // ***************** Operations ******************** //
    /**
     * Construct the Rays from the Camera to the place in te view plane
     * @param Nx num of rows
     * @param Ny num of columns
     * @param i index i
     * @param j index j
     * @param screenDistance Distance between the camera and the view plane
     * @param screenWidth real width of the view plane
     * @param screenHeight real height of the view plane
     * @return List of Rays from the Camera to the view plane
     */
    public List<Ray> constructRays (int Nx, int Ny, int i, int j, double screenDistance,
                             double screenWidth, double screenHeight){
        ArrayList<Ray> listOfRays = new ArrayList<>();
        Ray mainRay = constructRay(Nx, Ny, i, j, screenDistance, screenWidth, screenHeight);

        //add the DOF effect
        if(_isDOF)
            listOfRays.addAll(constructDOFRays(mainRay));

        listOfRays.add(mainRay);
        return listOfRays;
    }

    /**
     * Construct the Ray from the Camera to the place in te view plane
     * @param Nx num of rows
     * @param Ny num of columns
     * @param i index i
     * @param j index j
     * @param screenDistance Distance between the camera and the view plane
     * @param screenWidth real width of the view plane
     * @param screenHeight real height of the view plane
     * @return Ray from the Camera to the view plane
     */
    public Ray constructRay (int Nx, int Ny, int i, int j, double screenDistance,
                             double screenWidth, double screenHeight){

        if(Nx <=0 || Ny <= 0 || screenDistance <= 0 || screenWidth <= 0 || screenHeight <= 0)
            throw new IllegalArgumentException("Arguments of this function must be positive");

    	Point3D PC = _centerPoint.addVector(_toVector.scale(screenDistance)); //image center
    	double Rx = screenWidth/Nx; //width ratio
    	double Ry = screenHeight/Ny; //height ratio

    	double iScale = (Nx+1)/2.0d;
    	double jScale = (Ny+1)/2.0d;

        double dx = i - iScale; // the distance between Pij and Pc on the X line
        double dy = j - jScale; // the distance between Pij and Pc on the Y line


        Point3D Pij = PC;
        if (dx != 0) // move right/left
            Pij = Pij.addVector(_rightVector.scale(dx * Rx));

        if (dy != 0) // move up/down
            Pij = Pij.addVector(_upVector.scale(-dy * Ry));

    	Vector Vij = Pij.subtract(_centerPoint); //vector i,j
    	return new Ray(Vij, Pij);
    }

    /**
     * Construct the Depth Of Field rays
     * Construct the Ray from the view plane to the objects
     * And more rays from the aperture window for the depth of field effect
     * @param mainRay the main ray
     * @return Rays from the view plane
     */
    private List<Ray> constructDOFRays(Ray mainRay){
        double halfSize = _apertureSize /2;

        Vector mainVector = mainRay.getDirection();
        Point3D mainPoint = mainRay.getPoint();

        //focal point = point + vector * focal length
        ArrayList<Ray> listOfRays = new ArrayList<>();
        Point3D focalPoint = mainPoint.addVector(mainVector.scale(_focalLength));
        //calc the point and vectors
        Point3D halfUpPoint = mainPoint.addVector(_upVector.scale(halfSize));
        Point3D fullUpPoint = mainPoint.addVector(_upVector.scale(_apertureSize));
        Point3D halfDownPoint = mainPoint.addVector(_upVector.scale(-halfSize));
        Point3D fullDownPoint = mainPoint.addVector(_upVector.scale(-_apertureSize));
        Vector halfRight = _rightVector.scale(halfSize);
        Vector fullRight = _rightVector.scale(_apertureSize);
        Vector halfLeft = _rightVector.scale(-halfSize);
        Vector fullLeft = _rightVector.scale(-_apertureSize);

        //the middle rays
        Ray halfUpRay = new Ray(focalPoint.subtract(halfUpPoint),halfUpPoint);
        Ray fullUpRay = new Ray(focalPoint.subtract(fullUpPoint),fullUpPoint);
        Ray halfDownRay = new Ray(focalPoint.subtract(halfDownPoint),halfDownPoint);
        Ray fullDownRay = new Ray(focalPoint.subtract(fullDownPoint),fullDownPoint);

        listOfRays.add(halfUpRay);
        listOfRays.add(fullUpRay);
        listOfRays.add(halfDownRay);
        listOfRays.add(fullDownRay);

        //crate map like
        for(int i = 1;i<5;++i){
            switch (i){
                case 1:
                    mainPoint = halfUpPoint;
                    break;
                case 2:
                    mainPoint = fullUpPoint;
                    break;
                case 3:
                    mainPoint = halfDownPoint;
                    break;
                case 4:
                    mainPoint = fullDownPoint;
                    break;

            }

            Point3D point1 = mainPoint.addVector(halfRight);
            Ray focalRay6 = new Ray(focalPoint.subtract(point1),point1);
            listOfRays.add(focalRay6);

            Point3D point2 = mainPoint.addVector(fullRight);
            Ray focalRay7 = new Ray(focalPoint.subtract(point2),point2);
            listOfRays.add(focalRay7);

            Point3D point3 = mainPoint.addVector(halfLeft);
            Ray focalRay8 = new Ray(focalPoint.subtract(point3),point3);
            listOfRays.add(focalRay8);

            Point3D point4 = mainPoint.addVector(fullLeft);
            Ray focalRay9 = new Ray(focalPoint.subtract(point4),point4);
            listOfRays.add(focalRay9);

        }
        //set the rays from the view plane
        return listOfRays;
    }
    /**
     * Rotate the camera in X direction
     * @param angle to rotate
     */
    public void rotateX(double angle){
         _upVector.rotateX(angle);
         _toVector.rotateX(angle);
         _rightVector.rotateX(angle);
    }

    /**
     * Rotate the camera in X direction
     * @param angle to rotate
     */
    public void rotateY(double angle){
        _upVector.rotateY(angle);
        _toVector.rotateY(angle);
        _rightVector.rotateY(angle);
    }

    /**
     * Rotate the camera in X direction
     * @param angle to rotate
     */
    public void rotateZ(double angle){
        _upVector.rotateZ(angle);
        _toVector.rotateZ(angle);
        _rightVector.rotateZ(angle);
    }

}
