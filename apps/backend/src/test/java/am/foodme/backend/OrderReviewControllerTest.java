package am.foodme.backend;

import am.foodme.backend.security.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// KAN-5: customers rate delivered orders.
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OrderReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtService jwtService;

    @Test
    void reviewOrder_deliveredOwnOrder_savedAndShownEverywhere() throws Exception {
        String token = customerToken();
        String number = placeOrder(token, 1, 1);
        long orderId = deliver(number);

        mockMvc.perform(post("/api/customer/orders/" + number + "/review")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reviewPayload(4, "Warm and on time")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value(number))
                .andExpect(jsonPath("$.review.rating").value(4))
                .andExpect(jsonPath("$.review.comment").value("Warm and on time"))
                .andExpect(jsonPath("$.review.createdAt").exists());

        // My Orders
        mockMvc.perform(get("/api/customer/orders")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.list[0].number").value(number))
                .andExpect(jsonPath("$.list[0].review.rating").value(4));

        // Order tracking
        mockMvc.perform(get("/api/order/number/" + number))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.review.rating").value(4))
                .andExpect(jsonPath("$.review.comment").value("Warm and on time"));

        // Admin panel
        mockMvc.perform(get("/admin/order/" + orderId)
                        .header("Authorization", "Bearer " + adminToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.review.rating").value(4))
                .andExpect(jsonPath("$.review.comment").value("Warm and on time"))
                .andExpect(jsonPath("$.review.createdAt").exists());
    }

    @Test
    void reviewOrder_withoutComment_succeeds() throws Exception {
        String token = customerToken();
        String number = placeOrder(token, 1, 1);
        deliver(number);

        mockMvc.perform(post("/api/customer/orders/" + number + "/review")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("rating", 5))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.review.rating").value(5))
                .andExpect(jsonPath("$.review.comment").doesNotExist());
    }

    @Test
    void reviewOrder_notDelivered_rejected() throws Exception {
        String token = customerToken();
        String number = placeOrder(token, 1, 1);

        mockMvc.perform(post("/api/customer/orders/" + number + "/review")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reviewPayload(5, null)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Only delivered orders can be reviewed."));
    }

    @Test
    void reviewOrder_secondAttempt_rejected() throws Exception {
        String token = customerToken();
        String number = placeOrder(token, 1, 1);
        deliver(number);

        mockMvc.perform(post("/api/customer/orders/" + number + "/review")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reviewPayload(5, "Great")))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/customer/orders/" + number + "/review")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reviewPayload(1, "Changed my mind")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Order already reviewed."));

        mockMvc.perform(get("/api/order/number/" + number))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.review.rating").value(5))
                .andExpect(jsonPath("$.review.comment").value("Great"));
    }

    @Test
    void reviewOrder_someoneElsesOrder_looksLikeNotFound() throws Exception {
        String owner = customerToken();
        String number = placeOrder(owner, 1, 1);
        deliver(number);

        mockMvc.perform(post("/api/customer/orders/" + number + "/review")
                        .header("Authorization", "Bearer " + customerToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reviewPayload(1, null)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Order " + number + " not found"));

        mockMvc.perform(post("/api/customer/orders/FM-DOES-NOT-EXIST/review")
                        .header("Authorization", "Bearer " + owner)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reviewPayload(1, null)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Order FM-DOES-NOT-EXIST not found"));
    }

    @Test
    void reviewOrder_invalidRating_rejected() throws Exception {
        String token = customerToken();
        String number = placeOrder(token, 1, 1);
        deliver(number);

        for (Object rating : List.of(0, 6, -1, 4.5)) {
            mockMvc.perform(post("/api/customer/orders/" + number + "/review")
                            .header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(Map.of("rating", rating))))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Rating must be a whole number from 1 to 5"));
        }

        mockMvc.perform(post("/api/customer/orders/" + number + "/review")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("comment", "No stars"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Rating must be a whole number from 1 to 5"));

        // None of the refused attempts used up the order's one review.
        mockMvc.perform(get("/api/order/number/" + number))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.review").doesNotExist());
    }

    @Test
    void reviewOrder_commentLimit_enforced() throws Exception {
        String token = customerToken();
        String number = placeOrder(token, 1, 1);
        deliver(number);

        mockMvc.perform(post("/api/customer/orders/" + number + "/review")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reviewPayload(3, "x".repeat(1001))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Comment must be at most 1000 characters"));

        mockMvc.perform(post("/api/customer/orders/" + number + "/review")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reviewPayload(3, "x".repeat(1000))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.review.rating").value(3));
    }

    @Test
    void reviewOrder_withoutToken_unauthorized() throws Exception {
        mockMvc.perform(post("/api/customer/orders/FM-100001/review")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reviewPayload(5, null)))
                .andExpect(status().isUnauthorized());
    }

    // Chef 2 is reviewed only here, so its average is fully determined by this test.
    @Test
    void reviewOrder_updatesChefRatingToRoundedAverage() throws Exception {
        String token = customerToken();
        String first = placeOrder(token, 2, 3);
        String second = placeOrder(token, 2, 3);
        String third = placeOrder(token, 2, 3);
        deliver(first);
        deliver(second);
        deliver(third);

        submitReview(token, first, 5);
        mockMvc.perform(get("/api/chef/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rating").value(5.0));

        submitReview(token, second, 4);
        mockMvc.perform(get("/api/chef/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rating").value(4.5));

        // (5 + 4 + 4) / 3 = 4.333… → 4.3
        submitReview(token, third, 4);
        mockMvc.perform(get("/api/chef/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rating").value(4.3));
    }

    private void submitReview(String token, String number, int rating) throws Exception {
        mockMvc.perform(post("/api/customer/orders/" + number + "/review")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reviewPayload(rating, null)))
                .andExpect(status().isOk());
    }

    private String reviewPayload(int rating, String comment) throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("rating", rating);
        if (comment != null) {
            body.put("comment", comment);
        }
        return objectMapper.writeValueAsString(body);
    }

    private String placeOrder(String token, int chefId, int dishId) throws Exception {
        String response = mockMvc.perform(post("/api/order")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "chefId", chefId,
                                "receiverName", "Ann",
                                "receiverPhoneNumber", "+37491234567",
                                "receiverEmail", "ann@example.com",
                                "paymentType", "CASH",
                                "deliveryMethod", "TAKEAWAY",
                                "createOrderDishes", List.of(Map.of("dishId", dishId, "quantity", 1))
                        ))))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(response).get("number").asText();
    }

    /** Walks the order NEW → ACCEPTED → DELIVERED through the admin API and returns its id. */
    private long deliver(String number) throws Exception {
        String response = mockMvc.perform(get("/api/order/number/" + number))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        long id = objectMapper.readTree(response).get("id").asLong();
        for (String next : List.of("ACCEPTED", "DELIVERED")) {
            mockMvc.perform(patch("/admin/order/" + id + "/status")
                            .header("Authorization", "Bearer " + adminToken())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(Map.of("status", next))))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(next));
        }
        return id;
    }

    private String adminToken() {
        return jwtService.generateToken("admin", "ADMIN");
    }

    private String customerToken() throws Exception {
        String response = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "fullName", "Ann",
                                "email", "review-" + UUID.randomUUID() + "@example.com",
                                "phoneNumber", "+37491234567",
                                "password", "secret123"
                        ))))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(response).get("token").asText();
    }
}
