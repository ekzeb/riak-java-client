package com.basho.riak.client.raw;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.basho.riak.client.response.HttpResponse;
import com.basho.riak.client.response.RiakResponseException;

public class TestRawWalkResponse {

    final String BUCKET = "bucket";
    final String KEY = "key";
    final Map<String, String> HEADERS = new HashMap<String, String>();

    @Mock HttpResponse mockHttpResponse;

    @Before public void setup() {
        HEADERS.put("Content-Type".toLowerCase(), "multipart/mixed; boundary=BCVLGEKnH0gY7KsH5nW3xnzhYbU");

        MockitoAnnotations.initMocks(this);

        when(mockHttpResponse.getBucket()).thenReturn(BUCKET);
        when(mockHttpResponse.getKey()).thenReturn(KEY);
        when(mockHttpResponse.getHttpHeaders()).thenReturn(HEADERS);
    }

    @Test public void doesnt_throw_on_null_impl() throws JSONException {
        new RawWalkResponse(null);
    }

    @Test public void returns_empty_list_on_no_content() {
        when(mockHttpResponse.getBody()).thenReturn("");
        when(mockHttpResponse.isSuccess()).thenReturn(true);

        RawWalkResponse impl = new RawWalkResponse(mockHttpResponse);

        assertFalse(impl.hasSteps());
        assertEquals(0, impl.getSteps().size());
    }

    @Test public void parses_walk_steps() {
        final String BODY = "\n" + "--BCVLGEKnH0gY7KsH5nW3xnzhYbU\n"
                            + "Content-Type: multipart/mixed; boundary=7Ymillu08Tqzwb9Cm6Bs8OewFd5\n" + "\n"
                            + "--7Ymillu08Tqzwb9Cm6Bs8OewFd5\n" + "Location: /raw/b/k1\n" + "\n" + "foo\n"
                            + "--7Ymillu08Tqzwb9Cm6Bs8OewFd5\n" + "Location: /raw/b/k2\n" + "\n" + "bar\n"
                            + "--7Ymillu08Tqzwb9Cm6Bs8OewFd5--\n" + "\n" + "--BCVLGEKnH0gY7KsH5nW3xnzhYbU--\n";

        when(mockHttpResponse.getBody()).thenReturn(BODY);
        when(mockHttpResponse.isSuccess()).thenReturn(true);

        RawWalkResponse impl = new RawWalkResponse(mockHttpResponse);
        assertTrue(impl.hasSteps());
        assertEquals(1, impl.getSteps().size());

        assertEquals("b", impl.getSteps().get(0).get(0).getBucket());
        assertEquals("k1", impl.getSteps().get(0).get(0).getKey());
        assertEquals("foo", impl.getSteps().get(0).get(0).getValue());

        assertEquals("b", impl.getSteps().get(0).get(1).getBucket());
        assertEquals("k2", impl.getSteps().get(0).get(1).getKey());
        assertEquals("bar", impl.getSteps().get(0).get(1).getValue());
    }

    @Test(expected = RiakResponseException.class) public void throws_on_invalid_subpart_content_type() {
        final String BODY = "\n" + "--BCVLGEKnH0gY7KsH5nW3xnzhYbU\n" + "Content-Type: text/plain\n" + "\n"
                            + "--7Ymillu08Tqzwb9Cm6Bs8OewFd5\n" + "\n" + "--7Ymillu08Tqzwb9Cm6Bs8OewFd5--\n" + "\n"
                            + "--BCVLGEKnH0gY7KsH5nW3xnzhYbU--\n";

        when(mockHttpResponse.getBody()).thenReturn(BODY);
        when(mockHttpResponse.isSuccess()).thenReturn(true);

        new RawWalkResponse(mockHttpResponse);
    }

    // RawWalkResponse uses Multipart.parse, so we can rely on TestMultipart
    // to validate multipart parsing works.
}
