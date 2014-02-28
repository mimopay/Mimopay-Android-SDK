package com.bayninestudios.androidogremesh;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

import org.xmlpull.v1.XmlPullParserException;

import android.content.res.XmlResourceParser;
import android.util.Log;

public class DrawModel
{
    private FloatBuffer mVertexBuffer;
    private FloatBuffer mColorBuffer;
    private FloatBuffer mNormalBuffer;
    private ShortBuffer mIndexBuffer;
    private int faceCount = 0;

    public DrawModel(XmlResourceParser xrp)
    {
        float[] coords = null;
        float[] colcoords = null;
        short[] icoords = null;
        float[] ncoords = null;
        int vertexIndex = 0;
        int colorIndex = 0;
        int faceIndex = 0;
        int normalIndex = 0;
        try
        {
            while (xrp.getEventType() != XmlResourceParser.END_DOCUMENT)
            {
                if (xrp.getEventType() == XmlResourceParser.START_TAG)
                {
                    String s = xrp.getName();
                    if (s.equals("faces"))
                    {
                        int i = xrp.getAttributeIntValue(null, "count", 0);
                        // now we know how many faces, so we know how large the
                        // triangle array should be
                        faceCount = i * 3; // three vertexes per face v1,v2,v3
                        icoords = new short[faceCount];
                    }
                    if (s.equals("geometry"))
                    {
                        int i = xrp.getAttributeIntValue(null, "vertexcount", 0);
                        // now we know how many vertexes, so we know how large
                        // the vertex, normal, texture and color arrays should be
                        // three vertex attributes per vertex x,y,z
                        coords = new float[i * 3]; 
                        // three normal attributes per vertex x,y,z
                        ncoords = new float[i * 3];
                        // four color attributes per vertex r,g,b,a
                        colcoords = new float[i * 4];
                    }
                    if (s.equals("position"))
                    {
                        float x = xrp.getAttributeFloatValue(null, "x", 0);
                        float y = xrp.getAttributeFloatValue(null, "y", 0);
                        float z = xrp.getAttributeFloatValue(null, "z", 0);
                        if (coords != null)
                        {
                            coords[vertexIndex++] = x;
                            coords[vertexIndex++] = y;
                            coords[vertexIndex++] = z;
                        }
                    }
                    if (s.equals("normal"))
                    {
                        float x = xrp.getAttributeFloatValue(null, "x", 0);
                        float y = xrp.getAttributeFloatValue(null, "y", 0);
                        float z = xrp.getAttributeFloatValue(null, "z", 0);
                        if (ncoords != null)
                        {
                            ncoords[normalIndex++] = x;
                            ncoords[normalIndex++] = y;
                            ncoords[normalIndex++] = z;
                        }
                    }
                    if (s.equals("face"))
                    {
                        short v1 = (short) xrp.getAttributeIntValue(null, "v1", 0);
                        short v2 = (short) xrp.getAttributeIntValue(null, "v2", 0);
                        short v3 = (short) xrp.getAttributeIntValue(null, "v3", 0);
                        if (icoords != null)
                        {
                            icoords[faceIndex++] = v1;
                            icoords[faceIndex++] = v2;
                            icoords[faceIndex++] = v3;
                        }
                    }
                    if (s.equals("colour_diffuse"))
                    {
                        String colorVal = (String) xrp.getAttributeValue(null, "value");
                        String[] colorVals = colorVal.split(" ");
                        colcoords[colorIndex++] = Float.parseFloat(colorVals[0]);
                        colcoords[colorIndex++] = Float.parseFloat(colorVals[1]);
                        colcoords[colorIndex++] = Float.parseFloat(colorVals[2]);
                        colcoords[colorIndex++] = 1f;
                    }
                }
                else if (xrp.getEventType() == XmlResourceParser.END_TAG)
                {
                    ;
                }
                else if (xrp.getEventType() == XmlResourceParser.TEXT)
                {
                    ;
                }
                xrp.next();
            }
            xrp.close();
        }
        catch (XmlPullParserException xppe)
        {
            Log.e("TAG", "Failure of .getEventType or .next, probably bad file format");
            xppe.toString();
        }
        catch (IOException ioe)
        {
            Log.e("TAG", "Unable to read resource file");
            ioe.printStackTrace();
        }
        mVertexBuffer = makeFloatBuffer(coords);
        mColorBuffer = makeFloatBuffer(colcoords);
        mNormalBuffer = makeFloatBuffer(ncoords);
        mIndexBuffer = makeShortBuffer(icoords);
    }

    private FloatBuffer makeFloatBuffer(float[] arr)
    {
        ByteBuffer bb = ByteBuffer.allocateDirect(arr.length * 4);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer fb = bb.asFloatBuffer();
        fb.put(arr);
        fb.position(0);
        return fb;
    }

    private ShortBuffer makeShortBuffer(short[] arr)
    {
        ByteBuffer bb = ByteBuffer.allocateDirect(arr.length * 4);
        bb.order(ByteOrder.nativeOrder());
        ShortBuffer ib = bb.asShortBuffer();
        ib.put(arr);
        ib.position(0);
        return ib;
    }

    public void draw(GL10 gl)
    {
        gl.glFrontFace(GL10.GL_CCW);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVertexBuffer);
        gl.glColorPointer(4, GL10.GL_FLOAT, 0, mColorBuffer);
        gl.glNormalPointer(GL10.GL_FLOAT, 0, mNormalBuffer);
        gl.glDrawElements(GL10.GL_TRIANGLES, faceCount, GL10.GL_UNSIGNED_SHORT, mIndexBuffer);
    }
}
