package com.ugc.backend.dto;

import lombok.Data;
import lombok.Builder;
import java.util.List;

@Data
@Builder
public class GenerateMimicResponse {
    private String content;
    private Integer wordCount;
    private String sentiment;
    private List<String> keywords;
} 