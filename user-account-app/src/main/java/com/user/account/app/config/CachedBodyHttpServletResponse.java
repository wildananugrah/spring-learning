package com.user.account.app.config;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

/**
 * Wrapper class for HttpServletResponse that caches the response body
 * so it can be read for logging purposes
 */
public class CachedBodyHttpServletResponse extends HttpServletResponseWrapper {

    private final ByteArrayOutputStream cachedBody = new ByteArrayOutputStream();
    private ServletOutputStream outputStream;
    private PrintWriter writer;

    public CachedBodyHttpServletResponse(HttpServletResponse response) {
        super(response);
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        if (outputStream == null) {
            outputStream = new CachedBodyServletOutputStream(cachedBody, super.getOutputStream());
        }
        return outputStream;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        if (writer == null) {
            writer = new PrintWriter(new OutputStreamWriter(cachedBody, getCharacterEncoding()));
        }
        return writer;
    }

    @Override
    public void flushBuffer() throws IOException {
        if (writer != null) {
            writer.flush();
        }
        if (outputStream != null) {
            outputStream.flush();
        }
    }

    public byte[] getCachedBody() {
        try {
            flushBuffer();
        } catch (IOException e) {
            // Handle exception
        }
        return cachedBody.toByteArray();
    }

    private static class CachedBodyServletOutputStream extends ServletOutputStream {

        private final ByteArrayOutputStream cachedBody;
        private final ServletOutputStream originalOutputStream;

        public CachedBodyServletOutputStream(ByteArrayOutputStream cachedBody, ServletOutputStream originalOutputStream) {
            this.cachedBody = cachedBody;
            this.originalOutputStream = originalOutputStream;
        }

        @Override
        public boolean isReady() {
            return originalOutputStream.isReady();
        }

        @Override
        public void setWriteListener(WriteListener listener) {
            originalOutputStream.setWriteListener(listener);
        }

        @Override
        public void write(int b) throws IOException {
            cachedBody.write(b);
            originalOutputStream.write(b);
        }

        @Override
        public void write(byte[] b) throws IOException {
            cachedBody.write(b);
            originalOutputStream.write(b);
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            cachedBody.write(b, off, len);
            originalOutputStream.write(b, off, len);
        }
    }
}
