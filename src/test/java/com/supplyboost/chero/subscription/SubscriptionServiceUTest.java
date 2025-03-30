package com.supplyboost.chero.subscription;

import com.supplyboost.chero.subscription.model.Subscription;
import com.supplyboost.chero.subscription.model.SubscriptionPeriod;
import com.supplyboost.chero.subscription.model.SubscriptionStatus;
import com.supplyboost.chero.subscription.model.SubsriptionType;
import com.supplyboost.chero.subscription.repository.SubscriptionRepository;
import com.supplyboost.chero.subscription.service.SubscriptionService;
import com.supplyboost.chero.user.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SubscriptionServiceUTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Spy
    @InjectMocks
    private SubscriptionService subscriptionService;

    @Test
    void givenUser_whenCreateDefaultSubscription_thenSubscriptionIsInitializedAndSaved() {
        User user = User.builder()
                .id(UUID.randomUUID())
                .username("testUser")
                .build();

        Subscription subscription = Subscription.builder()
                .id(UUID.randomUUID())
                .owner(user)
                .type(SubsriptionType.DEFAULT)
                .period(SubscriptionPeriod.MONTHLY)
                .status(SubscriptionStatus.ACTIVE)
                .price(new BigDecimal("0.00"))
                .auto_renew(true)
                .createdOn(LocalDateTime.now())
                .expiredOn(LocalDateTime.now().plusMonths(1))
                .build();

        when(subscriptionRepository.save(any(Subscription.class))).thenReturn(subscription);

        Subscription result = subscriptionService.createDefaultSubscription(user);

        assertNotNull(result);
        assertEquals(user, result.getOwner());
        assertEquals(SubsriptionType.DEFAULT, result.getType());
        assertEquals(SubscriptionPeriod.MONTHLY, result.getPeriod());
        assertEquals(SubscriptionStatus.ACTIVE, result.getStatus());
        assertEquals(new BigDecimal("0.00"), result.getPrice());
        assertTrue(result.isAuto_renew());

        verify(subscriptionRepository).save(any(Subscription.class));
    }

}
