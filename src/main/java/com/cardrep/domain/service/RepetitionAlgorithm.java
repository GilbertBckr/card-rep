package com.cardrep.domain.service;

import com.cardrep.domain.model.Card;

import java.util.List;

public interface RepetitionAlgorithm {

    Card selectNextCard(List<Card> cards);

    String getName();
}
