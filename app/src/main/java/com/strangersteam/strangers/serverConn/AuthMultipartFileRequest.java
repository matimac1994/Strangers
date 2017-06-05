package com.strangersteam.strangers.serverConn;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyLog;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by kroli on 04.06.2017.
 */

public class AuthMultipartFileRequest extends Request<String> {

    private Context applicationContext;
    private final Response.Listener<String> mListener;
    private final byte[] data;
    private String fileName;

    private final String twoHyphens = "--";
    private final String lineEnd = "\r\n";
    private final String boundary = "apiclient-" + System.currentTimeMillis();
    private final String mimeType = "multipart/form-data;boundary=" + boundary;

    public AuthMultipartFileRequest(Context applicationContext, int method, String url,String fileName, byte[] data, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        this.applicationContext = applicationContext;
        mListener = listener;
        this.data = data;
        this.fileName = fileName;
    }

    @Override
    public String getBodyContentType()
    {
        return mimeType;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        return buildPart();
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        return Response.success("Uploaded", getCacheEntry());
    }

    @Override
    protected void deliverResponse(String response) {
        mListener.onResponse(response);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String,String> headers = new HashMap<>(super.getHeaders());
        headers.put(AuthTokenProvider.TOKEN_HEADER_NAME, AuthTokenProvider.getToken(applicationContext));
        return headers;
    }


    private byte[] buildPart(){

        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(bos);
            dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
            dataOutputStream.writeBytes("Content-Type: image/jpeg");
            dataOutputStream.writeBytes(lineEnd);
            dataOutputStream.writeBytes("Content-ID: <950118.AECB@XIson.com>");
            dataOutputStream.writeBytes(lineEnd);
            dataOutputStream.writeBytes("Content-Transfer-Encoding: BASE64");
            dataOutputStream.writeBytes(lineEnd);
            dataOutputStream.writeBytes("Content-Description: Picture B");
            dataOutputStream.writeBytes(lineEnd);
            dataOutputStream.writeBytes("Content-Disposition: form-data;");
            dataOutputStream.writeBytes("name=\"photo\"; filename=\""
                    + fileName + "\"" + lineEnd);
            dataOutputStream.writeBytes(lineEnd);

            ByteArrayInputStream fileInputStream = new ByteArrayInputStream(data);
            int bytesAvailable = fileInputStream.available();

            int maxBufferSize = 1024 * 1024;
            int bufferSize = Math.min(bytesAvailable, maxBufferSize);
            byte[] buffer = new byte[bufferSize];

            // read file and write it into form...
            int bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0) {
                dataOutputStream.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }
            dataOutputStream.writeBytes(lineEnd);
            dataOutputStream.writeBytes(twoHyphens + boundary + twoHyphens);

            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return new byte[0];
        }


    }
}
