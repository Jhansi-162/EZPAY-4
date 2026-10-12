package com.ezpay.repository;

import com.ezpay.entity.Payment;
import com.ezpay.entity.PaymentStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public class PaymentRepository {

    private final JdbcTemplate jdbcTemplate;

    public PaymentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Payment> paymentRowMapper = (rs, rowNumber) -> {
        Payment payment = new Payment();

        payment.setId(rs.getLong("ID"));
        payment.setAmount(rs.getBigDecimal("AMOUNT"));
        payment.setCurrency(rs.getString("CURRENCY"));
        payment.setDescription(rs.getString("DESCRIPTION"));
        payment.setStatus(PaymentStatus.valueOf(rs.getString("STATUS")));

        Timestamp createdAt = rs.getTimestamp("CREATED_AT");
        if (createdAt != null) {
            payment.setCreatedAt(createdAt.toLocalDateTime());
        }

        return payment;
    };

    public Payment save(Payment payment) {
        Long id = jdbcTemplate.queryForObject(
                "SELECT PAYMENT_SEQ.NEXTVAL FROM DUAL",
                (rs, rowNumber) -> rs.getLong(1)
        );

        payment.setId(id);

        String sql = "INSERT INTO PAYMENTS "
                + "(ID, AMOUNT, CURRENCY, DESCRIPTION, STATUS, CREATED_AT) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(
                sql,
                payment.getId(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getDescription(),
                payment.getStatus().name(),
                Timestamp.valueOf(payment.getCreatedAt())
        );

        return payment;
    }

    public List<Payment> findAll() {
        String sql = "SELECT ID, AMOUNT, CURRENCY, DESCRIPTION, STATUS, CREATED_AT "
                + "FROM PAYMENTS ORDER BY ID";

        return jdbcTemplate.query(sql, paymentRowMapper);
    }

    public Optional<Payment> findById(Long id) {
        String sql = "SELECT ID, AMOUNT, CURRENCY, DESCRIPTION, STATUS, CREATED_AT "
                + "FROM PAYMENTS WHERE ID = ?";

        List<Payment> results = jdbcTemplate.query(
                sql,
                paymentRowMapper,
                id
        );

        return results.stream().findFirst();
    }
}