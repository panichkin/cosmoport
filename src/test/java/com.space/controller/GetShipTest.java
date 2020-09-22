package com.space.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.space.controller.utils.ShipInfoTest;
import com.space.controller.utils.TestsHelper;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class GetShipTest extends AbstractTest {

    //test1
    @Test
    public void getShipByIdEqualZeroTest() throws Exception {
        mockMvc.perform(get("/rest/ships/0")
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest());
    }

    //test2
    @Test
    public void getShipByIdNotNumberTest() throws Exception {
        mockMvc.perform(get("/rest/ships/test")
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest());
    }

    //test3
    @Test
    public void getShipByIdNotExistTest() throws Exception {
        mockMvc.perform(get("/rest/ships/410")
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isNotFound());
    }

    //test4
    @Test
    public void getShipByIdTest() throws Exception {
        ShipInfoTest expected = new TestsHelper().getShipInfosById(14);

        ResultActions resultActions = mockMvc.perform(get("/rest/ships/14")
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        String contentAsString = resultActions.andReturn().getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        ShipInfoTest actual = mapper.readValue(contentAsString, ShipInfoTest.class);
        assertEquals("Вернулся неправильный объект при запросе GET /rest/ships/{id}", expected, actual);
    }
}