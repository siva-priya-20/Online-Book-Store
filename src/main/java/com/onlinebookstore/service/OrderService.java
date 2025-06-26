package com.onlinebookstore.service;

import com.onlinebookstore.dto.OrderDTO;
import com.onlinebookstore.dto.OrderItemDTO;
import com.onlinebookstore.dto.OrderResponseDTO;
import com.onlinebookstore.exception.ResourceNotFoundException;
import com.onlinebookstore.model.Order;
import com.onlinebookstore.model.OrderItem;
import com.onlinebookstore.repository.BookRepository;
import com.onlinebookstore.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private BookRepository bookRepository;

    public OrderResponseDTO placeOrder(OrderDTO dto) {
        Order order = new Order();
        order.setUserId(dto.getUserId());

        List<OrderItem> items = dto.getItems().stream().map(i -> {
            if (!bookRepository.existsById(i.getBookId())) {
                throw new ResourceNotFoundException("Book not found: ID = " + i.getBookId());
            }
            OrderItem item = new OrderItem();
            item.setBookId(i.getBookId());
            item.setQuantity(i.getQuantity());
            return item;
        }).collect(Collectors.toList());

        order.setItems(items);
        order.setTotalAmount(calculateTotal(dto));
        Order saved = orderRepository.save(order);
        return toResponseDTO(saved);
    }

    public List<OrderResponseDTO> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public OrderResponseDTO getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        return toResponseDTO(order);
    }

    private OrderResponseDTO toResponseDTO(Order order) {
        OrderResponseDTO dto = new OrderResponseDTO();
        dto.setOrderId(order.getId());
        dto.setUserId(order.getUserId());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setItems(order.getItems().stream().map(item -> {
            OrderItemDTO i = new OrderItemDTO();
            i.setBookId(item.getBookId());
            i.setQuantity(item.getQuantity());
            return i;
        }).collect(Collectors.toList()));
        return dto;
    }

    private double calculateTotal(OrderDTO dto) {
        return dto.getItems().stream().mapToDouble(item -> {
            return bookRepository.findById(item.getBookId())
                    .orElseThrow(() -> new ResourceNotFoundException("Book not found"))
                    .getPrice() * item.getQuantity();
        }).sum();
    }
}
