package com.test.iwomag.android.pubblico.util;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

public class SendFile {

	/** 
     * 拍照获取图片 
     * type 1拍摄图片， 2拍摄视频
     */  
    public static String takePhoto(Activity context, int type) {  
    	String path = "";
    	//Uri photoUri = null;
        //执行拍照前，应该先判断SD卡是否存在  
        String SDState = Environment.getExternalStorageState();  
        if(SDState.equals(Environment.MEDIA_MOUNTED))  
        {  
            //Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//"android.media.action.IMAGE_CAPTURE"  
            Intent intent = null;
            if(type == 1){
            	intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            	path = FileCache.getInstance().CACHE_PATH + "/CAPTURE_" + System.currentTimeMillis() + ".jpg";
            }else if(type == 2){
            	intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            	path = FileCache.getInstance().CACHE_PATH + "/CAPTURE_" + System.currentTimeMillis() + ".mp4";
            }
            
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(path)));
            
            /*** 
             * 需要说明一下，以下操作使用照相机拍照，拍照后的图片会存放在相册中的 
             * 这里使用的这种方式有一个好处就是获取的图片是拍照后的原图 
             * 如果不实用ContentValues存放照片路径的话，拍照后获取的图片为缩略图不清晰 
             */  
            //ContentValues values = new ContentValues();    
            //photoUri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);    
            //intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, photoUri);  
            context.startActivityForResult(intent, 3);  
        }else{  
            Toast.makeText(context,"内存卡不存在", Toast.LENGTH_LONG).show();  
        }
        
        return path;
    } 
    
    /**
	 * 选择图片后，获取图片的路径
	 * 
	 * @param requestCode
	 * @param data
	 */
	public static String doPhoto(Activity activity, int requestCode, Intent data) {
		Uri photoUri = null;
		String picPath = "";
		if (requestCode == 2) // 从相册取图片，有些手机有异常情况，请注意
		{
			if (data == null) {
				Toast.makeText(activity, "选择图片文件出错", Toast.LENGTH_LONG).show();
				return null;
			}
			photoUri = data.getData();
			if (photoUri == null) {
				Toast.makeText(activity, "选择图片文件出错", Toast.LENGTH_LONG).show();
				return null;
			}
		}
		String[] pojo = { MediaStore.Images.Media.DATA };
		Cursor cursor = activity.managedQuery(photoUri, pojo, null, null, null);
		if (cursor != null) {
			int columnIndex = cursor.getColumnIndexOrThrow(pojo[0]);
			cursor.moveToFirst();
			picPath = cursor.getString(columnIndex);
			try  
            {  
                //4.0以上的版本会自动关闭 (4.0--14;; 4.0.3--15)  
                if(Integer.parseInt(Build.VERSION.SDK) < 14)  
                {  
                    cursor.close();  
                }  
            }catch(Exception e)  
            {  
                Logger.i("-----");
            }  
		}
		Logger.i("imagePath = " + picPath);
		return picPath;
	}
}
