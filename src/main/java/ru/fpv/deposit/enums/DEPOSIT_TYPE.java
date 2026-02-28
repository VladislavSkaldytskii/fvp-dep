package ru.fpv.deposit.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DEPOSIT_TYPE {
    DEMAND,
    TERM,
    TERM_CAPITALIZATION
}
