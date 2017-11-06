package com.dong.panoramaplayer;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;

/**
 * author : DongMingXin
 * e-mail : sanjinmr@sina.com
 * time   : 2017/11/6
 * version: 1.0
 * desc   :
 */
public class Myglsurfaceview extends GLSurfaceView {

    private static final String TAG = Myglsurfaceview.class.getSimpleName();

    private GLRenderer mglRender;

    public Myglsurfaceview(Context context) {
        super(context);
    }

    public Myglsurfaceview(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        try {
            Log.d(TAG, "init");
            setEGLContextClientVersion(2);
            mglRender = new GLRenderer(context);
            setRenderer(mglRender);
            setRenderMode(RENDERMODE_WHEN_DIRTY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
