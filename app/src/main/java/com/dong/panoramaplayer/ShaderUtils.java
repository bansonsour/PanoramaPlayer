package com.dong.panoramaplayer;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static android.content.ContentValues.TAG;

/**
 * author : DongMingXin
 * e-mail : sanjinmr@sina.com
 * time   : 2017/11/6
 * version: 1.0
 * desc   :
 */
public class ShaderUtils {

    /**
     * 读取raw中的文本文件，并且以String的形式返回
     * @param context
     * @param resId
     * @return
     */
    public static String readRawTextFile(Context context, int resId) {
        InputStream inputStream = context.getResources().openRawResource(resId);
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            reader.close();
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 读取文件应该好理解，创建程序就比较复杂了，具体的步骤是这样的，我们先看创建程序之前要做的事情：
     1. 创建一个新的着色器对象
     2. 上传和编译着色器代码，就是我们之前读进来的String
     3. 读取编译状态（可选）

     shaderType用来指定着色器类型,取值有GLES20.GL_VERTEX_SHADER和GLES20.GL_FRAGMENT_SHADER，
     source就是刚才读入的代码，如果创建成功，那么shader会是一个非零的值，
     我们用GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
     来获取编译的状态，如果创建失败，就删除这个着色器：GLES20.glDeleteShader(shader);
     * @param shaderType
     * @param source
     * @return
     */
    public static int loadShader(int shaderType, String source) {
        int shader = GLES20.glCreateShader(shaderType);
        if (shader != 0) {
            GLES20.glShaderSource(shader, source);
            GLES20.glCompileShader(shader);
            int[] compiled = new int[1];
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
            if (compiled[0] == 0) {
                Log.e(TAG, "Could not compile shader " + shaderType + ":");
                Log.e(TAG, GLES20.glGetShaderInfoLog(shader));
                GLES20.glDeleteShader(shader);
                shader = 0;
            }
        }
        return shader;
    }

    /**
     * 我们先创建顶点着色器和片元着色器，然后用GLES20.glCreateProgram()创建程序，
     * 同样地，如果创建成功，会返回一个非零的值，我们用GLES20.glAttachShader(program, shaderID)
     * 这个函数把程序和着色器绑定起来，然后用GLES20.glLinkProgram(program)链接程序（编译链接，好有道理的样子。。）
     * GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);和之前的类似，是用来获取链接状态的。
     * @param vertexSource
     * @param fragmentSource
     * @return
     */
    public static int createProgram(String vertexSource, String fragmentSource) {
        // 我们先创建顶点着色器和片元着色器
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource);
        if (vertexShader == 0) {
            return 0;
        }
        int pixelShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource);
        if (pixelShader == 0) {
            return 0;
        }
        // 然后用GLES20.glCreateProgram()创建程序.同样地，如果创建成功，会返回一个非零的值
        int program = GLES20.glCreateProgram();
        if (program != 0) {
            // 我们用GLES20.glAttachShader(program, shaderID)这个函数把程序和着色器绑定起来
            GLES20.glAttachShader(program, vertexShader);
            checkGlError("glAttachShader");
            GLES20.glAttachShader(program, pixelShader);
            checkGlError("glAttachShader");
            // 然后用GLES20.glLinkProgram(program)链接程序（编译链接，好有道理的样子。。）
            GLES20.glLinkProgram(program);
            int[] linkStatus = new int[1];
            // GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);和之前的类似，是用来获取链接状态的。
            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
            if (linkStatus[0] != GLES20.GL_TRUE) {
                Log.e(TAG, "Could not link program: ");
                Log.e(TAG, GLES20.glGetProgramInfoLog(program));
                GLES20.glDeleteProgram(program);
                program = 0;
            }
        }
        return program;
    }

    /**
     * 打印错误日志的功能函数
     * @param label
     */
    public static void checkGlError(String label) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(TAG, label + ": glError " + error);
            throw new RuntimeException(label + ": glError " + error);
        }
    }
}
