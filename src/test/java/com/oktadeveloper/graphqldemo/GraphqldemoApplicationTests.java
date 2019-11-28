package com.oktadeveloper.graphqldemo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class GraphqldemoApplicationTests {

    @Autowired
    MockMvc mockMvc;

    @Test
    void listFoods() throws Exception {
        String expectedResponse = "{\"data\":{\"foods\":[" +
                "{\"id\":1,\"name\":\"Pizza\",\"isGood\":true}," +
                "{\"id\":2,\"name\":\"Spam\",\"isGood\":false}," +
                "{\"id\":3,\"name\":\"Eggs\",\"isGood\":true}," +
                "{\"id\":4,\"name\":\"Avocado\",\"isGood\":false}" +
                "]}}";

        mockMvc.perform(MockMvcRequestBuilders.post("/graphql")
                .content("{\"query\":\"{ foods { id name isGood } }\"}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse))
                .andReturn();
    }

    @Test
    void addAndRemoveFood() throws Exception {
        String expectedResponseBefore = "{\"data\":{\"foods\":[" +
                "{\"id\":1,\"name\":\"Pizza\"}," +
                "{\"id\":2,\"name\":\"Spam\"}," +
                "{\"id\":3,\"name\":\"Eggs\"}," +
                "{\"id\":4,\"name\":\"Avocado\"}" +
                "]}}";
        String expectedResponseAfter = "{\"data\":{\"foods\":[" +
                "{\"id\":1,\"name\":\"Pizza\"}," +
                "{\"id\":2,\"name\":\"Spam\"}," +
                "{\"id\":3,\"name\":\"Eggs\"}," +
                "{\"id\":4,\"name\":\"Avocado\"}," +
                "{\"id\":5,\"name\":\"Pasta\"}" +
                "]}}";

        // List cars, expect 'New Car' to not be there
        mockMvc.perform(MockMvcRequestBuilders.post("/graphql")
                .content("{\"query\":\"{ foods { id name } }\"}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponseBefore))
                .andReturn();

        // Add 'New Car'
        mockMvc.perform(MockMvcRequestBuilders.post("/graphql")
                .content("{\"query\":\"mutation { saveFood(food: { name: \\\"Pasta\\\" }) { id name } }\"}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"data\":{\"saveFood\":{\"id\":5,\"name\":\"Pasta\"}}}"))
                .andReturn();

        // List cars, expect 'New Car' to be there
        mockMvc.perform(MockMvcRequestBuilders.post("/graphql")
                .content("{\"query\":\"{ foods { id name } }\"}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponseAfter))
                .andReturn();

        // Remove 'New Car'
        mockMvc.perform(MockMvcRequestBuilders.post("/graphql")
                .content("{\"query\":\"mutation { deleteFood(id: 5) }\"}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // List cars, expect 'New Car' to not be there
        mockMvc.perform(MockMvcRequestBuilders.post("/graphql")
                .content("{\"query\":\"{ foods { id name } }\"}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponseBefore))
                .andReturn();
    }

}
