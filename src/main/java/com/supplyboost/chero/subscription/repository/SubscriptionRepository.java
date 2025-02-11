package com.supplyboost.chero.subscription.repository;

import com.supplyboost.chero.subscription.model.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {
}
