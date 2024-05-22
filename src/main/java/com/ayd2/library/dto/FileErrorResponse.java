package com.ayd2.library.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileErrorResponse {
    private int line;
    private String reason;

    public FileErrorResponse(int line, String reason){
        this.line = line;
        this.reason = reason;
    }
}
