package com.dong.panoramaplayer;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * author : DongMingXin
 * e-mail : sanjinmr@sina.com
 * time   : 2017/11/6
 * version: 1.0
 * desc   : 实际第一次的调用顺序 onSurfaceCreated()——> onSurfaceChanged() ——> onDrawFrame()
 * 我们再在raw文件夹中创建两个文件，fragment_shader.glsl和vertex_shader.glsl,
 * 他们分别是片元着色器和顶点着色器的脚本，之前说的可编程管线，就是指OpenGL ES 2.0可以即时编译这些脚本，来实现丰富的功能
 * 两个文件的内容如下:
 vertex_shader.glsl
 attribute vec4 aPosition;
 void main() {
 gl_Position = aPosition;
 }
 vec4是一个包含4个浮点数（float，我们约定，在OpenGL中提到的浮点数都是指float类型）的向量，attribute表示变元,用来在Java程序和OpenGL间传递经常变化的数据，
 gl_Position 是OpenGL ES的内建变量，表示顶点坐标（xyzw，w是用来进行投影变换的归一化变量），我们会通过aPosition把要绘制的顶点坐标传递给gl_Position

 fragment_shader.glsl

 precision mediump float;
 void main() {
 gl_FragColor = vec4(0,0.5,0.5,1);
 }

 precision mediump float用来指定运算的精度以提高效率（因为运算量还是蛮大的），
 gl_FragColor 也是一个内建的变量，表示颜色，以rgba的方式排布，范围是[0,1]的浮点数

 */
public class GLRenderer implements GLSurfaceView.Renderer {

    private Context context;
    private int aPositionHandle;
    private int programId;
    private FloatBuffer vertexBuffer;

    // OpenGL ES工作在native层（C、C++），如果要传送数据，我们需要使用特殊的方法把数据复制过去。
    // 首先定义一个顶点数组，这是我们要绘制的三角形的三个顶点坐标（逆时针），三个浮点数分别代表xyz，因为是在平面上绘制，我们把z设置为0
    private final float[] vertexData = {
            0f,0f,0f,
            1f,-1f,0f,
            1f,1f,0f
    };


    public GLRenderer(Context context) {
        this.context = context;
        // 一个float是4个字节，ByteBuffer用来在本地内存分配足够的大小，
        // 并设置存储顺序为nativeOrder（关于存储序的更多资料可以在维基百科上找到），
        // 最后把vertexData放进去，当然，不要忘了设定索引位置vertexBuffer.position(0);
        vertexBuffer = ByteBuffer.allocateDirect(vertexData.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertexData);
        vertexBuffer.position(0);
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
        //GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
        // 使用readRawTextFile把文件读进来(后面会用来创建一个OpenGL ES程序)
        String vertexShader = ShaderUtils.readRawTextFile(context, R.raw.vertex_shader);
        String fragmentShader = ShaderUtils.readRawTextFile(context, R.raw.fragment_shader);
        programId = ShaderUtils.createProgram(vertexShader,fragmentShader);
        aPositionHandle= GLES20.glGetAttribLocation(programId,"aPosition");
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
        //GLES20.glViewport(0, 0, width, height);
    }

    /**
     * onDrawFrame() - Called for each redraw of the view.
     * 这个是主要的函数，我们的绘制部分就在这里，每一次绘制时这个函数都会被调用，
     * 如果之前设置了GLSurfaceView.RENDERMODE_CONTINUOUSLY，也就是说按照正常的速度，每秒这个函数会被调用60次，虽然我们还什么都没做
     * @param gl
     */
    @Override
    public void onDrawFrame(GL10 gl) {
        // 第一行用来清空颜色缓冲区和深度缓冲区
        GLES20.glClear( GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
        // 然后我们指定使用刚才创建的那个程序。
        GLES20.glUseProgram(programId);
        // GLES20.glEnableVertexAttribArray(aPositionHandle);的作用是启用顶点数组,aPositionHandle就是我们传送数据的目标位置。
        GLES20.glEnableVertexAttribArray(aPositionHandle);
        // GLES20.glVertexAttribPointer的原型是这样的：
        // glVertexAttribPointer(int indx,int size, int type,boolean normalized,int stride,java.nio.Buffer ptr)
        // stride表示步长，因为一个顶点三个坐标，一个坐标是float（4字节），所以步长是12字节
        // (当然，这个只在一个数组中同时包含多个属性时才有作用，例如同时包含纹理坐标和顶点坐标，在只有一种属性时（例如现在），和传递0是相同效果)
        GLES20.glVertexAttribPointer(aPositionHandle, 3, GLES20.GL_FLOAT, false,
                12, vertexBuffer);
        // 我们用GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);把三角形画出来
        // glDrawArrays的原型如下：public static native void glDrawArrays(int mode,int first,int count);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);
    }

}
