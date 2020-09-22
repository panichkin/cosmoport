package com.space.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.space.controller.utils.ShipInfoTest;
import com.space.controller.utils.TestsHelper;
import com.space.model.ShipType;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CreateShipTest extends AbstractTest {

    private ObjectMapper mapper = new ObjectMapper();
    private ShipInfoTest expected;

    @Before
    public void setup() {
        super.setup();
        expected = new ShipInfoTest(41L, "123456789", "Earth", ShipType.MILITARY, 32998274577071L, true, 0.8, 14, 6.4);
    }

    //test1
    @Test
    public void createShipEmptyBodyTest() throws Exception {
        mockMvc.perform(post("/rest/ships/")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .content("{}"))
                .andExpect(status().isBadRequest());
    }

    //test2
    @Test
    public void createShipNoSpeedTest() throws Exception {
        mockMvc.perform(post("/rest/ships/")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .content(TestsHelper.NO_SPEED_JSON))
                .andExpect(status().isBadRequest());
    }

    //test3
    @Test
    public void createShipEmptyNameTest() throws Exception {
        mockMvc.perform(post("/rest/ships/")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .content(TestsHelper.EMPTY_NAME_JSON))
                .andExpect(status().isBadRequest());
    }

    //test4
    @Test
    public void createShipProdDateNegativeTest() throws Exception {
        mockMvc.perform(post("/rest/ships/")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .content(TestsHelper.NEGATIVE_PROD_DATE_JSON))
                .andExpect(status().isBadRequest());
    }

    //test5
    @Test
    public void createShipCrewSizeTooBigTest() throws Exception {
        mockMvc.perform(post("/rest/ships/")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .content(TestsHelper.TOO_BIG_CREW_SIZE_JSON))
                .andExpect(status().isBadRequest());
    }

    //test6
    @Test
    public void createShipPlanetLengthTooBigTest() throws Exception {
        mockMvc.perform(post("/rest/ships/")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .content(TestsHelper.TOO_BIG_PLANET_LENGTH_JSON))
                .andExpect(status().isBadRequest());
    }

    //test7
    @Test
    public void createShipIsUsedAbsentTest() throws Exception {
        expected.isUsed = false;
        expected.rating = 12.8;

        ResultActions resultActions = mockMvc.perform(post("/rest/ships/")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .content(TestsHelper.NO_IS_USED_JSON))
                .andExpect(status().isOk());

        String contentAsString = resultActions.andReturn().getResponse().getContentAsString();
        ShipInfoTest actual = mapper.readValue(contentAsString, ShipInfoTest.class);
        assertEquals("Возвращается не правильный результат при запросе создания корабля без параметра isUsed.", expected, actual);
    }

    //test8
    @Test
    public void createShipIsUsedTrueTest() throws Exception {
        ResultActions resultActions = mockMvc.perform(post("/rest/ships/")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .content(TestsHelper.IS_USED_TRUE_JSON))
                .andExpect(status().isOk());

        String contentAsString = resultActions.andReturn().getResponse().getContentAsString();
        ShipInfoTest actual = mapper.readValue(contentAsString, ShipInfoTest.class);
        assertEquals("Возвращается не правильный результат при запросе создания корабля с параметром isUsed.", expected, actual);
    }

    //test9
    @Test
    public void createShipIsUsedFalseTest() throws Exception {
        expected.isUsed = false;
        expected.rating = 12.8;

        ResultActions resultActions = mockMvc.perform(post("/rest/ships/")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .content(TestsHelper.IS_USED_FALSE_JSON))
                .andExpect(status().isOk());

        String contentAsString = resultActions.andReturn().getResponse().getContentAsString();
        ShipInfoTest actual = mapper.readValue(contentAsString, ShipInfoTest.class);
        assertEquals("Возвращается не правильный результат при запросе создания корабля с параметром isUsed.", expected, actual);
    }
}