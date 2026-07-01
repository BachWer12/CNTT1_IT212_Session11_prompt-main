package com.example.demo.candidate.controller;

import com.example.demo.candidate.dto.CandidateRequest;
import com.example.demo.candidate.model.Status;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class CandidateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGetAllCandidates() throws Exception {
        mockMvc.perform(get("/api/candidates"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(5)))
                .andExpect(jsonPath("$[0].fullName", is("nguyen van nam")));
    }

    @Test
    public void testGetCandidateById_Success() throws Exception {
        mockMvc.perform(get("/api/candidates/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName", is("nguyen van nam")))
                .andExpect(jsonPath("$.nationality", is("vietnam")));
    }

    @Test
    public void testGetCandidateById_NotFound() throws Exception {
        mockMvc.perform(get("/api/candidates/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Candidate not found with id: 999")));
    }

    @Test
    public void testCreateCandidate_Success() throws Exception {
        CandidateRequest request = CandidateRequest.builder()
                .fullName("Alex Chen")
                .age(25)
                .height(183.5)
                .weight(76.0)
                .nationality("Canada")
                .bio("Bilingual model and actor")
                .photoUrl("https://example.com/alex.jpg")
                .score(80.0)
                .status(Status.ACTIVE)
                .build();

        mockMvc.perform(post("/api/candidates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.fullName", is("Alex Chen")))
                .andExpect(jsonPath("$.nationality", is("Canada")));
    }

    @Test
    public void testCreateCandidate_ValidationFailed() throws Exception {
        CandidateRequest request = CandidateRequest.builder()
                .fullName("") // Blank name
                .age(17) // Too young
                .height(140.0) // Too short
                .weight(40.0) // Too light
                .nationality("Canada")
                .status(null) // Null status
                .build();

        mockMvc.perform(post("/api/candidates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Validation failed")))
                .andExpect(jsonPath("$.errors.fullName").exists())
                .andExpect(jsonPath("$.errors.age").exists())
                .andExpect(jsonPath("$.errors.height").exists())
                .andExpect(jsonPath("$.errors.weight").exists())
                .andExpect(jsonPath("$.errors.status").exists());
    }

    @Test
    public void testUpdateCandidate_Success() throws Exception {
        CandidateRequest request = CandidateRequest.builder()
                .fullName("Nguyen Van Nam Updated")
                .age(24)
                .height(185.0)
                .weight(78.0)
                .nationality("Vietnam")
                .bio("Updated bio info")
                .photoUrl("https://example.com/updated.jpg")
                .score(98.0)
                .status(Status.WINNER)
                .build();

        mockMvc.perform(put("/api/candidates/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName", is("Nguyen Van Nam Updated")))
                .andExpect(jsonPath("$.height", is(185.0)))
                .andExpect(jsonPath("$.score", is(98.0)));
    }

    @Test
    public void testDeleteCandidate_Success() throws Exception {
        mockMvc.perform(delete("/api/candidates/2"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/candidates/2"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testSearchCandidates() throws Exception {
        mockMvc.perform(get("/api/candidates/search")
                        .param("name", "nam")
                        .param("nationality", "vietnam")
                        .param("status", "WINNER")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].fullName", is("nguyen van nam")));
    }

    @Test
    public void testVoteCandidate() throws Exception {
        mockMvc.perform(post("/api/candidates/1/vote")
                        .param("points", "2.5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.score", is(98.0))); // Seed score is 95.5 + 2.5 = 98.0
    }

    @Test
    public void testUpdateStatus() throws Exception {
        Map<String, String> body = new HashMap<>();
        body.put("status", "ELIMINATED");

        mockMvc.perform(patch("/api/candidates/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("ELIMINATED")));
    }

    @Test
    public void testGetStats() throws Exception {
        mockMvc.perform(get("/api/candidates/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCandidates", is(5)))
                .andExpect(jsonPath("$.maxScore", is(95.5)));
    }
}
