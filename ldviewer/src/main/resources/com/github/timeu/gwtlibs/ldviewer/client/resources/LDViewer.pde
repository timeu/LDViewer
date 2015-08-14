LDBrowser browser = null;

var eventHandler = {};

void api_addEventHandler(String handler,callback)
{
    eventHandler[handler] = callback;
}

Object[] api_getHighlightedDataPoints() {
    if (browser.highlightDataPoints != null) 
    	return browser.highlightDataPoints.toArray();
    else
       return null;
}

void api_clearEventHandlers()
{
   eventHandler = {};
}

void api_setData(int[] snps,float[][] r2,int start,int end) {
    browser.setData(snps,r2,start,end);
}   

void api_setSize(int width,boolean isDraw) {
    if (width==0)
    	return;
    browser.setSize(width,width,isDraw);
}

void api_setHighlightPosition(int position) {
    browser.setHighlightPosition(position);
}
    
void setup()
{
  int width=1024;
  int height=1024;
  int threshold = 0.3;
  browser = new LDBrowser(width,height,threshold);
  frameRate(1);
  size(width,height);
  background(0);
  smooth();
  noLoop();
}

void draw()
{
  background(255);
  noFill();
  browser.display();
}
    
class LDBrowser {
    QuadTree quadTree;
    ArrayList dataPoints;
    ArrayList filteredDataPoints;
    int[] snps = null;
    float separation = 0;
    int width = 100;
    int height = 100;
    int viewStart = 0;
    int viewEnd = 0;
    int zoomStart = 0;
    int zoomEnd = 0;
    float pxPerBp = 0;
    float threshold = 0.3;
    float maxHue = 60;
    int offsetLegendX = 40;
    int offsetLegendY = 90;
    int snpPosBandHeight = 50;
    int scalerLegendWidth = 50;
    color bgrColor = null;
    color highlightBgrColor = #C0C0C0;
    ArrayList highlightDataPoints = new ArrayList();
    int highlightPosition = null;
    float scaleFactor = 1/sqrt(2);
    float rotationFactor  = radians(-45);
    PGraphics pg = null;
    PGraphics pgSNPs = null;
    
    public LDBrowser(width,height,threshold) {
        this.threshold = threshold;
        colorMode(HSB, 360, 100, 100,1);
        bgrColor = color(240,100,100);
        Bounds bounds = new Bounds(0,0,width,height);
        quadTree = new QuadTree(bounds,9999,50);
    }
    
    
    
    public void setSize(int width,int height,boolean isDraw) {
        if (this.width == width && this.height == height)
            return;
        size(width,width/2+snpPosBandHeight);
        pg = null;
        pgSNPs = null; 
    	this.width=width;
        this.height=height;
        initPxPerUnit();
        quadTree.clear();
        Bounds bounds = new Bounds(0,0,width,height);
        quadTree = new QuadTree(bounds,9999,50);
        if (filteredDataPoints != null) {
        	quadTree.insert(filteredDataPoints);
        }
        if (isDraw) {
        	draw();
        }
    }
    
    public void setHighlightDataPoints(ArrayList points) {
        highlightDataPoints = points;
        highlightPosition = null;
    }
    
    public void setHighlightPosition(int position) {
         if (position == null) {
              highlightDataPoints = new ArrayList();
              highlightPosition = null;
              return;
         }
         if (filteredDataPoints == null || highlightPosition == position)
             return;
         highlightPosition = position;
         ArrayList points = new ArrayList();
         for (int i = 0; i < filteredDataPoints.size(); i++) {
            DataPoint dataPoint = (DataPoint)filteredDataPoints.get(i);
            if (dataPoint.getPosX() == position || dataPoint.getPosY() == position)
                points.add(dataPoint);
        }
        highlightDataPoints = points;
        draw();
    }
    
    public ArrayList getHighlightDataPoints() {
        return highlightDataPoints;
    }
    
    public void setData(int[] snps,float[][] data, int viewStart,int viewEnd) {
       this.snps = snps;
       if (viewStart != null) 
           this.viewStart = viewStart;
       else 
           this.viewStart = snps[0];
        if (viewEnd != null)
            this.viewEnd = viewEnd;
        else 
           this.viewEnd = snps[snps.length-1];
        initPxPerUnit();
        pg = null;
        pgSNPs = null;
        dataPoints = new ArrayList(); 
        filteredDataPoints = new ArrayList();
        for (int i = 0; i <data.length ; i++) {
            for (int j = 0; j < data[i].length; j++) {
              DataPoint dataPoint = new DataPoint(0+j*separation,0+i*separation,separation,snps[j],snps[i],data[i][j]);
              dataPoints.add(dataPoint);
              if (data[i][j] > threshold)
                  filteredDataPoints.add(dataPoint);
            }
        }
        quadTree.clear();
        quadTree.insert(filteredDataPoints);
        draw()
    }
    
    public DataPoint getDataPointFromMousePos() {
        float[] mousePos  = getTransformedMousePos();
        ArrayList dataPointsToSearch = quadTree.retrieve(mousePos[0],mousePos[1]);
        for (int i = 0; i < dataPointsToSearch.size(); i++) {
           DataPoint dataPoint = (DataPoint)dataPointsToSearch.get(i);
           if (isMouseInDataPoint(dataPoint,mousePos)) {
               return dataPoint;
           }
        }
        return null;
    }
    
    public boolean isMouseInDataPoint(Datapoint point,int[] mousePos) {
       if (point.getX() <= mousePos[0] && (point.getX() + separation) >= mousePos[0]
            && point.getY() <= mousePos[1] && (point.getY() + separation) >= mousePos[1]    ) {
            return true;
        }
        return false;
    }
    
    public float[] getTransformedMousePos() {
        float[] mousePos = new int[2];
        int r = -rotationFactor;
        int newMouseY = mouseY - snpPosBandHeight;
        int transMouseX = mouseX * Math.cos(r) - newMouseY * Math.sin(r);
        int transMouseY = newMouseY * Math.cos(r) + mouseX * Math.sin(r);
        transMouseX = transMouseX * 1/scaleFactor;
        transMouseY = transMouseY * 1/scaleFactor;
        mousePos[0] = transMouseX;
        mousePos[1] = transMouseY;
        return mousePos;
    }
    
    private void setupBackBuffer() {
        if (pg == null) {
           pg = createGraphics(width, height, P2D);
           pg.beginDraw();
              pg.colorMode(HSB, 360, 100, 100,1);
              createBackground();
              createR2Boxes();
           pg.endDraw();
        }
        if (pgSNPs == null) {
            pgSNPs = createGraphics(width, snpPosBandHeight, P2D);
            pgSNPs.beginDraw();
               pgSNPs.colorMode(HSB, 360, 100, 100,1);
               createSNPsBand();
            pgSNPs.endDraw();
           
        }
    }
    
    private void createBackground() {
        pg.fill(bgrColor,0.4);
        pg.noStroke();
        pg.rect(0,0,width,height);
    }
    private void createR2Boxes() {
        for (int i = 0; i < filteredDataPoints.size(); i++) {
            DataPoint dataPoint = (DataPoint)filteredDataPoints.get(i);
            dataPoint.createR2Boxes(threshold,maxHue,pg);
        }
    }
    
     private void initPxPerUnit() {
       oldseparation = separation;
       if (snps != null)
       	   separation = width/(snps.length);
       if (this.zoomEnd - this.zoomStart != 0)
             this.pxPerBp  = (float)this.width / (this.zoomEnd - this.zoomStart);
       if (this.viewEnd - this.viewStart != 0)
           this.pxPerBp = (float)this.width / (this.viewEnd - this.viewStart);
       if (oldseparation != separation && dataPoints != null) {
           for (int i = 0;i<dataPoints.size();i++) {
               DataPoint dataPoint = (DataPoint)dataPoints.get(i);
               dataPoint.updateCoord(separation);
           }
       }
    }
    
    public void display() {
        if (filteredDataPoints == null)
            return;
        setupBackBuffer();
        float alpha = 1;
        pushMatrix();
        translate(0,snpPosBandHeight);
        scale(scaleFactor);
        rotate(rotationFactor);
        image(pg);
        if (highlightDataPoints.size() >0) {
            alpha = 0.4;                    
        }
        if (highlightDataPoints.size() >0) {
             displayOverlay();
             for (int i = 0; i < highlightDataPoints.size(); i++) {
                dataPoint = (DataPoint)highlightDataPoints.get(i);
                dataPoint.display(threshold,maxHue,1);                 
             }
        }
        //quadTree.display();
        popMatrix();
        displaySnpPosBand();
        displayLegend();
    }
    
    private void createSNPsBand() {
        pgSNPs.noStroke();
        pgSNPs.fill(0,0,100);
        pgSNPs.rect(0,0,width,snpPosBandHeight);
        pgSNPs.stroke(0);
        pgSNPs.line(0,0,width,0);
        for (i=0;i<snps.length;i++) {
            int startIndex = snps[i]-viewStart;
            pgSNPs.line(startIndex*pxPerBp,0,0+i*separation,snpPosBandHeight);
        }
    }
    
    private void displaySnpPosBand() {
        setupBackBuffer();
        image(pgSNPs);
        if (highlightDataPoints.size() >0) {
            noStroke();
            fill(255,0.7);
            rect(0,0,width,snpPosBandHeight);
            stroke(0,1);
            if (highlightPosition != null) {
                int i = binarySearch(snps,highlightPosition);
     line((highlightPosition-viewStart)*pxPerBp,0,0+i*separation,snpPosBandHeight)
            }
            else {
                 DataPoint point = (DataPoint)highlightDataPoints.get(0);
                 line((point.getPosX() - viewStart)*pxPerBp,0,point.getX(),snpPosBandHeight);
                 line((point.getPosY() - viewStart)*pxPerBp,0,point.getY(),snpPosBandHeight);
            }
        }
    }
    
    private int binarySearch(arr, key){
        var left = 0;
        var right = arr.length - 1;
        while (left <= right){
           var mid = parseInt((left + right)/2);
           if (arr[mid] == key)
               return mid;
           else if (arr[mid] < key)
              left = mid + 1;
           else
              right = mid - 1;
        }
        return arr.length;
     }
    
    
    private void displayOverlay() {
       noStroke();
       fill(255,0.7);
       rect(0,0,width,height);
    }
    
    
    private void displayLegend() {
        int calOffsetLegendY = snpPosBandHeight +  offsetLegendY;
        int legendHeight= height/2-calOffsetLegendY;
        int legendWidth= height/scalerLegendWidth;
    
        int firstSectionHeight = legendHeight-legendHeight*threshold;
        int tickDistance = legendHeight/10;
        stroke(0);
        noFill();
        rect(offsetLegendX,calOffsetLegendY,legendWidth,legendHeight);
        noStroke();
        color top = color(0,100,100);
        color bottom = color(60,100,100);
        for (int i =0;i<=firstSectionHeight;i++) {
            color legendColor = lerpColor(top, bottom, i/firstSectionHeight);
            stroke(legendColor);
      line(offsetLegendX+0.5,calOffsetLegendY+0.5+i,offsetLegendX+legendWidth-1,calOffsetLegendY+0.5+i);
        }
        
        fill(bgrColor);
        noStroke();
        int posYForSecondHalf = calOffsetLegendY + firstSectionHeight;
        rect(offsetLegendX+0.5,posYForSecondHalf,legendWidth-0.5,( height/2+0.5-posYForSecondHalf));
        
        textAlign(LEFT,CENTER);
        for (int i = 0; i<=10;i++) {
            stroke(0);
            line(offsetLegendX+legendWidth-legendWidth/3,calOffsetLegendY + tickDistance*i,offsetLegendX+legendWidth,calOffsetLegendY + tickDistance*i);
            text((1-i/10),offsetLegendX+legendWidth+5,calOffsetLegendY + tickDistance*i);
        }
    
       // display VALUE
       if (highlightDataPoints != null && highlightDataPoints.size() == 1) {
            DataPoint dataPoint = (DataPoint) highlightDataPoints.get(0);
            float valuePosY = calOffsetLegendY + (1-dataPoint.getR2())*legendHeight;
            line(offsetLegendX-legendWidth/3,valuePosY,offsetLegendX+legendWidth/3,valuePosY);
            textAlign(RIGHT,CENTER);
            text(dataPoint.getR2(),offsetLegendX-legendWidth/3-2,valuePosY);            
       }
    }
    
}
    
class DataPoint {
   float x,y,separation;
   int posX,posY;
   float r2;   
    
  
 public DataPoint(float x,float y,float separation,int posX,int posY,float r2) {
        this.x = x;
        this.separation = separation;
        this.y = y;
        this.posX = posX;
        this.posY = posY;
        this.r2 = r2;
   }
    
   public void display(float threshold,float maxHue,float alpha) {
       noStroke();
       colorMode(HSB, 360, 100, 100,1);
       float hue = calculateHue(threshold,maxHue);
       fill(hue,100,100,alpha);
       rect(x,y,separation,separation);
   }
    
    public void createR2Boxes(float threshold,float maxHue, PGraphics pg) {
        pg.noStroke();
        pg.colorMode(HSB, 360, 100, 100,1);
        float hue = calculateHue(threshold,maxHue);
        pg.fill(hue,100,100,1);
        pg.rect(x,y,separation,separation);
    }
    
    private float calculateHue(float threshold,float maxHue) {
         float hue = (1 - (r2 - threshold)/(1-threshold))*maxHue;
         if (hue < 0)
            hue=0;
         return hue;
    }
    
    public float getR2() {
       return r2;
    }
    
    public float getX() {
        return x;
    }
    public float getY() {
        return y;
    }
    
    public int getPosY() {
        return posY;
    }
    public int getPosX() {
        return posX;
    }
    
    public float getSeparation() {
        return separation;
    }
    
    public void updateCoord(float separation) {
        if(this.separation != separation) {
            if (this.separation != 0) {
            	this.x = this.x/this.separation*separation;
            	this.y = this.y/this.separation*separation;
            }
            else {
                this.x = this.x*separation;
            	this.y = this.y*separation;
            }
            this.separation = separation;
        }
    }
    
 }
    
 void mouseMoved() {
    ArrayList points = new ArrayList();
    if (mouseX < 0 || mouseX > width || mouseY < 0 || mouseY > height) {
        browser.setHighlightDataPoints(points);
        if (eventHandler['unhighlightEvent'] != null)
        	eventHandler['unhighlightEvent']();
        draw();
    }
    DataPoint point = browser.getDataPointFromMousePos();
    
    if (browser.getHighlightDataPoints() != null && browser.getHighlightDataPoints().size() == 1
        && ((DataPoint)browser.getHighlightDataPoints().get(0)) == point)   
        return;

    if (point != null)
       points.add(point);
    if (points.size() == 1) {
       if (eventHandler['highlightEvent'] != null)
        	eventHandler['highlightEvent'](point);
    }
    else {
       if (eventHandler['unhighlightEvent'] != null && browser.getHighlightDataPoints() != null && browser.getHighlightDataPoints().size() == 1)
        	eventHandler['unhighlightEvent']();
    }
    browser.setHighlightDataPoints(points);
    draw();
}

void mouseReleased()
{
	if (mouseButton == CENTER) 
	{
		 if (eventHandler['middleMouseClickEvent'] != null)
       	     eventHandler['middleMouseClickEvent']();
	} 
}
    
class QuadTree {
    Node root;
    int maxDepth = 4;
    
    public QuadTree(Bounds bounds,int maxDepth,int maxChildren) {
        root = new Node(bounds,0,maxDepth,maxChildren);
    }
    
    public void insert(ArrayList dataPoints) {
        for (int i =0;i<dataPoints.size();i++) {
            insertSingle((DataPoint)dataPoints.get(i));
        }   
    }
    public void insertSingle(DataPoint dataPoint) {
        root.insert(dataPoint);
    }
    
    public void clear() {
        this.root.clear();
    }
    
    public ArrayList retrieve(float x, float y) {
        return this.root.retrieve(x,y);
    }
    
    public void display() {
        root.display();
    }
}
    
class Node {
    
    Bounds bounds = null;
    Node[] nodes = null;
    ArrayList dataPoints = new ArrayList();
    ArrayList dataPointsPartial = new ArrayList();
    int depth,maxChildren,maxDepth;
    
    public Node(Bounds bounds,int depth,int maxDepth,int maxChildren) {
        this.bounds = bounds;
        this.depth = depth;
        this.maxChildren = maxChildren;
        this.maxDepth = maxDepth;
    }
    
     private void subdivide() {
        int newDepth = this.depth +1 ;
        Bounds[] subBounds = bounds.quarter();
        nodes  = new Node[4];
        for (int i=0;i<4;i++) {
           nodes[i] = new Node(subBounds[i],newDepth,maxDepth,maxChildren);    
        }
    }
    
    public void insert(DataPoint dataPoint) {
        if (this.nodes != null) {
            int index = this.findIndex(dataPoint.getX(),dataPoint.getY());
            Node node = nodes[index];
            if (node.bounds.pointInside(dataPoint)) {
                nodes[index].insert(dataPoint);
            }
            else {
                this.dataPointsPartial.add(dataPoint);
            }
            return;
        }
        
        if (!(this.depth >= this.maxDepth) && dataPoints.size() >= this.maxChildren) {
            this.subdivide();
            insert(dataPoint);
        }
        else {
            dataPoints.add(dataPoint);
        }
    }
    
    
    
    private int findIndex(float x, float y) {
        boolean left = !(x >= bounds.x + bounds.width/2);
        boolean top = !(y >= bounds.y + bounds.height/2);
        int index = 0;
        if (left) {
             if (!top) {
                 index = 1;
             }
        }
        else {
            if (top)
                index = 2;
            else 
                index = 3;
        }
        return index;
    }
   
    public ArrayList retrieve(float x, float y) {
        ArrayList list = new ArrayList();
        if (nodes != null) {
            int index  = findIndex(x,y);
            list.addAll(nodes[index].retrieve(x,y));
        }
               
        list.addAll(dataPointsPartial);
        list.addAll(dataPoints);
        return list;
    }
    
    public void clear() {
        this.dataPointsPartial.clear();
        this.dataPoints.clear();
        if (nodes != null) {
            for (int i =0;i<nodes.length;i++) {
                nodes[i].clear();
            }
            nodes = null;
        }
    }
    
    public void display() {
        bounds.display();
        if (nodes != null) {    
            for (int i =0;i<nodes.length;i++) {
                 nodes[i].display();
            }
        }
    }
}
    
class Bounds {
    float x,y,width,height;
    
    public Bounds(float x,float y,float width,float height) {
        this.width=width;
        this.height = height;
        this.x = x;
        this.y = y;
    }
    
    
    
    public boolean pointInside(DataPoint point) {
       return point.getX() >= this.x &&
              point.getX() + point.getSeparation() <= this.x + this.width &&
              point.getY() >= this.y && 
              point.getY() + point.getSeparation() <= this.y + this.height;
    }
    
    public Bounds[] quarter() {
        float sub_width = this.width/2;
        float sub_height = this.height/2;
        Bounds[] bounds = new Bounds[4];
        bounds[0] = new Bounds(this.x,this.y,sub_width,sub_height);
        bounds[1] = new Bounds(this.x,this.y+sub_height,sub_width,sub_height);
        bounds[2] = new Bounds(this.x+sub_width,this.y,sub_width,sub_height);
        bounds[3] = new Bounds(this.x+sub_width,this.y+sub_height,sub_width,sub_height);
        return bounds;
    }
    
    public void display() {
        stroke(0);
        noFill();
        rect(x,y,width,height);
    }
}