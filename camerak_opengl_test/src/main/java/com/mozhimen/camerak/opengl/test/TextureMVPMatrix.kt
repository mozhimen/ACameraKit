package com.mozhimen.camerak.opengl.test

import android.graphics.Bitmap
import android.opengl.GLES11Ext
import android.opengl.GLES30
import android.opengl.GLUtils
import com.mozhimen.openglk.basic.utils.GLES30Util
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

/**
 * @ClassName Texture
 * @Description TODO
 * @Author Mozhimen / Kolin Zhao
 * @Date 2024/4/14 0:10
 * @Version 1.0
 */
class TextureMVPMatrix(bitmap: Bitmap) {
    //顶点着色器
    private val _strShaderVertex = """
        uniform mat4 uMVPMatrix;
        attribute vec4 aPosition;
        attribute vec2 aTexCoord;
        varying vec2 vTexCoord; 
        void main() {
            gl_Position = uMVPMatrix * aPosition;
            vTexCoord = aTexCoord;
        }
    """.trimIndent()

    //片元着色器
    private val _strShaderFragment = """
        precision mediump float;
        uniform sampler2D uSampler;
        varying vec2 vTexCoord;
        void main() {
            gl_FragColor = texture2D(uSampler,vTexCoord);
        }
    """.trimIndent()

    //顶点
    private val _vertexTexture = floatArrayOf(
        //坐标顶点       纹理坐标
        -1f, 1f, 0f, 0f, 0f,//左上角
        -1f, -1f, 0f, 0f, 1f,//左下角
        1f, 1f, 0f, 1f, 0f,//右上角
        1f, -1f, 0f, 1f, 1f//右下角
    )


    //VBO
    private var _vboIds = IntArray(1)

    private var _vertexBuffer: FloatBuffer
    private var _program: Int = 0
    private var _aPosition = 0
    private var _aTexCoord = 0
    private var _mvpMatrix = 0
    private var _uSampler = 0
    private var _textureId = 0

    init {
        _textureId = getTextureId()

        val allocateBuffer = ByteBuffer.allocateDirect(_vertexTexture.size * 4)
        allocateBuffer.order(ByteOrder.nativeOrder())
        _vertexBuffer = allocateBuffer.asFloatBuffer()
        _vertexBuffer.put(_vertexTexture)
        _vertexBuffer.position(0)

        //创建shader,并为其指定源码
        val shaderVertex = GLES30Util.loadShader(GLES30.GL_VERTEX_SHADER, _strShaderVertex)
        val shaderFragment = GLES30Util.loadShader(GLES30.GL_FRAGMENT_SHADER, _strShaderFragment)

        _program = GLES30.glCreateProgram()
        GLES30.glAttachShader(_program, shaderVertex)
        GLES30.glAttachShader(_program, shaderFragment)

        GLES30.glLinkProgram(_program)

        GLES30.glDeleteShader(shaderVertex)
        GLES30.glDeleteShader(shaderFragment)

        //生成vbo
        GLES30.glGenBuffers(1, _vboIds, 0)
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, _vboIds[0])
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, allocateBuffer.capacity(), allocateBuffer, GLES30.GL_STATIC_DRAW)

        //将数据传递给shader
        _aPosition = GLES30.glGetAttribLocation(_program, "aPosition")
        GLES30.glEnableVertexAttribArray(_aPosition)

        _aTexCoord = GLES30.glGetAttribLocation(_program, "aTexCoord")
        GLES30.glEnableVertexAttribArray(_aTexCoord)

        _mvpMatrix = GLES30.glGetUniformLocation(_program, "uMVPMatrix")
        _uSampler = GLES30.glGetUniformLocation(_program, "uSampler")

        GLES30.glVertexAttribPointer(_aPosition, 3, GLES30.GL_FLOAT, false, 0, 0)

        //unbind
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0)
    }

    fun draw(mvpM: FloatArray) {
        //使用program
        GLES30.glUseProgram(_program)
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, _vboIds[0])

        GLES30.glVertexAttribPointer(_aPosition, 3, GLES30.GL_FLOAT, false, 5 * Float.SIZE_BYTES, 0)
        GLES30.glVertexAttribPointer(_aTexCoord, 2, GLES30.GL_FLOAT, false, 5 * Float.SIZE_BYTES, 3 * Float.SIZE_BYTES)

        GLES30.glUniformMatrix4fv(_mvpMatrix, 1, false, mvpM, 0)

        GLES30.glActiveTexture(GLES30.GL_TEXTURE0)
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, _textureId)
        GLES30.glUniform1i(_uSampler, 0)

        //drawArray, 绘制三角形
        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP, 0, 4)

        //解绑VBO
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0)
    }

    fun release() {
        //program
        GLES30.glDeleteProgram(_program)

        //vbo
        GLES30.glDeleteBuffers(1, _vboIds, 0)
    }


    private fun getTextureId(): Int {
        val textureIds = IntArray(1)
        GLES30.glGenTextures(1, textureIds, 0)
        GLES30.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureIds[0])

        GLES30.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR)
        GLES30.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR)

        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0)
        return textureIds[0]
    }
}