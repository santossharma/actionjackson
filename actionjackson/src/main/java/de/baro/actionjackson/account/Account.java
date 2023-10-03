package de.baro.actionjackson.account;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

/**
 * Created by santoshsharma on 01 Oct, 2023
 */

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record Account(Integer id, @JsonView(View.Summary.class) String firstName, @JsonView(View.ExtendedSummary.class)String lastName) {
}
