package com.space.controller;

import com.space.controller.utils.TestsHelper;
import com.space.model.ShipType;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import static org.junit.Assert.assertSame;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class GetCountTest extends AbstractTest {

    private TestsHelper testsHelper = new TestsHelper();

    //test1
    @Test
    public void getCountWithoutFiltersReturnAllShips() throws Exception {
        ResultActions resultActions = this.mockMvc.perform(get("/rest/ships/count")
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        MvcResult result = resultActions.andReturn();
        String contentAsString = result.getResponse().getContentAsString();
        int actual = Integer.parseInt(contentAsString);
        int expected = testsHelper.getAllShips().size();

        assertSame("Возвращается не правильный результат при запросе GET /rest/ships/count.", expected, actual);
    }

    //test2
    @Test
    public void getCountWithFiltersMinRatingMinCrewSizeMinSpeed() throws Exception {
        ResultActions resultActions = this.mockMvc.perform(get("/rest/ships/count?minRating=0.5&minCrewSize=100&minSpeed=0.3")
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        MvcResult result = resultActions.andReturn();
        String contentAsString = result.getResponse().getContentAsString();
        int actual = Integer.parseInt(contentAsString);
        int expected = testsHelper.getShipInfosByMinRating(0.5,
                testsHelper.getShipInfosByMinCrewSize(100,
                        testsHelper.getShipInfosByMinSpeed(0.3,
                                testsHelper.getAllShips()))).size();

        assertSame("Возвращается не правильный результат при запросе GET /rest/ships/count с параметрами minRating, minCrewSize и minSpeed.", expected, actual);
    }

    //test3
    @Test
    public void getCountWithFiltersNameAfterMaxRating() throws Exception {
        //after 00:00 01/01/2900
        ResultActions resultActions = this.mockMvc.perform(get("/rest/ships/count?name=nt&after=32188140000000&maxRating=3")
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        MvcResult result = resultActions.andReturn();
        String contentAsString = result.getResponse().getContentAsString();
        int actual = Integer.parseInt(contentAsString);
        int expected = testsHelper.getShipInfosByMaxRating(3.,
                testsHelper.getShipInfosByAfter(32188140000000L,
                        testsHelper.getShipInfosByName("nt",
                                testsHelper.getAllShips()))).size();

        assertSame("Возвращается не правильный результат при запросе GET /rest/ships/count с параметрами minRating,minCrewSize и minSpeed.", expected, actual);
    }

    //test4
    @Test
    public void getCountWithFiltersShipTypeIsUsed() throws Exception {
        ResultActions resultActions = this.mockMvc.perform(get("/rest/ships/count?shipType=MERCHANT&isUsed=true")
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        MvcResult result = resultActions.andReturn();
        String contentAsString = result.getResponse().getContentAsString();
        int actual = Integer.parseInt(contentAsString);
        int expected = testsHelper.getShipInfosByIsUsed(true,
                testsHelper.getShipInfosByShipType(ShipType.MERCHANT,
                        testsHelper.getAllShips())).size();

        assertSame("Возвращается не правильный результат при запросе GET rest/ships/count с параметрами shipType и isUsed.", expected, actual);
    }

    //test5
    @Test
    public void getCountWithFiltersShipTypeMaxCrewSize() throws Exception {
        ResultActions resultActions = this.mockMvc.perform(get("/rest/ships/count?shipType=MILITARY&maxCrewSize=400")
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        MvcResult result = resultActions.andReturn();
        String contentAsString = result.getResponse().getContentAsString();
        int actual = Integer.parseInt(contentAsString);
        int expected = testsHelper.getShipInfosByMaxCrewSize(400,
                testsHelper.getShipInfosByShipType(ShipType.MILITARY,
                        testsHelper.getAllShips())).size();

        assertSame("Возвращается не правильный результат при запросе GET /rest/ships/count с параметрами shipType и maxCrewSize.", expected, actual);
    }

    //test6
    @Test
    public void getCountWithFiltersPlanet() throws Exception {
        ResultActions resultActions = this.mockMvc.perform(get("/rest/ships/count?planet=us")
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        MvcResult result = resultActions.andReturn();
        String contentAsString = result.getResponse().getContentAsString();
        int actual = Integer.parseInt(contentAsString);
        int expected = testsHelper.getShipInfosByPlanet("us", testsHelper.getAllShips()).size();

        assertSame("Возвращается не правильный результат при запросе GET /rest/ships/count с параметром planet.", expected, actual);
    }

    //test7
    @Test
    public void getCountWithFiltersShipTypeBeforeMaxSpeed() throws Exception {
        //before 00:00 01/01/3015
        ResultActions resultActions = this.mockMvc.perform(get("/rest/ships/count?shipType=TRANSPORT&before=32976972000000&maxSpeed=0.7")
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        MvcResult result = resultActions.andReturn();
        String contentAsString = result.getResponse().getContentAsString();
        int actual = Integer.parseInt(contentAsString);
        int expected = testsHelper.getShipInfosByBefore(32976972000000L,
                testsHelper.getShipInfosByShipType(ShipType.TRANSPORT,
                        testsHelper.getShipInfosByMaxSpeed(0.7,
                                testsHelper.getAllShips()))).size();

        assertSame("Возвращается не правильный результат при запросе GET /rest/ships/count с параметрами shipType, before и maxSpeed.", expected, actual);
    }

    //test8
    @Test
    public void getCountWithFiltersIsUsedMinMaxSpeed() throws Exception {
        ResultActions resultActions = this.mockMvc.perform(get("/rest/ships/count?isUsed=false&minSpeed=0.3&maxSpeed=0.6")
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        MvcResult result = resultActions.andReturn();
        String contentAsString = result.getResponse().getContentAsString();
        int actual = Integer.parseInt(contentAsString);
        int expected = testsHelper.getShipInfosByIsUsed(false,
                testsHelper.getShipInfosByMinSpeed(0.3,
                        testsHelper.getShipInfosByMaxSpeed(0.6,
                                testsHelper.getAllShips()))).size();

        assertSame("Во звращается не правильный результат при запросе GET /rest/ships/count с параметрами isUsed, minSpeed и maxSpeed.", expected, actual);
    }
}