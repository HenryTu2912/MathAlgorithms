public interface IMatrix {
    IMatrix Add(IMatrix mRight) throws IllegalArgumentException;
    IMatrix Multiply(IMatrix mRight) throws  IllegalArgumentException;
    IMatrix ScalarMultiply(double dScalar);
    IMatrix Substract(IMatrix mRight) throws  IllegalArgumentException;

    IMatrix Scaling(double dX, double dY);
    IMatrix Translate(double dX, double dY);
    IMatrix Rotate(double radians);
    IMatrix Rotatee(double radians, double dOx, double dOy);
    IMatrix leastSquare(int m);
    IMatrix gaussJordanElimination();
}
