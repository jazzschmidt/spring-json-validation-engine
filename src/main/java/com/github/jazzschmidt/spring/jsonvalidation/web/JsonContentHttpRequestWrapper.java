package com.github.jazzschmidt.spring.jsonvalidation.web;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;
import java.nio.charset.Charset;

/**
 * Wraps a {@link HttpServletRequest}, consumes its content and statically provides it to the current thread, so that
 * any operation on that content will not affect the {@link InputStream} consuming Servlet request candidates.
 */
public class JsonContentHttpRequestWrapper extends HttpServletRequestWrapper {

    /**
     * Plain content of the current request
     */
    private static String content;

    /**
     * Encoding of the request
     */
    private final Charset encoding;

    /**
     * Constructs a request object wrapping the given request.
     *
     * @param request The request to wrap
     * @throws IllegalArgumentException if the request is null
     */
    public JsonContentHttpRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        encoding = Charset.forName(request.getCharacterEncoding());
        content = readInputStream(request.getInputStream(), encoding);
    }

    /**
     * Returns the content of the current request if charged via Servlet filter chain
     *
     * @return Body of the request
     */
    public static String getContent() {
        return content;
    }

    /**
     * Consumes the {@link InputStream} of the request.
     *
     * @param stream  HTTP request input stream
     * @param charset Encoding of the content
     * @return Content of the request body
     * @throws IOException if any I/O error occurs while reading the input stream
     */
    private String readInputStream(InputStream stream, Charset charset) throws IOException {
        if (!stream.markSupported()) {
            stream = new BufferedInputStream(stream);
        }

        stream.mark(0);
        final byte[] entity = stream.readAllBytes();
        stream.reset();

        return new String(entity, charset);
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream(), encoding));
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(content.getBytes());

        return new ServletInputStream() {
            private boolean finished = false;

            @Override
            public boolean isFinished() {
                return finished;
            }

            @Override
            public int available() throws IOException {
                return byteArrayInputStream.available();
            }

            @Override
            public void close() throws IOException {
                super.close();
                byteArrayInputStream.close();
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
                throw new UnsupportedOperationException();
            }

            public int read() throws IOException {
                int data = byteArrayInputStream.read();
                if (data == -1) {
                    finished = true;
                }
                return data;
            }
        };
    }
}
