package com.xjdl.framework.beans;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PropertyValue {
    private final String name;
    private final Object value;
}
