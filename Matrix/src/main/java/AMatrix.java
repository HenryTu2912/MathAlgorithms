public abstract class AMatrix implements IMatrix{
    private int nRows;
    private int nCols;
    public int getRows() {return nRows;}
    public int getCols() {return nCols;}
    public void setRows(int nRows) {this.nRows = nRows;}
    public void setCols(int nCols) {this.nCols = nCols;}
    protected abstract double getElement(int nRow, int nCol);
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
    public IMatrix Add(IMatrix mRight) throws IllegalArgumentException{
        AMatrix mLeftOp = this;
        AMatrix mRightOp = (AMatrix) mRight;
        AMatrix mRs = null;
        if(mLeftOp.getCols() != mRightOp.getCols() || mLeftOp.getRows() != mRightOp.getRows()){
            throw new IllegalArgumentException("Matrices must be the same size for addition.");
        }
        mRs = createMatrix(mLeftOp.nRows, mRightOp.nCols);
        for(int r = 1; r <= mLeftOp.nRows; r++){
            for(int c= 1; c <= mLeftOp.nCols; c++){
                mRs.setElement(r, c, mLeftOp.getElement(r, c) + ((AMatrix) mRight).getElement(r, c));
            }
        }
        return mRs;
    }
    public IMatrix Subtract(IMatrix mRight) throws IllegalArgumentException{
        return this.Add(mRight.ScalarMultiply(-1));
    }
    public IMatrix Multiply(IMatrix mRight) throws IllegalArgumentException{
        AMatrix mLeftOp = this;
        AMatrix mRightOp = (AMatrix) mRight;
        AMatrix mRs = null;
        if(mLeftOp.getCols() != mRightOp.getRows()){
            throw new IllegalArgumentException("Matrices must share dimension to ben multiply");
        }
        mRs = createMatrix(mLeftOp.nRows, mRightOp.nCols);
        double dSum;
        for(int r = 1; r <= mRs.getRows(); r++){
            for(int c = 1; c <= mRs.getCols(); c++){
                dSum = 0;
                for(int v = 1; v <= mLeftOp.getCols(); v++){
                    dSum += mLeftOp.getElement(r, v) * mRightOp.getElement(v, c);
                }
                mRs.setElement(r, c, dSum);
            }
        }
        return mRs;
    }
    @Override
    public IMatrix ScalarMultiply(double dScalar){
        AMatrix mLeftOp = this;
        AMatrix mRs = null;
        mRs = createMatrix(mLeftOp.nRows, mLeftOp.nCols);
        for(int r = 1; r <= mLeftOp.nRows; r++){
            for(int c = 1; c <= mLeftOp.nCols; c++){
                mRs.setElement(r, c, mLeftOp.getElement(r, c) * dScalar);
            }
        }
        return mRs;
    }
    @Override
    public IMatrix Scaling(double dX, double dY) {
        AMatrix mLeftOp = this;
        AMatrix mRs = null;
        mRs = createMatrix(mLeftOp.nRows, mLeftOp.nCols);
        for(int i = 1; i <= mLeftOp.nRows; i++){
            double x = mLeftOp.getElement(i, 1);
            double y = mLeftOp.getElement(i, 2);
            x *= dX;
            y *= dY;
            mRs.setElement(i, 1, x);
            mRs.setElement(i, 2, y);
        }
        return mRs;
    }
    @Override
    public IMatrix Translate(double dX, double dY){
        AMatrix mLeftOp = this;
        AMatrix mRs = createMatrix(mLeftOp.nRows, mLeftOp.nCols);
        for(int i = 1; i <= mLeftOp.nRows; i++){
            double x = mLeftOp.getElement(i, 1);
            double y = mLeftOp.getElement(i, 2);
            x += dX;
            y += dY;
            mRs.setElement(i, 1, x);
            mRs.setElement(i, 2, y);
        }
        return mRs;
    }
    public AMatrix identityMatrix(int number){
        AMatrix result = createMatrix(number, number);
        for(int i = 1; i <= number; i++){
            for(int j = 1; j <= number; j++){
                if(i == j){
                    result.setElement(i, j, 1);
                } else {
                    result.setElement(i, j, 0);
                }
            }
        }
        return result;
    }
    public AMatrix makeHomogenuous(AMatrix matrix){
        AMatrix homo = createMatrix(matrix.nRows, matrix.nCols+1);
        for(int i = 1; i <= matrix.nRows; i++){
            for(int j = 1; j <= matrix.nCols; j++){
                if(j < matrix.nCols){
                    homo.setElement(i, j, getElement(i,j));
                } else {
                    homo.setElement(i, j, 1);
                }
            }
        }
        return homo;
    }
    public IMatrix TranslateMultiplication(double dX, double dY){
        AMatrix mLeftOp = this;
        AMatrix mRs = null;
        //1. Create matrix T
        AMatrix T = identityMatrix(3);
        T.setElement(3, 1, dX);
        T.setElement(3, 2, dY);
        //2. Make the mLeftOp homogenous
        mLeftOp = makeHomogenuous(mLeftOp);
        //3. Dot
        mRs = (AMatrix) mLeftOp.Multiply(T);
        return mRs;
    }
    @Override
    public IMatrix Rotate(double radians){
        AMatrix mLeftOp = this;
        AMatrix mRs = null;
        mRs = createMatrix(mLeftOp.nRows, mLeftOp.nCols);
        for(int i = 1; i <= mLeftOp.nRows; i++){
            double x = getElement(i, 1);
            double y = getElement(i, 2);
            x = x*Math.cos(radians) + y*Math.sin(radians);
            y = -x*Math.sin(radians) + y*Math.cos(radians);
            mRs.setElement(i, 1, x);
            mRs.setElement(i, 2, y);
        }
        return mRs;
    }
    public IMatrix leastSquare(int m){
        //Confirm matrix has 2 cols
        if(this.nCols != 2){
            throw new IllegalArgumentException("Invoking matrix must be a matrix of data point (col = 2)");
        }
        if(this.nRows < m+1){
            throw new IllegalArgumentException(String.format("Invoking matrix must be a matrix with at least %d data points", m+1));
        }
        if(m < 0){
            throw new IllegalArgumentException("m must be greater than -1");
        }
        int nXSums = 2*m+1;
        int nYSums = m+1;
        double[] xSums = new double[nXSums];
        for(int i = 0; i < nXSums; i++){ //i is the power
            for(int k = 1; k <= this.nRows; k++){ //k is the sample index
                xSums[i] += Math.pow(this.getElement(k, 1), i);
            }
        }
        double[] ySums = new double[nYSums];
        for(int i = 0; i < nYSums; i++){
            for(int k = 1; k <= this.nRows; k++){
                ySums[i] += Math.pow(this.getElement(k, 1), i) * this.getElement(k, 2);
            }
        }
        //Generate matrix for G-J-E
        AMatrix mRs = createMatrix(m+1, m+2);
        for(int r = 1; r <= mRs.nRows; r++){
            for(int c = 1; c <= mRs.nCols; c++){
                if(c == mRs.nCols){
                    mRs.setElement(r, c, ySums[r-1]);
                } else {
                    mRs.setElement(r, c, xSums[r+c-2]);
                }
            }
        }
        return mRs.gaussJordanElimination();
    }
    @Override
    public AMatrix gaussJordanElimination(){
        AMatrix mRs = this.copyMatrix();
        if(this.nRows != this.nCols - 1){
            throw new IllegalArgumentException("Must be an augmented matrix");
        }
        for(int i = 1; i <= mRs.nRows; i++){
            mRs.systemSolvable(i);
            double dPivot = mRs.getElement(i, i);
            //Make the element at pivot is 1
            for(int j = 1; j <= mRs.nCols; j++){
                mRs.setElement(i, j, mRs.getElement(i, j)/dPivot);
            }
            //Make the rest in the same cols to 0
            for(int k = 1; k <= mRs.nRows; k++){
                if(k != i){
                    double dFactor = -1 * mRs.getElement(k, i);
                    for(int j = 1; j <= mRs.nCols; j++){
                        double dVal = mRs.getElement(k, j) + mRs.getElement(i, j)*dFactor;
                        mRs.setElement(k, j, dVal);
                    }
                }
            }
        }
        return mRs;
    }
    private void systemSolvable(int nRows){
        double dPivot = this.getElement(nRows,nRows);
        int nNextRow = nRows + 1;
        while(dPivot == 0 && nNextRow <= this.nRows){
            dPivot = this.getElement(nNextRow, nRows);
            if(dPivot != 0){
                //Swap
                double dTemp = 0;
                for(int j = 1; j <= this.nCols; j++){
                    dTemp = this.getElement(nRows, j);
                    this.setElement(nRows, j, this.getElement(nNextRow, j));
                    this.setElement(nNextRow, j, dTemp);
                }
            } else {
                nNextRow ++;
            }
        }
        if(dPivot == 0){
            throw new ArithmeticException("Not solvable");
        }
    }
}
