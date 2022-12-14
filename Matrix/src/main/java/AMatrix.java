import org.example.Main;

public abstract class AMatrix implements IMatrix{
    private int nRows;
    private int nCols;
    public int getRows(){
        return nRows;
    }
    public int getCols(){
        return nCols;
    }
    public void setRows(int nRows){
        this.nRows = nRows;
    }
    public void setCols(int nCols){
        this.nCols = nCols;
    }
    protected abstract double getElement(int nRows, int nCols);
    protected abstract void setElement(int nRows, int nCols, double dVal);
    protected abstract AMatrix createMatrix(int nRows, int nCols);
    protected abstract AMatrix copyMatrix();
    @Override
    public String toString() {
        String s = "";
        char cUL = (char)0x250C;
        char cUR = (char)0x2510;
        char cLL = (char)0x2514;
        char cLR = (char)0x2518;
        char cVLine = (char)0x2502;

        //build the top row
        s += cUL;
        for (int j = 1; j <= this.getCols(); j++)
        {
            s += "\t\t";
        }
        s += cUR + "\n";

        //build the data rows
        for (int i = 1; i <= this.getRows(); i++)
        {
            s += cVLine;
            for (int j = 1; j <= this.getCols(); j++)
            {
                if (this.getElement(i, j) >= 0)
                {
                    s += " ";
                }
                s += String.format("%.2f", this.getElement(i, j)) + "\t";

            }
            s += cVLine + "\n";
        }
        //Build the bottom row
        s += cLL;
        for (int j = 1; j <= this.getCols(); j++)
        {
            s += "\t\t";
        }
        s += cLR + "\n";
        return s;
    }
    @Override
    public IMatrix Add(IMatrix iRight) throws IllegalArgumentException{
        AMatrix mLeft = this;
        AMatrix mRight = (AMatrix) iRight;
        AMatrix mResult = null;
        if(mLeft.getCols() != mRight.getCols() || mLeft.getRows() != mRight.getRows()){
            throw new IllegalArgumentException("Matrices must be the same size for addition.");
        }
        mResult = createMatrix(mLeft.nRows, mLeft.nCols);
        for(int r = 1; r <= mLeft.nRows; r++){
            for(int c = 1; c <= mLeft.nCols; c++){
                mResult.setElement(r, c, mLeft.getElement(r, c) + mRight.getElement(r, c));
            }
        }
        return mResult;
    }
    @Override
    public IMatrix Subtract(IMatrix iRight) throws IllegalArgumentException{
        return this.Add(iRight.ScalarMultiply(-1));
    }

    @Override
    public IMatrix Multiply(IMatrix iRight) throws IllegalArgumentException {
        AMatrix mLeft = this;
        AMatrix mRight = (AMatrix) iRight;
        AMatrix mResult = null;
        if(mLeft.getCols() != mRight.getRows()){
            throw new IllegalArgumentException("Matrices must share dimension to be multiply");
        }
        mResult = createMatrix(mLeft.nRows, mRight.nCols);
        double dSum;
        for(int r = 1; r <= mResult.nRows; r++){
            for(int c = 1; c <= mResult.nCols; c++){
                dSum = 0d;
                for(int v = 1; v <= mLeft.getRows(); v++){
                    dSum += mLeft.getElement(r, v) * mRight.getElement(v, c);
                }
                mResult.setElement(r, c, dSum);
            }
        }
        return mResult;
    }
    @Override
    public IMatrix ScalarMultiply(double dScalar) {
        AMatrix mLeft = this;
        AMatrix mResult = null;
        mResult = createMatrix(mLeft.nRows, mLeft.nCols);
        for(int r = 1; r <= mLeft.nRows; r++){
            for(int c = 1; c <= mLeft.nCols; c++){
                mResult.setElement(r, c, mLeft.getElement(r, c) * dScalar);
            }
        }
        return mResult;
    }

    @Override
    public IMatrix Scaling(double dX, double dY) {
        AMatrix mLeft = this;
        AMatrix mResult = null;
        mResult = createMatrix(mLeft.nRows, mLeft.nCols);
        for(int i = 1; i <= mLeft.nRows; i++){
            double x = mLeft.getElement(i, 1);
            double y = mLeft.getElement(i, 2);
            x *= dX;
            y *= dY;
            mResult.setElement(i, 1, x);
            mResult.setElement(i, 2, y);
        }
        return mResult;
    }

    @Override
    public IMatrix Translate(double dX, double dY) {
        AMatrix mLeft = this;
        AMatrix mResult = null;
        mResult = createMatrix(mLeft.nRows, mLeft.nCols);
        for(int i = 1; i <= mLeft.nRows; i++){
            double x = mLeft.getElement(i, 1);
            double y = mLeft.getElement(i, 2);
            x += dX;
            y += dY;
            mResult.setElement(i, 1, x);
            mResult.setElement(i, 2, y);
        }
        return mResult;
    }
    //Translate using homogeneous matrix
    public IMatrix TranslateT(double dX, double dY) {
        AMatrix mLeft = this;
        AMatrix mResult = null;
        //1. Create Matrix T
        AMatrix T = createMatrix(3, 3);
        for(int r = 1; r < 4; r++){
            for(int c = 1; c < 4; c++){
                if(r == c){
                    T.setElement(r, c, 1);
                }else{
                    T.setElement(r, c, 0);
                }
            }
        }
        T.setElement(3, 1, dX);
        T.setElement(3, 2, dY);
        //2. Make the mLeft homogeneous
        AMatrix H = createMatrix(3, 3);
        for(int r = 1; r < 4; r++){
            for(int c = 1; c < 4; c++){
                if(c < 3){
                    H.setElement(r, c, mLeft.getElement(r, c));
                }else{
                    H.setElement(r, c, 1);
                }
            }
        }
        //3. Dot
        mResult = (AMatrix) H.Multiply(T);
        return mResult;
    }
    //It is a passive transform not active transform
    @Override
    public IMatrix Rotate(double radians) {
        AMatrix mLeft = this;
        AMatrix mResult = null;
        mResult = createMatrix(mLeft.nRows, mLeft.nCols);
        for(int i = 1; i <= mLeft.nRows; i++){
            double x = mLeft.getElement(i, 1);
            double y = mLeft.getElement(i, 2);
            x = x * Math.cos(radians) + y * Math.sin(radians);
            y = -x * Math.sin(radians) + y * Math.cos(radians);
            mResult.setElement(i, 1, x);
            mResult.setElement(i, 2, y);
        }
        return mResult;
    }

    @Override
    public IMatrix Rotate(double radians, double dOx, double dOy) {
        AMatrix mLeft = this;
        AMatrix mResult = null;
        mResult = createMatrix(mLeft.nRows, mLeft.nCols);

        return  mResult;
    }


}
