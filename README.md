
1. 绘制图片的三种方式
a.
    ```java
        Bitmap bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getPath() + File.separator + "11.jpg");
        mImage.setImageBitmap(bitmap);
    ```
b. SurfaceView
c. 自定义 view

