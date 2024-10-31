package com.auth.auth.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActionResult {
    private Boolean status;
    private String message;
    private Object data;
    private Object errors;
}
