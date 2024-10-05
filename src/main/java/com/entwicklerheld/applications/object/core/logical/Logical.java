package com.entwicklerheld.applications.object.core.logical;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.function.BiFunction;

@Getter
@AllArgsConstructor
public enum Logical {
    AND((a, b) -> a && b),
    OR((a, b) -> a || b),
    NOT((a, b) -> a != b), // NOT als binäre Operation behandeln
    NAND((a, b) -> !(a && b)),
    NOR((a, b) -> !(a || b)),
    XOR((a, b) -> a ^ b);

    private final BiFunction<Boolean, Boolean, Boolean> operation;
}