package com.space.controller;

import org.junit.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class DeleteShipTest extends AbstractTest {

    //test1
    @Test
    public void deleteShipByIdZeroTest() throws Exception {
        mockMvc.perform(delete("/rest/ships/0")
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest());
    }

    //test2
    @Test
    public void deleteShipByIdNotNumberTest() throws Exception {
        mockMvc.perform(delete("/rest/ships/test")
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest());
    }

    //test3
    @Test
    public void deleteShipByIdNotExistTest() throws Exception {
        mockMvc.perform(delete("/rest/ships/426")
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isNotFound());
    }

    //test4
    @Test
    public void deleteShipByIdTest() throws Exception {
        mockMvc.perform(delete("/rest/ships/1")
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        mockMvc.perform(get("/rest/ships/1")
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isNotFound());
    }
}