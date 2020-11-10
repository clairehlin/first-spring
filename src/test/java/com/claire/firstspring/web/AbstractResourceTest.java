package com.claire.firstspring.web;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultHandler;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.concurrent.Callable;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@AutoConfigureMockMvc
public abstract class AbstractResourceTest {
    private static final boolean debug = true;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    protected <T> T get(String uri, int status, Class<T> tClass) {
        return propagate(() -> objectMapper.readValue(get(uri, status), tClass));
    }

    protected <T> T get(String uri, int status, TypeReference<T> collectionTypeReference) {
        return propagate(() -> objectMapper.readValue(get(uri, status), collectionTypeReference));
    }

    protected <T> void put(String uri, int status, T t) {
        propagate(() -> {
            final String body = objectMapper.writeValueAsString(t);
            final MockHttpServletRequestBuilder requestBuilder = put(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body);
            mockMvc.perform(requestBuilder)
                .andExpect(status().is(status))
                .andDo(debugHandler());
        });
    }

    protected <T> void post(String uri, int status, T t) {
        propagate(() -> {
            final String body = objectMapper.writeValueAsString(t);
            final MockHttpServletRequestBuilder requestBuilder = post(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body);
            mockMvc.perform(requestBuilder)
                .andExpect(status().is(status))
                .andDo(debugHandler());
        });
    }

    private byte[] get(String uri, int status) {
        MvcResult mvcResult = propagate(
            () -> mockMvc.perform(get(uri))
                .andExpect(status().is(status))
                .andDo(debugHandler())
                .andReturn()
        );
        return mvcResult.getResponse().getContentAsByteArray();
    }

    protected void delete(String uri, int status) {
        propagate(
            () -> mockMvc.perform(delete(uri))
                .andExpect(status().is(status))
                .andDo(debugHandler())
        );
    }

    protected void put(String uri, int status) {
        propagate(
            () -> mockMvc.perform(put(uri))
                .andExpect(status().is(status))
                .andDo(debugHandler())
        );
    }

    private ResultHandler debugHandler() {
        return debug
            ? printBody()
            : doNothing();
    }

    private ResultHandler doNothing() {
        return result -> {
        };
    }

    private ResultHandler printBody() {
        return result -> System.out.println(
            "printing body: " +
                new String(
                    result.getResponse().getContentAsByteArray(),
                    UTF_8
                )
        );
    }

    private MockHttpServletRequestBuilder get(String uri) {
        return MockMvcRequestBuilders.get(uri);
    }

    private MockHttpServletRequestBuilder delete(String uri) {
        return MockMvcRequestBuilders.delete(uri);
    }

    private MockHttpServletRequestBuilder put(String uri) {
        return MockMvcRequestBuilders.put(uri);
    }

    private MockHttpServletRequestBuilder put(String uri, String body) {
        return MockMvcRequestBuilders.put(uri).content(body);
    }

    private MockHttpServletRequestBuilder post(String uri) {
        return MockMvcRequestBuilders.post(uri);
    }

    private <T> T propagate(Callable<T> callable) {
        try {
            return callable.call();
        } catch (Exception e) {
            throw new RuntimeException("failed executing callable", e);
        }
    }

    private void propagate(ThrowingRunnable runnable) {
        try {
            runnable.run();
        } catch (Exception e) {
            throw new RuntimeException("failed executing runnable", e);
        }
    }

    private interface ThrowingRunnable {
        void run() throws Exception;
    }
}
