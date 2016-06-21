package com.android.iwo.media.view;


import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;

import android.os.Build;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.widget.ImageView;


public class ImageTouchView extends ImageView {
	 private PointF startPoint = new PointF();  
		private Matrix matrix = new Matrix();  
	private Matrix currentMaritx = new Matrix();  
	  private int mode = 0;  
	 private static final int DRAG = 1;
	  private static final int ZOOM = 2; 
		  private float startDis = 0;  
	  private PointF midPoint;
	  public ImageTouchView(Context context){    
	      super(context);    
		 }    
		
	  public ImageTouchView(Context context,AttributeSet paramAttributeSet){    
	      super(context,paramAttributeSet);    
	  }  
	    
		public boolean onTouchEvent(MotionEvent event) {  
	     switch (event.getAction() & MotionEvent.ACTION_MASK) {  
		    case MotionEvent.ACTION_DOWN:  
		       mode = DRAG;  
	      currentMaritx.set(this.getImageMatrix());
	      startPoint.set(event.getX(),event.getY());
		        break;  

	  case MotionEvent.ACTION_MOVE:
	       if (mode == DRAG) { 
		        float dx = event.getX() - startPoint.x;
	       float dy = event.getY() - startPoint.y;  
	          matrix.set(currentMaritx); 
	  matrix.postTranslate(dx, dy);  
	      
	     } else if(mode == ZOOM){  
	        float endDis = distance(event);
	        if(endDis > 10f){  
		            float scale = endDis / startDis;   
	          //Log.v("scale=", String.valueOf(scale));   
	            matrix.set(currentMaritx);  
	          matrix.postScale(scale, scale, midPoint.x, midPoint.y);  
		     }  
	          
	       }  
		
	    break;  
	        
	 case MotionEvent.ACTION_UP:  
	      mode = 0;  
		    break;  
		     
	  case MotionEvent.ACTION_POINTER_UP:  
	      mode = 0;  
		       break;  
		    
	 case MotionEvent.ACTION_POINTER_DOWN:  
	     mode = ZOOM;  
	     startDis = distance(event);  
		      
	   if(startDis > 10f){ 
		        midPoint = mid(event);  
	         currentMaritx.set(this.getImageMatrix());
		     }  
		       
	     break;  


		     }  
		    this.setImageMatrix(matrix);  
		   return true;  
	 }  
		 @TargetApi(Build.VERSION_CODES.ECLAIR)
		private static float distance(MotionEvent event){  
			   
			    float dx = event.getX(1) - event.getX(0);  
		      float dy = event.getY(1) - event.getY(0);  
			   return FloatMath.sqrt(dx*dx + dy*dy);  
			 }  
			
			 @TargetApi(Build.VERSION_CODES.ECLAIR)
			private static PointF mid(MotionEvent event){  
		       float midx = event.getX(1) + event.getX(0);  
		      float midy = event.getY(1) - event.getY(0);  
		       
		      return new PointF(midx/2, midy/2);  
		  }  
		

	

}
