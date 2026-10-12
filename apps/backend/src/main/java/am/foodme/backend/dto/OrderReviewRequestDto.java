package am.foodme.backend.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class OrderReviewRequestDto {
    private static final String RATING_MESSAGE = "Rating must be a whole number from 1 to 5";

    // BigDecimal rather than Integer: Jackson would silently truncate 4.5 to 4.
    @NotNull(message = RATING_MESSAGE)
    @DecimalMin(value = "1", message = RATING_MESSAGE)
    @DecimalMax(value = "5", message = RATING_MESSAGE)
    @Digits(integer = 1, fraction = 0, message = RATING_MESSAGE)
    private BigDecimal rating;

    @Size(max = 1000, message = "Comment must be at most 1000 characters")
    private String comment;
}
