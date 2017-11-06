package com.dong.panoramaplayer;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * author : DongMingXin
 * e-mail : sanjinmr@sina.com
 * time   : 2017/11/6
 * version: 1.0
 * desc   : 实际第一次的调用顺序 onSurfaceCreated()——> onSurfaceChanged() ——> onDrawFrame()
 */
public class GLRenderer implements GLSurfaceView.Renderer {

    private final Context context;

    public GLRenderer(Context context) {
        this.context = context;
    }

    /**
     * onSurfaceCreated() - Called once to set up the view's OpenGL ES environment.
     * 从名字可以看出，这个函数在Surface被创建的时候调用，每次我们将应用切换到其他地方，
     * 再切换回来的时候都有可能被调用，在这个函数中，我们需要完成一些OpenGL ES相关变量的初始化
     * @param gl
     * @param config
     */
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
    }

    /**
     * onSurfaceChanged() - Called if the geometry of the view changes, for example when the       device's screen orientation changes.
     * 每当屏幕尺寸发生变化时，这个函数会被调用（包括刚打开时以及横屏、竖屏切换），width和height就是绘制区域的宽和高
     * @param gl
     * @param width
     * @param height
     */
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
    }

    /**
     * onDrawFrame() - Called for each redraw of the view.
     * 这个是主要的函数，我们的绘制部分就在这里，每一次绘制时这个函数都会被调用，
     * 如果之前设置了GLSurfaceView.RENDERMODE_CONTINUOUSLY，也就是说按照正常的速度，每秒这个函数会被调用60次，虽然我们还什么都没做
     * @param gl
     */
    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
    }

}
