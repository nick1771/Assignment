package com.assignment.model.ticket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Passenger {
    @NotNull(message = "Passenger type is required")
    private PassengerType type;

    @Min(value = 0)
    private int luggageCount;
}
