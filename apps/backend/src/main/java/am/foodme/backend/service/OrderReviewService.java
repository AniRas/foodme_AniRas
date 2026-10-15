package am.foodme.backend.service;

import am.foodme.backend.dto.OrderDto;
import am.foodme.backend.dto.OrderReviewRequestDto;
import am.foodme.backend.exceptionHandler.BadRequestException;
import am.foodme.backend.exceptionHandler.NotFoundException;
import am.foodme.backend.model.Chef;
import am.foodme.backend.model.Customer;
import am.foodme.backend.model.Order;
import am.foodme.backend.model.OrderReview;
import am.foodme.backend.repository.ChefRepository;
import am.foodme.backend.repository.CustomerRepository;
import am.foodme.backend.repository.OrderRepository;
import am.foodme.backend.repository.OrderReviewRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
public class OrderReviewService {

    static final String NOT_DELIVERED_MESSAGE = "Only delivered orders can be reviewed.";
    static final String ALREADY_REVIEWED_MESSAGE = "Order already reviewed.";

    private final OrderRepository orderRepository;
    private final OrderReviewRepository orderReviewRepository;
    private final CustomerRepository customerRepository;
    private final ChefRepository chefRepository;

    public OrderReviewService(OrderRepository orderRepository, OrderReviewRepository orderReviewRepository,
                              CustomerRepository customerRepository, ChefRepository chefRepository) {
        this.orderRepository = orderRepository;
        this.orderReviewRepository = orderReviewRepository;
        this.customerRepository = customerRepository;
        this.chefRepository = chefRepository;
    }

    @Transactional
    public OrderDto reviewOrder(String orderNumber, OrderReviewRequestDto request, String customerEmail) {
        Customer customer = customerRepository.findByEmail(customerEmail == null ? "" : customerEmail.trim().toLowerCase())
                .orElseThrow(() -> new NotFoundException("Customer not found"));

        Order order = orderRepository.findByNumber(orderNumber)
                .filter(o -> o.getCustomer() != null && o.getCustomer().getId().equals(customer.getId()))
                // Someone else's order looks exactly like a missing one.
                .orElseThrow(() -> new NotFoundException("Order " + orderNumber + " not found"));

        if (!"DELIVERED".equals(order.getStatus())) {
            throw new BadRequestException(NOT_DELIVERED_MESSAGE);
        }

        // Lock the chef first: it serialises concurrent reviews for this chef, so the
        // duplicate check and the average below both see every committed review.
        Chef chef = chefRepository.findByIdForUpdate(order.getChef().getId())
                .orElseThrow(() -> new NotFoundException("Chef " + order.getChef().getId() + " not found"));

        if (orderReviewRepository.existsByOrderId(order.getId())) {
            throw new BadRequestException(ALREADY_REVIEWED_MESSAGE);
        }

        OrderReview review = new OrderReview();
        review.setOrder(order);
        review.setCustomer(customer);
        review.setChef(chef);
        review.setRating(request.getRating().intValueExact());
        String comment = request.getComment() == null ? null : request.getComment().trim();
        review.setComment(comment == null || comment.isEmpty() ? null : comment);
        review.setCreatedAt(LocalDateTime.now());
        try {
            orderReviewRepository.saveAndFlush(review);
        } catch (DataIntegrityViolationException e) {
            // Unique order_id backstop for any race the lock didn't cover.
            throw new BadRequestException(ALREADY_REVIEWED_MESSAGE);
        }
        order.setReview(review);

        chef.setRating(roundToOneDecimal(orderReviewRepository.averageRatingForChef(chef.getId())));
        chefRepository.save(chef);

        return OrderDto.mapEntityToDto(order);
    }

    static double roundToOneDecimal(double value) {
        return BigDecimal.valueOf(value).setScale(1, RoundingMode.HALF_UP).doubleValue();
    }
}
