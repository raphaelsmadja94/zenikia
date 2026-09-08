package com.zenika.zenikia.cv.api;

import com.zenika.zenikia.cv.application.CvAnalysisService;
import com.zenika.zenikia.shared.domain.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Verifies the controller/error-handling boundary: a thin controller that
 * validates the upload shape and delegates everything else, with
 * {@code GlobalExceptionHandler} translating failures into stable
 * {@code ApiError} payloads.
 */
@WebMvcTest(CvController.class)
class CvControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CvAnalysisService cvAnalysisService;

    @Test
    void analyze_rejectsNonPdfContentTypeWithBadRequest() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "cv.txt", "text/plain", "hello".getBytes());

        mockMvc.perform(multipart("/api/cvs/analyze").file(file))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"));
    }

    @Test
    void analyze_rejectsEmptyFileWithBadRequest() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "cv.pdf", "application/pdf", new byte[0]);

        mockMvc.perform(multipart("/api/cvs/analyze").file(file))
                .andExpect(status().isBadRequest());
    }

    @Test
    void get_unknownProfileReturnsNotFound() throws Exception {
        when(cvAnalysisService.getProfile("missing")).thenThrow(ResourceNotFoundException.of("CandidateProfile", "missing"));

        mockMvc.perform(get("/api/cvs/missing"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("NOT_FOUND"));
    }
}
